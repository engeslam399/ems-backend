-- Database Creation Script for Employee Management System (EMS)

CREATE DATABASE IF NOT EXISTS `ems_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `ems_db`;

-- 1. Create Department Table
CREATE TABLE IF NOT EXISTS `department` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `code` VARCHAR(50) NOT NULL UNIQUE,
  `name` VARCHAR(100) NOT NULL,
  `description` VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Create Employee Table
CREATE TABLE IF NOT EXISTS `employee` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `code` VARCHAR(50) NOT NULL UNIQUE,
  `name` VARCHAR(100) NOT NULL,
  `date_of_birth` DATE,
  `address` VARCHAR(255),
  `mobile` VARCHAR(20),
  `salary` DOUBLE NOT NULL,
  `department_id` BIGINT NOT NULL,
  `image` LONGBLOB,
  CONSTRAINT `fk_employee_department` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Insert Initial Sample Seed Data (Optional)
INSERT INTO `department` (`code`, `name`, `description`) VALUES
('HR', 'Human Resources', 'Handles recruiting, onboarding, and employee relations'),
('IT', 'Information Technology', 'Manages software development, network systems, and tech support'),
('FIN', 'Finance', 'Responsible for accounting, budgeting, and financial reports')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `description` = VALUES(`description`);
