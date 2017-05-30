<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
    <title>"教育部-IBM精品课程建设项目"软件项目管理课程</title>
    <link href="${ctx}/css/fontStyle.css" rel="stylesheet" type="text/css" />

    <style type="text/css">

        .vdlist{width:100%;}
        .vdlist *{margin:0;padding: 0;}
        .vdlist li{float:left;width:210px;height:225px;margin:0 10px;position: relative;z-index: 100;}
        .vdlist img{width:200px;height:150px;padding:4px;border:1px solid #fff;}
        .vdlist p{height:24px;line-height: 24px;overflow: hidden;}
        .vdlist p span{float:right;}
    </style>

</head>
<body>
<script>

</script>
<div class="article">
    <h1><center>学生进度</center></h1>
    <input id="inputid" type="hidden">
    <h2>
        详情
    </h2>


    <s:iterator value="studentList" status="status"  var="student">
    <ul class="vdlist">
        <li>
            <span>学生:<s:property value="#student.userId"/></span>
            <span>进度:<s:property value="#student.videoTime"/>分钟</span>

        </li>
    </ul>
    </s:iterator>

</body>
</html>

