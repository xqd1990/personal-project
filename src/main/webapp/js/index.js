var model = "<tr><td><img src='forsrc' class='img-circle'><br><span>forscreenname</span></td><td>fortext</td><td>fortime</td><td>forretweeted</td><td>forfavorited</td></tr>"
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
		
	});
});
function changeToHtml(data){
	var content = "";
	$.each(data,function(i,n){
		content = content + model.replace("forsrc",n.twitterUser.profileImageUrlHttps).replace("forscreenname",n.twitterUser.screenname).replace("fortext",n.text).replace("fortime",n.createdAt).replace("forretweeted",n.retweetCount).replace("forfavorited",n.favouriteCount);
	});
	return content;
}