CREATE DATABASE IF NOT EXISTS Account_FTMS;
USE Account_FTMS;

DROP TABLE IF EXISTS Member;

CREATE TABLE Member (
    memberID INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255),
    email VARCHAR(100) NOT NULL UNIQUE,
    membershipStatus ENUM('Active', 'Inactive') NOT NULL DEFAULT 'Active'
);

INSERT INTO Member (username, password, firstName, lastName, phone, address, email, membershipStatus) VALUES
('alexm', 'pass123', 'Alex', 'Miller', '416-555-1111', '123 King St', 'alex@example.com', 'Active'),
('jordans', 'pass123', 'Jordan', 'Smith', '416-555-2222', '456 Queen St', 'jordan@example.com', 'Active'),
('inactive1', 'pass123', 'Taylor', 'Green', '416-555-3333', '789 Dundas St', 'taylor@example.com', 'Inactive');
