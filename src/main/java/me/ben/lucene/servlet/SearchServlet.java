package me.ben.lucene.servlet;

import me.ben.lucene.bean.FileBean;
import me.ben.lucene.constant.Constants;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.ben.lucene.crawler.LocalFileSystemCrawler;
import me.ben.lucene.util.FileUtils;
import me.ben.lucene.util.PropertiesUtils;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.*;


/**
 * 
 * @author Administrator
 */
public class SearchServlet extends HttpServlet{
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			
			String searchString=req.getParameter("keyword");
			//通过lucene search, 获取数据  这里适合使用java bean
			/*
			// 0. Specify the analyzer for tokenizing text.
		    //    The same analyzer should be used for indexing and searching
		    StandardAnalyzer analyzer = new StandardAnalyzer();
		 
		    // 1. create directory to store index 
		    //这里使用RAMDirectory,内存存储，方便移植  但是每次关闭JVM index数据丢失
		    //需要改为固定目录 
		    Directory indexFolder = new RAMDirectory();
		 
		    IndexWriterConfig config = new IndexWriterConfig(analyzer);
		 
		    IndexWriter indexWriter = new IndexWriter(indexFolder, config);
		    */
			
			//创建索引信息
			LocalFileSystemCrawler.createIndex(Constants.INDEX_ROOT);
			
			//搜索
			List<FileBean> fileList= LocalFileSystemCrawler.searchIndex(searchString);
			req.getSession(true).setAttribute("fileList", fileList);
			resp.sendRedirect("index.jsp");
		}
		
		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			// TODO Auto-generated method stub
			doGet(req, resp);
		}
}
