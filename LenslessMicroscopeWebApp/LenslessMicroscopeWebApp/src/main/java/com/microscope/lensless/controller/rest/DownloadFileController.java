package com.microscope.lensless.controller.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.microscope.lensless.controller.filestorage.FileStorage;
 
@RestController
public class DownloadFileController {
	
	@Autowired
	FileStorage fileStorage;
	
	/*
	 * List All Files
	 */
	@GetMapping("/api/file/all")
	public List<String> getListFiles() {
		return fileStorage.loadFiles().map(
                path -> MvcUriComponentsBuilder.fromMethodName(DownloadFileController.class,
                        "downloadFile", path.getFileName().toString()).build().toString())
                .collect(Collectors.toList());
	}
	
    /*
     * Download Files
     */
	@GetMapping("/api/file/{filename}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
		Resource file = fileStorage.loadFile(filename);
		return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
					.body(file);	
	}
}
