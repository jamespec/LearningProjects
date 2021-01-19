
from flask import Flask, render_template, abort, request, redirect, url_for
import model


app = Flask(__name__)


@app.route("/")
def welcome_view():
    return render_template("welcome.html")


@app.route("/team", methods=["GET", "POST"])
def team_view():
    if request.method == "POST":
        year = request.form['year']
        teamid = request.form['teamid']
        rows = model.fetch_team(year, teamid)
        return render_template("team.html", players=rows, num_players=len(rows), year=year, teamid=teamid)
    else:
        return render_template("team.html", num_players=0)


@app.route("/player", methods=["GET", "POST"])
def player_view():
    return "Coming soon!"


if __name__ == "__main__":
    app.run(debug=True)
