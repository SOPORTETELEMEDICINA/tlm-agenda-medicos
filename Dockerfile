# Dockerfile de nio-medicos
FROM openjdk:8-jdk-alpine
ENV JAVA_HOME /usr/lib/jvm/java-1.8-openjdk
#COPY ./src/main/resources/static/dev.niomedic.com.key /opt/secrets/
COPY ./src/main/resources/static/telemedicina_lat.key /opt/secrets/
# SECCION PARA PRUEBAS INICIA
#COPY ./src/jonima2019.crt $JAVA_HOME/jre/lib/security
#VOLUME /tmp
#VOLUME /logs
#VOLUME /var/www/html/niomedic/cedula
#VOLUME /var/www/html/niomedic/titulo
#ADD target/nio-medicos-1.0.4-SNAPSHOT.jar app.jar
#ENV JAVA_OPTS="-Xmx768m -Duser.timezone=America/Mexico_City"
#ENV JRE_KEYSTORE=$JAVA_HOME/jre/lib/security/cacerts
#ENV CER_DIR=$JAVA_HOME/jre/lib/security/jonima2019.crt
#RUN keytool -import -alias jonimacert -storepass changeit -noprompt -keystore $JRE_KEYSTORE -trustcacerts -file $CER_DIR
# SECCION PARA PRUEBAS TERMINA

# SECCION PARA PRODUCCION INICIA
COPY ./src/STAR_telemedicina_lat.crt $JAVA_HOME/jre/lib/security
VOLUME /tmp
VOLUME /logs
ADD target/nio-agenda-medicos-1.0.4-SNAPSHOT.jar app.jar
ENV JAVA_OPTS="-Duser.timezone=America/Mexico_City"
ENV JRE_KEYSTORE=$JAVA_HOME/jre/lib/security/cacerts
ENV CER_DIR=$JAVA_HOME/jre/lib/security/STAR_telemedicina_lat.crt
RUN keytool -import -alias cct.telemedicina.lat -storepass changeit -noprompt -keystore $JRE_KEYSTORE -trustcacerts -file $CER_DIR
# SECCION PARA PRODUCCION TERMINA
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar