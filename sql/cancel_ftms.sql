CREATE DATABASE IF NOT EXISTS Cancel_FTMS;
USE Cancel_FTMS;

DROP TABLE IF EXISTS Booking;
DROP TABLE IF EXISTS Trainer_Slot;

CREATE TABLE Trainer_Slot (
    slotID INT PRIMARY KEY,
    trainerID INT NOT NULL DEFAULT 0,
    trainerName VARCHAR(100) NOT NULL,
    startTime DATETIME NOT NULL,
    endTime DATETIME NOT NULL,
    slotStatus ENUM('available', 'booked') NOT NULL DEFAULT 'available'
);

CREATE TABLE Booking (
    bookingID INT PRIMARY KEY,
    memberID INT NOT NULL,
    slotID INT NOT NULL,
    trainerName VARCHAR(100) NOT NULL,
    startTime DATETIME NOT NULL,
    endTime DATETIME NOT NULL,
    bookedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    bookingStatus ENUM('booked', 'cancelled') NOT NULL DEFAULT 'booked',
    CONSTRAINT fk_cancel_slot FOREIGN KEY (slotID) REFERENCES Trainer_Slot(slotID)
);

INSERT INTO Trainer_Slot (slotID, trainerID, trainerName, startTime, endTime, slotStatus) VALUES
(1, 1, 'Alex Trainer', '2026-03-20 09:00:00', '2026-03-20 11:00:00', 'available'),
(2, 1, 'Alex Trainer', '2026-03-20 13:00:00', '2026-03-20 15:00:00', 'available'),
(3, 2, 'Jordan Trainer', '2026-03-20 10:00:00', '2026-03-20 12:00:00', 'available'),
(4, 2, 'Jordan Trainer', '2026-03-20 15:00:00', '2026-03-20 17:00:00', 'available'),
(5, 3, 'Taylor Trainer', '2026-03-21 09:00:00', '2026-03-21 11:00:00', 'available'),
(6, 3, 'Taylor Trainer', '2026-03-21 14:00:00', '2026-03-21 16:00:00', 'available');
