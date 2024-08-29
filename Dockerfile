FROM openjdk:17               
ADD patient-service-0.0.1-SNAPSHOT.jar patient-service-0.0.1-SNAPSHOT.jar 
ENTRYPOINT ["java","-jar","patient-service-0.0.1-SNAPSHOT.jar"]   
EXPOSE 9091
