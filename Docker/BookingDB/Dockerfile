FROM mysql:8.0

ENV MYSQL_ROOT_PASSWORD=change-me
ENV MYSQL_DATABASE=Booking_FTMS

COPY booking_ftms.sql /docker-entrypoint-initdb.d/01-booking_ftms.sql

EXPOSE 3306
