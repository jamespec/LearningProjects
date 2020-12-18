import psycopg2
from psycopg2 import Error


class SalaryDB:
    """SalaryDB Class - contains routines for managing Salary rows in the database.
    Uses a trivial scheme for managing the id space where each year gets a slice of 5000 ids.
    The ids are negative and start from (yearid-2300)*5000 and go more negative by one for each row.
    We'll deal with 2301 when it comes. This allows for multiple SalaryDB objects to be working on the database.
    """
    _connection = None
    _cursor = None
    _nextId = None
    _yearid = None
    _debug = False
    _team_map = {}
    _player_map = {}

    def __init__(self, connection, yearid: int, debug=False) -> None:
        if not connection or not yearid or yearid < 1850:
            raise RuntimeError("Must supply connection and yearid SalaryDB constructor")

        self._connection = connection
        self._cursor = connection.cursor()
        self._debug = debug
        self._yearid = yearid
        self._nextId = (yearid-2300)*5000
        self._load_team_map()
        self._load_player_map()

    def _load_team_map(self) -> None:
        """_load_team_map - internal routine to initialize _team_map with team records keyed by franchid.
        """
        self._cursor.execute("select id, franchid, teamid, lgid from teams where yearid = %s;", (self._yearid,))
        all_teams = self._cursor.fetchall()
        for team in all_teams:
            r = {'id': team[0], 'franchid': team[1], 'teamid': team[2], 'lgid': team[3]}
            self._team_map[team[1]] = r

    def _load_player_map(self) -> None:
        """_load_player_map - internal routine to initialize _player_map with People records keyed by
        namefirst, namelast, and team_id with spaced removed. Used to lookup playerid.
        """
        # Loading people that have had ab appearance in the year specified
        # This might not be general enough as some players get paid even if they don't play
        sql = """\
        select p.playerid, p.namefirst, p.namelast, p.namegiven, a.team_id
        from people p
        INNER JOIN appearances a ON p.playerid = a.playerid and a.yearid = %s
        """

        self._cursor.execute(sql, (self._yearid,))
        duplicates = 0
        all_players = self._cursor.fetchall()
        for player in all_players:
            r = {'playerid': player[0], 'namefirst': player[1], 'namelast': player[2],
                 'namegiven': player[3], 'team_id': player[4]}

            # Build a key from namefirst, namelast and team_id, then remove all spaces
            # Make sure we don't already have the player loaded, count and report duplicates.
            key = "{}{}{}".format(player[1], player[2], player[4]).replace(" ", "")
            if self._player_map.get(key) is None:
                self._player_map[key] = r
            else:
                duplicates += 1

            # We'll add the player again using his given first name if different from namefirst
            given_first = player[3].split()[0]
            if given_first != player[1]:
                key2 = "{}{}{}".format(given_first, player[2], player[4]).replace(" ", "")
                if self._player_map.get(key2) is None:
                    self._player_map[key2] = r
                else:
                    duplicates += 1

        if duplicates > 0:
            raise RuntimeError("Duplicates found building player map: " + str(duplicates))

    def delete_salary_rows(self, debug: bool = False) -> None:
        """delete_salary_rows - remove existing Salary rows from the database for a specified yearid
        """
        try:
            sql = "DELETE FROM SALARIES where id < 0 and yearid = {};".format(self._yearid)
            if debug:
                print(sql)

            self._cursor.execute(sql)
            self._connection.commit()

        except Error as error:
            self._connection.rollback()
            raise RuntimeError("Database error deleting old rows", error)

    def insert_salary_row(self, name: str, franchid: str, salary: float, debug: bool = False) -> None:
        """insert_salary_row - insert a single salary row
        This is meant to be called a number of times in a batch before committing changes.
        Exceptions should be caught and managed by caller.
        """
        # Build a key from namefirst, namelast and team_id, then remove all spaces
        key = name.replace(" ", "")

        team = self._team_map.get(franchid)
        if team and team.get('id'):
            key = key + str(team.get('id'))

        player = self._player_map.get(key)
        if player:
            sql = """INSERT INTO SALARIES VALUES ( {}, {}, '{}', {}, '{}', '{}', {} );\
            """.format(self._nextId, self._yearid, team['teamid'], team['id'], team['lgid'], player['playerid'], salary)
            if debug:
                print(sql)

            self._cursor.execute(sql)
            self._nextId -= 1

        else:
            raise LookupError("Player not found in map: " + key)


if __name__ == "__main__":
    def test_main():
        connection = None

        try:
            connection = psycopg2.connect(user="postgres",
                                          password="honey6",
                                          host="craftroom.local",
                                          port="5432",
                                          database="baseball")

            yearid = 2017
            salarydb = SalaryDB(connection, yearid)
            salarydb.delete_salary_rows(True)
            salarydb.insert_salary_row("Clayton Kershaw", "LAD", 33000000, True)
            connection.commit()

        finally:
            if connection:
                connection.close()

    test_main()
