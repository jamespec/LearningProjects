#! /usr/bin/env python
import psycopg2
import os
import sys
from salarydb import SalaryDB


def spotrac_main(filename: str, yearid: int, franchid: str, debug: bool = False) -> None:

    player_name_override_map = {'Raffy Lopez': 'Rafael Lopez',
                                'Dwight Smith Jr.': 'Dwight Smith',
                                'A.J. Ramos': 'AJ Ramos',
                                'Zack Wheeler': 'Zach Wheeler',
                                'Tom Milone': 'Tommy Milone',
                                'C.C. Sabathia': 'CC Sabathia',
                                'Tom Layne': 'Tommy Layne',
                                'D.J. LeMahieu': 'DJ LeMahieu',
                                }

    num_bad = 0

    connection = None
    try:
        connection = psycopg2.connect(user="postgres",
                                      password="honey6",
                                      host="craftroom.local",
                                      port="5432",
                                      database="baseball")

        salarydb = SalaryDB(connection, yearid, debug)
        salarydb.delete_salary_rows(debug)

        hold_name = None
        with open(filename, "r") as input_file:

            # One line per record:
            # We need fields 0 for name and 7 for Payroll Salary, including bonuses received
            for line in input_file:
                row_fields = line.split('\t')

                # Special case - The injured players have an embedded \n we need to deal with
                # The name will appear on it's own, we save it.
                # the next line will have the injury in the name field, we replace it
                if hold_name:
                    row_fields[0] = hold_name
                    hold_name = None

                if len(row_fields) == 1:
                    hold_name = row_fields[0]
                    continue

                try:
                    name = row_fields[0].strip()
                    salary = float(row_fields[7].lstrip('$').replace(",", ""))
                    name = player_name_override_map.get(name, name)

                except Exception as error:
                    # Bad row, ignore and keep going
                    print("Error: ", error)
                    num_bad += 1
                    continue

                # Lookup errors are caught and ignored, others end the program
                try:
                    salarydb.insert_salary_row(name, franchid, salary, debug)

                except LookupError as error:
                    print("LookupError: ", error)
                    num_bad += 1
                    continue

            connection.commit()

            print("Number of skipped rows: " + str(num_bad))

    except Exception as error:
        print("Error: ", error)
    finally:
        if connection:
            connection.close()


if __name__ == '__main__':
    if len(sys.argv) != 4:
        print("Usage: {} <filename> <yearid, e.g. 2018> <franchid, e.g NYY>".format(sys.argv[0]))
    else:
        filename = sys.argv[1]
        yearid = int(sys.argv[2])
        franchid = sys.argv[3]

        if not os.access(filename, os.R_OK) or  yearid < 1850:
            print("Parameter error, file not readable or year before 1850")
        else:
            spotrac_main(filename, yearid, franchid)
