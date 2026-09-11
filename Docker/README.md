## FTMS Docker Folder

This folder contains the container packaging files for the application:

- one Dockerfile for each web microservice
- one Dockerfile for each database
- each service folder contains its WAR file
- each database folder contains its SQL dump

Before rebuilding the Java service images, regenerate the WAR files and recopy them into this folder.
