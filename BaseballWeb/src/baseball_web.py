
from flask import Flask, render_template, abort, request, redirect, url_for
import model


app = Flask(__name__)


@app.route("/")
def welcome_view():
    return render_template("welcome.html", db=model.db)


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


@app.route("/add_salary", methods=["GET", "POST"])
def add_salary():
    if request.method == "POST":
        salary_record = {"name": request.form['name'],
                         "salary": request.form['salary']}
        model.db.append(salary_record)
        model.save_db()
        return redirect(url_for('salary_view', index=len(model.db)-1))
    else:
        return render_template("add_salary.html")


@app.route("/delete_salary/<int:index>", methods=["GET", "POST"])
def delete_salary(index):
    try:
        if request.method == "POST":
            del model.db[index]
            model.save_db()
            return redirect(url_for('welcome_view'))
        else:
            return render_template("delete_salary.html",
                                   salary_record=model.db[index])
    except IndexError:
        abort(404)


if __name__ == "__main__":
    app.run(debug=True)
