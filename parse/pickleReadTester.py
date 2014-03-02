import sys
import pickle

rawDict = pickle.load(open('stem_dict_small.pkl','r'))
for item in rawDict.iteritems():
	print item