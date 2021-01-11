#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Wed Dec 30 19:14:09 2020

@author: James
"""

import os
import pandas as pd
from pandas import isnull

people_o = pd.read_csv("baseballdatabank-master/people.csv")
people_o.head()

people_o.shape
people_o.columns

people_o['birthYear'].pipe(isnull).value_counts()

isnull(people_o['birthYear']).value_counts()

people = people_o.dropna(subset=["playerID"])

people = people.loc[people["birthYear"] > 1960]

people.filter(regex="(playerID|height|weight|^name)")


def mem_mib(df):
    print("{0:.2f} MiB".format(df.memory_usage().sum() / (1024*1024)))


people = people.set_index('playerID')
people.columns

pd.Categorical(people['birthDay'])

appearances = pd.read_csv("baseballdatabank-master/appearances.csv")
appearances.head()
appearances = appearances.loc[appearances['G_all'] > 10]
appearances = appearances.filter(['playerID', 'yearID', 'teamID', 'G_all', 'GS', 'G_batting', 'G_defense'])
appearances = appearances.loc[appearances['yearID'] > 1980]
appearances.reset_index(drop=True, inplace=True)

teams = pd.read_csv("baseballdatabank-master/teams.csv")
teams = teams[['yearID', 'teamID', 'name']]
teams = teams.loc[teams['yearID'] > 1980]
pd.Categorical(teams['teamID'])
pd.Categorical(teams['name'])
teams.reset_index(drop=True, inplace=True)
teams.to_pickle('teams.pickle')
teams.nunique()


teams.index
