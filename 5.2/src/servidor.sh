#!/bin/bash
clear

javac ambos/*.java cliente/*.java servidor/*.java 
sudo java servidor/Servidor
