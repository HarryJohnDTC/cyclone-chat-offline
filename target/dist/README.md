# Chatbot Cyclone

Un assistant conversationnel spécialisé dans les cyclones et la sécurité, avec une interface graphique intuitive.

## Avantages

- Interface graphique simple et conviviale
- Spécialement configuré pour les questions sur les cyclones
- Réponses optimisées pour la sécurité des utilisateurs
- Rappel automatique du numéro d'urgence (118)
- Historique des conversations facilement consultable
- Pas besoin de connaissances techniques ou de ligne de commande
- Design adapté avec codes couleurs pour une meilleure lisibilité

## Prérequis

- Java 17 ou supérieur
- Ollama installé (https://ollama.ai)
- Le modèle llama3.2 installé

## Build

1. Installez Java 17 et Maven
2. Clonez le repository
3. Pour Windows : exécutez `build.bat`
   Pour Linux/Mac : exécutez `./build.sh`
4. Le JAR exécutable sera créé dans `target/ChatbotCyclone-jar-with-dependencies.jar`

## Exécution

1. Installez Ollama depuis https://ollama.ai
2. Installez le modèle : `ollama pull llama3.2`
3. Double-cliquez sur le JAR ou exécutez :
   ```bash
   java -jar target/ChatbotCyclone-jar-with-dependencies.jar
   ```

## Utilisation

1. Lancer l'application
2. Poser vos questions sur les cyclones
3. En cas d'urgence, appeler le 118

# Chatbot Cyclone - Installation

## Installation automatique

1. Téléchargez et décompressez ChatbotCyclone-Install.zip
2. Exécutez install.bat
3. Suivez les instructions à l'écran

## Installation manuelle

Si l'installation automatique ne fonctionne pas :

1. Installez Java 17 : https://download.oracle.com/java/17/latest/jdk-17_windows-x64_bin.exe
2. Installez Ollama : https://ollama.ai/download/windows
3. Ouvrez un terminal et exécutez : `ollama pull llama3.2`
4. Copiez le dossier ChatbotCyclone où vous voulez
5. Double-cliquez sur launch.bat pour démarrer

## En cas de problème

- Vérifiez que Java 17+ est installé : `java -version`
- Vérifiez qu'Ollama est installé : `ollama -v`
- Vérifiez que le modèle est installé : `ollama list`

## Support

En cas d'urgence : 118
