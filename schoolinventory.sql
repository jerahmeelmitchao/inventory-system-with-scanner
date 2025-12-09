-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 09, 2025 at 05:48 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `schoolinventory`
--

-- --------------------------------------------------------

--
-- Table structure for table `audit_log`
--

CREATE TABLE `audit_log` (
  `audit_id` bigint(20) NOT NULL,
  `event_time` timestamp NOT NULL DEFAULT current_timestamp(),
  `user_who` varchar(100) DEFAULT 'system',
  `event_type` varchar(50) DEFAULT NULL,
  `description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `audit_log`
--

INSERT INTO `audit_log` (`audit_id`, `event_time`, `user_who`, `event_type`, `description`) VALUES
(1, '2025-01-08 00:00:01', 'admin', 'LOGIN', 'User admin logged in'),
(2, '2025-01-08 00:05:12', 'admin', 'ADD_ITEM', 'Added Laptop Dell Inspiron'),
(3, '2025-01-08 00:06:55', 'admin', 'ADD_ITEM', 'Added Projector Epson X400'),
(4, '2025-01-08 00:07:30', 'admin', 'ADD_CATEGORY', 'Added category Electronics'),
(5, '2025-01-08 00:07:55', 'admin', 'ADD_CATEGORY', 'Added category Furniture'),
(6, '2025-01-08 01:02:41', 'jane_doe', 'LOGIN', 'User jane_doe logged in'),
(7, '2025-01-08 01:10:28', 'jane_doe', 'UPDATE_ITEM', 'Updated Folding Table'),
(8, '2025-01-08 02:20:12', 'admin', 'ADD_ITEM', 'Added Basketball Molten'),
(9, '2025-01-08 02:23:44', 'admin', 'ADD_ITEM', 'Added Volleyball Mikasa'),
(10, '2025-01-08 03:00:10', 'michael', 'LOGIN', 'User michael logged in'),
(11, '2025-01-08 03:10:01', 'michael', 'ADD_ITEM', 'Added HDMI Cable 5m'),
(12, '2025-01-08 03:15:43', 'michael', 'ADD_ITEM', 'Added Wireless Mouse Logitech'),
(13, '2025-01-08 03:30:13', 'admin', 'ADD_ITEM', 'Added Microscope Amscope'),
(14, '2025-01-08 04:10:25', 'teacher_ana', 'LOGIN', 'User teacher_ana logged in'),
(15, '2025-01-08 04:20:47', 'teacher_ana', 'ADD_ITEM', 'Added Test Tube Set'),
(16, '2025-01-08 05:01:22', 'admin', 'ADD_ITEM', 'Added Acoustic Guitar Yamaha'),
(17, '2025-01-08 05:15:40', 'admin', 'DISPOSE_ITEM', 'Disposed Drum Set Pearl'),
(18, '2025-01-08 05:30:20', 'admin', 'ADD_ITEM', 'Added Hammer Stanley'),
(19, '2025-01-08 05:32:54', 'admin', 'ADD_ITEM', 'Added Screwdriver Set'),
(20, '2025-01-08 06:00:22', 'jane_doe', 'ADD_USER', 'Added new user michael'),
(21, '2025-01-09 00:10:45', 'admin', 'BORROW_ITEM', 'Projector borrowed by Juan Dela Cruz'),
(22, '2025-01-09 00:15:02', 'admin', 'BORROW_ITEM', 'Test Tube Set borrowed by Leo Ramirez'),
(23, '2025-01-09 00:20:31', 'admin', 'BORROW_ITEM', 'Badminton Racket borrowed by Carlo Jimenez'),
(24, '2025-01-10 01:01:12', 'admin', 'RETURN_ITEM', 'Basketball returned by Anna Reyes'),
(25, '2025-01-10 01:20:31', 'michael', 'SCAN_ITEM', 'Scanned Laptop Dell Inspiron'),
(26, '2025-01-10 02:05:11', 'inventory_john', 'LOGIN', 'User inventory_john logged in'),
(27, '2025-01-10 02:20:12', 'inventory_john', 'UPDATE_ITEM', 'Updated LAN Cable location'),
(28, '2025-01-10 03:02:44', 'admin', 'ADD_ITEM', 'Added Printer HP LaserJet'),
(29, '2025-01-10 03:20:18', 'admin', 'UPDATE_CATEGORY', 'Updated Computer Accessories'),
(30, '2025-01-10 03:45:55', 'admin', 'MARK_MISSING', 'Marked Mouse Logitech as Missing'),
(31, '2025-01-10 05:10:10', 'teacher_ana', 'SCAN_ITEM', 'Scanned Microscope'),
(32, '2025-01-10 06:01:11', 'admin', 'OVERDUE_ITEM', 'Marked Beaker Set Overdue'),
(33, '2025-01-11 01:10:55', 'admin', 'BORROW_ITEM', 'Keyboard borrowed by Robert Aguilar'),
(34, '2025-01-11 01:15:05', 'admin', 'BORROW_ITEM', 'Item A8 borrowed by Maria Santos'),
(35, '2025-01-11 02:25:44', 'admin', 'RETURN_ITEM', 'Item A5 returned by Nathan Torres'),
(36, '2025-01-11 03:45:22', 'admin', 'SCAN_ITEM', 'Scanned Screwdriver Set'),
(37, '2025-01-11 04:30:10', 'inventory_john', 'UPDATE_ITEM', 'Updated Soccer Ball to Missing'),
(38, '2025-01-12 00:00:13', 'admin', 'LOGIN', 'User admin logged in'),
(39, '2025-01-12 00:15:00', 'admin', 'SYSTEM_CHECK', 'Auto inventory integrity scan'),
(40, '2025-01-12 00:16:00', 'admin', 'SYSTEM_START', 'System boot sequence initiated'),
(41, '2025-12-09 16:45:21', 'TextField[id=addedByField, styleClass=text-input text-field input]', 'UPDATE_ITEM', 'Updated item ID: 1'),
(42, '2025-12-09 16:46:47', 'TextField[id=addedByField, styleClass=text-input text-field input]', 'UPDATE_ITEM', 'Updated item ID: 2'),
(43, '2025-12-09 16:46:58', 'TextField[id=addedByField, styleClass=text-input text-field input]', 'UPDATE_ITEM', 'Updated item ID: 2');

-- --------------------------------------------------------

--
-- Table structure for table `borrowers`
--

CREATE TABLE `borrowers` (
  `borrower_id` int(11) NOT NULL,
  `borrower_name` varchar(150) NOT NULL,
  `position` varchar(100) DEFAULT NULL,
  `borrower_type` enum('Student','Teacher','Staff') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `borrowers`
--

INSERT INTO `borrowers` (`borrower_id`, `borrower_name`, `position`, `borrower_type`) VALUES
(1, 'Juan Dela Cruz', 'Grade 10', 'Student'),
(2, 'Maria Santos', 'Teacher', 'Teacher'),
(3, 'Leo Ramirez', 'Staff', 'Staff'),
(4, 'Anna Reyes', 'Grade 12', 'Student'),
(5, 'Carlo Jimenez', 'Teacher', 'Teacher'),
(6, 'Faith Domingo', 'Staff', 'Staff'),
(7, 'Robert Aguilar', 'Grade 11', 'Student'),
(8, 'Elaine Gomez', 'Teacher', 'Teacher'),
(9, 'Patrick Cruz', 'Staff', 'Staff'),
(10, 'Nathan Torres', 'Grade 9', 'Student');

-- --------------------------------------------------------

--
-- Table structure for table `borrow_records`
--

CREATE TABLE `borrow_records` (
  `record_id` bigint(20) NOT NULL,
  `item_id` int(11) NOT NULL,
  `borrower_id` int(11) NOT NULL,
  `borrow_date` datetime NOT NULL DEFAULT current_timestamp(),
  `return_date` datetime DEFAULT NULL,
  `status` enum('Borrowed','Returned','Overdue','Cancelled') NOT NULL DEFAULT 'Borrowed',
  `remarks` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `borrow_records`
--

INSERT INTO `borrow_records` (`record_id`, `item_id`, `borrower_id`, `borrow_date`, `return_date`, `status`, `remarks`) VALUES
(1, 2, 1, '2025-01-10 00:00:00', '2025-01-15 00:00:00', 'Returned', 'Working'),
(2, 5, 4, '2025-01-09 00:00:00', NULL, 'Borrowed', 'For PE class'),
(3, 6, 7, '2025-01-05 00:00:00', '2025-01-07 00:00:00', 'Returned', 'Good'),
(4, 10, 3, '2025-01-02 00:00:00', NULL, 'Borrowed', 'Lab use'),
(5, 12, 8, '2025-01-01 00:00:00', NULL, 'Cancelled', 'Borrow cancelled'),
(6, 23, 2, '2024-12-28 00:00:00', NULL, 'Overdue', 'Not returned'),
(7, 20, 6, '2024-12-20 00:00:00', '2024-12-22 00:00:00', 'Returned', ''),
(8, 32, 9, '2024-12-19 00:00:00', NULL, 'Borrowed', ''),
(9, 27, 10, '2024-12-18 00:00:00', '2024-12-20 00:00:00', 'Returned', 'OK'),
(10, 31, 5, '2024-12-15 00:00:00', NULL, 'Borrowed', 'On hand'),
(11, 29, 1, '2024-12-10 00:00:00', NULL, 'Overdue', ''),
(12, 21, 3, '2024-11-29 00:00:00', '2024-12-02 00:00:00', 'Returned', ''),
(13, 33, 4, '2024-11-22 00:00:00', NULL, 'Borrowed', ''),
(14, 36, 2, '2024-11-15 00:00:00', NULL, 'Borrowed', ''),
(15, 38, 7, '2024-11-11 00:00:00', NULL, 'Cancelled', ''),
(16, 40, 9, '2024-11-09 00:00:00', NULL, 'Borrowed', ''),
(17, 42, 3, '2024-11-05 00:00:00', NULL, 'Borrowed', ''),
(18, 44, 6, '2024-11-03 00:00:00', '2024-11-05 00:00:00', 'Returned', ''),
(19, 47, 10, '2024-11-01 00:00:00', NULL, 'Borrowed', ''),
(20, 50, 4, '2024-10-29 00:00:00', NULL, 'Overdue', ''),
(21, 44, 1, '2025-12-10 00:45:04', NULL, 'Borrowed', ''),
(22, 3, 4, '2025-12-10 00:47:09', '2025-12-10 00:47:44', 'Returned', ''),
(23, 4, 8, '2025-12-10 00:47:18', NULL, 'Borrowed', ''),
(24, 16, 6, '2025-12-10 00:47:28', NULL, 'Borrowed', ''),
(25, 43, 3, '2025-12-10 00:47:36', NULL, 'Borrowed', '');

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `category_id` int(11) NOT NULL,
  `category_name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`category_id`, `category_name`) VALUES
