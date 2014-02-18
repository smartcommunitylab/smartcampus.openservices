package eu.trentorise.smartcampus.openservices.controllers;

import java.io.*;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import eu.trentorise.smartcampus.openservices.entities.Organization;
import eu.trentorise.smartcampus.openservices.entities.ResponseObject;
import eu.trentorise.smartcampus.openservices.managers.OrganizationManager;

@Controller
@RequestMapping(value="/api/file/")
@PropertySource("classpath:openservice.properties")
public class FileController {
	
	private static final Logger logger = LoggerFactory.getLogger(FileController.class);
	/**
	 * Instance of {@link OrganizationManager}
	 */
	@Autowired
	private OrganizationManager orgManager;
	/**
	 * {@link ResponseObject} Response object contains requested data, 
	 * status of response and if necessary a custom error message.
	 */
	private ResponseObject responseObject;
	/**
	 * Instance of {@link Environment} to get all variables in properties file
	 */
	@Autowired
	private Environment env;
	
	@RequestMapping(value="home")
	public String fileShow(){
		return "file";
	}
	
	@RequestMapping(value = "upload/{organizationId}", method = RequestMethod.POST)
	public @ResponseBody ResponseObject uploadFile(@PathVariable int organizationId, 
			@RequestParam("file") MultipartFile file, HttpServletRequest request) {
		logger.info("-- FILE -- Uploading file ...");
		
		String dirFile = env.getProperty("filedir");
		
		logger.info("Multipart file content type: "+file.getContentType());
		
		responseObject = new ResponseObject();
		logger.info("Request Real Path: "+request.getSession().getServletContext().getRealPath("/"));
		if(!file.isEmpty() && file!=null){
			//logger.info("File "+file);
			try {
				/*
				//check directory if exists
				File dir = new File(dirFile+organizationId);
				if(dir.exists()){
					//check if there is a file
					logger.info(".. Directory already exists..");
					if(dir.listFiles()[0].exists()){
						//delete file
						if(dir.listFiles()[0].delete()){
							logger.info(".. Deleting file ..");
						}
						else{
							logger.info(".. Error in deleting ..");
						}
					}
				}*/
				File f = new File(dirFile+organizationId+"/"
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
				
				//Save new logo relative path in organization
				// /images/org_id/file.getOriginalFilename()
				/*boolean result = orgManager.updateLogo("sara", organizationId, "/images/"+organizationId+
						"/"+file.getOriginalFilename());
				if(result){
					logger.info(".. Organization logo update ");
				}else{
					logger.info(".. Failed to save logo in db... ");
				}*/
				
				String format = new MimetypesFileTypeMap().getContentType(f);
				logger.info(".. Checking image format... "+format);
				
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
	/*
	@RequestMapping(value = "download/{organizationId}", method = RequestMethod.GET)
	public @ResponseBody ResponseObject downloadFile(@PathVariable int organizationId, HttpServletResponse response) {
		logger.info("-- FILE -- Download file ...");
		responseObject = new ResponseObject();
		logger.info("Org id: "+organizationId);//+", filename: "+filename+", extension: "+extension);
		
		String dirFile = env.getProperty("filedir");
		
		try{
			File f = new File(dirFile+organizationId);//+"/"+filename+"."+extension);
			if(!f.exists()){
				logger.info("File does not exist");
				responseObject.setError("File does not exist");
				responseObject.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
			else{
				logger.info("File exists");
				
				File image = new File(dirFile+organizationId+"/"+f.listFiles()[0].getName());
				logger.info("File name: "+image.getName());
				BufferedImage img = ImageIO.read(image);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				String format = new MimetypesFileTypeMap().getContentType(image);

				logger.info("Format image: "+format+" and split: "+format.split("/")[1]);
				ImageIO.write(img,format.split("/")[1], baos);
				baos.flush();
				byte[] imageInByte = baos.toByteArray();
				
				baos.close();
				
				responseObject.setData(format+";base64,"+new String(Base64.encodeBase64(imageInByte,false)));
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
	}*/

}
