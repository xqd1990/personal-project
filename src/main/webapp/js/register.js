$(function(){
	$("#birth").datepicker({
		format:"dd/mm/yyyy"
	});
	$("#cancel").click(function(){
		window.history.back();
	});
	$("#reg_form").validate({
		rules:{
			name:{
				required:true
			}
		}
	});
});