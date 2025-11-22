### BUILD AND RUN THE SERVER APPLICATION ###
#!/bin/bash

# Define the port variable
PORT=7575

# First clean, then build and install the application
./gradlew clean build installDist



# Now run the server application 
./app/build/install/app/bin/app server --port=$PORT
