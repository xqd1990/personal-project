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
		<title>TwitterData</title>
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">
	</head>
	
	<body>
		<nav class="navbar navbar-inverse">
			<div class="container-fluid">
				<!-- Brand and toggle get grouped for better mobile display -->
			    <div class="navbar-header">
			      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#links" aria-expanded="false">
			        <span class="sr-only">Toggle navigation</span>
			        <span class="icon-bar"></span>
			        <span class="icon-bar"></span>
			        <span class="icon-bar"></span>
			      </button>
			      <a class="navbar-brand" href="http://localhost:8080/pp">Home</a>
			    </div>
			    <div class="collapse navbar-collapse" id="links">
			    	<ul class="nav navbar-nav">
			    		<li><a href="https://twitter.com">Twitter</a></li>
			    		<li><a href="#">Link</a></li>
			    		<li class="dropdown">
			    			<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Sets <span class="caret"></span></a>
          					<ul class="dropdown-menu">
          						<li><a href="user/change">Change Settings</a></li>
          						<li role="separator" class="divider"></li>
          						<li><a href="user/getback">Forget Password</a></li>
          					</ul>
			    		</li>
			    	</ul>
			    	<c:if test="${empty user}">
			    	<form action="user/login" method="post" class="navbar-form navbar-right">
			    		<c:if test="${not empty error}"><span class="label label-danger">${error}</span></c:if>
				        <div class="form-group">
				        	<input type="text" name="email" class="form-control input-sm" placeholder="email">
				        	<input type="password" name="pwd" class="form-control input-sm" placeholder="password">
				        </div>
				        <button type="submit" class="btn btn-success btn-sm">Log in</button>
				        <button type="button" id="register" class="btn btn-default btn-sm">Register</button>
      				</form>
      				</c:if>
      				<c:if test="${not empty user}">
      				<ul class="nav navbar-nav navbar-right">
      					<li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#"><span class="glyphicon glyphicon-user"></span>&nbsp;${user.firstname}<span class="caret"></span></a>
      						<ul class="dropdown-menu">
      							<li>
                					<a href="user/logout">Log out</a></li>
            					<li>
      						</ul>
      					</li>
      				</ul>
      				</c:if>
			    </div>
			</div>
		</nav>
		
		
		
		<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
		<script src="js/index.js"></script>
	</body>
</html>
