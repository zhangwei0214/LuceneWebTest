官方Demo:
http://lucene.apache.org/core/5_3_1/demo/overview-summary.html#overview_description

介绍:
http://www.cnblogs.com/xing901022/p/3933675.html#_label2

file system crawler:
http://www.cnblogs.com/xing901022/p/3933675.html

env:
1)基于Lucene 5.3.1
2) JDK1.7+ (需要NIO2的支持)

Test:
http://localhost:8080/LuceneWebTest/search.do?keyword=Lucene

需要解决的问题
1) index的storage问题，应该需要绝对路径存储， 也就意味着这个应用的数据移植是一个问题
    (本身也不需要移植，index需要永久存储)
2) Lucene核心是index和search(基于index信息)


todo:
1)文件系统检索[50%]
1)加入网络crawler特性(学习性质, 因为通常lucene是做基于内网/特定数据的站内搜索)
3)UI优化


