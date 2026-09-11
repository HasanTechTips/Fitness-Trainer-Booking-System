FROM mysql:8.0

ENV MYSQL_ROOT_PASSWORD=change-me
ENV MYSQL_DATABASE=Cancel_FTMS

COPY cancel_ftms.sql /docker-entrypoint-initdb.d/01-cancel_ftms.sql

EXPOSE 3306
