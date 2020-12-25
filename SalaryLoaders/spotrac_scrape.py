#!/usr/bin/env python
import sys
import requests
from bs4 import BeautifulSoup

if __name__ == "__main__":
    if not len(sys.argv) == 4:
        print("Usage: {} <team name> <franchid> <year>".format(sys.argv[0]))
        exit(1)

    team_name = sys.argv[1]
    franchid = sys.argv[2]
    year = int(sys.argv[3])

    URL = "https://www.spotrac.com/mlb/{}/payroll/{}/".format(team_name, year)
    print("URL: " + URL)

    # # Fill in your details here to be posted to the login form.
    # payload = {
    #     'email': 'spotrac1999@icloud.com',
    #     'password': '1999Spot'
    # }
    #
    # # Use 'with' to ensure the session context is closed after use.
    # with requests.Session() as s:
    #     p = s.post('https://www.spotrac.com/signin/submit/', data=payload)
    #     r = s.get(URL)
    r = requests.get(URL)
    soup = BeautifulSoup(r.content, 'html5lib')

    all_tables = soup.find_all("table")
    for table in all_tables:
        if "captotals" not in table["class"]:
            all_rows = table.find_all("tr")
            for row in all_rows:
                name = row.find("a")
                if name:
                    salary = row.find("span", title="Total Adjusted Salary including base salary and bonuses")
                    if salary and salary.text[0] == "$":
                        print("{}\t{}\t{}".format(name.text, franchid, salary.text))
