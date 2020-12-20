#!/usr/bin/env python
import sys
import os
import requests
from bs4 import BeautifulSoup

if not len(sys.argv) in [2,3]:
    print("Usage: {} <year> [-o <outfile>]".format(sys.argv[0]))
    exit(1)

yearid = sys.argv[1]

URL = "https://www.usatoday.com/sports/mlb/salaries/{}/player/all/".format(yearid)
r = requests.get(URL)
soup = BeautifulSoup(r.content, 'html5lib')

table_rows = soup.find_all("tr")
for row in table_rows:
    name_tag = row.find("td", class_="player_display")
    team_tag = row.find("td", class_="team_abbr")
    salary_tag = row.find("td", class_="salary")
    if name_tag and team_tag and salary_tag:
        print("{}\t{}\t{}\t{}".format(yearid, name_tag.text.strip(), team_tag.text.strip(), salary_tag.text.strip()))
