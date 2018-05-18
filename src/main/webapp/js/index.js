var model = "<tr><td><img src='forsrc' class='img-circle' onclick='predictPerson(this,event)' onmouseout='mouseout()'><span class='sr-only'>forUserId</span><span class='sr-only'>forUserName</span><br><span>forscreenname</span></td><td>fortext</td><td>fortime</td><td>forretweeted</td><td>forfavorited</td></tr>"
$(function(){
	$("#register").click(function(){
		window.location.href="pages/register.html";
	});
	$("#start").datepicker({
		format:"yyyy-mm-dd",
		autoclose:true
	});
	$("#end").datepicker({
		format:"yyyy-mm-dd",
		autoclose:true
	});
	var search_flag = 0;
	var json_data = [];
	var current_data = 0;
	$("#search").click(function(){
		json_data = [];
		current_data = 0;
		var params = {
			keyword:$("#keyword").val(),
			screenname:$("#screenname").val(),
			start:$("#start").val(),
			end:$("#end").val()
		};
		if(params.screenname.replace(/(^\s*)|(\s*$)/g, "").length ==0)
			search_flag = 1;
		else
			search_flag = 0;
		$.ajax({
			type:"post",
			url:"tweets/search",
			data:params,
			dataType:"json",
			success:function(data){
				json_data.push(data);
				var content = changeToHtml(json_data[0]);
				$("#table_content").html(content);
			}
		});
	});
	$("#next_page").click(function(){
		if(search_flag==0) return;
		if(current_data+1<json_data.length){
			current_data++;
			var content = changeToHtml(json_data[current_data]);
			$("#table_content").html(content);
			return;
		}
		$.ajax({
			type:"post",
			url:"tweets/nextPage",
			dataType:"json",
			success:function(data){
				if(data.error){
					$("#info_tile").text("Warning");
					$("#info_content").text(data.error);
					$("#info_modal").modal();
				}else{
					json_data.push(data.news);
					current_data++;
					var content = changeToHtml(json_data[current_data]);
					$("#table_content").html(content);
				}
			}
		});
	});
	$("#last_page").click(function(){
		if(search_flag==0||current_data<=0) return;
		current_data--;
		var content = changeToHtml(json_data[current_data]);
		$("#table_content").html(content);
	});
	$("#track").click(function(){
		if(document.getElementById("login_form")){
			$("#info_title").text("Inform");
			$("#info_content").text("Please Login First!");
			$("#info_modal").modal();
			return;
		}
		var params = {
			keywords:$("#keywords").val(),
			screenname:$("#sn").val(),
			hour:$("#hour").val(),
			count:$("#count").val()
		};
		$.ajax({
			type:"post",
			url:"tweets/secret/track",
			data:params,
			success:function(data){
				$("#info_title").text("Inform");
				$("#info_content").text(data);
				$("#info_modal").modal();
			}
		});
	});
	$("#stop").click(function(){
		$.ajax({
			type:"post",
			url:"tweets/secret/stop",
			success:function(data){
				$("#info_title").text("Inform");
				$("#info_content").text(data);
				$("#info_modal").modal();
			}
		});
	});
	$("#predict").click(function(){
		if(search_flag==0) return;
		var params = {
			keyword:$("#keyword").val(),
			start:$("#start").val(),
			end:$("#end").val()
		};
		$.ajax({
			type:"post",
			url:"tweets/predict",
			data:params,
			dataType:"json",
			success:function(data){
				google.charts.load('current', {'packages':['corechart']});
				google.charts.setOnLoadCallback(function(){
					var pie1data = google.visualization.arrayToDataTable([
					                      ['Task','None'],
                                          ['male',data.male],
                                          ['female',data.female],
                                          ['institute',data.totalNames-data.male-data.female]
					                      ]);
					var pie1options = {'title':'Gender Analysis'};
					var chart1 = new google.visualization.PieChart(document.getElementById('chart1'));
					chart1.draw(pie1data, pie1options);
					var pie2data = google.visualization.arrayToDataTable([['Task','None'],['positive',data.positive],['negative',data.negative]]);
					var pie2options = {'title':'Sentiment Analysis'};
					var chart2 = new google.visualization.PieChart(document.getElementById('chart2'));
					chart2.draw(pie2data, pie2options);
				});
			}
		});
	});
});
function changeToHtml(data){
	var content = "";
	$.each(data,function(i,n){
		content = content + model.replace("forsrc",n.twitterUser.profileImageUrlHttps).replace("forUserId",n.twitterUser.id).replace("forUserName",n.twitterUser.name).replace("forscreenname",n.twitterUser.screenname).replace("fortext",n.text).replace("fortime",n.createdAt).replace("forretweeted",n.retweetCount).replace("forfavorited",n.favouriteCount);
	});
	return content;
}
function predictPerson(obj,e){
	var id = $(obj).next().html();
	var screenname = $(obj).next().next().next().next().html();
	var name = $(obj).next().next().html();
	var params = {"screenname":screenname,"name":name};
	$("#predictPerson").html("predicting...");
	$("#predictPerson").css("display","block");
	$("#predictPerson").css("left",e.pageX);
	$("#predictPerson").css("top",e.pageY);
	$.ajax({
		type:"post",
		url:"tweets/predictPerson",
		data:params,
		dataType:"json",
		success:function(data){
			var content = "id:"+id+"<br>name:"+name+"<br>gender:";
			if(data.gender>0.5) content+="male";
			else if(data.gender<0.5) content+="female";
			else content+="unknown";
			if(data.sentiment>0.65) content+="<br>recent sentiment:positive";
			else if(data.sentiment<0.35) content+="<br>recent sentiment:positive";
			else content+="<br>recent sentiment:neutral";
			$("#predictPerson").html(content);
		}
	});
}
function mouseout(){
	$("#predictPerson").css("display","none");
}