(4, 'Computer Accessories'),
(1, 'Electronics'),
(2, 'Furniture'),
(5, 'Laboratory'),
(6, 'Musical Instruments'),
(3, 'Sports Equipment'),
(7, 'Tools');

-- --------------------------------------------------------

--
-- Table structure for table `incharge`
--

CREATE TABLE `incharge` (
  `incharge_id` int(11) NOT NULL,
  `incharge_name` varchar(150) NOT NULL,
  `position` varchar(100) DEFAULT NULL,
  `contact_info` varchar(150) DEFAULT NULL,
  `assigned_category_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `incharge`
--

INSERT INTO `incharge` (`incharge_id`, `incharge_name`, `position`, `contact_info`, `assigned_category_id`) VALUES
(1, 'Mr. Santos', 'Coordinator', '09281234567', 1),
(2, 'Ms. Dela Cruz', 'Staff', '09991234567', 2),
(3, 'Coach Ramirez', 'PE Teacher', '09171234567', 3),
(4, 'Sir Mendoza', 'IT Staff', '09193456789', 4),
(5, 'Maam Torres', 'Science Teacher', '09275678901', 5),
(6, 'Mr. Amador', 'Music Teacher', '09081239812', 6);

-- --------------------------------------------------------

--
-- Table structure for table `items`
--

CREATE TABLE `items` (
  `item_id` int(11) NOT NULL,
  `item_name` varchar(200) NOT NULL,
  `barcode` varchar(100) DEFAULT NULL,
  `category_id` int(11) DEFAULT NULL,
  `unit` varchar(50) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `date_acquired` date DEFAULT NULL,
  `last_scanned` datetime DEFAULT NULL,
  `storage_location` varchar(100) DEFAULT NULL,
  `incharge_id` int(11) DEFAULT NULL,
  `added_by` varchar(100) DEFAULT NULL,
  `status` enum('Available','Damaged','Borrowed','Missing','Disposed') NOT NULL DEFAULT 'Available'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `items`
--

INSERT INTO `items` (`item_id`, `item_name`, `barcode`, `category_id`, `unit`, `description`, `date_acquired`, `last_scanned`, `storage_location`, `incharge_id`, `added_by`, `status`) VALUES
(1, 'Laptop Dell Inspiron', 'BC1001', 1, 'pcs', 'School laptop', '2024-03-12', NULL, 'IT Room', 4, 'admin', 'Available'),
(2, 'Projector Epson X400', 'BC1002', 1, 'pcs', 'HD projector', '2023-11-20', NULL, 'AV Room', 1, 'admin', 'Missing'),
(3, 'Wooden Armchair', 'BC2001', 2, 'pcs', 'Library chair', '2022-05-15', NULL, 'Library', 2, 'admin', 'Available'),
(4, 'Folding Table', 'BC2002', 2, 'pcs', 'Event table', '2023-01-09', NULL, 'Storage A', 2, 'jane_doe', 'Borrowed'),
(5, 'Basketball Molten', 'BC3001', 3, 'pcs', 'Official size 7', '2023-06-18', NULL, 'Gym', 3, 'admin', 'Missing'),
(6, 'Volleyball Mikasa', 'BC3002', 3, 'pcs', 'Training ball', '2023-07-21', NULL, 'Gym', 3, 'admin', 'Borrowed'),
(7, 'HDMI Cable 5m', 'BC4001', 4, 'pcs', 'Long HDMI cable', '2024-02-10', NULL, 'IT Room', 4, 'michael', 'Available'),
(8, 'Wireless Mouse Logitech', 'BC4002', 4, 'pcs', 'Wireless mouse', '2024-01-12', NULL, 'IT Room', 4, 'john', 'Missing'),
(9, 'Microscope Amscope', 'BC5001', 5, 'pcs', 'High precision', '2023-09-05', NULL, 'Lab A', 5, 'admin', 'Available'),
(10, 'Test Tube Set', 'BC5002', 5, 'sets', '12pcs set', '2023-08-14', NULL, 'Lab A', 5, 'teacher_ana', 'Missing'),
(11, 'Acoustic Guitar Yamaha', 'BC6001', 6, 'pcs', 'Classroom guitar', '2023-03-11', NULL, 'Music Room', 6, 'admin', 'Available'),
(12, 'Drum Set Pearl', 'BC6002', 6, 'pcs', 'Full-size drum set', '2022-10-15', NULL, 'Music Room', 6, 'admin', 'Disposed'),
(13, 'Hammer Stanley', 'BC7001', 7, 'pcs', 'Steel hammer', '2024-03-01', NULL, 'Tool Shed', 1, 'admin', 'Available'),
(14, 'Screwdriver Set', 'BC7002', 7, 'sets', 'Precision set', '2024-01-10', NULL, 'Tool Shed', 1, 'admin', 'Available'),
(15, 'Laptop Acer Aspire', 'BC1003', 1, 'pcs', 'Backup laptop', '2024-02-18', NULL, 'IT Room', 4, 'admin', 'Available'),
(16, 'Printer HP LaserJet', 'BC1004', 1, 'pcs', 'Monochrome printer', '2024-05-02', NULL, 'IT Room', 4, 'admin', 'Borrowed'),
(17, 'Office Chair Black', 'BC2003', 2, 'pcs', 'Ergonomic chair', '2023-09-19', NULL, 'Admin Office', 2, 'admin', 'Available'),
(18, 'Whiteboard 6ft', 'BC2004', 2, 'pcs', 'Classroom whiteboard', '2023-11-25', NULL, 'Storage B', 2, 'john', 'Available'),
(19, 'Soccer Ball Adidas', 'BC3003', 3, 'pcs', 'Outdoor ball', '2024-02-11', NULL, 'Gym', 3, 'admin', 'Missing'),
(20, 'Badminton Racket Yonex', 'BC3004', 3, 'pcs', 'Training racket', '2024-03-08', NULL, 'Gym', 3, 'admin', 'Available'),
(21, 'Keyboard Mechanical', 'BC4003', 4, 'pcs', 'RGB keyboard', '2024-01-29', NULL, 'IT Room', 4, 'michael', 'Borrowed'),
(22, 'LAN Cable 10m', 'BC4004', 4, 'pcs', 'Network cable', '2024-01-15', NULL, 'Server Room', 4, 'admin', 'Available'),
(23, 'Beaker Set 500ml', 'BC5003', 5, 'sets', 'Laboratory beakers', '2023-04-09', NULL, 'Lab B', 5, 'teacher_ana', ''),
(24, 'Digital Multimeter', 'BC7003', 7, 'pcs', 'Electrical tester', '2024-06-01', NULL, 'Tool Shed', 1, 'admin', 'Available'),
(25, 'Item A1', 'GEN001', 1, 'pcs', 'Generic item A1', '2023-01-12', NULL, 'Storage C', 1, 'admin', 'Available'),
(26, 'Item A2', 'GEN002', 2, 'pcs', 'Generic item A2', '2023-02-11', NULL, 'Storage C', 2, 'admin', 'Borrowed'),
(27, 'Item A3', 'GEN003', 3, 'pcs', 'Generic item A3', '2023-03-13', NULL, 'Storage C', 3, 'admin', 'Damaged'),
(28, 'Item A4', 'GEN004', 4, 'pcs', 'Generic item A4', '2023-04-14', NULL, 'Storage C', 4, 'admin', 'Available'),
(29, 'Item A5', 'GEN005', 5, 'pcs', 'Generic item A5', '2023-05-16', NULL, 'Storage C', 5, 'admin', 'Missing'),
(30, 'Item A6', 'GEN006', 6, 'pcs', 'Generic item A6', '2023-06-18', NULL, 'Storage C', 6, 'admin', 'Available'),
(31, 'Item A7', 'GEN007', 7, 'pcs', 'Generic item A7', '2023-07-20', NULL, 'Storage C', 1, 'admin', 'Missing'),
(32, 'Item A8', 'GEN008', 1, 'pcs', 'Generic item A8', '2023-08-22', NULL, 'Storage C', 4, 'admin', 'Missing'),
(33, 'Item A9', 'GEN009', 2, 'pcs', 'Generic item A9', '2023-09-24', NULL, 'Storage C', 2, 'admin', 'Missing'),
(34, 'Item A10', 'GEN010', 3, 'pcs', 'Generic item A10', '2023-10-25', NULL, 'Storage C', 3, 'admin', 'Disposed'),
(35, 'Item B1', 'GEN011', 4, 'pcs', 'Generic item B1', '2024-01-01', NULL, 'Storage C', 4, 'admin', 'Available'),
(36, 'Item B2', 'GEN012', 5, 'pcs', 'Generic item B2', '2024-02-02', NULL, 'Storage C', 5, 'admin', 'Missing'),
(37, 'Item B3', 'GEN013', 6, 'pcs', 'Generic item B3', '2024-03-03', NULL, 'Storage C', 6, 'admin', 'Available'),
(38, 'Item B4', 'GEN014', 7, 'pcs', 'Generic item B4', '2024-04-04', NULL, 'Storage C', 1, 'admin', 'Missing'),
(39, 'Item B5', 'GEN015', 1, 'pcs', 'Generic item B5', '2024-05-05', NULL, 'Storage C', 1, 'admin', 'Available'),
(40, 'Item B6', 'GEN016', 2, 'pcs', 'Generic item B6', '2024-06-06', NULL, 'Storage C', 2, 'admin', 'Missing'),
(41, 'Item B7', 'GEN017', 3, 'pcs', 'Generic item B7', '2024-07-07', NULL, 'Storage C', 3, 'admin', 'Available'),
(42, 'Item B8', 'GEN018', 4, 'pcs', 'Generic item B8', '2024-08-08', NULL, 'Storage C', 4, 'admin', 'Missing'),
(43, 'Item B9', 'GEN019', 5, 'pcs', 'Generic item B9', '2024-09-09', NULL, 'Storage C', 5, 'admin', 'Borrowed'),
(44, 'Item B10', 'GEN020', 6, 'pcs', 'Generic item B10', '2024-10-10', NULL, 'Storage C', 6, 'admin', 'Borrowed'),
(45, 'Item C1', 'GEN021', 1, 'pcs', 'Generic item C1', '2024-11-11', NULL, 'Storage C', 4, 'admin', 'Available'),
(46, 'Item C2', 'GEN022', 2, 'pcs', 'Generic item C2', '2024-11-12', NULL, 'Storage C', 2, 'admin', 'Borrowed'),
(47, 'Item C3', 'GEN023', 3, 'pcs', 'Generic item C3', '2024-11-13', NULL, 'Storage C', 3, 'admin', 'Missing'),
(48, 'Item C4', 'GEN024', 4, 'pcs', 'Generic item C4', '2024-11-14', NULL, 'Storage C', 4, 'admin', 'Available'),
(49, 'Item C5', 'GEN025', 5, 'pcs', 'Generic item C5', '2024-11-15', NULL, 'Storage C', 5, 'admin', 'Available'),
(50, 'Item C6', 'GEN026', 6, 'pcs', 'Generic item C6', '2024-11-16', NULL, 'Storage C', 6, 'admin', 'Borrowed');

-- --------------------------------------------------------

--
-- Table structure for table `scan_log`
--

CREATE TABLE `scan_log` (
  `scan_id` bigint(20) NOT NULL,
  `item_id` int(11) NOT NULL,
  `scan_date` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `scan_log`
--

INSERT INTO `scan_log` (`scan_id`, `item_id`, `scan_date`) VALUES
(1, 1, '2025-01-10 08:00:00'),
(2, 2, '2025-01-10 08:10:00'),
(3, 5, '2025-01-09 09:33:00'),
(4, 6, '2025-01-07 14:20:00'),
(5, 10, '2025-01-05 10:45:00'),
(6, 12, '2025-01-04 11:03:00'),
(7, 23, '2024-12-28 15:20:00'),
(8, 20, '2024-12-22 16:20:00'),
(9, 27, '2024-12-20 09:50:00'),
(10, 31, '2024-12-18 13:40:00'),
(11, 33, '2024-12-15 10:10:00'),
(12, 45, '2024-12-10 07:55:00');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `firstName` varchar(100) DEFAULT NULL,
  `lastName` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `created_at`, `firstName`, `lastName`) VALUES
(1, 'admin', 'admin', '2025-12-09 16:41:46', 'System', 'Admin'),
(2, 'jane_doe', 'hashed123', '2025-12-09 16:41:46', 'Jane', 'Doe'),
(3, 'michael', 'hashed123', '2025-12-09 16:41:46', 'Michael', 'Cruz'),
(4, 'teacher_ana', 'hashed123', '2025-12-09 16:41:46', 'Ana', 'Reyes'),
(5, 'inventory_john', 'hashed123', '2025-12-09 16:41:46', 'John', 'Lopez');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `audit_log`
--
ALTER TABLE `audit_log`
  ADD PRIMARY KEY (`audit_id`);

--
-- Indexes for table `borrowers`
--
ALTER TABLE `borrowers`
  ADD PRIMARY KEY (`borrower_id`);

--
-- Indexes for table `borrow_records`
--
ALTER TABLE `borrow_records`
  ADD PRIMARY KEY (`record_id`),
  ADD KEY `idx_borrow_item` (`item_id`),
  ADD KEY `idx_borrow_borrower` (`borrower_id`);

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`category_id`),
  ADD UNIQUE KEY `category_name` (`category_name`);

--
-- Indexes for table `incharge`
--
ALTER TABLE `incharge`
  ADD PRIMARY KEY (`incharge_id`),
  ADD KEY `assigned_category_id` (`assigned_category_id`);

--
-- Indexes for table `items`
--
ALTER TABLE `items`
  ADD PRIMARY KEY (`item_id`),
  ADD UNIQUE KEY `barcode` (`barcode`),
  ADD KEY `fk_items_incharge` (`incharge_id`),
  ADD KEY `idx_items_category` (`category_id`),
  ADD KEY `idx_items_barcode` (`barcode`);

--
-- Indexes for table `scan_log`
--
ALTER TABLE `scan_log`
  ADD PRIMARY KEY (`scan_id`),
  ADD KEY `item_id` (`item_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `audit_log`
--
ALTER TABLE `audit_log`
  MODIFY `audit_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=44;

--
-- AUTO_INCREMENT for table `borrowers`
--
ALTER TABLE `borrowers`
  MODIFY `borrower_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `borrow_records`
--
ALTER TABLE `borrow_records`
  MODIFY `record_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `category_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `incharge`
--
ALTER TABLE `incharge`
  MODIFY `incharge_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `items`
--
ALTER TABLE `items`
  MODIFY `item_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=51;

--
-- AUTO_INCREMENT for table `scan_log`
--
ALTER TABLE `scan_log`
  MODIFY `scan_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `borrow_records`
--
ALTER TABLE `borrow_records`
  ADD CONSTRAINT `fk_borrow_borrower` FOREIGN KEY (`borrower_id`) REFERENCES `borrowers` (`borrower_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_borrow_item` FOREIGN KEY (`item_id`) REFERENCES `items` (`item_id`) ON UPDATE CASCADE;

--
-- Constraints for table `incharge`
--
ALTER TABLE `incharge`
  ADD CONSTRAINT `incharge_ibfk_1` FOREIGN KEY (`assigned_category_id`) REFERENCES `categories` (`category_id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `items`
--
ALTER TABLE `items`
  ADD CONSTRAINT `fk_items_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_items_incharge` FOREIGN KEY (`incharge_id`) REFERENCES `incharge` (`incharge_id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `scan_log`
--
ALTER TABLE `scan_log`
  ADD CONSTRAINT `scan_log_ibfk_1` FOREIGN KEY (`item_id`) REFERENCES `items` (`item_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
