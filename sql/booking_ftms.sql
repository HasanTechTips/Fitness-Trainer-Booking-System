CREATE DATABASE IF NOT EXISTS Booking_FTMS;
USE Booking_FTMS;

DROP TABLE IF EXISTS Booking;
DROP TABLE IF EXISTS Trainer_Slot;
DROP TABLE IF EXISTS Trainer;

CREATE TABLE Trainer (
    trainerID INT AUTO_INCREMENT PRIMARY KEY,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    gender VARCHAR(20),
    specialty VARCHAR(100),
    rating DECIMAL(3,2)
);

CREATE TABLE Trainer_Slot (
    slotID INT AUTO_INCREMENT PRIMARY KEY,
    trainerID INT NOT NULL,
    startTime DATETIME NOT NULL,
    endTime DATETIME NOT NULL,
    slotStatus ENUM('available', 'booked') NOT NULL DEFAULT 'available',
    CONSTRAINT fk_booking_trainer FOREIGN KEY (trainerID) REFERENCES Trainer(trainerID),
    CONSTRAINT uq_trainer_slot UNIQUE (trainerID, startTime)
);

CREATE TABLE Booking (
    bookingID INT AUTO_INCREMENT PRIMARY KEY,
    memberID INT NOT NULL,
    slotID INT NOT NULL,
    bookedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    bookingStatus ENUM('booked', 'cancelled') NOT NULL DEFAULT 'booked',
    CONSTRAINT fk_booking_slot FOREIGN KEY (slotID) REFERENCES Trainer_Slot(slotID)
);

INSERT INTO Trainer (firstName, lastName, gender, specialty, rating) VALUES
('Alex', 'Trainer', 'Non-binary', 'Strength', 4.70),
('Jordan', 'Trainer', 'Female', 'Cardio', 4.50),
('Taylor', 'Trainer', 'Male', 'Yoga', 4.80);

INSERT INTO Trainer_Slot (trainerID, startTime, endTime, slotStatus) VALUES
(1, '2026-03-20 09:00:00', '2026-03-20 11:00:00', 'available'),
(1, '2026-03-20 13:00:00', '2026-03-20 15:00:00', 'available'),
(2, '2026-03-20 10:00:00', '2026-03-20 12:00:00', 'available'),
(2, '2026-03-20 15:00:00', '2026-03-20 17:00:00', 'available'),
(3, '2026-03-21 09:00:00', '2026-03-21 11:00:00', 'available'),
(3, '2026-03-21 14:00:00', '2026-03-21 16:00:00', 'available');
