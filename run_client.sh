### BUILD AND RUN THE CLIENT APPLICATION ###
#!/bin/bash

# Define the port variable
PORT=7575

# First clean, then build and install the application
./gradlew clean build installDist



# Now run the client application 
./app/build/install/app/bin/app client --port=$PORT
