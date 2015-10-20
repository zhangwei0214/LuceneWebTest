package me.ben.lucene.crawler;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import me.ben.lucene.bean.FileBean;
import me.ben.lucene.constant.Constants;
import me.ben.lucene.util.FileUtils;
import me.ben.lucene.util.PropertiesUtils;
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
//import org.apache.poi.hwpf.HWPFDocument;
/**
 * 
 * @author Administrator 2015-10-18
 * @desc, 从网络上面爬行搜索内容, 整理成index信息(作为数据源)
 *             存放在index directory提供给lucene search
 */
public class LocalFileSystemCrawler{

	public static String INDEX_DIR =  "E:/studyWorkSpace/LuceneWebTest/indexFolder";
	
	/**
     * 创建当前文件目录的索引
     * @param path 当前文件目录
     * @return 是否成功
     * index保存路径:INDEX_DIR -> sys.properties
     */
    public static boolean createIndex(String path){
        Date date1 = new Date();
        //List<File> fileList = FileUtils.getFileList(path);
        //Recursive get file
        List<File> fileList = FileUtils.getFileListRecursive(path);
        String content;
        for (File file : fileList) {
            content = "";
            //获取文件后缀
            String type = file.getName().substring(file.getName().lastIndexOf(".")+1);
            if("txt".equalsIgnoreCase(type)){
                content += FileUtils.txt2String(file);
            }else if("doc".equalsIgnoreCase(type)){
            
                content += FileUtils.doc2String(file);
            
            }else if("xls".equalsIgnoreCase(type)){
                
                content += FileUtils.xls2String(file);
                
            }else{
            	//文件类型不在我们的范围 直接pass
            	continue;
            }
            
            System.out.println("name :"+file.getName());
            System.out.println("path :"+file.getPath());
//            System.out.println("content :"+content);
            System.out.println();
            
            
            try{
                Analyzer analyzer = new StandardAnalyzer();
                Directory directory = FSDirectory.open(new File(INDEX_DIR).toPath());
                File indexFile = new File(INDEX_DIR);
                
                //如果有内容 先删除,目前是没有固定的index存储路径  暂时的方案是每次查询都创建新的索引，然后search这个新创建的索引
                if (indexFile.exists()) {
                	indexFile.delete();
                }
                indexFile.mkdirs();
                
                IndexWriter indexWriter=null;
                IndexWriterConfig config = new IndexWriterConfig(analyzer);
                indexWriter = new IndexWriter(directory, config);
                
                Document document = new Document();
                document.add(new TextField("filename", file.getName(), Store.YES));
                document.add(new TextField("content", content, Store.YES));
                document.add(new TextField("path", file.getPath(), Store.YES));
                System.out.println("path="+path+"filename="+ file.getName() + "\tcontentLength="+content.length());
                indexWriter.addDocument(document);
                indexWriter.commit();
                //closeWriter();
                indexWriter.close();
                
            }catch(Exception e){
                e.printStackTrace();
            }
            content = "";
        }
        Date date2 = new Date();
        System.out.println("创建索引-----耗时：" + (date2.getTime() - date1.getTime()) + "ms\n");
        return true;
    }
    
    /**
     * 查找索引，返回符合条件的文件
     * @param text 查找的字符串
     * @return 符合条件的文件List
     */
    public static List<FileBean> searchIndex(String text){
        Date date1 = new Date();
        List<FileBean> documentList = new ArrayList<FileBean>();
        try{
            Directory directory = FSDirectory.open(new File(Constants.INDEX_FOLDER).toPath());
            Analyzer analyzer = new StandardAnalyzer();
            DirectoryReader ireader = DirectoryReader.open(directory);
            IndexSearcher isearcher = new IndexSearcher(ireader);
    
            QueryParser parser = new QueryParser("content", analyzer);
            Query query = parser.parse(text);
            
            ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
            
            for (int i = 0; i < hits.length; i++) {
            	FileBean filebean = new FileBean();
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
                filebean.setFields(hitDoc.getFields());
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
}
