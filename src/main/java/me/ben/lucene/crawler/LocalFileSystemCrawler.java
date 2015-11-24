package me.ben.lucene.crawler;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import me.ben.lucene.bean.LocalFile;
import me.ben.lucene.util.FileUtils;
import me.ben.lucene.util.PropertyUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.wltea.analyzer.lucene.IKAnalyzer;
//import org.apache.poi.hwpf.HWPFDocument;
/**
 * 
 * @author Administrator 2015-10-18
 * @desc, search本地文件系统
 */
public class LocalFileSystemCrawler{
	static String indexFolder = System.getProperty("user.dir") + "/" + new PropertyUtils("sys").getProperty("lucene.fsindexfolder");
	/**
     * 创建当前文件目录的索引
     * @param path 当前文件目录
     * @return 是否成功
     * index保存路径:INDEX_DIR -> sys.properties
	 * @throws Exception 
     */
    public static List<LocalFile> crawLocalFS(String searchPath) throws Exception{
    	
    	//删除现在索引目录中的内容
    	//FileUtils.deleteByDir(indexFolder);
        Date date1 = new Date();
        //List<File> fileList = FileUtils.getFileList(path);
        //Recursive get file
        List<File> fileList = FileUtils.getFileListRecursive(searchPath);
        
        List<LocalFile> lfileList = new ArrayList<LocalFile>();
        String content;
        
        try{
            
            //清空index
            //indexWriter.deleteAll();
	        for (File file : fileList) {
	            content = "";
	            //获取文件后缀
	            String extension = file.getName().substring(file.getName().lastIndexOf(".")+1);
	            if("txt".equalsIgnoreCase(extension)){
	                content += FileUtils.txt2String(file);
	            }/*else if("doc".equalsIgnoreCase(type)){
	                content = FileUtils.doc2String(file);
	            }
	            else if("xls".equalsIgnoreCase(type)){	//xls检索有问题
	                content = FileUtils.xls2String(file);
	               
	            } */else{
	            	//文件类型不在我们的范围 直接pass
	            	continue;
	            }
	            System.out.println("------------------------------------------------------");
		            System.out.println("name :"+file.getName());
		            System.out.println("path :"+file.getPath());
		            //System.out.println("content :"+content);
		            
		            LocalFile lfile = new LocalFile();
		            lfile.setName(file.getName());
		            lfile.setPath(file.getPath());
		            lfile.setContent(content);
		            lfileList.add(lfile);
	                //System.out.println("searchPath="+searchPath+"filename="+ file.getName() + "\tcontentLength="+content.length());
	        }
        }catch(Exception e){
            e.printStackTrace();
        }
        Date date2 = new Date();
        System.out.println("本地文件系统抓取成功-----耗时：" + (date2.getTime() - date1.getTime()) + "ms\n");
        return lfileList;
    }
    
    /**
     * 查找索引，返回符合条件的文件
     * @param text 查找的字符串
     * @return 符合条件的文件List
     */
    public static List<LocalFile> search(String text){
        Date date1 = new Date();
        List<LocalFile> documentList = new ArrayList<LocalFile>();
        try{
            Directory directory = FSDirectory.open(new File(indexFolder).toPath());
            Analyzer analyzer = new StandardAnalyzer();
            DirectoryReader ireader = DirectoryReader.open(directory);
            IndexSearcher isearcher = new IndexSearcher(ireader);
    
            QueryParser parser = new QueryParser("content", analyzer);
            Query query = parser.parse(text);
            
            ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
            
            for (int i = 0; i < hits.length; i++) {
            	LocalFile filebean = new LocalFile();
                Document hitDoc = isearcher.doc(hits[i].doc);
                /*
                System.out.println("____________________________");
                System.out.println(hitDoc.get("filename"));
                System.out.println(hitDoc.get("content"));
                System.out.println(hitDoc.get("path"));
                System.out.println("____________________________");
                */
                filebean.setName(hitDoc.get("name"));
                filebean.setContent(hitDoc.get("content"));
                filebean.setPath(hitDoc.get("path"));
                documentList.add(filebean);
            }
            
            ireader.close();
            directory.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        Date date2 = new Date();
        System.out.println("查看索引-----耗时：" + (date2.getTime() - date1.getTime()) + "ms\n");
        return documentList;
    }
    
    public static void main(String[] args) throws Exception{
    	crawLocalFS("E:/studyWorkSpace");
    	//List<FileBean> resultList = searchIndex("Lucene");
    	//System.out.println(resultList);
    }
}
