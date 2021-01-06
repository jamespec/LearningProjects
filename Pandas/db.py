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

    print()
    for col in cursor.description:
        print("{0:<13}".format(col.name), end="")

    print()

    for col in cursor.description:
        print("------------ ", end="")

    print("")
    
    for r in rows:
        for c in r:
            print("{0:<13}".format(c), end="")
            
        print("")


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
            cursor.execute("select namelast, namefirst, playerid from people where playerid LIKE %s order by namelast;", (playerid,))
            display_result_table(cursor)

        elif action == "2":  # Team
            teamid = input("Enter teamid: ").strip()
            cursor.execute("select yearid, teamid, franchid, name from teams where teamid LIKE %s order by yearid, teamid;", (teamid,))
            display_result_table(cursor)

        elif action.upper() == "Q":
            break
