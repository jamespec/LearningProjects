#! /usr/bin/env python
import psycopg2
import os
import sys
from salarydb import SalaryDB


def salaries_main(filename: str, yearid: int, debug: bool = False) -> None:
    franchid_override_map = {'SF': 'SFG', 'LAA': 'ANA', 'WSH': 'WSN',
                             'KC': 'KCR', 'MIA': 'FLA', 'CWS': 'CHW',
                             'TB': 'TBD', 'SD': 'SDP'}

    player_name_override_map = {'Zach Britton': 'Zack Britton',
                                'Daniel R. Robertson': 'Daniel Robertson',
                                'Jose A. Ramirez': 'Jose Ramirez',
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

        with open(filename, "r") as f:

            # Three lines to a record:
            #  1) Ignored, the salary rank
            #  2) Name, full name, first followed by last. May contain initials or suffix
            #  3) Tab delimited record including franchid, field 0, and salary, field 2
            while f.readline():
                name = f.readline().rstrip()
                data = f.readline().split("\t")

                try:
                    franchid = franchid_override_map.get(data[0], data[0])
                    salary = float(data[2].lstrip('$').replace(",", ""))

                    # Look for a name override and remove a training Jr.
                    name = player_name_override_map.get(name, name)
                    names = name.split()
                    if names[-1] == "Jr.":
                        name = "".join(names[:-1])

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
    if len(sys.argv) != 3:
        print("Usage: {} <filename> <yearid, e.g. 2018>".format(sys.argv[0]))
    else:
        filename = sys.argv[1]
        yearid = int(sys.argv[2])

        if not os.access(filename, os.R_OK) or  yearid < 1850:
            print("Parameter error, file not readable or year before 1850")
        else:
            salaries_main(filename, yearid)
