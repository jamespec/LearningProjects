import requests
from bs4 import BeautifulSoup

franchid = NYY

URL = "https://www.spotrac.com/mlb/new-york-yankees/payroll/"
r = requests.get(URL)

soup = BeautifulSoup(r.content, 'html5lib')

all_tables = soup.find_all("table")
for table in all_tables:
    if not "captotals" in table["class"]:
        all_rows = table.find_all("tr")
        for row in all_rows:
            name = row.find("a")
            if name:
                salary = row.find("span", title="Total Adjusted Salary including base salary and bonuses")
                if salary and salary.text[0] == "$":
                    print("{}\t{}\t{}".format(name.text, franchid, salary.text))


print(soup.prettify())
tables = soup.find_all("table")