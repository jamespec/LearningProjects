import json


def load_db():
    with open("data.json") as f:
        return json.load(f)


db = load_db()
