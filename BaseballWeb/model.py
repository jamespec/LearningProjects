import psycopg2
from pprint import pprint as pp

conn = None


def fetch_team(year, teamid):
    global conn
    if conn is None:
        conn = psycopg2.connect(user="postgres", password="honey6")

    result = []
    with conn.cursor() as cursor:
        cursor.execute("""\
            select
                p.playerid,
                p.namefirst, 
                p.namelast,
                p.birthmonth || '/' || p.birthday || '/' || p.birthyear as birthday
            from
                appearances a
            inner join people p on a.playerid=p.playerid and a.yearid=%s
            where
                a.teamid=%s and a.g_all > 11
            """, (year, teamid))

        all_rows = cursor.fetchall()
        for row in all_rows:
            p = {'playerid': row[0], 'namefirst': row[1], 'namelast': row[2], 'birthday': row[3]}
            result.append(p)

        return result


if __name__ == '__main__':
    def main():
        result = fetch_team(2019, 'NYN')
        pp(result)

    main()
