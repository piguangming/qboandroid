package com.jd.qbo;

/**
包头:0XFF（1个字节）
命令:0（1个字节）
长度：1个字节
数据：
	避障角度：0-360 （2个字节）
	避障的障碍物距离：cm（1个字节）
	线速度：cm/s（1个字节）
	角速度：弧度/s（4个字节）
	位移x：cm（4个字节）
	位移y：cm（4个字节）
	角度：0-360（2个字节）
	陀螺仪（x、y、z）：弧度/s（12字节）
	加速度（x、y、z）：cm/s2 （12字节）
	传感器编号：1-10[ 1*n字节（n=10，6超声波、4红外）]
	障碍物距离:cm[ 1*n字节（n=10，6超声波、4红外）]
	状态告警:2字节
哈希值：1个字节
包尾：0XFE(1个字节)
 * @author piguangming
 *
 */
public class ParseProtocol {
	/** sample package **/
	private String s = "ff00112200000000000000000000000000000000a7feff00112200000000000000000000000000000000a7fe";
		
	/**
	 * 解析命令类型
	 * @param str - 包字符串
	 * @return 命令（1个字节）
	 */
	public static String parseCmd(String str) {
		return str.substring(0, 2);
	}
	
	/**
	 * 解析包长
	 * @param str - 包字符串
	 * @return 长度（1个字节）
	 */
	public static String parseLen(String str) {
		return str.substring(2, 4);
	}

	/**
	 * 解析避障角度
	 * @param str - 包字符串
	 * @return 避障角度：0-360 （2个字节）
	 */
	public static String parseAngle(String str) {
		return str.substring(4, 8);
	}
	
	/**
	 * 解析避障的障碍物距离
	 * @param str - 包字符串
	 * @return 避障的障碍物距离：cm（1个字节）
	 */
	public static String parseDistance(String str) {
		return str.substring(8, 10);
	}
	
	/**
	 * 解析线速度
	 * @param str - 包字符串
	 * @return 线速度：cm/s（1个字节）
	 */
	public static String parseLineSpeed(String str) {
		return str.substring(10, 12);
	}
	
	/**
	 * 解析角速度
	 * @param str - 包字符串
	 * @return 角速度：弧度/s（4个字节）
	 */
	public static String parseAngleSpeed(String str) {
		return str.substring(12, 20);
	}
	
	/**
	 * 解析位移x
	 * @param str - 包字符串
	 * @return 位移x：cm（4个字节）
	 */
	public static String parseX(String str) {
		return str.substring(20, 28);
	}
	
	/**
	 * 解析位移y
	 * @param str - 包字符串
	 * @return 位移y：cm（4个字节）
	 */
	public static String parseY(String str) {
		return str.substring(28, 36);
	}
	
	/**
	 * 解析角度
	 * @param str - 包字符串
	 * @return 角度：0-360（2个字节）
	 */
	public static String parseDegree(String str) {
		return str.substring(36, 40);
	}
	
	/**
	 * 解析陀螺仪 X
	 * @param str - 包字符串
	 * @return 陀螺仪（x、y、z）：弧度/s（12字节）
	 */
	public static String parseCompassX(String str) {
		return str.substring(40, 48);
	}
	
	/**
	 * 解析陀螺仪 Y
	 * @param str - 包字符串
	 * @return 陀螺仪（x、y、z）：弧度/s（12字节）
	 */
	public static String parseCompassY(String str) {
		return str.substring(48, 56);
	}
	
	/**
	 * 解析陀螺仪 Z
	 * @param str - 包字符串
	 * @return 陀螺仪（x、y、z）：弧度/s（12字节）
	 */
	public static String parseCompassZ(String str) {
		return str.substring(56, 64);
	}
	
	/**
	 * 解析加速度X
	 * @param str - 包字符串
	 * @return 加速度（x、y、z）：cm/s2 （12字节）
	 */
	public static String parseAccelX(String str) {
		return str.substring(64, 72);
	}
	
	/**
	 * 解析加速度Y
	 * @param str - 包字符串
	 * @return 加速度（x、y、z）：cm/s2 （12字节）
	 */
	public static String parseAccelY(String str) {
		return str.substring(72, 80);
	}
	
	/**
	 * 解析加速度Z
	 * @param str - 包字符串
	 * @return 加速度（x、y、z）：cm/s2 （12字节）
	 */
	public static String parseAccelZ(String str) {
		return str.substring(80, 88);
	}
	
	/**
	 * 解析超声波 1
	 * @param str - 包字符串
	 * @return 传感器编号：1-10[ 1*n字节（n=10，6超声波、4红外）]
	 */
	public static String parseChaoSheng1(String str) {
		return str.substring(88, 90);
	}
	
	/**
	 * 解析超声波 2
	 * @param str - 包字符串
	 * @return 传感器编号：1-10[ 1*n字节（n=10，6超声波、4红外）]
	 */
	public static String parseChaoSheng2(String str) {
		return str.substring(90, 92);
	}
	
	/**
	 * 解析超声波 3
	 * @param str - 包字符串
	 * @return 传感器编号：1-10[ 1*n字节（n=10，6超声波、4红外）]
	 */
	public static String parseChaoSheng3(String str) {
		return str.substring(92, 94);
	}
	
