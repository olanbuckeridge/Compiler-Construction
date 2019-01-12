#!/bin/bash

jjtree CALOlanBuckeridge.jjt
echo ""
javacc CALOlanBuckeridge.jj
echo ""
javac *.java
echo "Built"
