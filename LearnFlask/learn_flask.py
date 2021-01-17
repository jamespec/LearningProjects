
from flask import Flask, render_template, abort, jsonify
import model


app = Flask(__name__)

view_count = 0


@app.route("/")
def welcome_view():
    global view_count
    view_count += 2
    return render_template("welcome.html", db=model.db, views=view_count)


@app.route("/salary/<int:index>")
def salary_view(index):
    try:
        salary_record = model.db[index]
        return render_template("salary.html",
                               salary_record=salary_record,
                               index=index,
                               max_index=len(model.db)-1)
    except IndexError:
        abort(404)


if __name__ == "__main__":
    app.run(debug=True)
