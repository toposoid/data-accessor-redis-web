FROM toposoid/toposoid-scala-lib-base:0.6-SNAPSHOT

WORKDIR /app
ARG TARGET_BRANCH
ENV DEPLOYMENT=local


RUN git clone https://github.com/toposoid/data-accessor-redis-web.git \
&& cd data-accessor-redis-web \
&& git fetch origin ${TARGET_BRANCH} \
&& git checkout ${TARGET_BRANCH} \
&& sbt playUpdateSecret 1> /dev/null \
&& sbt dist \
&& cd /app/data-accessor-redis-web/target/universal \
&& unzip -o data-accessor-redis-web-0.6-SNAPSHOT.zip


COPY ./docker-entrypoint.sh /app/
ENTRYPOINT ["/app/docker-entrypoint.sh"]
