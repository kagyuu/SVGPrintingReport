#!/bin/bash
 
if [ ! -d ./a4 ]; then
  mkdir ./a4
fi
 
ls ./a3 | while read A3_FILE
do
  A4_FILE=./a4/${A3_FILE%[.]*}
  A4L_FILE=${A4_FILE}L.pdf
  A4R_FILE=${A4_FILE}R.pdf
  echo "${A3_FILE} => ${A4L_FILE}, ${A4R_FILE}"
  gs -o $A4L_FILE -sDEVICE=pdfwrite -sPAPERSIZE=a4 -g5950x8420 -c "<</PageOffset[0 0]>> setpagedevice" -f ./a3/$A3_FILE
  gs -o $A4R_FILE -sDEVICE=pdfwrite -sPAPERSIZE=a4 -g5950x8420 -c "<</PageOffset[-595 0]>> setpagedevice" -f ./a3/$A3_FILE
done

