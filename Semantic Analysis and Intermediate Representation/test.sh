#!/bin/bash
FILES="/CAL/*"
for f in $FILES
do
  echo "File name: $f"
  java CALParser $f
done
