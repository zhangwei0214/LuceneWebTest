package me.ben.lucene.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;

public class FileUtils {
	/**
	 * 未完成: 参考 http://blog.csdn.net/zhongweijian/article/details/8453293/
	 * 
	 * @param path
	 * @return
	 */
	public static List<File> getFileListRecursive(String path) {
		final List<File> fileList = new ArrayList<File>();

		// 内部类 用于访问fileList成员变量(必须为final)
		class ListTree extends SimpleFileVisitor<Path> {
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
				//迭代访问所有的dir
				//System.out.println("Visited directory: " + dir.toString());
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file,
					BasicFileAttributes attrs) throws IOException {
				// TODO Auto-generated method stub
				//
				//System.out.println("Visited file: " + file.toAbsolutePath());
				if(!fileList.contains(file.toFile())){
					fileList.add(file.toFile());
				}else{
					//System.out.println("文件重复搜索!");
				}
				return super.visitFile(file, attrs);
			}
		}

		// 清空fileList
		//fileList.clear();

		// 使用NIO2提供的API重新获得列表:
		Path listDir = Paths.get(path); // define the starting file
		
		ListTree walk = new ListTree();
		try {
			Files.walkFileTree(listDir, walk);
		} catch (IOException e) {
			System.err.println(e);
		}

		// 遍历的时候跟踪链接
		EnumSet opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
		try {
			Files.walkFileTree(listDir, opts, Integer.MAX_VALUE, walk);
		} catch (IOException e) {
			System.err.println(e);
		}

		// 还不太清楚NIO2的具体逻辑
		// 是在这个函数里面确定对File的operation还是能够返回文件列表?
		return fileList;
	}

	/**
	 * 读取doc文件内容
	 * 
	 * @param file
	 *            想要读取的文件对象
	 * @return 返回文件内容
	 */
	public static String doc2String(File file) {
		String result = "";
		try {
			FileInputStream fis = new FileInputStream(file);
			HWPFDocument doc = new HWPFDocument(fis);
			Range rang = doc.getRange();
			result += rang.text();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 读取txt文件的内容
	 * 
	 * @param file
	 *            想要读取的文件对象
	 * @return 返回文件内容
	 */
	public static String txt2String(File file) {
		String result = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));// 构造一个BufferedReader类来读取文件
			String s = null;
			while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
				result = result + "\n" + s;
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 读取xls文件内容
	 * 
	 * @param file
	 *            想要读取的文件对象
	 * @return 返回文件内容
	 */
	public static String xls2String(File file) {
		String result = "";
		try {
			FileInputStream fis = new FileInputStream(file);
			StringBuilder sb = new StringBuilder();
			jxl.Workbook rwb = Workbook.getWorkbook(fis);
			Sheet[] sheet = rwb.getSheets();
			for (int i = 0; i < sheet.length; i++) {
				Sheet rs = rwb.getSheet(i);
				for (int j = 0; j < rs.getRows(); j++) {
					Cell[] cells = rs.getRow(j);
					for (int k = 0; k < cells.length; k++)
						sb.append(cells[k].getContents());
				}
			}
			fis.close();
			result += sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 目前不带递归搜索 注意Path 是jdk1.7 提供的新特性， jdk1.7对文件系统(nio)和网络socket进行了性能增强 refer
	 * to: http://my.oschina.net/ielts0909/blog/93341
	 * 
	 */
	public static List<File> getFileList(String path) {

		File folder = new File(path);
		if (!folder.isDirectory()) {
			throw new IllegalArgumentException(path + "is not a folder!");
		}
		File[] fileArray = folder.listFiles();
		List<File> fileList = new ArrayList<File>();
		for (File file : fileArray)
			fileList.add(file);
		return fileList;
	}
	/**
	 * 删除文件
	 * 
	 * @param src
	 *            源文件
	 */
	public static void deleteFile(String src) {
		new File(src).delete();
	}
	
	/**
	 * 删除多个文件
	 * 
	 * @param src
	 *            源文件数组
	 */
	public static void deleteFiles(String... src) {
		for (String s : src) {
			FileUtils.deleteFile(s);
		}
	}

	/**
	 * 根据路径删除文件
	 * 
	 * @param dir
	 *            目录
	 * @throws Exception
	 */
	public static void deleteByDir(String dir) throws Exception {
		File d = new File(dir);
		if (!d.isDirectory()) throw new Exception("\"" + dir + "\"" + "不是一个目录");
		String[] fileNameArray = d.list();
		FileUtils.deleteFiles(fileNameArray);
	}
	
	public static void main(String[] args) {
		List<File> fileList = getFileListRecursive("E:/studyWorkSpace/LuceneWebTest/");
		for(File file: fileList){
			System.out.println(file.getPath());
		}
	}
}
