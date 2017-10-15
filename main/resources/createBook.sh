#!/bin/bash

# SVG to A3 PDF
if [ ! -d ./a3 ]; then
  mkdir ./a3
fi
 
ls | grep svg | while read SVG_FILE
do
  PDF_FILE=./a3/${SVG_FILE%[.]*}.pdf
  if [ ! -e $PDF_FILE ]; then
    echo "$SVG_FILE -> $PDF_FILE"
    inkscape $SVG_FILE --export-pdf=$PDF_FILE
  fi
done

# A3 PDF to A4 PDF
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

# CONBINE A BLANK PAGE AND A4 DIARY PAGES
pdftk blanksheet-a4-portrait.pdf ./a4/* blanksheet-a4-portrait.pdf cat output diary.pdf