	/**
	 * 解析超声波 4
	 * @param str - 包字符串
	 * @return 传感器编号：1-10[ 1*n字节（n=10，6超声波、4红外）]
	 */
	public static String parseChaoSheng4(String str) {
		return str.substring(94, 96);
	}
	
	/**
	 * 解析超声波 5
	 * @param str - 包字符串
	 * @return 传感器编号：1-10[ 1*n字节（n=10，6超声波、4红外）]
	 */
	public static String parseChaoSheng5(String str) {
		return str.substring(96, 98);
	}
	
	/**
	 * 解析超声波 6
	 * @param str - 包字符串
	 * @return 传感器编号：1-10[ 1*n字节（n=10，6超声波、4红外）]
	 */
	public static String parseChaoSheng6(String str) {
		return str.substring(98, 100);
	}
	
	/**
	 * 解析红外 1
	 * @param str - 包字符串
	 * @return 传感器编号：1-10[ 1*n字节（n=10，6超声波、4红外）]
	 */
	public static String parseHongWai1(String str) {
		return str.substring(100, 102);
	}
	
	/**
	 * 解析红外 2
	 * @param str - 包字符串
	 * @return 传感器编号：1-10[ 1*n字节（n=10，6超声波、4红外）]
	 */
	public static String parseHongWai2(String str) {
		return str.substring(102, 104);
	}
	
	/**
	 * 解析红外 3
	 * @param str - 包字符串
	 * @return 传感器编号：1-10[ 1*n字节（n=10，6超声波、4红外）]
	 */
	public static String parseHongWai3(String str) {
		return str.substring(104, 106);
	}
	
	/**
	 * 解析红外4
	 * @param str - 包字符串
	 * @return 传感器编号：1-10[ 1*n字节（n=10，6超声波、4红外）]
	 */
	public static String parseHongWai4(String str) {
		return str.substring(106, 108);
	}
	
	/**
	 * 解析超声波 1
	 * @param str - 包字符串
	 * @return 障碍物距离:cm[ 1*n字节（n=10，6超声波、4红外）]
	 */
	public static String parseChaoShengDistance1(String str) {
		return str.substring(108, 110);
	}
	
	/**
	 * 解析超声波 2
	 * @param str - 包字符串
	 * @return 障碍物距离:cm[ 1*n字节（n=10，6超声波、4红外）]
	 */
	public static String parseChaoShengDistance2(String str) {
		return str.substring(110, 112);
	}
	
	/**
	 * 解析超声波 3
	 * @param str - 包字符串
	 * @return 障碍物距离:cm[ 1*n字节（n=10，6超声波、4红外）]
	 */
	public static String parseChaoShengDistance3(String str) {
		return str.substring(112, 114);
	}
	
	/**
	 * 解析超声波 4
	 * @param str - 包字符串
	 * @return 障碍物距离:cm[ 1*n字节（n=10，6超声波、4红外）]
	 */
	public static String parseChaoShengDistance4(String str) {
		return str.substring(114, 116);
	}
	
	/**
	 * 解析超声波5
	 * @param str - 包字符串
	 * @return 障碍物距离:cm[ 1*n字节（n=10，6超声波、4红外）]
	 */
	public static String parseChaoShengDistance5(String str) {
		return str.substring(116, 118);
	}
	
	/**
	 * 解析超声波 6
	 * @param str - 包字符串
	 * @return 障碍物距离:cm[ 1*n字节（n=10，6超声波、4红外）]
	 */
	public static String parseChaoShengDistance6(String str) {
		return str.substring(118, 120);
	}
	
	/**
	 * 解析红外 1
	 * @param str - 包字符串
	 * @return 障碍物距离:cm[ 1*n字节（n=10，6超声波、4红外）]
	 */
	public static String parseHongWaiDistance1(String str) {
		return str.substring(120, 122);
	}
	
	/**
	 * 解析红外2
	 * @param str - 包字符串
	 * @return 障碍物距离:cm[ 1*n字节（n=10，6超声波、4红外）]
	 */
	public static String parseHongWaiDistance2(String str) {
		return str.substring(122, 124);
	}
	
	/**
	 * 解析红外 3
	 * @param str - 包字符串
	 * @return 障碍物距离:cm[ 1*n字节（n=10，6超声波、4红外）]
	 */
	public static String parseHongWaiDistance3(String str) {
		return str.substring(124, 126);
	}
	
	/**
	 * 解析红外 4
	 * @param str - 包字符串
	 * @return 障碍物距离:cm[ 1*n字节（n=10，6超声波、4红外）]
	 */
	public static String parseHongWaiDistance4(String str) {
		return str.substring(126, 128);
	}
	
	/**
	 * 解析告警
	 * @param str - 包字符串
	 * @return 状态告警:2字节
	 */
	public static String parseZhuangTaiGaoJing(String str) {
		return str.substring(128, 132);
	}
	
	/**
	 * 解析哈希
	 * @param str - 包字符串
	 * @return 哈希值：1个字节
	 */
	public static String parseHash(String str) {
		return str.substring(132, 134);
	}
	
}
