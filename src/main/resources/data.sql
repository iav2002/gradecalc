-- =============================================
-- GRADING SCALES SEED DATA
-- =============================================

-- TUD (Technological University Dublin)
-- Granulated 4.0 GPA Scale (effective September 2025)
INSERT INTO grading_scales (university, grade_code, grade_name, min_percentage, max_percentage, gpa_points) VALUES
('TUD', 'A1', 'First Class Honours', 80.00, 100.00, 4.00),
('TUD', 'A2', 'First Class Honours', 75.00, 79.99, 3.80),
('TUD', 'A3', 'First Class Honours', 70.00, 74.99, 3.60),
('TUD', 'B1', 'Second Class Honours I', 65.00, 69.99, 3.20),
('TUD', 'B2', 'Second Class Honours I', 60.00, 64.99, 3.00),
('TUD', 'B3', 'Second Class Honours II', 55.00, 59.99, 2.80),
('TUD', 'C1', 'Second Class Honours II', 50.00, 54.99, 2.60),
('TUD', 'C2', 'Pass', 45.00, 49.99, 2.40),
('TUD', 'C3', 'Pass', 40.00, 44.99, 2.00),
('TUD', 'D1', 'Compensating Fail', 35.00, 39.99, 1.60),
('TUD', 'F', 'Fail', 0.00, 34.99, 0.00);

-- UCD (University College Dublin)
-- 4.2 GPA Scale with letter grades
INSERT INTO grading_scales (university, grade_code, grade_name, min_percentage, max_percentage, gpa_points) VALUES
('UCD', 'A+', 'Excellent', 90.00, 100.00, 4.20),
('UCD', 'A', 'Excellent', 80.00, 89.99, 4.00),
('UCD', 'A-', 'Excellent', 70.00, 79.99, 3.80),
('UCD', 'B+', 'Very Good', 66.67, 69.99, 3.60),
('UCD', 'B', 'Very Good', 63.33, 66.66, 3.40),
('UCD', 'B-', 'Very Good', 60.00, 63.32, 3.20),
('UCD', 'C+', 'Good', 56.67, 59.99, 3.00),
('UCD', 'C', 'Good', 53.33, 56.66, 2.80),
('UCD', 'C-', 'Good', 50.00, 53.32, 2.60),
('UCD', 'D+', 'Acceptable', 46.67, 49.99, 2.40),
('UCD', 'D', 'Acceptable', 43.33, 46.66, 2.20),
('UCD', 'D-', 'Acceptable', 40.00, 43.32, 2.00),
('UCD', 'FM', 'Fail', 0.00, 39.99, 0.00);

-- TCD (Trinity College Dublin)
-- Roman numeral grades with proxy GPA for cross-system comparison
INSERT INTO grading_scales (university, grade_code, grade_name, min_percentage, max_percentage, gpa_points) VALUES
('TCD', 'I', 'First Class Honours', 70.00, 100.00, 4.00),
('TCD', 'II.1', 'Second Class Honours I', 60.00, 69.99, 3.00),
('TCD', 'II.2', 'Second Class Honours II', 50.00, 59.99, 2.00),
('TCD', 'III', 'Third Class Honours', 40.00, 49.99, 1.00),
('TCD', 'F1', 'Fail', 30.00, 39.99, 0.00),
('TCD', 'F2', 'Fail', 0.00, 29.99, 0.00);

-- STANDARD (Irish System Fallback)
-- Simple letter grades for universities not yet configured
INSERT INTO grading_scales (university, grade_code, grade_name, min_percentage, max_percentage, gpa_points) VALUES
('STANDARD', 'A', 'First Class Honours', 70.00, 100.00, 4.00),
('STANDARD', 'B', 'Second Class Honours I', 60.00, 69.99, 3.00),
('STANDARD', 'C', 'Second Class Honours II', 50.00, 59.99, 2.00),
('STANDARD', 'D', 'Pass', 40.00, 49.99, 1.00),
('STANDARD', 'F', 'Fail', 0.00, 39.99, 0.00);