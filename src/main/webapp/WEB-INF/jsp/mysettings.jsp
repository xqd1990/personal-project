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
		<title>My Settings</title>
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.4.1/css/bootstrap-datepicker3.css"/>
	</head>
	<body>
		<nav class="navbar navbar-inverse">
			<div class="container-fluid">
			    <div class="navbar-header">
			      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#links" aria-expanded="false">
			        <span class="sr-only">Toggle navigation</span>
			        <span class="icon-bar"></span>
			        <span class="icon-bar"></span>
			        <span class="icon-bar"></span>
			      </button>
			      <a class="navbar-brand" href="index.jsp">Home</a>
			    </div>
			 </div>
		</nav>
		<div class="container" style="margin-top:50px">
			<form action="user/update" class="form-horizontal col-sm-8 col-sm-offset-2" role="form" method="post">
				<span class="sr-only" id="userid">${user.id}</span>
				<div class="form-group">
					<label for="firstname" class="col-sm-2 control-label">Given name</label>
					<div class="col-sm-10">
						<input type="text" class="form-control" id="firstname" value="${user.firstname}">
					</div>
				</div>
				<div class="form-group">
					<label for="lastname" class="col-sm-2 control-label">Surname</label>
					<div class="col-sm-10">
						<input type="text" class="form-control" id="lastname" value="${user.lastname}">
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">Gender</label>
					<div class="col-sm-10">
						<label class="radio-inline"><input type="radio" name="gender" value=1 <c:if test="${1==user.gender}">checked</c:if> >Male</label>
						<label class="radio-inline"><input type="radio" name="gender" value=0 <c:if test="${0==user.gender}">checked</c:if> >Female</label>
					</div>
				</div>
				<div class="form-group">
					<label for="birth" class="col-sm-2 control-label">Birthday</label>
					<div class="col-sm-10">
						<input type="text" class="form-control" id="birth" value="${user.birth}">
					</div>
				</div>
				<div class="form-group">
					<label for="email" class="col-sm-2 control-label">Email</label>
					<div class="col-sm-10">
						<input type="text" class="form-control" id="email" value="${user.email}">
					</div>
				</div>
				<div class="form-group">
					<label for="tel" class="col-sm-2 control-label">Tel</label>
					<div class="col-sm-10">
						<input type="text" class="form-control" id="tel" value="${user.tel}">
					</div>
				</div>
				<div class="form-group">
					<label for="pwd" class="col-sm-2 control-label">Password</label>
					<div class="col-sm-10">
						<input type="password" class="form-control" id="pwd" value="${user.pwd}">
					</div>
				</div>
				<div class="form-group">
					<label for="postcode" class="col-sm-2 control-label">Postcode</label>
					<div class="col-sm-10">
						<input type="text" class="form-control" id="postcode" value="${user.postcode}">
					</div>
				</div>
				<div class="form-group">
					<div class="col-sm-10 col-sm-offset-2">
						<button type="button" id="update" class="btn btn-success">Update</button>
						<button type="button" id="cancel" class="btn btn-danger">Cancel</button>
					</div>
				</div>
			</form>
		</div>
		
		<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.4.1/js/bootstrap-datepicker.min.js"></script>
		<script>
			$(function(){
				$("#birth").datepicker({
					format:"yyyy-mm-dd",
					autoclose:true
				});
				$("#cancel").click(function(){
					window.location.href="index.jsp";
				});
				$("#update").click(function(){
					var user = {
							id:$("#userid").html(),
							firstname:$("#firstname").val(),
							lastname:$("#lastname").val(),
							gender:$("input[name='gender']:checked").val(),
							birth:$("#birth").val(),
							email:$("#email").val(),
							tel:$("#tel").val(),
							pwd:$("#pwd").val(),
							postcode:$("#postcode").val()
					}
					$.ajax({
						url:"user/update",
						type:"post",
						data:user,
						success:function(data){
							alert(data);
							window.location.href="user/mysettings";
						}
					});
				});
			});
		</script>
	</body>
</html>