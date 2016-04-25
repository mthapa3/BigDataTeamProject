import sys
import json
'''
The LIWC .dic format looks like this:
%
1   funct
2   pronoun
.
.
.
..
%
a   1   10
abdomen*    146 147
about   1   16  17

give that file into this as command line argument, get a json trie on stdout
'''

categories = {}
trie = {}


def addToTrie(key, categories):
    cursor = trie
    for letter in key:
        if letter == '*':
            cursor['*'] = categories
            break
        if letter not in cursor:
            cursor[letter] = {}
        cursor = cursor[letter]
    cursor['$'] = categories


dictionary_file = sys.argv[1]
with open(dictionary_file, 'r') as fp:
	for line in fp.readlines():
		if not line.startswith('%'):
			parts = line.strip().split('\t')
			if parts[0].isdigit():
				# store category names
				categories[parts[0]] = parts[1]
			else:
				# print parts[0], ':', parts[1:]
				addToTrie(parts[0], [categories[category_id] for category_id in parts[1:]])

print (json.dumps(trie, sort_keys=True))
