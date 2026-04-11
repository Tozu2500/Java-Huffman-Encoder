@echo off
cd /d "%~dp0"
start "" javaw -cp "bin;HuffmanEncoder\lib\*" huffman.Main
exit
