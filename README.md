# Eye Of Maven

Déploiement automatique de projet Maven dans un serveur d'application.
Ce projet est sous licence **CeCILL** (**CE**A **C**NRS **I**NRIA **L**ogiciel **L**ibre),
une licence de logiciel libre compatible avec la **GNU GPL**.

> En savoir plus sur la licence [CeCILL](http://cecill.info/index.fr.html)

## Description

L'application **eye-of-maven** scanne votre projet Maven, 
dès qu'une modification est détectée, 
il est alors recompilé puis déployé dans le répertoire de déploiement automatique du serveur d'application.

Cette application est conçue pour fonctionner avec les applications Java web au format **WAR**.

## Environnement

Ce projet est réalisé en **Java 11** *(OpenJDK)*.
Il utilise l'outil **Maven** en version 3.6.2.

### Exécution

Récupération du projet:
~~~
    git clone https://github.com/zelmo-dragon/eye-of-maven.git
    cd eye-of-maven
    mvn install
~~~

Lancement:
~~~
    java -jar eye-of-maven-{version}.jar
~~~

### Configuration

Lors de la première exécution un fichier `eye-of-maven.properties` est généré au même niveau que l'exécutable Java.

Modifier ce fichier pour l'adapter à votre environnement:
~~~
# Répertoire racine de Maven
maven.home          = /opt/maven

# Commande maven pour la compilation du projet
maven.goals         = clean package

# Propriétés Maven a ajouter a la commande (Ne pas suffixer de -D)
maven.properties    = maven.test.skip=true maven.source.skip=true

# Emplacement racine du projet Maven à déployer automatiquement
project.directory   = .

# Emplacement du répertoire de déploiement automatique du serveur d'application
server.autodeploy.directory = ./payara5/glassfish/domains/domain1/autodeploy
~~~

> **Note:**
> Privilégier les chemins absolus dans le fichier de configuration.
