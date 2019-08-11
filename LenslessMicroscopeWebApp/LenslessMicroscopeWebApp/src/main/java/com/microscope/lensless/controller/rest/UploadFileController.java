package com.microscope.lensless.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.microscope.lensless.controller.filestorage.FileStorage;
  
@RestController
public class UploadFileController {
	 
	@Autowired 
	FileStorage fileStorage;
	
    /*
     * MultipartFile Upload
     */
    @PostMapping("/api/file/upload")
    public String uploadMultipartFile(@RequestParam("uploadfile") MultipartFile file,@RequestParam("uploadfile2") MultipartFile file2) {
    	try {
    		if(file != null)
    			fileStorage.store(file);
    		if(file2 != null)
    			fileStorage.store(file2);
	    	return "File uploaded successfully! \n 1. " + file.getOriginalFilename() + "\n 2. " + file2.getOriginalFilename();
		} catch (Exception e) {
			return "Error test -> message = " + e.getMessage();
		}    
    }
}