/*
 Navicat MySQL Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50727
 Source Host           : localhost:3306
 Source Schema         : roses_order

 Target Server Type    : MySQL
 Target Server Version : 50727
 File Encoding         : 65001

 Date: 04/09/2019 22:42:03
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for rocketmq_transaction_log
-- ----------------------------
DROP TABLE IF EXISTS `rocketmq_transaction_log`;
CREATE TABLE `rocketmq_transaction_log`  (
  `id` bigint(20) NOT NULL COMMENT '主键id',
  `transaction_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '事务id',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of rocketmq_transaction_log
-- ----------------------------
INSERT INTO `rocketmq_transaction_log` VALUES (1169258405755531265, '64671bb4-a2a2-4d4d-8277-177e151c838b', '存储本地事务');
INSERT INTO `rocketmq_transaction_log` VALUES (1169259223137300482, 'cb3f2f2a-c56d-4ff3-b3c9-3d7e13e8bb5e', '存储本地事务');

SET FOREIGN_KEY_CHECKS = 1;
