package me.ben.lucene.controller;

import java.io.File;
import java.util.List;

import me.ben.lucene.bean.LocalFile;
import me.ben.lucene.bean.News;
import me.ben.lucene.crawler.LocalFileSystemCrawler;
import me.ben.lucene.crawler.NTESCrawler;
import me.ben.lucene.util.LuceneManager;
import me.ben.lucene.util.LuceneUtils;
import me.ben.lucene.util.PropertyUtils;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.SimpleFSDirectory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping("/crawl")
public class CrawlController {
	
	public CrawlController(){
		
	}
	@ResponseBody
	@RequestMapping("ntes")
	@SuppressWarnings("deprecation")
	public String ntes(String category) throws Exception{
		//抓取网易新闻头条
		List<News> list=NTESCrawler.crawl163LatestNews(category);
		//重复抓取会重复添加索引
		//indexWriter.deleteAll();
		String indexFolder = System.getProperty("user.dir") + "/" + new PropertyUtils("sys").getProperty("lucene.webindexfolder");
		IndexWriter indexWriter = LuceneUtils.getIndexWrtier(indexFolder, new IndexWriterConfig(new IKAnalyzer(true)));
		//IndexWriter indexWriter = new IndexWriter(new SimpleFSDirectory(new File(indexFolder).toPath()),);
		Document doc = null;
		
		for (News news : list) {
			doc = new Document();
			
			Field title = new Field("title", news.getTitle(), Field.Store.YES,Field.Index.ANALYZED);
			Field content = new Field("content", news.getContent(),Field.Store.YES, Field.Index.ANALYZED);
			Field shortContent = new Field("shortContent", news.getShortContent(),Field.Store.YES, Field.Index.NO);
			Field url = new Field("url", news.getUrl(), Field.Store.YES,Field.Index.NO);
			Field date = new Field("date", news.getDate(), Field.Store.YES,Field.Index.NO);
			doc.add(title);
			doc.add(content);
			doc.add(shortContent);
			doc.add(url);
			doc.add(date);
			indexWriter.addDocument(doc);
		}
		indexWriter.commit();
		return JSONObject.toJSONString(list);
	}
	
	@ResponseBody
	@RequestMapping("fs")
	@SuppressWarnings("deprecation")
	//local file system
	public String fs(String srcDir) throws Exception{
		System.out.println("开始抓取 srcDir:" + srcDir);
		String indexFolder = System.getProperty("user.dir") + "/" + new PropertyUtils("sys").getProperty("lucene.fsindexfolder");
		IndexWriter indexWriter = LuceneUtils.getIndexWrtier(indexFolder, new IndexWriterConfig(new IKAnalyzer(true)));
		//IndexWriter indexWriter = new IndexWriter(new SimpleFSDirectory(new File(indexFolder).toPath()),new IndexWriterConfig(new IKAnalyzer(true)));
		//抓取本地文件系统
		List<LocalFile>fileList =  LocalFileSystemCrawler.crawLocalFS(srcDir);
		
		for (int i=0 ; i< fileList.size();i++) {
			LocalFile file = fileList.get(i);
			Document doc = new Document();
			
			Field name = new Field("name", file.getName(), Field.Store.YES,Field.Index.ANALYZED);
			Field path = new Field("path", file.getPath(),Field.Store.YES, Field.Index.ANALYZED);
			Field content = new Field("content", file.getContent(),Field.Store.YES, Field.Index.ANALYZED);
			doc.add(name);
			doc.add(path);
			doc.add(content);
			//truncate
			int length=doc.get("content").length();
			String fileContent = doc.get("content").substring(0, length>50?50:length);
			file.setContent(fileContent);
			indexWriter.addDocument(doc);
		}
		indexWriter.commit();
		if(fileList.size()>20)	//只返回最多20条给前端
			fileList.subList(0, 20);
		return JSONObject.toJSONString(fileList);
	}
	
}
