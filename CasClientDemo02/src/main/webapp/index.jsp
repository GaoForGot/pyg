<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>二品优购</title>
</head>
<body>
欢迎来到二品优购<%=request.getRemoteUser()%>
<a href="http://localhost:9300/cas/logout?service=https://www.baidu.com">点击退出</a>
</body>
</html>