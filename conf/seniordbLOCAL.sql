/*
Navicat MySQL Data Transfer

Source Server         : SeniorDB
Source Server Version : 50542
Source Host           : localhost:3306
Source Database       : seniordb

Target Server Type    : MYSQL
Target Server Version : 50542
File Encoding         : 65001

Date: 2015-03-14 01:52:24
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for answer
-- ----------------------------
DROP TABLE IF EXISTS `answer`;
CREATE TABLE `answer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `answer-text` varchar(255) DEFAULT NULL,
  `question-id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `question-id` (`question-id`),
  CONSTRAINT `ANSWER-QUESTION-ID` FOREIGN KEY (`question-id`) REFERENCES `question` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of answer
-- ----------------------------

-- ----------------------------
-- Table structure for customer-review
-- ----------------------------
DROP TABLE IF EXISTS `customer-review`;
CREATE TABLE `customer-review` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `review-text` varchar(255) DEFAULT NULL,
  `date-added` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `source` varchar(255) DEFAULT NULL,
  `product-codes` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `product-code_FK` (`product-codes`),
  CONSTRAINT `product-code_FK` FOREIGN KEY (`product-codes`) REFERENCES `product-codes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of customer-review
-- ----------------------------
INSERT INTO `customer-review` VALUES ('1', 'Lolinator', '2015-03-13 21:42:18', 'www.lol.com', '2');
INSERT INTO `customer-review` VALUES ('2', 'Lolinator', '2015-03-13 21:42:38', 'www.lol.com', '2');

-- ----------------------------
-- Table structure for desc
-- ----------------------------
DROP TABLE IF EXISTS `desc`;
CREATE TABLE `desc` (
  `seller-id` int(11) NOT NULL,
  `product-codes` int(11) NOT NULL,
  `text` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`seller-id`,`product-codes`),
  KEY `seller-product_product-codes_FK` (`product-codes`),
  CONSTRAINT `seller-product_seller-id_FK` FOREIGN KEY (`seller-id`) REFERENCES `seller` (`id`),
  CONSTRAINT `seller-product_product-codes_FK` FOREIGN KEY (`product-codes`) REFERENCES `product-codes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of desc
-- ----------------------------
INSERT INTO `desc` VALUES ('6', '2', 'Really fucking shiny22');
INSERT INTO `desc` VALUES ('6', '3', 'Really fucking shiny22');

-- ----------------------------
-- Table structure for expert-review
-- ----------------------------
DROP TABLE IF EXISTS `expert-review`;
CREATE TABLE `expert-review` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `url` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `website-name` varchar(255) DEFAULT NULL,
  `product-codes` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `product_codes_FK` (`product-codes`),
  CONSTRAINT `product_codes_FK` FOREIGN KEY (`product-codes`) REFERENCES `product-codes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of expert-review
-- ----------------------------
INSERT INTO `expert-review` VALUES ('3', 'www.lol.com', 'why iphones suck', 'Lolinator', '2');
INSERT INTO `expert-review` VALUES ('4', 'www.lol.com', 'why iphones suck', 'Lolinator', '2');
INSERT INTO `expert-review` VALUES ('5', 'www.lol.com', 'why iphones suck', 'Lolinator', '2');
INSERT INTO `expert-review` VALUES ('6', 'www.lol.com', 'why iphones suck', 'Lolinator', '2');

-- ----------------------------
-- Table structure for get-notifed
-- ----------------------------
DROP TABLE IF EXISTS `get-notifed`;
CREATE TABLE `get-notifed` (
  `product-codes` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `long` varchar(255) DEFAULT NULL,
  `lat` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`product-codes`,`username`),
  KEY `usr-name_FK` (`username`),
  CONSTRAINT `usr-name_FK` FOREIGN KEY (`username`) REFERENCES `user-account` (`username`),
  CONSTRAINT `product-codess_FK` FOREIGN KEY (`product-codes`) REFERENCES `product-codes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of get-notifed
-- ----------------------------

-- ----------------------------
-- Table structure for images
-- ----------------------------
DROP TABLE IF EXISTS `images`;
CREATE TABLE `images` (
  `URL` varchar(255) NOT NULL,
  `seller-id` int(11) NOT NULL,
  `product-codes-id` int(11) NOT NULL,
  PRIMARY KEY (`URL`),
  KEY `sellerID_FK` (`seller-id`),
  KEY `images_product-codes_FK` (`product-codes-id`),
  CONSTRAINT `images_product-codes_FK` FOREIGN KEY (`product-codes-id`) REFERENCES `product-codes` (`id`),
  CONSTRAINT `sellerID_FK` FOREIGN KEY (`seller-id`) REFERENCES `seller` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of images
-- ----------------------------
INSERT INTO `images` VALUES ('iphone.jpg', '6', '2');
INSERT INTO `images` VALUES ('iphone2.jpg', '6', '2');
INSERT INTO `images` VALUES ('iphone22.jpg', '6', '2');
INSERT INTO `images` VALUES ('iphone222.jpg', '6', '2');
INSERT INTO `images` VALUES ('iphone2232.jpg', '6', '3');

-- ----------------------------
-- Table structure for independent-seller
-- ----------------------------
DROP TABLE IF EXISTS `independent-seller`;
CREATE TABLE `independent-seller` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `lat` varchar(255) DEFAULT NULL,
  `long` varchar(255) DEFAULT NULL,
  `active` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`username`),
  KEY `id` (`id`),
  CONSTRAINT `seller-independent-id` FOREIGN KEY (`id`) REFERENCES `seller` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of independent-seller
-- ----------------------------

-- ----------------------------
-- Table structure for local-seller
-- ----------------------------
DROP TABLE IF EXISTS `local-seller`;
CREATE TABLE `local-seller` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `logo` varchar(255) DEFAULT NULL,
  `lat` varchar(255) DEFAULT NULL,
  `long` varchar(255) DEFAULT NULL,
  `open_hours` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`username`),
  KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of local-seller
-- ----------------------------

-- ----------------------------
-- Table structure for offer
-- ----------------------------
DROP TABLE IF EXISTS `offer`;
CREATE TABLE `offer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `price` decimal(10,0) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `date_added` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `seller_id` int(11) DEFAULT NULL,
  `view-count` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `seller_id` (`seller_id`),
  CONSTRAINT `seller id` FOREIGN KEY (`seller_id`) REFERENCES `seller` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of offer
-- ----------------------------

-- ----------------------------
-- Table structure for offer_products
-- ----------------------------
DROP TABLE IF EXISTS `offer_products`;
CREATE TABLE `offer_products` (
  `offer_id` int(11) NOT NULL,
  `product-codes` int(11) NOT NULL,
  PRIMARY KEY (`offer_id`,`product-codes`),
  KEY `upc` (`product-codes`),
  CONSTRAINT `offers_product-codes_FK` FOREIGN KEY (`product-codes`) REFERENCES `product-codes` (`id`),
  CONSTRAINT `offer id` FOREIGN KEY (`offer_id`) REFERENCES `offer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of offer_products
-- ----------------------------

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `codes` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `date_added` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `category-id` int(255) DEFAULT NULL,
  PRIMARY KEY (`codes`),
  KEY `category-id` (`category-id`),
  KEY `category-id_2` (`category-id`),
  CONSTRAINT `product-category` FOREIGN KEY (`category-id`) REFERENCES `product-category` (`id`),
  CONSTRAINT `product-codes_FK` FOREIGN KEY (`codes`) REFERENCES `product-codes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES ('2', 'iphone', '2015-03-13 21:22:31', '29');
INSERT INTO `product` VALUES ('3', 'iphone', '2015-03-13 22:18:35', '29');

-- ----------------------------
-- Table structure for product-category
-- ----------------------------
DROP TABLE IF EXISTS `product-category`;
CREATE TABLE `product-category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parent_id` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `parent_id` (`parent_id`),
  CONSTRAINT `parent-categoy` FOREIGN KEY (`parent_id`) REFERENCES `product-category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of product-category
-- ----------------------------
INSERT INTO `product-category` VALUES ('29', null, 'phones');

-- ----------------------------
-- Table structure for product-codes
-- ----------------------------
DROP TABLE IF EXISTS `product-codes`;
CREATE TABLE `product-codes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `UPC` varchar(255) DEFAULT NULL,
  `EAN` varchar(255) DEFAULT NULL,
  `NPN` varchar(255) DEFAULT NULL,
  `ISBN` varchar(255) DEFAULT NULL,
  `ASIN` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UPC` (`UPC`),
  UNIQUE KEY `EAN` (`EAN`),
  UNIQUE KEY `NPN` (`NPN`),
  UNIQUE KEY `ISBN` (`ISBN`),
  UNIQUE KEY `ASIN` (`ASIN`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of product-codes
-- ----------------------------
INSERT INTO `product-codes` VALUES ('2', '2555', null, null, null, null);
INSERT INTO `product-codes` VALUES ('3', '25555', null, null, null, null);

-- ----------------------------
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `question-text` varchar(255) DEFAULT NULL,
  `date-added` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `date` date DEFAULT NULL,
  `product-codes` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `upc` (`product-codes`),
  KEY `id` (`id`),
  CONSTRAINT `question_product-codes_FK` FOREIGN KEY (`product-codes`) REFERENCES `product-codes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of question
-- ----------------------------

-- ----------------------------
-- Table structure for seller
-- ----------------------------
DROP TABLE IF EXISTS `seller`;
CREATE TABLE `seller` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of seller
-- ----------------------------
INSERT INTO `seller` VALUES ('6', 'Amazon');

-- ----------------------------
-- Table structure for seller-product
-- ----------------------------
DROP TABLE IF EXISTS `seller-product`;
CREATE TABLE `seller-product` (
  `price` decimal(10,0) NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `product-codes` int(11) NOT NULL,
  `seller_id` int(11) NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `upc` (`product-codes`),
  KEY `seller_id` (`seller_id`),
  KEY `upc_2` (`product-codes`),
  KEY `seller_id_2` (`seller_id`),
  KEY `upc_3` (`product-codes`),
  KEY `seller_id_3` (`seller_id`),
  KEY `upc_4` (`product-codes`),
  KEY `seller_id_4` (`seller_id`),
  CONSTRAINT `sellerProduct_product-codes_FK` FOREIGN KEY (`product-codes`) REFERENCES `product-codes` (`id`),
  CONSTRAINT `seller_product_sellerid` FOREIGN KEY (`seller_id`) REFERENCES `seller` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of seller-product
-- ----------------------------
INSERT INTO `seller-product` VALUES ('50000', '2015-03-13 21:22:31', '2', '6', '3', 'www.amazon.com/iphoneLOL');
INSERT INTO `seller-product` VALUES ('50000', '2015-03-13 21:25:48', '2', '6', '4', 'www.amazon.com/iphoneLOL');
INSERT INTO `seller-product` VALUES ('50000', '2015-03-13 22:19:09', '3', '6', '5', 'www.amazon.com/iphoneLOL');

-- ----------------------------
-- Table structure for user-account
-- ----------------------------
DROP TABLE IF EXISTS `user-account`;
CREATE TABLE `user-account` (
  `username` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `firstname` varchar(255) DEFAULT NULL,
  `lastname` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `active` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of user-account
-- ----------------------------
INSERT INTO `user-account` VALUES ('kariem', 'password', 'kariem', 'fahmi', 'moml31@hotmail.com', '0');

-- ----------------------------
-- Table structure for web-based-seller
-- ----------------------------
DROP TABLE IF EXISTS `web-based-seller`;
CREATE TABLE `web-based-seller` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `logo` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `ID_FK` FOREIGN KEY (`id`) REFERENCES `seller` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of web-based-seller
-- ----------------------------
INSERT INTO `web-based-seller` VALUES ('6', 'amazon.png', 'www.Amazon.com');
