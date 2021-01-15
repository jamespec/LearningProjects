#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Jan  5 10:16:34 2021

@author: James
"""
import psycopg2


def get_next_action():
    print()
    print("1) Player Info")
    print("2) Team Info")
    print("Q) Quit")
    return input("Choose Command[1,2,Q]: ")


def display_result_table(cursor):
    rows = cursor.fetchall()

    if len(rows) == 0:
        return
    elif len(rows) == 1:
        row = rows[0]
        for i, col in enumerate(row):
            print("{0:>20}: {1}".format(cursor.description[i].name, col))
    else:
        print()
        for col in cursor.description:
            print("{0:<13}".format(col.name), end="")

        print()

        for col in cursor.description:
            print("------------ ", end="")

        print("")

        for r in sorted(rows, key=row_key):
            for c in r:
                if c is None:
                    c = 'unknown'

                print("{0:<13}".format(c), end="")

            print("")


def row_key(row):
    return [row[2], row[0]]


def chooseOne(players):
    print("    LastName        FirstName    BYear   Playerid")
    for num, player in enumerate(players):
        if player['namelast'] is None or \
                player['namefirst'] is None or \
                player['birthyear'] is None:
            continue

        print("{0:>2}) {1:15} {2:12} {3:<7} {4}".format(num,
                                                        player['namelast'],
                                                        player['namefirst'],
                                                        player['birthyear'],
                                                        player['playerid']))

    choice = input("Enter choice: ").strip()
    try:
        choice = int(choice)
        if choice < num:
            return players[choice]

    except Exception:
        pass

    return None


def display_record(rec_dict):
    for field, value in rec_dict.items():
        if value:
            print("{0:>20}: {1}".format(field, value))


def load_players(connection, playerid):
    players = []

    with conn.cursor() as cursor:
        cursor.execute("""
                       select *
                       from people 
                       where playerid LIKE %s 
                       order by namelast;""", (playerid,))

        for row in cursor:
            nextPlayer = {}
            for colNum, colVal in enumerate(row):
                colName = cursor.description[colNum][0]
                nextPlayer[colName] = colVal

            players.append(nextPlayer)

        return players


if __name__ == "__main__":
    print("=" * 40)
    print("*      Baseball Statistics R Us!       *")
    print("*--------------------------------------*")

    conn = psycopg2.connect(user="postgres", password="honey6", database="baseball")
    cursor = conn.cursor()

    while True:
        action = get_next_action().strip()
        if action == "1":  # Player
            playerid = input("Enter playerid: ").strip()
            players = load_players(conn, playerid)
            if len(players) == 0:
                continue

            if len(players) > 1:
                player = chooseOne(players)
            else:
                player = players[0]

            if player:
                display_record(player)

        elif action == "2":  # Team
            teamid = input("Enter teamid: ").strip()
            cursor.execute(
                "select yearid, teamid, franchid, name from teams where teamid LIKE %s order by yearid, teamid;",
                (teamid,))
            display_result_table(cursor)

        elif action.upper() == "Q":
            break
