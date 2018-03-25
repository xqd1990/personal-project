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
		<title>Login</title>
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">
	</head>
	
	<body>
		
		<div class="container" style="margin-top:50px">
			<div class="col-sm-4 col-sm-offset-4">
				<img src="images/twitter_logo1.jpg" style="width:100%;height:280px">
			</div>
			<br>
			<div class="col-sm-4 col-sm-offset-4">
			<form action="user/login" method="post" style="width:100%" class="form-horizontal col-sm-6 col-sm-offset-3">	
				<div class="form-group">
					<span class="glyphicon glyphicon-user" aria-hidden="true"></span>
					<input name="email" placeholder="email" />
				</div>
				<div class="form-group">
					<span class="glyphicon glyphicon-lock" aria-hidden="true"></span>
					<input name="pwd" placeholder="password" />
				</div>
				<button type="submit" id="login" class="btn btn-success">Log in</button>
				<button type="button" id="register" class="btn btn-default">Register</button>
			</form>
			</div>
		</div>
		
		<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
		<script src="js/index.js"></script>
	</body>
</html>
