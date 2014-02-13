package eu.trentorise.smartcampus.openservices.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import eu.trentorise.smartcampus.openservices.entities.ResponseObject;

@Controller
@RequestMapping(value="/api/file/")
public class FileController {
	
	private static final Logger logger = LoggerFactory.getLogger(FileController.class);
	/**
	 * {@link ResponseObject} Response object contains requested data, 
	 * status of response and if necessary a custom error message.
	 */
	private ResponseObject responseObject;
	
	@RequestMapping(value="home")
	public String fileShow(){
		return "file";
	}
	
	@RequestMapping(value = "upload/{organizationId}", method = RequestMethod.POST)
	public @ResponseBody ResponseObject uploadFile(@PathVariable int organizationId, @RequestParam("file") MultipartFile file,
			HttpServletRequest request) {
		logger.info("-- FILE -- Uploading file ...");
		responseObject = new ResponseObject();
		logger.info("Request Real Path: "+request.getSession().getServletContext().getRealPath("/"));
		if(!file.isEmpty() && file!=null){
			logger.info("File "+file);
			try {
				//src/main/webapp/uploadedFile/
				File f = new File("/uploadedFile/"+organizationId+"/"
						+ file.getOriginalFilename());
				logger.info("Absolute path: "+f.getAbsolutePath());
				//check if this exists
				if(!f.exists()){
					logger.info("Directory does not exist, then creating...");
					if(f.mkdirs()){
						logger.info("Directory created successfully");
					}
				}
				file.transferTo(f);
				responseObject.setData(file.getOriginalFilename());
				responseObject.setStatus(HttpServletResponse.SC_OK);
				logger.info("-- File uploaded correctly --");
			} catch (IllegalStateException e) {
				logger.info("-- Error in uploading file, server error --");
				e.printStackTrace();
				responseObject.setError("Problem in uploading the file.");
				responseObject.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
			} catch (IOException e) {
				logger.info("-- Error in uploading file, problem in reading --");
				e.printStackTrace();
				responseObject.setError("Error: Cannot read file.");
				responseObject.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		}
		else{
			logger.info("-- Error in uploading file, file not found --");
			responseObject.setError("File not found");
			responseObject.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		return responseObject;
	}
	
	@RequestMapping(value = "download/{organizationId}/{filename}/{extension}", method = RequestMethod.GET)
	public @ResponseBody ResponseObject downloadFile(@PathVariable int organizationId, @PathVariable String filename, 
			@PathVariable String extension, HttpServletResponse response) {
		logger.info("-- FILE -- Download file ...");
		responseObject = new ResponseObject();
		logger.info("Org id: "+organizationId+", filename: "+filename+", extension: "+extension);
		
		try{
			File f = new File("/uploadedFile/"+organizationId+"/"+filename+"."+extension);
			if(!f.exists()){
				logger.info("File does not exist");
				responseObject.setError("File does not exist");
				responseObject.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
			else{
				//FileSystemResource fsr = new FileSystemResource(f);
				logger.info("File exists");
				//pass file and not filesystemresource due to message converters error.
				responseObject.setData(f);
				responseObject.setStatus(HttpServletResponse.SC_OK);
				logger.info("Download successfully");
			}
		} catch (Throwable e) {
			logger.info("Download error");
			responseObject.setError("File not found");
			responseObject.setStatus(HttpServletResponse.SC_NOT_FOUND);
			e.printStackTrace();
		}
		
		return responseObject;
	}

}
