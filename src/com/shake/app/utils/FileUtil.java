package com.shake.app.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.List;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;


public class FileUtil {

	/**
     * 读取文件
     */
	public static String readFile(String filename) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(filename));
			String tmp = null;
			StringBuilder resultStr = new StringBuilder();
			while ((tmp = reader.readLine()) != null) {
				resultStr.append(tmp);
			}
			return resultStr.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}
		
	
	/**
	 * 写入文件
	 * @param strFileName 文件名
	 * @param ins 流
	 */
	public static void writeToFile(String strFileName, InputStream ins) {
		try {
			File file = new File(strFileName);

			FileOutputStream fouts = new FileOutputStream(file);
			int len;
			int maxSize = 1024 * 1024;
			byte buf[] = new byte[maxSize];
			while ((len = ins.read(buf, 0, maxSize)) != -1) {
				fouts.write(buf, 0, len);
				fouts.flush();
			}

			fouts.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static final int IO_BUFFER_SIZE = 1024;
    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] b = new byte[IO_BUFFER_SIZE];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }
	
	/**
	 * 写入文件
	 * @param strFileName 文件名
	 * @param bytes bytes
	 */
	public static boolean writeToFile(String strFileName, byte[] bytes) {
		try {
			File file = new File(strFileName);

			FileOutputStream fouts = new FileOutputStream(file);
			fouts.write(bytes, 0, bytes.length);
			fouts.flush();
			fouts.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
     * Prints some data to a file using a BufferedWriter
     */
	public static boolean writeToFile(String filename, String data,boolean append) {
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(filename,append));
			bufferedWriter.write(data);
			return true;
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (bufferedWriter != null) {
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * 新建目录
	 * @param folderPath String 如 c:/fqf
	 * @return boolean
	 */
	public static void newFolder(String folderPath) {
		try {
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.mkdir();
			}
		} catch (Exception e) {
			System.out.println("新建目录操作出错");
			e.printStackTrace();
		}
	}

	/**
	 * 新建文件
	 * @param filePathAndName String 文件路径及名称 如c:/fqf.txt
	 * @param fileContent String 文件内容
	 * @return boolean
	 */
	public static void newFile(String filePathAndName, String fileContent) {

		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.createNewFile();
			}
			FileWriter resultFile = new FileWriter(myFilePath);
			PrintWriter myFile = new PrintWriter(resultFile);
			String strContent = fileContent;
			myFile.println(strContent);
			resultFile.close();

		} catch (Exception e) {
			System.out.println("新建目录操作出错");
			e.printStackTrace();
		}
	}

	/**
	 * 新建文件
	 * @param fileFullName
	 * @return
	 * @throws IOException
	 */
	public static void newFile(String fileFullName) throws IOException {
		int pos = StringUtil.getPathLastIndex(fileFullName);
		if (pos > 0)
		{
			String strFolder = fileFullName.substring(0, pos);
			File file = new File(strFolder);
			if (!file.isDirectory())
			{
				file.mkdirs();
			}
		}
		File file = new File(fileFullName);
		if (file.exists())
		{
			file.delete();
		}
		file.createNewFile();
	}
	
	/**
	 * 删除文件
	 * @param filePathAndName  String 文件路径及名称 如c:/fqf.txt
	 * @param fileContent String
	 * @return boolean
	 */
	public static void delFile(String filePathAndName) {
		try {
			java.io.File myDelFile = new java.io.File(filePathAndName);
			myDelFile.delete();
		} catch (Exception e) {
			System.out.println("删除文件操作出错");
			e.printStackTrace();
		}
	}

	/**
	 * 删除文件夹
	 * 
	 * @param filePathAndName
	 *            String 文件夹路径及名称 如c:/fqf
	 * @param fileContent
	 *            String
	 * @return boolean
	 */
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			java.io.File myFilePath = new java.io.File(folderPath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			System.out.println("删除文件夹操作出错");
			e.printStackTrace();
		}
	}

	/**
	 * 删除文件夹里面的所有文件
	 * @param path String 文件夹路径 如 c:/fqf
	 */
	public static void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
			}
		}
	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath String 原文件路径 如：c:/fqf.txt
	 * @param newPath String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 */
	public static void copyFile(String oldPath, String newPath) {
		try {
//			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];

				while ((byteread = inStream.read(buffer)) != -1) {
//					bytesum += byteread; // 字节数 文件大小
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				fs.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();
		}
	}
	
	/**
	 * 复制单个文件，IO存盘时用，added by chenjieyu
	 * 
	 * @param in InputStream 原文件流，template是放在assets中，找不到完整路径，要用InputStream
	 * @param destFile 复制后路径 如：c:/fqf.txt
	 */
	public static void copyFile(InputStream in, String destFile)
	{
		if(in == null || destFile == null)
			return;
		 
		File outFile = new File(destFile);
		if (outFile.exists())
		{
			outFile.delete();
		}
		try
		{
			outFile.createNewFile();
			OutputStream out = new FileOutputStream(outFile);
			byte[] buf = new byte[IO_BUFFER_SIZE];
			int len;
			while ((len = in.read(buf)) > 0)
			{
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
		catch (IOException e)
		{
			System.out.println("复制文件流操作出错");
			e.printStackTrace();
		}
	}

	/**
	 * 复制整个文件夹内容
	 * 
	 * @param oldPath  String 原文件路径 如：c:/fqf
	 * @param newPath String 复制后路径 如：f:/fqf/ff
	 * @return boolean
	 */
	public static void copyFolder(String oldPath, String newPath) {

		try {
			(new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}

				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath
							+ "/" + (temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {// 如果是子文件夹
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		} catch (Exception e) {
			System.out.println("复制整个文件夹内容操作出错");
			e.printStackTrace();
		}
	}

	/**
	 * 移动文件到指定目录
	 * @param oldPath String 如：c:/fqf.txt
	 * @param newPath String 如：d:/fqf.txt
	 */
	public static void moveFile(String oldPath, String newPath) {
		copyFile(oldPath, newPath);
		delFile(oldPath);
	}

	/**
	 * 移动文件到指定目录
	 * @param oldPath String 如：c:/fqf.txt
	 * @param newPath String 如：d:/fqf.txt
	 */
	public static void moveFolder(String oldPath, String newPath) {
		copyFolder(oldPath, newPath);
		delFolder(oldPath);
	}
	
	 /**
	  * 从文件路径得到文件名。
	  * @param filePath 文件的路径，可以是相对路径也可以是绝对路径
	  * @return 对应的文件名
	  */
	public static String getFileName(String filePath) {
	    File file = new File(filePath);
	    return file.getName();
	}

	/**
	 * 将文件名中的类型部分去掉。
	 * @param filename 文件名
	 * @return 去掉类型部分的结果
	 * @since  0.5
	 */
	public static String trimType(String filename) {
		int index = filename.lastIndexOf(".");
	    if (index != -1) {
	      return filename.substring(0, index);
	    }
	    else {
	      return filename;
	    }
	}
	
	/**
	 * 由mineType得到文件名中的类型
	 * 
	 * @param mimeType
	 * @return 文件名中的类型
	 */
	public static String getFileTypeFromMimeType(String mineType) 
	{
		if ("text/plain".equals(mineType))
		{
			return ".txt";
		}
		else if ("text/html".equals(mineType) || "application/xhtml+xml".equals(mineType))
		{
			return ".html";
		}
		else if ("text/x-mht".equals(mineType))
		{
			return ".mht";
		}
		else if ("application/rtf".equals(mineType))
		{
			return ".rtf";
		}
		else if ("application/vnd.ms-works".equals(mineType) || "application/kswps".equals(mineType))
		{
			return ".wps";
		}
		else if ("application/kset".equals(mineType))
		{
			return ".et";
		}
		else if ("application/ksdps".equals(mineType))
		{
			return ".dps";
		}
		else if("application/msexcel".equals(mineType) || "application/vnd.ms-excel".equals(mineType))
		{
			return ".xls";
		}
		else if("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(mineType))
		{
			return ".xlsx";
		}
		else if("application/mspowerpoint".equals(mineType) || "application/vnd.ms-powerpoint".equals(mineType))
		{
			return ".ppt";
		}
		else if("application/vnd.openxmlformats-officedocument.presentationml.presentation".equals(mineType))
		{
			return ".pptx";
		}
		else if("application/msword".equals(mineType) || "application/vnd.ms-word".equals(mineType))
		{
			return ".doc";
		}
		else if ("application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(mineType))
		{
			return ".docx";
		}
		return ".file";
	}
	
	public static String getAutoNewFileName(String fileName)
	{
		return getAutoNewFileName(fileName, true);
	}
	/**
	 * 得到合适的自动命名
	 * @param fileName 源全路径文件名
	 * @return 自动命名后的文件名称
	 */
	public static String getAutoNewFileName(String fileName, boolean hasExtension)
	{
		if (!new File(fileName).exists())
		{
			return fileName;
		}
		
		String newName = null;
		int[] num = new int[1];
		num[0] = 1;
		if (!hasExtension) 
		{
			String originName = getOriginFileName(fileName, num);
			while(true)
			{
				newName = String.format("%s(%d)", originName, num[0]);
				if (!new File(newName).exists())
				{
					break;
				}
				num[0]++;
			}
		}
		else
		{
			String ext = StringUtil.pathExtension(fileName);
			fileName = StringUtil.stringByDeletingPathExtension(fileName);	//删去后缀
			String originName = getOriginFileName(fileName, num);
			while(true)
			{
				newName = String.format("%s(%d).%s", originName, num[0], ext);
				if (!new File(newName).exists())
				{
					break;
				}
				num[0]++;
			}
		}
		return newName;
	}
	
	/**
	 * 得到原始文件名，不含自动重命名时附加的数字
	 * @param fileName	不含后缀的文件名
	 * @param num		长度为1的数组，用来返回附加数字
	 * @return	返回原始文件名
	 */
	private static String getOriginFileName(String fileName, int[] num)
	{
		int len = fileName.length();
		char endChar = fileName.charAt(len - 1);
		if (endChar != ')')
		{
			return fileName;
		}
		
		int i = 0;
		for (i = len -2; i >= 0; i--)
		{
			char c = fileName.charAt(i);
			if (c == '(')
			{
				break;
			}
			if (!Character.isDigit(c))
			{
				return fileName;
			}
		}
		if (i == len - 2)
		{
			return fileName;
		}
		
		String numString = fileName.substring(i + 1, fileName.length() - 1);
		num[0] = Integer.valueOf(numString);
		
		return fileName.substring(0, i);
	}
	
	/**  
     * 递归查找文件  
     * @param baseDirName  查找的文件夹路径  
     * @param targetFileName  需要查找的文件名  (通配符)
     * @param fileList  查找到的文件集合  
     */  
    public static void findFiles(String baseDirName, String targetFileName, List<String> fileList) {   
        /**  
         * 算法简述：  
         * 从某个给定的需查找的文件夹出发，搜索该文件夹的所有子文件夹及文件，  
         * 若为文件，则进行匹配，匹配成功则加入结果集，若为子文件夹，则进队列。  
         * 队列不空，重复上述操作，队列为空，程序结束，返回结果。  
         */  
        String tempName = null;   
        //判断目录是否存在   
        File baseDir = new File(baseDirName);   
        if (!baseDir.exists() || !baseDir.isDirectory()){   
            return;
        } else {   
            String[] filelist = baseDir.list();   
            for (int i = 0; i < filelist.length; i++) {   
                File readfile = new File(baseDirName + File.separator + filelist[i]);   
                if(!readfile.isDirectory()) {
                    tempName =  readfile.getName();    
                    if (wildcardMatch(targetFileName, tempName)) {   
                        //匹配成功，将文件名添加到结果集   
                        fileList.add(readfile.getAbsolutePath());    
                    }   
                } else if(readfile.isDirectory()){   
                    findFiles(baseDirName + File.separator + filelist[i],targetFileName, fileList);   
                }
            }
        }   
    }   
       
    /**  
     * 通配符匹配  
     * @param pattern    通配符模式  
     * @param str    待匹配的字符串  
     * @return    匹配成功则返回true，否则返回false  
     */  
    private static boolean wildcardMatch(String pattern, String str) {   
        int patternLength = pattern.length();   
        int strLength = str.length();   
        int strIndex = 0;   
        char ch;   
        for (int patternIndex = 0; patternIndex < patternLength; patternIndex++) {   
            ch = pattern.charAt(patternIndex);   
            if (ch == '*') {   
                //通配符星号*表示可以匹配任意多个字符   
                while (strIndex < strLength) {   
                    if (wildcardMatch(pattern.substring(patternIndex + 1),   
                            str.substring(strIndex))) {   
                        return true;   
                    }   
                    strIndex++;   
                }   
            } else if (ch == '?') {   
                //通配符问号?表示匹配任意一个字符   
                strIndex++;   
                if (strIndex > strLength) {   
                    //表示str中已经没有字符匹配?了。   
                    return false;   
                }   
            } else {   
                if ((strIndex >= strLength) || (ch != str.charAt(strIndex))) {   
                    return false;   
                }   
                strIndex++;   
            }   
        }   
        return (strIndex == strLength);   
    }
    
    public static void sendMail(Activity activity, Uri uri)
    {
    	sendMail(activity, uri, -1);
    }
    
	public static void sendMail(Activity activity, Uri uri, int requestCode)
	{
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });
		intent.putExtra(Intent.EXTRA_SUBJECT, "");
		intent.putExtra(Intent.EXTRA_TEXT, "");
		intent.putExtra(Intent.EXTRA_STREAM, uri);
		try {
//			activity.startActivityForResult(Intent.createChooser(intent, activity.getString(R.string.chooseEmail)), requestCode);
		} catch (ActivityNotFoundException e) {
		}
	}
	
	public static boolean isFileCanReadAndWrite(String filePath)
	{
		if (null != filePath && filePath.length() > 0) {
			File f = new File(filePath);
			if (null != f && f.exists()) {
				return f.canRead() && f.canWrite();
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @return 文件的大小，带单位(MB、KB等)
	 */
	public static String fileLength(String filePath) {
        return fileLength(new File(filePath).length());
	}
	
	/**
	 * 
	 * @return 文件的大小，带单位(MB、KB等)
	 */
	public static String fileLength(long length) {
	    String lenStr = null;
	    DecimalFormat formater = new DecimalFormat("#0.##");
	    if (length < 1024) {
	        lenStr = formater.format(length) + " Byte";
	    }
	    else if (length < 1024 * 1024) {
	        lenStr = formater.format(length / 1024.0f) + " KB";
	    }
	    else if (length < 1024 * 1024 * 1024) {
	        lenStr = formater.format(length / (1024 * 1024)) + " MB";
	    }
	    else {
	        lenStr = formater.format(length / (1024 * 1024 * 1024)) + " GB";
	    }
	    return lenStr;
	}
}
