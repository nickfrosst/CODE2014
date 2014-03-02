#!/usr/bin/env python
import nltk
from nltk.stem.lancaster import LancasterStemmer
st = LancasterStemmer()

import urllib2

import pickle

import sys
import xml.etree.ElementTree as ET
tree = ET.parse('acts.xml')
root = tree.getroot()


tagger = pickle.load(open("treebank_brill_aubt.pickle"))

stem_dict = {}

def save_obj(obj, name ):
    with open(name + '.pkl', 'wb') as f:
        pickle.dump(obj, f, pickle.HIGHEST_PROTOCOL)

def load_obj(name ):
    with open(name + '.pkl', 'r') as f:
        return pickle.load(f)


def fill_dict (node,act):
	if node.find('Text') is not None and node.find('Text').text:
		l = stems_list(node.find('Text').text)
		stem_pair_size = len(l)
		stem_pair_i = 0
		for stem_pair in l:
			k = act+'-'+stem_pair[0]+'-'+stem_pair[1]
			if k in stem_dict.keys():
				stem_dict[k] += 1
			else :
				stem_dict[k] = 1
			#print ('stem_pair 			' + str(stem_pair_i) + '/' + str(stem_pair_size))
			stem_pair_i += 1

	
def stems (url,act) : 
	print '		getting from ' + url
	data = urllib2.urlopen(url) 
	data_str = data.read()
	act_text = ET.fromstring(data_str)
	print '		got'

	sec_size = len(list(act_text.iter()))
	sec_i = 0
	for sec in act_text.iter():
		print (act + ' sec		' + str(sec_i) + '/' + str(sec_size))
		sec_i += 1

		bit_size = len(list(sec.iter()))
		bit_i = 0
		for bit in sec.iter():			
			print (act + ' bit		' + str(bit_i) + '/' + str(bit_size))
			bit_i += 1

			fill_dict(bit,act)


			





def stems_list (s):
	return map(lambda x: [st.stem(x[0]), x[0]],filter(lambda x: x[1] == 'NN' or x[1][0] == 'V', tagger.tag(nltk.word_tokenize(s))))
		#



size = len(list(root.iter('Act')))
i = 0
AID =0

target = range(int(sys.argv[1]),int(sys.argv[2]))

for acts in root.iter('Act'): 
	stem_dict = {}
	lang = acts.find('Language').text
	print 'acts ' +  str(i) + '/' + str(size)
	i +=1 
	if lang == 'eng'  :
		if i in target:
			stems(acts.find('LinkToXML').text,str(AID))
			save_obj(stem_dict,'dicts/'+str(AID))
		AID +=1


save_obj(stem_dict,'stem_dict_small')
sys.exit()		
		


