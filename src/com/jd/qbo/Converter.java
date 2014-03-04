package com.jd.qbo;

/**
 * 转换类
 * @author piguangming
 *
 */
public class Converter {
   /**
    * 直接数组转换成16进制字符串
    * @param src 字节数组
    * @return 16进制字符串
    */
	public static String bytesToHexString(byte[] src){
       StringBuilder stringBuilder = new StringBuilder("");
       if (src == null || src.length <= 0) {
           return null;
       }
       for (int i = 0; i < src.length; i++) {
           int v = src[i] & 0xFF;
           String hv = Integer.toHexString(v);
           if (hv.length() < 2) {
               stringBuilder.append(0);
           }
           stringBuilder.append(hv);
       }
       return stringBuilder.toString();
   }
   /**
    * 直接16进制字符串转换成数组
    * @param hexString 16进制字符串
    * @return 字节数组
    */
   public static byte[] hexStringToBytes(String hexString) {
       if (hexString == null || hexString.equals("")) {
           return null;
       }
       hexString = hexString.toUpperCase();
       int length = hexString.length() / 2;
       char[] hexChars = hexString.toCharArray();
       byte[] d = new byte[length];
       for (int i = 0; i < length; i++) {
           int pos = i * 2;
           d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
       }
       return d;
   }
   
   /**
    * 16进制字符串转换成整形
    * @param str 16进制字符串
    * @return 整形
    */
   public static int hexStringToInt(String str){  
       return Integer.parseInt(str, 16);/*16 为要转化的数的基数，这里表示str是16进制表示的字符串*/  
   }  
   
   /**
    * 16进制字符串转换成浮点型
    * @param str
    * @return
    */
   public static float hexStringToFloat(String str){  
       return Float.parseFloat(str);  
   }  
   
   /**
    * 字符转换成字节
    * @param c char 字符
    * @return byte 字节
    */
    private static byte charToByte(char c) {
       return (byte) "0123456789ABCDEF".indexOf(c);
   }
    
    // 
    /**
     * 整形转字节数组：将iSource转为长度为iArrayLen的byte数组，字节数组的低位是整型的低字节位 
     * @param iSource 整形
     * @param iArrayLen 字节数组长度
     * @return 字节数组
     */
    public static byte[] toByteArray(int iSource, int iArrayLen) { 
        byte[] bLocalArr = new byte[iArrayLen]; 
        for ( int i = 0; (i < 4) && (i < iArrayLen); i++) { 
            bLocalArr[i] = (byte)( iSource>>8*i & 0xFF ); 
           
        } 
        return bLocalArr; 
    }    

    /**
     * 字节数组转整形：将byte数组bRefArr转为一个整数,字节数组的低位是整型的低字节位
     * @param bRefArr 字节数组
     * @return 整数
     */
    public static int toInt(byte[] bRefArr) { 
        int iOutcome = 0; 
        byte bLoop; 
         
        for ( int i =0; i<4 ; i++) { 
            bLoop = bRefArr[i]; 
            iOutcome+= (bLoop & 0xFF) << (8 * i); 
           
        }   
         
        return iOutcome; 
    }   
    
    /**
     * 将字符串转换成整形
     * @param str 字符串
     * @return 整形
     */
    public static int strToIntNoException(String str) {
    	int i = 0;
    	try {
    		i = new Integer(str).intValue();
    	} catch(Exception e) {
//    		e.printStackTrace();
    	}
    	
    	return i;
    }
}
