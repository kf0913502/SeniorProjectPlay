/*
Navicat MySQL Data Transfer

Source Server         : localSeniorDB
Source Server Version : 50621
Source Host           : localhost:3306
Source Database       : seniordb

Target Server Type    : MYSQL
Target Server Version : 50621
File Encoding         : 65001

Date: 2015-05-05 18:16:35
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `answer`
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
-- Table structure for `customer-review`
-- ----------------------------
DROP TABLE IF EXISTS `customer-review`;
CREATE TABLE `customer-review` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `review-text` text,
  `date-added` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `source` varchar(255) DEFAULT NULL,
  `product-codes` int(11) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `product-code_FK` (`product-codes`),
  CONSTRAINT `product-code_FK` FOREIGN KEY (`product-codes`) REFERENCES `product-codes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of customer-review
-- ----------------------------

-- ----------------------------
-- Table structure for `desc`
-- ----------------------------
DROP TABLE IF EXISTS `desc`;
CREATE TABLE `desc` (
  `seller-id` int(11) NOT NULL,
  `product-codes` int(11) NOT NULL,
  `text` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`seller-id`,`product-codes`),
  KEY `seller-product_product-codes_FK` (`product-codes`),
  CONSTRAINT `seller-product_product-codes_FK` FOREIGN KEY (`product-codes`) REFERENCES `product-codes` (`id`),
  CONSTRAINT `seller-product_seller-id_FK` FOREIGN KEY (`seller-id`) REFERENCES `seller` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of desc
-- ----------------------------

-- ----------------------------
-- Table structure for `expert-review`
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of expert-review
-- ----------------------------

-- ----------------------------
-- Table structure for `get-notifed`
-- ----------------------------
DROP TABLE IF EXISTS `get-notifed`;
CREATE TABLE `get-notifed` (
  `product-codes` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `long` varchar(255) DEFAULT NULL,
  `lat` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`product-codes`,`username`),
  KEY `usr-name_FK` (`username`),
  CONSTRAINT `product-codess_FK` FOREIGN KEY (`product-codes`) REFERENCES `product-codes` (`id`),
  CONSTRAINT `usr-name_FK` FOREIGN KEY (`username`) REFERENCES `user-account` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of get-notifed
-- ----------------------------

-- ----------------------------
-- Table structure for `images`
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

-- ----------------------------
-- Table structure for `independent-seller`
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
-- Table structure for `local-seller`
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of local-seller
-- ----------------------------

-- ----------------------------
-- Table structure for `offer`
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
-- Table structure for `offer_products`
-- ----------------------------
DROP TABLE IF EXISTS `offer_products`;
CREATE TABLE `offer_products` (
  `offer_id` int(11) NOT NULL,
  `product-codes` int(11) NOT NULL,
  PRIMARY KEY (`offer_id`,`product-codes`),
  KEY `upc` (`product-codes`),
  CONSTRAINT `offer id` FOREIGN KEY (`offer_id`) REFERENCES `offer` (`id`),
  CONSTRAINT `offers_product-codes_FK` FOREIGN KEY (`product-codes`) REFERENCES `product-codes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of offer_products
-- ----------------------------

-- ----------------------------
-- Table structure for `ontology-features`
-- ----------------------------
DROP TABLE IF EXISTS `ontology-features`;
CREATE TABLE `ontology-features` (
  `feature` varchar(20) NOT NULL DEFAULT '',
  `nodeID` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`nodeID`,`feature`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of ontology-features
-- ----------------------------
INSERT INTO `ontology-features` VALUES ('phone', '2');
INSERT INTO `ontology-features` VALUES ('sexy', '3');
INSERT INTO `ontology-features` VALUES ('hot', '4');

-- ----------------------------
-- Table structure for `ontology-nodes`
-- ----------------------------
DROP TABLE IF EXISTS `ontology-nodes`;
CREATE TABLE `ontology-nodes` (
  `parent` int(11) DEFAULT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category-id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `ontologyNodes_FK` (`parent`),
  KEY `ontologyNodes_CategoryID_FK` (`category-id`),
  CONSTRAINT `ontologyNodes_CategoryID_FK` FOREIGN KEY (`category-id`) REFERENCES `product-category` (`id`),
  CONSTRAINT `ontologyNodes_FK` FOREIGN KEY (`parent`) REFERENCES `ontology-nodes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of ontology-nodes
-- ----------------------------
INSERT INTO `ontology-nodes` VALUES (null, '2', '0');
INSERT INTO `ontology-nodes` VALUES ('2', '3', '0');
INSERT INTO `ontology-nodes` VALUES ('2', '4', '0');

-- ----------------------------
-- Table structure for `price-reduction`
-- ----------------------------
DROP TABLE IF EXISTS `price-reduction`;
CREATE TABLE `price-reduction` (
  `product-codes` int(11) DEFAULT NULL,
  `sellerID` int(11) DEFAULT NULL,
  `oldPrice` decimal(10,2) DEFAULT NULL,
  `newPrice` decimal(10,2) DEFAULT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  KEY `priceReduction_productCodes_FK` (`product-codes`),
  KEY `priceReduction_sellerID_FK` (`sellerID`),
  CONSTRAINT `priceReduction_productCodes_FK` FOREIGN KEY (`product-codes`) REFERENCES `product-codes` (`id`),
  CONSTRAINT `priceReduction_sellerID_FK` FOREIGN KEY (`sellerID`) REFERENCES `seller` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of price-reduction
-- ----------------------------

-- ----------------------------
-- Table structure for `product`
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
INSERT INTO `product` VALUES ('78', 'Apple Mac Mini MGEN2LL/A Desktop (NEWEST VERSION)', '2015-05-02 14:14:34', '34');

-- ----------------------------
-- Table structure for `product-category`
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
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of product-category
-- ----------------------------
INSERT INTO `product-category` VALUES ('0', null, 'camera');
INSERT INTO `product-category` VALUES ('33', null, 'General Things');
INSERT INTO `product-category` VALUES ('34', '33', 'Phone');

-- ----------------------------
-- Table structure for `product-codes`
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
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of product-codes
-- ----------------------------
INSERT INTO `product-codes` VALUES ('78', 'test7', 'test5', '', '', 'test3');

-- ----------------------------
-- Table structure for `question`
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
-- Table structure for `related`
-- ----------------------------
DROP TABLE IF EXISTS `related`;
CREATE TABLE `related` (
  `PID1` int(11) NOT NULL,
  `PID2` int(11) NOT NULL,
  KEY `related_PID1_FK1` (`PID1`),
  KEY `related_PID2_FK1` (`PID2`),
  CONSTRAINT `related_PID1_FK1` FOREIGN KEY (`PID1`) REFERENCES `product` (`codes`),
  CONSTRAINT `related_PID2_FK1` FOREIGN KEY (`PID2`) REFERENCES `product` (`codes`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of related
-- ----------------------------

-- ----------------------------
-- Table structure for `seller`
-- ----------------------------
DROP TABLE IF EXISTS `seller`;
CREATE TABLE `seller` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of seller
-- ----------------------------

-- ----------------------------
-- Table structure for `seller-product`
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of seller-product
-- ----------------------------

-- ----------------------------
-- Table structure for `sentiment`
-- ----------------------------
DROP TABLE IF EXISTS `sentiment`;
CREATE TABLE `sentiment` (
  `product_codes` int(11) NOT NULL,
  `sentiment_neg` int(11) DEFAULT NULL,
  `sentiment_pos` int(11) DEFAULT NULL,
  `feature` varchar(20) NOT NULL,
  PRIMARY KEY (`product_codes`,`feature`),
  CONSTRAINT `sentiment_productCodes_FK` FOREIGN KEY (`product_codes`) REFERENCES `product-codes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of sentiment
-- ----------------------------

-- ----------------------------
-- Table structure for `user-account`
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

-- ----------------------------
-- Table structure for `web-based-seller`
-- ----------------------------
DROP TABLE IF EXISTS `web-based-seller`;
CREATE TABLE `web-based-seller` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `logo` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `ID_FK` FOREIGN KEY (`id`) REFERENCES `seller` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of web-based-seller
-- ----------------------------
