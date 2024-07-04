FROM toposoid/scala-base:2.13.11

WORKDIR /app
ARG TARGET_BRANCH
ARG JAVA_OPT_XMX
ENV DEPLOYMENT=local
ENV _JAVA_OPTIONS="-Xms512m -Xmx"${JAVA_OPT_XMX}

RUN apt-get update \
&& apt-get -y install git unzip \
&& git clone https://github.com/toposoid/scala-common.git \
&& cd scala-common \
&& git fetch origin ${TARGET_BRANCH} \
&& git checkout ${TARGET_BRANCH} \
&& sbt publishLocal \
&& rm -Rf ./target \
&& cd .. \
&& git clone https://github.com/toposoid/toposoid-knowledgebase-model.git \
&& cd toposoid-knowledgebase-model \
&& git fetch origin ${TARGET_BRANCH} \
&& git checkout ${TARGET_BRANCH} \
&& sbt publishLocal \
&& rm -Rf ./target \
&& cd .. \
&& git clone https://github.com/toposoid/toposoid-deduction-protocol-model.git \
&& cd toposoid-deduction-protocol-model \
&& git fetch origin ${TARGET_BRANCH} \
&& git checkout ${TARGET_BRANCH} \
&& sbt publishLocal \
&& rm -Rf ./target \
&& cd .. \
&& git clone https://github.com/toposoid/data-accessor-redis-web.git \
&& cd data-accessor-redis-web \
&& git fetch origin ${TARGET_BRANCH} \
&& git checkout ${TARGET_BRANCH} \
&& sbt playUpdateSecret 1> /dev/null \
&& sbt dist \
&& cd /app/data-accessor-redis-web/target/universal \
&& unzip -o data-accessor-redis-web-0.6-SNAPSHOT.zip


COPY ./docker-entrypoint.sh /app/
ENTRYPOINT ["/app/docker-entrypoint.sh"]
