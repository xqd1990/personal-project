<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML>
<html>

  	<head>
	    <base href="<%=basePath%>">
	    <meta charset="UTF-8">
	    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
		<title>My Downloads</title>
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.4.1/css/bootstrap-datepicker3.css"/>
	</head>
	<body>
		<div class="container" style="margin-top:30px">
			<table class="table table-striped table-bordered col-sm-8">
	  			<caption><a href="index.jsp">Back</a></caption>
	  			<thead>
				    <tr>
				    	<th>No.</th>
				    	<th>File Name</th>
				    	<th>Option</th>
				    </tr>
				</thead>
				<tbody>
					<c:forEach items="${records}" var="record" varStatus="status">
					<tr>
						<td>${status.index+1}</td>
						<td><a href="track/${record.path}">${record.path}</a></td>
						<td><button id="delete" class="btn btn-default">delete</button><span class="sr-only">${record.id}</span></td>
					</tr>
					</c:forEach>
				</tbody>
	  		</table>
  		</div>
	<body>
	
		<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.4.1/js/bootstrap-datepicker.min.js"></script>
		<script>
			$(function(){
				$("#delete").click(function(){
					var id = $(this).next().html();
					window.location.href="user/deleteMydownloads?id="+id;
				});
			});
		</script>
	</body>
</html>
	