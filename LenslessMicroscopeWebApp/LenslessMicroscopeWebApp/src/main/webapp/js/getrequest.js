$( document ).ready( () => {
	// GET REQUEST
	$("#btnGetFiles").click( (event) => {
		event.preventDefault();
		ajaxGet();
	});
	
	// DO GET
	function ajaxGet(){
		$.ajax({
			type : "GET",
			dataType: "json",	
			url : "/api/file/all",
			success: (data) => {
				//clear old data
				$("#listFiles").html("");
				
				/*
					render list of files
				*/
				$("#listFiles").append('<ul>');
				$.each(data, (index, fileUrl) => {
					var filename = fileUrl.split('\\').pop().split('/').pop();
					$("#listFiles").append('<li><a href=' + fileUrl + '>' + filename + '</a></li>');
				});
				$("#listFiles").append('</ul>');
			},
			error : (err) => {
				$("#listFiles").html(err.responseText);
			}
		});	
	}
})