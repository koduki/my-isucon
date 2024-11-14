@echo off
docker run -it --rm --name builder-mvn -v "%cd%:/usr/src/mymaven" -v "m2_repos:/root/.m2" -w /usr/src/mymaven maven:3.6-jdk-8 mvn %*