<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
//该jsp是给文件系统搜索
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>Test</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<script type="text/javascript" src="resources/jquery.min.js"></script>
	<style type="text/css">
		.container{
			margin:0 auto;
			width:80%;
			height:140;
			border:1px solid green;
			border-bottom:none;
		}
		.container2{
			margin:0 auto;
			width:80%;
			border:1px solid green;
			border-top:none;
		}
		#show{
			width:100%;
		}
		#tip{
			width:100%;text-align:center;float:none;color:green;
		}
		.box{
			border: 1px solid rgb(0, 0, 0);
			color: rgb(255, 255, 255);
			margin: 5px;
			background: rgb(83, 83, 83);
		}
		.box:hover{
			background: green;
			font-weight: bold;
		}
		button{
			border:1px solid black;
			background:white;
			color:black;
		}
		button:hover{
			border:1px solid black;
			background:gray;
			color:white;
		}
		
		fieldset{
			border:1px solid black;
			width:30%;
			height:120px;
			float:left;
		}
		select{
			border:1px solid green;
			color:green;
		}
		textarea{
			width:100%;
			height:50px;
		}
		
	</style>
	<script type="text/javascript">
	
	function tip(text){
		$("#tip").html(text);
	}
	
	function crawl(){
		var srcDir=$("#srcDir").val();
		tip("正在抓取 『"+srcDir+"』 的内容，请稍后");
		$.ajax({
             type: "post",
             url: "crawl/fs.do",
             data: {"srcDir":srcDir},
             dataType: "json",
             success: function(data){
            	 var html="";
             		$.each(data,function(i,file){
             			html+="path="+file.path+" name="+file.name+"<p>"+file.content +" </p>";
             		});
             		tip("<font color=blue>抓取成功</font>");
             		$("#show").empty();
             		$("#show").append(html);
             },error:function(data){
            	 tip("<font color=red>抓取失败</font>");
             }
         });
	}
	
	
	
	function indexFiles(){
		tip("正在执行");
		$.ajax({
            type: "post",
            url: "lucene/fs/indexFiles.do",
            data: {},
            dataType: "json",
            success: function(data){
           	 		var html="<ul>";
            		$.each(data,function(i,n){
            			html+="<li class=''>"+n+"</li>";
            		});
            		html+="<ul>";
            		tip("<font color=blue>操作结束</font>");
            		$("#show").empty();
            		$("#show").append(html);
            },
            error:function(data){
           	 	tip("<font color=red>操作失败</font>");
            }
        });
	}
	function deleteIndexes(){
		tip("正在执行");
		$.ajax({
            type: "post",
            url: "lucene/fs/deleteIndexes.do",
            data: {},
            dataType: "text",
            success: function(data){
           	 		if(data=="suc"){
           	 			tip("已删除所有索引");
           	 		}
           	 	indexFiles();	//重新刷新indexFiles 列表(前台清空)
            },error:function(data){
           	 tip("<font color=red>操作失败</font>");
            }
        });
	}
	function listIndexes(){
		tip("正在执行");
		$.ajax({
             type: "post",
             url: "lucene/fs/listIndexed.do",
             data: {},
             dataType: "json",
             success: function(data){
            	 	var html="<ul>";
             		$.each(data,function(i,file){
             				html+="<li>path="+file.path+" name="+file.name+"<p>"+file.content +" </p></li>";
             		});
             		html+="</ul>";
             		tip("<font color=blue>操作成功</font>");
             		$("#show").empty();
             		$("#show").append(html);
             },error:function(data){
            	 tip("<font color=red>操作失败</font>");
             }
         });
	}
	
	function search(){
		tip("正在查询");
		$.ajax({
             type: "post",
             url: "lucene/fs/search.do",
             data: {"searchStr":$("#keyword").val()},
             dataType: "json",
             success: function(data){
            	 	console.log(data);
            	 	var html="<ul>";
             		$.each(data,function(i,file){
             			html+="<li>path="+file.path+" name="+file.name+"<p>"+file.content +" </p></li>";
             		});
             		html+="</ul>";
             		console.log(html);
             		tip("<font color=blue>查询结束</font>");
             		$("#show").empty();
             		$("#show").append(html);
             },error:function(data){
            	 tip("<font color=red>操作失败</font>");
             }
         });
	}
	</script>
  </head>
  
  <body>
    	<div class="container">
    			<fieldset>
				    <legend>数据抓取</legend>
				    <textarea id="srcDir">E:/studyWorkSpace</textarea>
				    <button id="crawl" onclick="crawl()">抓取本地txt文件</button>
				</fieldset>
				<fieldset>
				    <legend>Lucene操作</legend>
				    <button id="indexFiles" onclick="indexFiles()">查看所有文件</button>
				    <button id="deleteIndexes" onclick="deleteIndexes()">删除索引</button>
				    <button id="listIndexes" onclick="listIndexes()">列出所有索引数据</button>
				    <textarea id="keyword">用户</textarea>
				   <button id="search" onclick="search()">查询</button>
				</fieldset>
    	</div>
    	<div class="container2">
				<div id="tip">...</div>
    			<div id="show"></div>
		</div>
  </body>
</html>
