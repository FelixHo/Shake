package com.shake.app.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Comparator;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.util.EncodingUtils;



public class StringUtil {
	private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();

	/**
	 * 判断String数组包含某一string
	 * @param strs
	 * @param str
	 * @return
	 */
	public static boolean isHave(String[] strs, String str) {
		for (int i = 0; i < strs.length; i++) {
			if (strs[i].equals(str)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断str以string数组某一项开头
	 * @param strs
	 * @param str
	 * @return
	 */
	public static boolean isStartWith(String[] strs, String str) {
		if (str == null || strs == null) {
			return false;
		}
		for (int i = 0; i < strs.length; i++) {
			if (str.startsWith(strs[i])) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 解析一个带 token 分隔符的字符串，这个方法的效率比直接调用String的split()方法快大约1倍。
	 * 
	 * @param tokenedStr
	 * @param token
	 * @return String[]
	 */
	public static String[] splitString(String tokenedStr, String token) {
		String[] ids = null;
		if (tokenedStr != null) {
			StringTokenizer st = new StringTokenizer(tokenedStr, token);
			final int arraySize = st.countTokens();
			if (arraySize > 0) {
				ids = new String[arraySize];
				int counter = 0;
				while (st.hasMoreTokens()) {
					ids[counter++] = st.nextToken();
				}
			}
		}
		return ids;
	}

	/**
	 * 把字符串数组组合成一个以指定分隔符分隔的字符串，并追加到给定的<code>StringBuilder</code>
	 * 
	 * @param strs
	 *            字符串数组
	 * @param seperator
	 *            分隔符
	 * @return
	 */
	public static void mergeString(String[] strs, String seperator,
			StringBuilder sb) {
		if (strs == null || strs.length == 0)
			return;
		for (int i = 0; i < strs.length; i++) {
			if (i != 0) {
				sb.append(seperator);
			}
			sb.append(strs[i]);
		}
	}
	
	public static String stringWithFormat(String str, Object... args) {
		str = String.format(str, args);
		return str;
	}

	/**
	 * 把字符串数组组合成一个以指定分隔符分隔的字符串。
	 * 
	 * @param strs
	 *            字符串数组
	 * @param seperator
	 *            分隔符
	 * @return
	 */
	public static String mergeString(String[] strs, String seperator) {
		StringBuilder sb = new StringBuilder();
		mergeString(strs, seperator, sb);
		return sb.toString();
	}
	
	/**
	 * MessageEncrypt 文件后缀名小写
	 * 
	 * @param strFileName
	 * @return
	 */
	public static String lowerCaseExtension(String strFileName) {
		// 去掉文件后缀
		String strFileNameBody = stringByDeletingPathExtension(strFileName);
		// 得到文件后缀
		String strExt = pathExtension(strFileName).toLowerCase();
		strFileName = strFileNameBody + "." + strExt;
		return strFileName;
	}

	/**
	 * 得到文件的类型。 实际上就是得到文件名中最后一个“.”后面的部分。
	 * 
	 * @param fileName
	 *            文件名
	 * @return 文件名中的类型部分
	 */
	public static String pathExtension(String fileName) {
		int point = fileName.lastIndexOf('.');
		int length = fileName.length();
		if (point == -1 || point == length - 1) {
			return "";
		} else {
			return fileName.substring(point + 1, length);
		}
	}

	/**
	 * 将文件名中的类型部分去掉。不含点
	 * 
	 * @param filename
	 *            文件名
	 * @return 去掉类型部分的结果
	 */
	public static String stringByDeletingPathExtension(String filename) {
		int index = filename.lastIndexOf(".");
		if (index != -1) {
			return filename.substring(0, index);
		} else {
			return filename;
		}
	}
	
	/**
	 * strFolder/filename
	 * @param strFolder
	 * @param filename
	 * @return
	 */
	public static String stringByAppendingPathComponent(String strFolder, String filename) {
		return strFolder + File.separator + filename;
	}
	
	/**
	 * trimExt.ext
	 * @param trimExt
	 * @param ext
	 * @return
	 */
	public static String stringByAppendingPathExtension(String trimExt, String ext) {
		return trimExt + "." + ext;
	}
	
   /**
    * 得到文件的名字部分。
    * 实际上就是路径中的最后一个路径分隔符后的部分。
    * @param fileName 文件名
    * @return 文件名中的名字部分
    * @since   0.5
    */
   public static String getNamePart(String fileName) {
     int point = getPathLastIndex(fileName);
     int length = fileName.length();
     if (point == -1) {
       return fileName;
     }
     else if (point == length - 1) {
       int secondPoint = getPathLastIndex(fileName, point - 1);
       if (secondPoint == -1) {
         if (length == 1) {
           return fileName;
         }
         else {
           return fileName.substring(0, point);
         }
       }
       else {
         return fileName.substring(secondPoint + 1, point);
       }
     }
     else {
       return fileName.substring(point + 1);
     }
   }
   
   /**
    * 得到除去文件名部分的路径
    * 实际上就是路径中的最后一个路径分隔符前的部分。
    * @param fileName
    * @return
    */
   public static String getNameDelLastPath(String fileName)
   {
	     int point = getPathLastIndex(fileName);
	     if (point == -1) {
	       return fileName;
	     }
	     else {
	       return fileName.substring(0, point);
	     }
   }
   
   /**
    * 得到路径分隔符在文件路径中最后出现的位置。
    * 对于DOS或者UNIX风格的分隔符都可以。
    * @param fileName 文件路径
    * @return 路径分隔符在路径中最后出现的位置，没有出现时返回-1。
    * @since   0.5
    */
   public static int getPathLastIndex(String fileName) {
     int point = fileName.lastIndexOf('/');
     if (point == -1) {
       point = fileName.lastIndexOf('\\');
     }
     return point;
   }

   /**
    * 得到路径分隔符在文件路径中指定位置前最后出现的位置。
    * 对于DOS或者UNIX风格的分隔符都可以。
    * @param fileName 文件路径
    * @param fromIndex 开始查找的位置
    * @return 路径分隔符在路径中指定位置前最后出现的位置，没有出现时返回-1。
    * @since   0.5
    */
   public static int getPathLastIndex(String fileName, int fromIndex) {
     int point = fileName.lastIndexOf('/', fromIndex);
     if (point == -1) {
       point = fileName.lastIndexOf('\\', fromIndex);
     }
     return point;
   }
   
   public static final String toString(byte[] ba)
   {
	   return toString(ba, 0, ba.length);
   }
   
   public static final String toString(byte[] ba, int offset, int length) 
   {
	      char[] buf = new char[length * 2];
	      for (int i = 0, j = 0, k; i < length; ) 
	      {
	         k = ba[offset + i++];
	         buf[j++] = HEX_DIGITS[(k >>> 4) & 0x0F];
	         buf[j++] = HEX_DIGITS[ k        & 0x0F];
	      }
	      return new String(buf);
   }

	/**
	 * 过滤特殊字符
	 * @param str
	 * @return
	 */
	public static String removeInvalidChar(String str) 
	{
		// 清除掉所有特殊字符
		String regEx = "[`~!@#$%^&*()+=|{}':;＇,\\[\\].<>/?～！＠＃￥％……＆＊（）——＋｜｛｝【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
	
	public static class StringComparator implements Comparator<String>
	{
		@Override
		public int compare(String object1, String object2) {
			return object1.compareTo(object2);
		}
	}
	
    /**
     * 生成size位随机数
     * @param size
     * @return
     */
	public static String createRandomKey(int size) {
        char[] c = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };
        Random random = new Random(); // 初始化随机数产生器
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < size; i++) {
            sb.append(c[Math.abs(random.nextInt()) % c.length]);
        }
        return sb.toString();
    }
	
	public static String getTempHTML(String htmlContent){
	   String htmlString = String.format("<html><meta charset=\"utf-8\"><head><link rel=\"stylesheet\" href=\"styles.css\"></head>%1$s</html>", htmlContent);
	    return htmlString;
	}
	
	public static String Read(String fileName) {
		String res = "";
		try {
			FileInputStream fin = new FileInputStream(fileName);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

}
