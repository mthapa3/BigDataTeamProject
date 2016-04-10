import json
import gzip


#####################################
#
# PROGRAM TO CREATE STRICT JSON FILES
#
#####################################
def parse(path):
  g = gzip.open(path, 'r')
  for l in g:
    yield json.dumps(eval(l))

#output file path
f = open("output.strict.complete", 'w')


for l in parse("meta_Electronics.json.gz"): #input gzip file path
  f.write(l + '\n')