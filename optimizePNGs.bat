@echo off
setlocal

set "ASSETS=%~dp0src\main\resources\assets"
oxipng -o max --strip safe --zopfli --recursive "%ASSETS%"