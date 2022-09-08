from ctypes import alignment
from subprocess import *
import os
import shutil


bin = os.fsencode("./data/bins/")
alignments = os.fsencode("./data/alignments/")

bins = []
for file in os.listdir(bin):
    filename = os.fsdecode(file)
    
    if filename.endswith(".fasta"):
        taxa = "".join(filename.split(".")[0])
        taxa = "-".join(taxa.split("-")[1:])
        bins.append(taxa)   

aligns = []
for file in os.listdir(alignments):
    filename = os.fsdecode(file)
    
    if filename.endswith(".blasttab"):
        taxa = "".join(filename.split(".")[0])
        taxa = "-".join(taxa.split("-")[1:])
        aligns.append(taxa)    

#print(bins)
#print(aligns)


diff = [x for x in aligns if x not in bins]
print(len(diff))


for file in os.listdir(alignments):

    filename = os.fsdecode(file)
    if filename.endswith(".blasttab"):
        
        for x in diff:
            if x in filename:
                shutil.move("data/alignments/{0}".format(filename) , "data/unpaired/{0}".format(filename))
                print(x)
                print(filename)
                break

bins_list = os.listdir(bin)
alignments_list = os.listdir(alignments)
for i in range(len(bins_list)):
    process = Popen(['java', '-jar', 'out/artifacts/taxonomic_segmentation_jar/taxonomic_segmentation.jar', 
    '-t', "data/bins/{0}".format(bins_list[i]) , "data/alignments/{0}".format(alignments_list[i], 'data/database/megan-map-Feb2022.db' )], stdout=PIPE, stderr=PIPE)
    result = process.communicate()
    print(result)