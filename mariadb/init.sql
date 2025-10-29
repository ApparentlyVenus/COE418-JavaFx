-- Initialize database with sample tables
-- This file runs automatically when MariaDB container starts for the first time

USE student_db;

-- Create students table
CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    major VARCHAR(50),
    gpa DECIMAL(3,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create departments table
CREATE TABLE IF NOT EXISTS departments (
    dept_id VARCHAR(10) PRIMARY KEY,
    dept_name VARCHAR(50) NOT NULL,
    building VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert sample students (optional - comment out if you don't want sample data)
INSERT INTO students (name, major, gpa) VALUES
    ('John Doe', 'CS', 3.80),
    ('Jane Smith', 'EE', 3.95),
    ('Bob Johnson', 'ME', 3.50),
    ('Alice Williams', 'CS', 3.70),
    ('Charlie Brown', 'CE', 3.60);

-- Insert sample departments (optional - comment out if you don't want sample data)
INSERT INTO departments (dept_id, dept_name, building) VALUES
    ('CS', 'Computer Science', 'Tech Building'),
    ('EE', 'Electrical Engineering', 'Engineering Hall'),
    ('ME', 'Mechanical Engineering', 'Workshop'),
    ('CE', 'Civil Engineering', 'Main Hall');

-- Display initialization status
SELECT 
    '✅ Database initialized successfully!' as status,
    COUNT(*) as student_count 
FROM students;

SELECT 
    '✅ Departments created!' as status,
    COUNT(*) as dept_count 
FROM departments;