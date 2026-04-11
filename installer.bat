@echo off
cd /d "%~dp0"

javaw -cp "bin;lib/*" huffman.Main

pause