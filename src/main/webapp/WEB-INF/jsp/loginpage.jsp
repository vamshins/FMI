<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="shortcut icon" href="${pageContext.servletContext.contextPath}/resources/images/unm_favicon_1.ico" type="image/x-icon" />
<title>Financial Aid - Find Missing ISIRS</title>
<style type="text/css">
<!--
.tab { margin-left: 40px; }
-->
</style>
<script>
function setFocusToTextBox(){
    document.getElementById("j_username").focus();
}
</script>
</head>
<body onload='setFocusToTextBox()'>
<%@ include file="header.jsp" %>



<form action="../../j_spring_security_check" method="post" class="tab">
<h1>Login</h1>
<p>
	<label for="j_username">Username</label>
	<input id="j_username" name="j_username" type="text" class="tab"/>
</p>

<p>
	<label for="j_password">Password&nbsp;</label>
	<input id="j_password" name="j_password" type="password" class="tab"/>
</p>
<br>
<blockquote><input  type="submit" value="Login" class="tab"/>	</blockquote>							
	
</form>
<br>
<br>
<div id="login-error">${error}</div>
</body>
</html>