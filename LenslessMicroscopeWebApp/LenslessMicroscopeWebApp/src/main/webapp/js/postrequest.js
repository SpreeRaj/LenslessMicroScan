$(document).ready( () => {
    $("#btnSubmit").click((event) => {
        //stop submit the form, we will post it manually.
    	//alert('here');
        event.preventDefault();
        doAjax();
        //doReconstruct();
    });
 
});
 
function doAjax() {
 
    // Get form
    var form = $('#fileUploadForm')[0];
    var data = new FormData(form);
 
    $.ajax({
        type: "POST",
        enctype: 'multipart/form-data',
        url: "/api/file/upload",
        data: data,
        processData: false, //prevent jQuery from automatically transforming the data into a query string
        contentType: false,
        cache: false,
        success: (data) => {
            $("#listFiles").text(data);
            doReconstruct();
        },
        error: (e) => {
            $("#listFiles").text(e.responseText);
        }
    });
}


function doReconstruct(){
	
	var form = $('#phaseRetrievalForm')[0];
    var data = {};
//    data['hologramImageFakePath']=$("#uploadfile").val();
//    data['refrenceImagePathFakePath']=$("#uploadfile2").val();
    data['tolerance']=$("#tolerance").val();
    data['radius']=$("#radius").val();
    data['iterations']=$("#iterations").val();
    data['phase']=$("#phase").prop('checked');
    data['amplitude']=$("#amplitude").prop('checked');
    data['butterworth']=$("#butterworth").prop('checked');
    data['dx']=$("#dx").val();
    data['dy']=$("#dy").val();
    data['wavelength']=$("#wavelength").val();
    data['distance']=$("#distance").val();
    
  //  alert("");
    $.ajax({
        type : "POST",
        url : "api/reconstruct",
        contentType : "application/json",
        dataType : "json",      
        data : JSON.stringify(data),
        error : function(data) {
            alert('Error');
        },
        success : function(data) {  
                alert('Success!');
        }
    });
    }
    
    
    
   // alert("test")
	
