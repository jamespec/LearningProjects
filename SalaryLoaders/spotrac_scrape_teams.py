#!/usr/bin/env python
import sys
import requests
from bs4 import BeautifulSoup

if __name__ == "__main__":
    if not len(sys.argv) == 2:
        print("Usage: {} <year>".format(sys.argv[0]))
        exit(1)

    year = int(sys.argv[1])

    URL = "https://www.spotrac.com/mlb/payroll/{}/".format(year)
    # print("URL: " + URL)

    # Fill in your details here to be posted to the login form.
    payload = {
        'email': 'spotrac1999@icloud.com',
        'password': '1999Spot'
    }

    # Use 'with' to ensure the session context is closed after use.
    with requests.Session() as session:
        p = session.post('https://www.spotrac.com/signin/submit/', data=payload)
        teams_page = session.get(URL)
        teams_soup = BeautifulSoup(teams_page.content, 'html5lib')

        all_teams = teams_soup.find_all("td", class_="player")
        for team in all_teams:
            if not team.a:  # No anchor to call, skip
                continue

            # print(team.a.find("span", class_="xs-visible").text, end="")
            # print(" " + team.a['href'])
            franchid = team.a.find("span", class_="xs-visible").text
            team_url = team.a['href']

            print("Loading {} from {}".format(franchid, team_url), file=sys.stderr)

            team_page = session.get(team_url)
            team_soup = BeautifulSoup(team_page.content, 'html5lib')

            all_tables = team_soup.find_all("table")
            for table in all_tables:
                if "captotals" in table["class"]:  # Skip summary table
                    continue

                all_rows = table.find_all("tr")
                for row in all_rows:
                    name = row.find("a")
                    if not name:  # No name anchor?!?!? Skip
                        continue

                    salary = row.find("span", title="Total Adjusted Salary including base salary and bonuses")
                    if salary and salary.text[0] == "$":
                        print("{}\t{}\t{}\t{}".format(year, name.text, franchid, salary.text))
