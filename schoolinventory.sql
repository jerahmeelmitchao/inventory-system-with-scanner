-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 21, 2025 at 05:51 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

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
(1, '2025-12-09 14:08:56', 'admin', 'ADD_ITEM', 'Added item: asdasf'),
(2, '2025-12-09 14:11:12', 'admin', 'ADD_ITEM', 'Added item: asdsad'),
(3, '2025-12-09 14:32:45', 'admin', 'LOGIN', 'User logged into the system');

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
(4, 'asdsad', 'sdads', 'Student');

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
(1, 14, 4, '2025-12-09 22:16:00', '2025-12-09 22:16:25', 'Returned', ''),
(2, 14, 4, '2025-12-09 22:21:14', '2025-12-09 22:21:27', 'Returned', ''),
(3, 14, 4, '2025-12-01 22:22:22', NULL, 'Borrowed', NULL);

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
(9, 'new categoryy');

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
(7, 'person 1', 'dogstyleee', '12345678900', 9);

-- --------------------------------------------------------

--
-- Table structure for table `items`
--

CREATE TABLE `items` (
  `item_id` int(11) NOT NULL,
  `item_name` varchar(200) NOT NULL,
  `barcode` varchar(100) DEFAULT NULL,
  `category_id` int(11) DEFAULT NULL,
  `location_id` int(11) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `date_acquired` date DEFAULT NULL,
  `last_scanned` datetime DEFAULT NULL,
  `incharge_id` int(11) DEFAULT NULL,
  `added_by` varchar(100) DEFAULT NULL,
  `status` enum('Available','Damaged','Borrowed','Missing','Disposed') NOT NULL DEFAULT 'Available',
  `unit_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `items`
--

INSERT INTO `items` (`item_id`, `item_name`, `barcode`, `category_id`, `location_id`, `description`, `date_acquired`, `last_scanned`, `incharge_id`, `added_by`, `status`, `unit_id`) VALUES
(14, 'asdasf', 'A2A7C1D13588', 9, 3, 'mao nani', '2025-12-09', '2025-12-09 22:24:46', 7, 'admin', 'Missing', 2),
(15, 'asdsad', '824DFC1E4BFB', 9, 1, 'asdasdd', '2025-12-09', '2025-12-09 22:46:28', 7, 'admin', 'Available', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `locations`
--

CREATE TABLE `locations` (
  `location_id` int(11) NOT NULL,
  `location_name` varchar(100) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `locations`
--

INSERT INTO `locations` (`location_id`, `location_name`, `description`, `created_at`) VALUES
(1, 'Storage Room', 'Main storage area', '2025-12-18 19:01:34'),
(2, 'Room 101', 'Computer Lab', '2025-12-18 19:01:34'),
(3, 'Library', 'Library storage', '2025-12-18 19:01:34'),
(4, 'Office', 'Admin office', '2025-12-18 19:01:34');

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
(1, 14, '2025-12-09 22:15:21'),
(2, 14, '2025-12-09 22:15:30'),
(3, 14, '2025-12-09 22:15:36'),
(4, 14, '2025-12-09 22:15:55'),
(5, 14, '2025-12-09 22:16:13'),
(6, 14, '2025-12-09 22:21:10'),
(7, 14, '2025-12-09 22:22:12'),
(8, 14, '2025-12-09 22:22:16'),
(9, 14, '2025-12-09 22:23:34'),
(10, 14, '2025-12-09 22:23:42'),
(11, 14, '2025-12-09 22:24:26'),
(12, 14, '2025-12-09 22:24:46'),
(13, 15, '2025-12-09 22:44:01'),
(14, 15, '2025-12-09 22:46:28');

-- --------------------------------------------------------

--
-- Table structure for table `units`
--

CREATE TABLE `units` (
  `unit_id` int(11) NOT NULL,
  `unit_name` varchar(50) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `units`
--

INSERT INTO `units` (`unit_id`, `unit_name`, `description`, `created_at`) VALUES
(2, 'set', '', '2025-12-21 16:24:11');

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
(6, 'admin', 'admin', '2025-12-09 14:06:44', 'admin', 'nimda');

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
  ADD KEY `idx_items_barcode` (`barcode`),
  ADD KEY `fk_items_location` (`location_id`),
  ADD KEY `fk_items_unit` (`unit_id`);

--
-- Indexes for table `locations`
--
ALTER TABLE `locations`
  ADD PRIMARY KEY (`location_id`),
  ADD UNIQUE KEY `location_name` (`location_name`);

--
-- Indexes for table `scan_log`
--
ALTER TABLE `scan_log`
  ADD PRIMARY KEY (`scan_id`),
  ADD KEY `item_id` (`item_id`);

--
-- Indexes for table `units`
--
ALTER TABLE `units`
  ADD PRIMARY KEY (`unit_id`),
  ADD UNIQUE KEY `unit_name` (`unit_name`);

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
  MODIFY `audit_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `borrowers`
--
ALTER TABLE `borrowers`
  MODIFY `borrower_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `borrow_records`
--
ALTER TABLE `borrow_records`
  MODIFY `record_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `category_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `incharge`
--
ALTER TABLE `incharge`
  MODIFY `incharge_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `items`
--
ALTER TABLE `items`
  MODIFY `item_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `locations`
--
ALTER TABLE `locations`
  MODIFY `location_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `scan_log`
--
ALTER TABLE `scan_log`
  MODIFY `scan_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT for table `units`
--
ALTER TABLE `units`
  MODIFY `unit_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

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
  ADD CONSTRAINT `fk_items_incharge` FOREIGN KEY (`incharge_id`) REFERENCES `incharge` (`incharge_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_items_location` FOREIGN KEY (`location_id`) REFERENCES `locations` (`location_id`) ON DELETE SET NULL,
  ADD CONSTRAINT `fk_items_unit` FOREIGN KEY (`unit_id`) REFERENCES `units` (`unit_id`) ON DELETE SET NULL;

--
-- Constraints for table `scan_log`
--
ALTER TABLE `scan_log`
  ADD CONSTRAINT `scan_log_ibfk_1` FOREIGN KEY (`item_id`) REFERENCES `items` (`item_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
