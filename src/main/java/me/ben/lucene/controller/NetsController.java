package me.ben.lucene.controller;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import me.ben.lucene.bean.News;
import me.ben.lucene.util.LuceneUtils;
import me.ben.lucene.util.PropertyUtils;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping("/lucene")
public class NetsController {
	String indexFolder=System.getProperty("user.dir") + "/" + new PropertyUtils("sys").getProperty("lucene.webindexfolder");
	IndexWriterConfig config;
	//@Autowired(required = false) 
	IndexWriter indexWriter;
	//@Autowired(required = false) 
	IKAnalyzer analyzer;
	
	private static final String STARTTAG="<font color='red'>";
	private static final String ENDTAG="</font>";
	
	public NetsController() throws IOException{
		this.analyzer = new IKAnalyzer(true);
		this.config = new IndexWriterConfig(analyzer);
		this.indexWriter = LuceneUtils.getIndexWrtier(indexFolder, config);
		//this.indexWriter = new IndexWriter(new SimpleFSDirectory(new File(indexFolder).toPath()),config);
	}
	@ResponseBody
	@RequestMapping("listIndexed")
	public String listIndexed() throws CorruptIndexException, IOException{
		IndexSearcher indexSearcher=getSearcher();
		int size=indexSearcher.getIndexReader().maxDoc();
		Document doc=null;
		News news=null;
		List<News> list=new ArrayList<News>();
		for(int i=0;i<size;i++){
			news=new News();
			doc=indexSearcher.doc(i);
			news.setTitle(doc.get("title"));
			news.setUrl(doc.get("url"));
			list.add(news);
		}
		return JSONObject.toJSONString(list);
	}
	
	@ResponseBody
	@RequestMapping("indexFiles")
	public String indexFiles() throws IOException{
		Directory d=indexWriter.getDirectory();
		String[] fs=d.listAll();
		return JSONObject.toJSONString(fs);
	}
	
	@ResponseBody
	@RequestMapping("deleteIndexes")
	public String deleteIndexes(){
		
		String flag="";
		
		try {
			indexWriter.deleteAll();
			indexWriter.commit();
			flag="suc";
		} catch (IOException e) {
			e.printStackTrace();
			try {
				indexWriter.rollback();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			flag="error";
		}
		
		return flag;
	}
	
	@ResponseBody
	@RequestMapping("deleteIndex")
	public String deleteIndex(){
		
		
		return null;
	}
	
	@ResponseBody
	@RequestMapping("updateIndex")
	public String updateIndex(){
		
		return null;
	}
	
	@ResponseBody
	@RequestMapping("search")
	public String search(String text) throws ParseException, IOException, InvalidTokenOffsetsException{
        
        IndexSearcher searcher=getSearcher();
        
        QueryParser parser=new MultiFieldQueryParser(new String[]{"title","content"}, analyzer);
        
        Query query=parser.parse(text);
        
        TopDocs td=searcher.search(query,10);
        
        ScoreDoc[] sd=td.scoreDocs;
        
        SimpleHTMLFormatter simpleHtmlFormatter=new SimpleHTMLFormatter(STARTTAG,ENDTAG);
        
        Highlighter highlighter=new Highlighter(simpleHtmlFormatter,new QueryScorer(query));
        
        Document doc;
        
        TokenStream tokenStream=null;
        
	    News news=null;
		List<News> list=new ArrayList<News>();
		
		String title;
		String content;
        
        for(int i=0;i<sd.length;i++){
        	news=new News();
        	
        	int docId=sd[i].doc;
        	doc=searcher.doc(docId);
        	
			title=doc.get("title");
			tokenStream=analyzer.tokenStream("title", new StringReader(title));
			title=highlighter.getBestFragment(tokenStream, title);
			news.setTitle(title==null?doc.get("title"):title);
    	
	    	content=doc.get("content");
	    	tokenStream=analyzer.tokenStream("content", new StringReader(content));
	    	content=highlighter.getBestFragment(tokenStream, content);
	    	
	    	//正文部分，如果没有匹配的关键字，截取前200个字符
	    	news.setContent(content==null?(doc.get("content").length()<200?doc.get("content"):doc.get("content").substring(0, 199)):content);
			news.setUrl(doc.get("url"));
			news.setDate(doc.get("date"));
			news.setOther1(docId+"");
	    	list.add(news);
        	
        }
        return JSONObject.toJSONString(list);
	}
	
	@SuppressWarnings("deprecation")
	private IndexSearcher getSearcher() throws IOException{
		Directory directory = FSDirectory.open(new File(indexFolder).toPath());
        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);
		return isearcher;
	}
	
}
