#!/usr/bin/bash
ALIGNMENTS=data/alignments
BINS=data/bins

 for a b in {ALIGNMENTS:^BINS};
   do
     echo $a $b;
   done
