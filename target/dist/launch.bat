@echo off
echo Lancement du Chatbot Cyclone...
cd %~dp0
start javaw -jar target\dist\ChatbotCyclone-jar-with-dependencies.jar
exit 