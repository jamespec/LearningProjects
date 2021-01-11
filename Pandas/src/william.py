#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Jan  5 10:16:10 2021

@author: William
"""

import psycopg2

c = psycopg2.connect(user = "postgres", password = "honey6", database = "baseball")

cursor = c.cursor()

while True:
    myteam = input("Enter team ID: ").strip()
    if myteam == "":
        break
    cursor.execute("select teamid, franchid, name from teams where teamid like %s;", (myteam))
    
    rows = cursor.fetchall()
    
    for col in cursor.description:
        print("{0:<21}".format(col.name), end="")
    print("")
    for r in rows:
        for c in r:
            print("{0:<21}".format(c), end="")
        
        print("")
        