package me.ben.lucene.controller;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import me.ben.lucene.bean.LocalFile;
import me.ben.lucene.util.LuceneUtils;
import me.ben.lucene.util.PropertyUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping("/lucene/fs")
public class FSController {
	String indexFolder = System.getProperty("user.dir") + "/" + new PropertyUtils("sys").getProperty("lucene.fsindexfolder");
	//@Autowired(required = false) 
	IndexWriter indexWriter;
	//@Autowired(required = false) 
	IKAnalyzer analyzer;
	
	private static final String STARTTAG="<font color='red'>";
	private static final String ENDTAG="</font>";
	
	public FSController() throws IOException{
		this.analyzer=new IKAnalyzer(true);
		this.indexWriter = LuceneUtils.getIndexWrtier(indexFolder, new IndexWriterConfig(new IKAnalyzer(true)));
		//this.indexWriter = new IndexWriter(new SimpleFSDirectory(new File(indexFolder).toPath()),new IndexWriterConfig(analyzer));
	}
	@ResponseBody
	@RequestMapping("listIndexed")
	public String listIndexed() throws CorruptIndexException, IOException{
		IndexSearcher indexSearcher=getSearcher();
		
		int size=indexSearcher.getIndexReader().maxDoc();
		Document doc=null;
		LocalFile file=null;
		List<LocalFile> list=new ArrayList<LocalFile>();
		
		for(int i=0;i<size && i<20;i++){	//最多return 20 条记录
			file=new LocalFile();
			doc=indexSearcher.doc(i);
			file.setName(doc.get("name"));
			file.setPath(doc.get("path"));
			
			//truncate content
			int length = doc.get("content").length();
			String content = doc.get("content").substring(0, length>50?50:length);
			file.setContent(content);
			list.add(file);
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
	public String search(String searchStr) throws ParseException, IOException, InvalidTokenOffsetsException{
		List<LocalFile> list=new ArrayList<LocalFile>();
        System.out.println("开始搜索 searchStr:" + searchStr);
        if(StringUtils.isBlank(searchStr)){
        	return JSONObject.toJSONString(list);
        }
        IndexSearcher searcher=getSearcher();
        
        QueryParser parser=new MultiFieldQueryParser(new String[]{"name","content"}, analyzer);
        
        Query query=parser.parse(searchStr);
        
        TopDocs td=searcher.search(query,10);
        
        ScoreDoc[] sd=td.scoreDocs;
        
        SimpleHTMLFormatter simpleHtmlFormatter=new SimpleHTMLFormatter(STARTTAG,ENDTAG);
        
       Highlighter highlighter=new Highlighter(simpleHtmlFormatter,new QueryScorer(query));
        
        Document doc;
        
        TokenStream tokenStream=null;
        
	    LocalFile file=null;
		
		
		String name;
		String content;
        
        for(int i=0;i<sd.length;i++){
        	file=new LocalFile();
        	
        	int docId=sd[i].doc;
        	doc=searcher.doc(docId);
        	
        	name=doc.get("name");
			tokenStream=analyzer.tokenStream("name", new StringReader(name));
			//name=highlighter.getBestFragment(tokenStream, name);
			file.setName(doc.get("name"));
    	
	    	content=doc.get("content");
	    	tokenStream=analyzer.tokenStream("content", new StringReader(content));
	    	content=highlighter.getBestFragment(tokenStream, content);
	    	
	    	//正文部分，如果没有匹配的关键字，截取前200个字符
	    	file.setContent(content==null?(doc.get("content").length()<200?doc.get("content"):doc.get("content").substring(0, 200)):content);
	    	file.setName(name);
	    	file.setPath(doc.get("path"));
	    	list.add(file);
        	
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
