<%@ page language="java" contentType="text/html;  charset=UTF-8;" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Lucene搜索</title>
<style type="text/css">


body,div,form,input{font-size:14px;margin:0;padding:0}
a{color:#0000cc}
a:active{color:#f90}
#top_right{color:#999;font-size:12px;padding:6px 10px;position:absolute;right:0;top:0}
#top_right a{font-size:12px}


#main{margin:25px auto 100px auto;padding:0 35px;width:530px}
#main h1{margin-bottom:4px;text-align:center}
#main h1 a img{border:none}
#main ul{list-style-type:none;padding:0 0 0 45px;width:410px}
#main ul li{float:left;margin-right:18px}
#main ul li a.mr{color:#000;font-weight:bold}
#search_form{clear:both;margin-left:15px;padding-top:4px}


#keyword{
background:url(
) no-repeat -304px 0px;
border-color:#999 #ccc #ccc #999;
border-style:solid;
border-width:1px;
float:left;
font-size:18px;
height:30px;
line-height:30px;
text-indent:10px;
width:408px;
}


#main .search_btn{background:url(
) no-repeat 0 0;border:none;cursor:pointer;float:left;font-size:12px;height:32px;line-height:32px;margin-left:6px;width:95px; }
#main .down_btn{}
#main p{float:left;margin-top:35px;text-align:center;width:520px; }
#cl{clear:both}


#footer{margin:50px auto;width:600px}
#footer p{font-size:12px;margin-bottom:15px;text-align:center}
#footer p.jr{margin-bottom:5px}
#footer p a{font-size:12px}
#footer p.bq{color:#77c}
#footer p.bq a{color:#77c}
</style>
</head>


<body>
<div id="top_right">
<a href="#">搜索设置</a> | <a href="#">登陆</a> <a href="#">注册</a>
</div>
<div id="main">
<h1>
<a href="#" class="logo"><img src="http://www.baidu.com/img/bdlogo.png" width="270" height="129" margin-left= "2cm"></a></h1>
<ul class="nav">
<li><a href="#">新闻</a></li>
<li><a href="#" class="mr">网页</a></li>
<li><a href="#">贴吧</a></li>
<li><a href="#">知道</a></li>
<li><a href="#">MP3</a></li>
<li><a href="#">图片</a></li>
<li><a href="#">视频</a></li>
<li><a href="#">地图</a></li>
</ul>
<form action="search.do" method="post" id="search_form">
<input name="keyword" type="text" id="keyword">
<input name=""  type="submit" value="百度一下" class="search_btn" onmouseout="'this.className='search_btn'" onmousedown="'this.className.down_btn'">
</form>
<p>

</div>
<div id="footer">
<p>
<table>
<c:forEach items="${fileList}"  var="file">
<tr>
<td>${file.name}</td>
<td>${file.path}</td>
</tr>
</c:forEach>
</table>
</p>
</div>

</body>
</html>