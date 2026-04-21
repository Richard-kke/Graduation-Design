package com.qf.common.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class FileUtils {
	
	//创建文件夹 一个月一个文件夹
	public static File createDir(String dir) {
		//子文件名称：201805 201806
		String month=new SimpleDateFormat("yyyyMM").format(new Date());
		File dir1=new File(new File(dir).getParent(),"fmwimages");
		File dir2=new File(dir1,month) ;
		if(!dir2.exists()) {
			dir2.mkdirs();
		}
		return dir2;
	}
	//创建唯一名称 
	public static String createFileName(String fn) {
		if(fn.length()>30) {
			fn=fn.substring(fn.length()-30);
		}
		return UUID.randomUUID().toString()+"_"+fn;
	}
	
	// 写入文件内容
	public static void writeFile(String filePath, String content, boolean append) {
		java.io.BufferedWriter writer = null;
		try {
			// 创建文件对象
			java.io.File file = new java.io.File(filePath);
			
			// 确保父目录存在
			java.io.File parentDir = file.getParentFile();
			if (parentDir != null && !parentDir.exists()) {
				parentDir.mkdirs();
			}
			
			// 创建写入流
			writer = new java.io.BufferedWriter(new java.io.FileWriter(file, append));
			
			// 写入内容
			writer.write(content);
			writer.flush();
		} catch (java.io.IOException e) {
			e.printStackTrace();
			System.err.println("写入文件失败：" + e.getMessage());
		} finally {
			// 关闭流
			if (writer != null) {
				try {
					writer.close();
				} catch (java.io.IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
