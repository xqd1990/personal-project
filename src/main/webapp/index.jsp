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
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.4.1/css/bootstrap-datepicker3.css"/>
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
			    		<li><a href="https://apps.twitter.com/">Dev</a></li>
			    		<li class="dropdown">
			    			<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">FQ <span class="caret"></span></a>
          					<ul class="dropdown-menu">
          						<li><a href="#">About Us</a></li>
          						<li role="separator" class="divider"></li>
          						<li><a href="pages/getback.html">Forget Password</a></li>
          					</ul>
			    		</li>
			    	</ul>
			    	<c:if test="${empty user}">
			    	<form id="login_form" action="user/login" method="post" class="navbar-form navbar-right">
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
                					<a href="user/mysettings">My Settings</a></li>
            					<li>
      							<li role="separator" class="divider"></li>
            					<li>
                					<a href="user/mydownloads">My Downloads</a></li>
            					<li>
            					<li role="separator" class="divider"></li>
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
		
		<div class="container" style="margin-top:30px;width:100%">
			<div class="col-sm-6">
				<div class="panel panel-info">
					<div class="panel-heading">
		        		Searching Conditions
		    		</div>
					<div class="panel-body">
						<form class="form-horizontal" id="search_form" role="form">
							<div class="form-group">
								<label for="keyword" class="col-sm-2 control-label">Keyword</label>
								<div class="col-sm-10">
									<input type="text" class="form-control" name="keyword" placeholder="keyword" id="keyword">
								</div>
							</div>
							<!-- 
							<div class="form-group">
								<label for="hashtag" class="col-sm-2 control-label">Hashtag</label>
								<div class="col-sm-10">
									<input type="text" class="form-control" name="hashtag" placeholder="hashtag" id="hashtag">
								</div>
							</div>
							 -->
							<div class="form-group">
								<label for="screenname" class="col-sm-2 control-label">Screenname</label>
								<div class="col-sm-10">
									<input type="text" class="form-control" name="screenname" placeholder="e.g. uniofleicester" id="screenname">
								</div>
							</div>
							<div class="form-group">
								<label for="start" class="col-sm-2 control-label">Start Time</label>
								<div class="col-sm-10">
									<input type="text" class="form-control" name="start" placeholder="start time" id="start">
								</div>
							</div>
							<div class="form-group">
								<label for="end" class="col-sm-2 control-label">End Time</label>
								<div class="col-sm-10">
									<input type="text" class="form-control" name="end" placeholder="end time" id="end">
								</div>
							</div>
							<button type="button" id="search" class="btn btn-success">Search</button>
							<button type="button" id="predict" class="btn btn-default">Predict</button>
						</form>
					</div>
				</div>
				
				<div class="panel panel-info">
					<div class="panel-heading">
		        		Tracking Conditions
		    		</div>
					<div class="panel-body">
						<form class="form-horizontal" id="track_form" role="form" action="tweets/secret/track" method="post">
							<div class="form-group">
								<label for="keywords" class="col-sm-2 control-label">Keywords</label>
								<div class="col-sm-10">
									<input type="text" class="form-control" name="keywords" placeholder="word1 word2 ..." id="keywords">
								</div>
							</div>
							<!-- 
							<div class="form-group">
								<label for="hashtags" class="col-sm-2 control-label">Hashtags</label>
								<div class="col-sm-10">
									<input type="text" class="form-control" name="hashtags" placeholder="hashtag1 hashtag2 ..." id="hashtags">
								</div>
							</div>
							 -->
							<div class="form-group">
								<label for="sn" class="col-sm-2 control-label">Screenname</label>
								<div class="col-sm-10">
									<input type="text" class="form-control" name="sn" placeholder="e.g. uniofleicester" id="sn">
								</div>
							</div>
							<div class="form-group">
								<label for="hour" class="col-sm-2 control-label">Hour</label>
								<div class="col-sm-10">
									<select class="form-control" name="hour" id="hour"><option value="1">1</option><option value="2">2</option><option value="3">3</option><option value="4">4</option><option value="5">5</option><option value="6">6</option><option value="7">7</option><option value="8">8</option><option value="9">9</option><option value="10">10</option><option value="11">11</option><option value="12">12</option><option value="13">13</option><option value="14">14</option><option value="15">15</option><option value="16">16</option><option value="17">17</option><option value="18">18</option><option value="19">19</option><option value="20">20</option><option value="21">21</option><option value="22">22</option><option value="23">23</option><option value="24">24</option></select>
								</div>
							</div>
							<div class="form-group">
								<label for="count" class="col-sm-2 control-label">Count</label>
								<div class="col-sm-10">
									<input type="text" class="form-control" name="count" placeholder="e.g. 1000" id="count">
								</div>
							</div>
							<button type="button" id="track" class="btn btn-success">Track</button>
							<button type="button" id="stop" class="btn btn-danger">Stop</button>
						</form>
					</div>
				</div>
				
			</div>
			<div class="col-sm-6">
				<div class="panel panel-primary">
					<div class="panel-heading">
						Searching Result
					</div>
					<div class="panel-body">
						<table class="table table-striped table-bordered">
							<caption>See Result Here</caption>
							<thead>
							    <tr>
							      <th>User</th>
							      <th>Text</th>
							      <th>Time</th>
							      <th>Retweeted</th>
							      <th>Favorited</th>
							    </tr>
							</thead>
							<tbody id="table_content">
								
							</tbody>
						</table>
					</div>			
					<div class="panel-footer">
						<div class="btn-group btn-group-xs col-sm-offset-5">
							<button type="button" class="btn btn-default" id="last_page">&laquo;</button>
    						<button type="button" class="btn btn-default" disabled>&nbsp;&nbsp;&nbsp;</button>
    						<button type="button" class="btn btn-default" id="next_page">&raquo;</button>
						</div>
					</div>		
				</div>
				
				<div class="panel panel-success">
		    		<div class="panel-heading">
						Prediction Result
					</div>
					<div class="panel-body">
						<div id="chart1"></div>
						<div id="chart2"></div>
					</div>
				</div>
			</div>
		</div>
		
		<!-- alert modal -->
		<div class="modal fade" id="info_modal" tabindex="-1" role="dialog" aria-labelledby="info_title">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						<h5 class="modal-title" id="info_title"></h5>
					</div>
					<div class="modal-body" id="info_content">
					
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-danger" data-dismiss="modal"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span>close</button>
					</div>
				</div>
			</div>
		</div>
		<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.4.1/js/bootstrap-datepicker.min.js"></script>
		<script src="https://www.gstatic.com/charts/loader.js"></script> 
		<script src="js/index1.js"></script>
	</body>
</html>
