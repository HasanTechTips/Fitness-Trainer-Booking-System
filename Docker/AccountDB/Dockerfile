FROM mysql:8.0

ENV MYSQL_ROOT_PASSWORD=change-me
ENV MYSQL_DATABASE=Account_FTMS

COPY account_ftms.sql /docker-entrypoint-initdb.d/01-account_ftms.sql

EXPOSE 3306
