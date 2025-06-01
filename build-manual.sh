#!/bin/bash

echo "Building Advanced Mana manually..."

# Compiler et traiter les ressources
./gradlew clean compileJava processResources

# Créer le dossier libs
mkdir -p build/libs

# Créer le JAR
cd build
jar cf libs/advancedmana-1.0.0-manual.jar \
    -C classes/java/main . \
    -C resources/main .

echo "JAR créé : build/libs/advancedmana-1.0.0-manual.jar"

# Vérifier le contenu
echo "Contenu du JAR :"
jar tf libs/advancedmana-1.0.0-manual.jar | head -20

cd ..

echo ""
echo "⚠️  ATTENTION: Ce JAR n'est PAS remappé et ne fonctionnera PAS dans Minecraft!"
echo "Il faut utiliser Fabric Loom pour le remapping."