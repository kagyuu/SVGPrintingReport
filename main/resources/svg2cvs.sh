#!/bin/bash
 
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
