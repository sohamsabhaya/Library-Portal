-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Sep 01, 2024 at 10:10 AM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `library`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `add_user` (IN `nam` VARCHAR(20), IN `pass` VARCHAR(20))   BEGIN
INSERT INTO user(uname, upassword) VALUES (nam, pass);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `remove_user` (IN `rid` INT)   BEGIN
DELETE FROM user WHERE uid=rid;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `book`
--

CREATE TABLE `book` (
  `bid` int(11) NOT NULL,
  `btitle` varchar(20) NOT NULL,
  `bauthor` varchar(20) NOT NULL,
  `bquantity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- RELATIONSHIPS FOR TABLE `book`:
--

--
-- Dumping data for table `book`
--

INSERT INTO `book` (`bid`, `btitle`, `bauthor`, `bquantity`) VALUES(1, 'Great Gatsby', 'F. Scott Fitzgerald', 2);
INSERT INTO `book` (`bid`, `btitle`, `bauthor`, `bquantity`) VALUES(2, 'Atomic Habits', 'James Clear', 5);
INSERT INTO `book` (`bid`, `btitle`, `bauthor`, `bquantity`) VALUES(3, 'Harry Potter', 'JK Rowling', 8);
INSERT INTO `book` (`bid`, `btitle`, `bauthor`, `bquantity`) VALUES(4, 'Song of Ice and Fire', 'George RR Martin', 10);
INSERT INTO `book` (`bid`, `btitle`, `bauthor`, `bquantity`) VALUES(5, 'Theory of Everything', 'Stephen Hawking', 5);

-- --------------------------------------------------------

--
-- Table structure for table `borrow`
--

CREATE TABLE `borrow` (
  `bid` int(11) NOT NULL,
  `btitle` varchar(20) NOT NULL,
  `uid` int(11) NOT NULL,
  `uname` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- RELATIONSHIPS FOR TABLE `borrow`:
--   `uid`
--       `user` -> `uid`
--   `uid`
--       `user` -> `uid`
--   `bid`
--       `book` -> `bid`
--

-- --------------------------------------------------------

--
-- Table structure for table `returned`
--

CREATE TABLE `returned` (
  `bid` int(11) NOT NULL,
  `btitle` varchar(20) NOT NULL,
  `uid` int(11) NOT NULL,
  `uname` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- RELATIONSHIPS FOR TABLE `returned`:
--   `uid`
--       `user` -> `uid`
--   `bid`
--       `book` -> `bid`
--   `uid`
--       `user` -> `uid`
--   `bid`
--       `book` -> `bid`
--

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `uid` int(11) NOT NULL,
  `uname` varchar(20) NOT NULL,
  `upassword` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- RELATIONSHIPS FOR TABLE `user`:
--

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`uid`, `uname`, `upassword`) VALUES(1, 'soham', '12345678');
INSERT INTO `user` (`uid`, `uname`, `upassword`) VALUES(2, 'namra', 'namra12345');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `book`
--
ALTER TABLE `book`
  ADD PRIMARY KEY (`bid`);

--
-- Indexes for table `borrow`
--
ALTER TABLE `borrow`
  ADD KEY `uid` (`uid`),
  ADD KEY `bid` (`bid`);

--
-- Indexes for table `returned`
--
ALTER TABLE `returned`
  ADD KEY `uid` (`uid`),
  ADD KEY `bid` (`bid`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`uid`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `book`
--
ALTER TABLE `book`
  MODIFY `bid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `uid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `borrow`
--
ALTER TABLE `borrow`
  ADD CONSTRAINT `borrow_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`),
  ADD CONSTRAINT `borrow_ibfk_2` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`),
  ADD CONSTRAINT `borrow_ibfk_3` FOREIGN KEY (`bid`) REFERENCES `book` (`bid`);

--
-- Constraints for table `returned`
--
ALTER TABLE `returned`
  ADD CONSTRAINT `returned_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`),
  ADD CONSTRAINT `returned_ibfk_2` FOREIGN KEY (`bid`) REFERENCES `book` (`bid`),
  ADD CONSTRAINT `returned_ibfk_3` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`),
  ADD CONSTRAINT `returned_ibfk_4` FOREIGN KEY (`bid`) REFERENCES `book` (`bid`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
