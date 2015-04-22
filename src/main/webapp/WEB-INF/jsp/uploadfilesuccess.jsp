<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<link rel="shortcut icon" href="${pageContext.servletContext.contextPath}/resources/images/unm_favicon_1.ico" type="image/x-icon" />
<%@ include file="header.jsp" %>
<title>Process ISIRS</title>
</style>
<script type="text/javascript">
		function validateAidYear() {
			var vAidYear = document.getElementById("aidYear").value;
			if (vAidYear.length == 4) {
				var fileList = [];
				<c:forEach items="${files}" var="file">
				fileList.push("${file}");
				</c:forEach>
				var i;
				for (i = 0, l = fileList.length; i < l; i++) {
					console.log(fileList[i]);
					// absFileName = fileList[i].substring(0, fileList[i].lastIndexOf("."));
					absFileName = fileList[i];
					console.log(absFileName);
					console.log(vAidYear.substring(2,4));
					console.log(vAidYear.substring(0,2));
					console.log(parseInt(vAidYear.substring(0,2)) != parseInt(vAidYear.substring(2,4))-1);

					if(parseInt(vAidYear.substring(0,2)) != parseInt(vAidYear.substring(2,4))-1){
						alert("Entered aid year " + vAidYear + " is not valid. Please try again!");
						return false;
					}
					if (absFileName.indexOf(vAidYear.substring(2,4)) == -1) {
						alert("Atleast one of the files submitted doesn't belong to the entered aid year "
								+ vAidYear);
						return false;
					}
				}
			} else {
				alert(vAidYear +" - Entered wrong number of characters. Please enter the aid year in the format YYYY");
				return false;
			}
			return true;
		}
	</script>
</head>

<body>
<br>
<br>
<div align="center">

	<h1>Process ISIRS</h1>
	<h2>Files uploaded:</h2>
	<ol>
		<c:forEach items="${files}" var="file">
           - ${file} <br>
		</c:forEach>
	</ol>
	<form id="processFiles" method="post" action="processfiles"
		modelAttribute="processForm" enctype="multipart/form-data"
		onsubmit="return validateAidYear()">
		<table id="fileTable">
			<tr>
				<td>Enter the Aid Year</td>
				<td><input type="text" name="aidYear" id="aidYear" /></td>
			</tr>
		</table>
		<br /> <input type="submit" value="Process" /> <br /> <br />
	</form>
	
	<br>
	<br>
</div>
</body>
</html>