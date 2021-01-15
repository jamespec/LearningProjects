#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Jan  5 10:16:10 2021

@author: William
"""

import psycopg2


if __name__ == "__main__":
    c = psycopg2.connect(user="postgres", password="honey6", database="baseball")

    cursor = c.cursor()

    while True:
        myteam = input("Enter team ID: ").strip()
        if myteam == "":
            break

        cursor.execute("select name, teamid, franchid from teams where yearid > 2010 and teamid like %s;", (myteam,))

        rows = cursor.fetchall()
        maxwidths = [0] * len(cursor.description)
        for row in rows:
            for i, col in enumerate(row):
                if len(str(col)) > maxwidths[i]:
                    maxwidths[i] = len(str(col))

        for i, col in enumerate(cursor.description):
            if len(col.name) > maxwidths[i]:
                maxwidths[i] = len(col.name)

            print("{0:<21}".format(col.name), end="")

        print("")
        for r in rows:
            for c in r:
                print("{0:<{width}}".format(c, width=5), end="")

            print("")
