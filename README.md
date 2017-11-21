# SweetHome 3D Plugin

## Usage:
- import pom.xml
- run 'mvn clean install'
- sudo cp ./target/sweethome-plugin-1.0-SNAPSHOT.jar <<customInstallPath>>/eTeks/Sweet\ Home\ 3D/plugins/sweethome-plugin-1.0-SNAPSHOT.sh3p

## Debug:
- create Run Config as Follow:
    - MainClass: com.eteks.sweethome3d.SweetHome3D
    - WorkingDir: .
    - WorkingModule: sweethome-plugin
    - JRE: 1.6 (min)