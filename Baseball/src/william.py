#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Jan  5 10:16:10 2021

@author: James
"""

import psycopg2


def display_table(rows, cols):
    """
    Prints a list of lists as a table with corresponding column headers
    Args:
        rows: the list of lists to treat as a table
        cols: a list of columns names

    Returns:
        Nothing
    """

    # Initialize max_widths with zero's for the maximum column widths
    # This creates a list by cloning "[0]" for each element of the cols list
    max_widths = [0] * len(cols)

    # Iterate over all the rows and compare the value with the max length
    # update if we have a new larger value.
    for row in rows:
        for i, colValue in enumerate(row):
            if len(colValue) > max_widths[i]:
                max_widths[i] = len(colValue)

    # Print the column headers,
    # first look to see if the column name is longer than the data and update the width
    for i, colName in enumerate(cols):
        if len(colName) > max_widths[i]:
            max_widths[i] = len(colName)

        # Notice that the field width is a parameter.
        # {0:<{1}} means use the 0 value, left justify and use the 1 value as the width
        print("{0:<{1}}  ".format(colName, max_widths[i]), end="")

    print()

    for row in rows:
        for i, colValue in enumerate(row):
            print("{0:<{1}}  ".format(colValue, max_widths[i]), end="")

        print()


if __name__ == "__main__":
    conn = psycopg2.connect(user="postgres", password="honey6", database="baseball")
    cursor = conn.cursor()

    while True:
        my_team = input("Enter team ID: ").strip()
        if my_team == "":
            break

        cursor.execute("select name, teamid, franchid from teams where yearid > 2010 and teamid like %s;", (my_team,))
        all_rows = cursor.fetchall()

        # !!! This is something new
        # This is called a "List Comprehension", building a list from elements in another list
        # in this case we iterate over the Columns in the list: cursor.description, assigning each to t
        # the new element is t.name, the name attribute of the Column object
        # this will produce a list that looks like this:  ['name', 'teamid', 'franchid' ]
        all_cols = [t.name for t in cursor.description]
        display_table(all_rows, all_cols)
