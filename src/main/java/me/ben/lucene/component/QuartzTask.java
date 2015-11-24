package me.ben.lucene.component;

import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
/**
 * 用于进行index更新操作
 * @author Administrator
 *
 */
@Component
public class QuartzTask {
	
	/**
	 * 3分钟优化一次，实际中不要频繁的优化索引，隔个十天半个月或者几个月的，因为优化索引很浪费资源
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	@Scheduled(fixedRate=5000)
	public void optimize() throws CorruptIndexException, IOException {
//		System.out.println("正在优化索引...");
//		System.out.println("索引优化结束");
		
		
	}
}
