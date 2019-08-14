package com.microscope.lensless.controller.rest;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microscope.lensless.controller.filestorage.FileStorage;
import com.microscope.lensless.controller.reconstruct.ReconstructImage;
import com.microscope.lensless.models.ReconstructRequestBody;

@RestController
public class ReconstructController  {
	 
	@Autowired
	ReconstructImage reconstructImage;
	@Autowired
	FileStorage fileStorage;
    @PostMapping("/api/reconstruct")
//	@RequestMapping(value="/api/reconstruct", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public  ResponseEntity<ReconstructRequestBody>  reconstruct(@RequestBody  ReconstructRequestBody formdata) {
    	
    	//String extensionHologram=formdata.getHologramImageFakePath().substring(formdata.getHologramImageFakePath().lastIndexOf('.'));
    	String rootPath=fileStorage.getRootLocation().toUri().getPath();
    	Stream<Path> list=fileStorage.loadFiles();
    	Iterator<Path> path=list.iterator();
    	String fileName="",hologramPath="",referencePath="";
    	//rootPath.replace('/', '\\');
    	while(path.hasNext()) {
    		fileName=path.next().toString();
    		System.out.println(fileName);
    		if(fileName.indexOf("hologramImage")>-1)
    		{
    			hologramPath=rootPath.substring(1)+fileName;
    			continue;
    		}
    		if(fileName.indexOf("refrenceImage")>-1)
    		{
    			referencePath=rootPath.substring(1)+fileName;
    		}
    	}
    	System.out.println(rootPath);
    	hologramPath="R:\\College\\Project\\Matlab_CODE\\Matlab_CODE\\Petri_dish_simulation_lensfree_hologram.tif";
    	referencePath="R:\\College\\Project\\Matlab_CODE\\Matlab_CODE\\Petri_dish_simulation_lensfree_hologram.tif";
    	//Paths.
    	System.out.println(formdata);
    	reconstructImage.startPlugin();
   	reconstructImage.operate(hologramPath, referencePath, formdata.getDx(), formdata.getDy(), formdata.getDistance(), formdata.getWavelength(), formdata.getTolerance(), formdata.getIterations(), formdata.getRadius(), 
    			formdata.isButterworth(), formdata.isAmplitude(), formdata.isPhase());
    //	reconstructImage.operate(hologramPath, refrencePath, dx, dy, distance, wavelength, tolerance, iterations, sigma, butterworth, amplitude, phase);
		return new ResponseEntity<ReconstructRequestBody>(formdata,HttpStatus.OK);
    }

	
    
   
    
}