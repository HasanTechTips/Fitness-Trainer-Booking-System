# Fitness Trainer Booking System

A Java microservices application for booking and cancelling fitness trainer sessions. Members can log in, view trainers and available time slots, create bookings, and cancel existing bookings through a JSP/Servlet frontend.

## Tech Stack

- Java, JSP, Servlets
- JAX-RS / Jersey REST APIs
- MySQL
- Docker
- Kubernetes
- KubeMQ
- JWT authentication

## Project Structure

- `FitnessFrontend/` - member login and booking/cancellation UI
- `FitnessBookingService/` - trainer, slot, and booking REST APIs
- `FitnessCancelService/` - cancellation REST APIs
- `sql/` - database initialization scripts
- `Docker/` - Dockerfiles and packaged WAR files
- `Kube/` - Kubernetes manifests
- `docs/` - project documentation

## REST APIs

### Booking Service

Base path: `/FitnessBookingService/api/bookings`

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/trainers` | List trainers |
| `GET` | `/slots?trainerId={id}` | List available slots |
| `POST` | `/FitnessBookingService/api/bookings` | Create a booking |
| `POST` | `/release` | Release a booking |
| `GET` | `/member/{memberId}` | List member bookings |

### Cancellation Service

Base path: `/FitnessCancelService/api/cancellations`

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/member/{memberId}` | List cancellable bookings |
| `POST` | `/cancel` | Cancel a booking |
| `POST` | `/register` | Register a booking copy |

## Build

```bash
cd FitnessFrontend && mvn clean package
cd ../FitnessBookingService && mvn clean package
cd ../FitnessCancelService && mvn clean package
```

## Deploy

```bash
kubectl apply -f Kube/deployment.yaml
kubectl apply -f Kube/service.yaml
```
