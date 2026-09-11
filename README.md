# Fitness Trainer Booking System

A Java microservices project for a fitness trainer booking workflow. Members can log in, view trainers and open slots, book a trainer, and cancel existing bookings.

## Project Structure

```text
.
├── FitnessFrontend/        # JSP/Servlet frontend and member authentication
├── FitnessBookingService/  # REST API for trainers, slots, and booking creation
├── FitnessCancelService/   # REST API for viewing and cancelling bookings
├── Docker/                 # Dockerfiles, WAR files, and database SQL dumps
├── Kube/                   # Kubernetes deployment and service manifests
├── sql/                    # Database initialization scripts
└── docs/                   # Project documentation
```

## Services

- **FitnessFrontend**: Web UI for members. It talks to the booking and cancellation services over HTTP.
- **FitnessBookingService**: Provides booking-related REST endpoints and stores booking data in `Booking_FTMS`.
- **FitnessCancelService**: Provides cancellation-related REST endpoints and stores cancellation data in `Cancel_FTMS`.
- **AccountDB, BookingDB, CancelDB**: MySQL databases initialized from the SQL scripts.

## REST API Endpoints

### Booking Service

Base path: `/FitnessBookingService/api/bookings`

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/trainers` | List all trainers |
| `GET` | `/slots?trainerId={id}` | List available slots, optionally filtered by trainer |
| `POST` | `/` | Create a booking |
| `POST` | `/release` | Release a booking slot |
| `GET` | `/member/{memberId}` | List bookings for a member |

### Cancellation Service

Base path: `/FitnessCancelService/api/cancellations`

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/member/{memberId}` | List cancellable bookings for a member |
| `POST` | `/cancel` | Cancel a booking |
| `POST` | `/register` | Register a booking copy in the cancellation service |

## Build

Each Java service is a Maven web application:

```bash
cd FitnessFrontend
mvn clean package

cd ../FitnessBookingService
mvn clean package

cd ../FitnessCancelService
mvn clean package
```

After rebuilding the WAR files, copy them into the matching folders under `Docker/` before rebuilding images.

## Deployment

Kubernetes manifests are provided in `Kube/`:

```bash
kubectl apply -f Kube/deployment.yaml
kubectl apply -f Kube/service.yaml
```

The frontend is exposed through a `LoadBalancer` service. Backend services and databases are exposed internally as `ClusterIP` services.

## Configuration

The Kubernetes deployment sets these key environment variables:

- `BOOKING_SERVICE_URL`
- `CANCEL_SERVICE_URL`
- `FRONTEND_DB_URL`
- `BOOKING_DB_URL`
- `CANCEL_DB_URL`
- `DB_USER`
- `DB_PASSWORD`
- `FTMS_JWT_SECRET`
- `kubeMQAddress`

For local development, the frontend clients include localhost fallback URLs for the booking and cancellation services.
