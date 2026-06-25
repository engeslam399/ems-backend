INSERT INTO `department` (`code`, `name`, `description`) VALUES
('HR', 'Human Resources', 'Handles recruiting, onboarding, and employee relations'),
('IT', 'Information Technology', 'Manages software development, network systems, and tech support'),
('FIN', 'Finance', 'Responsible for accounting, budgeting, and financial reports')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `description` = VALUES(`description`);
