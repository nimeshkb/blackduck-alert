FROM blackducksoftware/hub-docker-common:1.0.4 as docker-common
FROM adoptopenjdk/openjdk11:alpine-slim

ARG VERSION

LABEL com.blackducksoftware.integration.alert.vendor="Black Duck Software, Inc." \
      com.blackducksoftware.integration.alert.version="$VERSION"

ENV APPLICATION_NAME blackduck-alert
ENV BLACKDUCK_HOME /opt/blackduck
ENV CERTIFICATE_MANAGER_DIR $BLACKDUCK_HOME/alert/bin

ENV ALERT_HOME $BLACKDUCK_HOME/alert
ENV ALERT_CONFIG_HOME $ALERT_HOME/alert-config
ENV SECURITY_DIR $ALERT_HOME/security
ENV ALERT_TAR_HOME $ALERT_HOME/alert-tar
ENV PATH $ALERT_TAR_HOME/bin:$PATH
ENV ALERT_DATA_DIR $ALERT_CONFIG_HOME/data
ENV ALERT_IMAGES_DIR $ALERT_TAR_HOME/images

ENV ALERT_MAX_HEAP_SIZE 2048m

ENV BLACKDUCK_ALERT_OPTS="$BLACKDUCK_ALERT_OPTS -Djava.security.properties=${SECURITY_DIR}/java.security"

RUN set -e \
    && mkdir -p $ALERT_HOME \
    && apk add --no-cache --virtual .blackduck-alert-run-deps \
    		curl \
    		jq \
    		openssl \
    		bash \
    		zip \
    		postgresql-client

RUN mkdir -p -m 774 $CERTIFICATE_MANAGER_DIR
RUN mkdir -p -m 774 $SECURITY_DIR
RUN mkdir -p -m 774 $ALERT_CONFIG_HOME
RUN mkdir -p -m 774 $ALERT_DATA_DIR

COPY blackduck-alert-boot-$VERSION $ALERT_HOME/alert-tar

COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
COPY --from=docker-common healthcheck.sh /usr/local/bin/docker-healthcheck.sh
COPY --from=docker-common certificate-manager.sh "$CERTIFICATE_MANAGER_DIR/certificate-manager.sh"
COPY --from=docker-common java.security "$SECURITY_DIR/java.security"

ENTRYPOINT [ "docker-entrypoint.sh", "blackduck-alert" ]
