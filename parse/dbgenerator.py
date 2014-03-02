import sys
import xml.etree.ElementTree as ET
import sqlite3
import pickle

root = ET.parse('acts.xml')
con = sqlite3.connect('act.db')

c = con.cursor()

#"""
c.execute ('DELETE FROM Act')
c.execute ('DELETE FROM Stem')  
c.execute ('DELETE FROM Keyword')
con.commit()     
"""
c.execute('''CREATE TABLE Stem (SID INTEGER, Stem TEXT)''')
c.execute('''CREATE TABLE Act (AID INTEGER, Title TEXT, Link TEXT, Year INTEGER)''')
c.execute('''CREATE TABLE Keyword (SID INTEGER, AID INTEGER, Freq INTEGER, MedWord TEXT)''')
"""

aid = 0
Act = []
for act in root.iter('Act'): 
	lang = act.find('Language')
	if lang is not None and lang.text == 'eng':
		Act.append((aid, act.find('Title').text,act.find('LinkToHTMLToC').text,int(act.find('CurrentToDate').text.split('-',1).pop(0))))
		aid += 1 

c.executemany('INSERT INTO Act VALUES (?,?,?,?)', Act)
con.commit()

sid = 0
stemTable = dict()

for aiDict in range(aid):
	print aiDict
	rawDict = pickle.load(open('collection_of_dicts/'+str(aiDict)+'.pkl','r'))

	stemToWordDict = dict()


	for i in range(aid+1):
		stemToWordDict[i] = dict()

	for item in rawDict.iteritems():

		[AID, stem, word] = item[0].split('-',2)

		if stem not in stemToWordDict[int(AID)]:
			stemToWordDict[int(AID)][stem] = dict()	

		if word not in stemToWordDict[int(AID)][stem]:
			stemToWordDict[int(AID)][stem][word] = item[1]

		if stem not in stemTable:
			stemTable[stem] = sid
			sid +=1

	Keyword = []

	for iterAid in stemToWordDict.iterkeys():
		for stem in stemToWordDict[iterAid].iterkeys():
			totalFreq = 0
			for freq in stemToWordDict[iterAid][stem].itervalues():
				totalFreq += freq
			medWord = ""
			tempMaxF = 0

			for wordItem in stemToWordDict[iterAid][stem].iteritems():
				if wordItem[1] > tempMaxF:
					tempMaxF = wordItem[1]
					medWord = wordItem[0]

			Keyword.append((stemTable[stem],iterAid,totalFreq,medWord))

	c.executemany('INSERT INTO Keyword VALUES (?,?,?,?)', Keyword)
	con.commit()

Stem = dict()
for stemItem in stemTable.iteritems():
	Stem[stemItem[1]] = stemItem[0]

c.executemany('INSERT INTO Stem VALUES (?,?)', Stem.items())
con.commit()

con.close()