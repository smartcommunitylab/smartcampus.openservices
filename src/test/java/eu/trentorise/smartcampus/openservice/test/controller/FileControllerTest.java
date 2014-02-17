/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package eu.trentorise.smartcampus.openservice.test.controller;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;

import java.io.File;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.mchange.io.FileUtils;

import eu.trentorise.smartcampus.openservices.entities.ResponseObject;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value= {"file:src/main/webapp/WEB-INF/spring/root-context.xml",
		"file:src/main/webapp/WEB-INF/spring/spring-security.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml",
		"file:src/main/webapp/WEB-INF/spring/spring-embedded-db.xml"})
		@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=false)
public class FileControllerTest {

	private static final String BASE_URL = "http://localhost:8080/openservice/api/file";
	private RestTemplate restTemplate;
	//Log
	private Logger log = LoggerFactory.getLogger(FileControllerTest.class);
	
	@Before
	public void setUp(){
		restTemplate = new RestTemplate();		
	}
	
	@Test
	public void testUploadFile() throws Exception{
		log.info("* Test File REST: /upload - STARTING");
		
		//try create a File
		//File file = new File("src/test/resources/test.txt");

		MultiValueMap<String, Object> mvm = new LinkedMultiValueMap<String, Object>();
		/*log.info("file absolute path: "+file.getAbsolutePath());
		log.info("Can read: "+file.canRead());
		mvm.add("file", new FileSystemResource(file));//logo
		
		log.info("***** POST txt file");
		ResponseEntity<ResponseObject> respEnt = restTemplate.postForEntity(BASE_URL+"/upload/1", mvm,ResponseObject.class);
		
		log.info("Headers: "+respEnt.getHeaders());
		log.info("Body data: "+respEnt.getBody().getData()+", status: "+respEnt.getBody().getStatus()
				+", error: "+respEnt.getBody().getError());
		log.info("Status: "+respEnt.getStatusCode());
		
		assertNotNull("File not found", respEnt.getBody().getData());
		assertTrue("Error in uploading", respEnt.getBody().getStatus()==HttpServletResponse.SC_OK);
		assertNull("Error exists", respEnt.getBody().getError());
		*/
		//upload a jpg
		File file2 = new File("src/test/resources/sample.jpg");
		log.info("file absolute path: "+file2.getAbsolutePath());
		//mvm.clear();
		mvm.add("file", new FileSystemResource(file2));
		
		log.info("***** POST jpg file");
		ResponseEntity<ResponseObject> respEnt2 = restTemplate.postForEntity(BASE_URL+"/upload/1", mvm,ResponseObject.class);
		
		log.info("Headers: "+respEnt2.getHeaders());
		log.info("Body data: "+respEnt2.getBody().getData()+", status: "+respEnt2.getBody().getStatus()
				+", error: "+respEnt2.getBody().getError());
		log.info("Status: "+respEnt2.getStatusCode());
		
		assertNotNull("File not found", respEnt2.getBody().getData());
		assertTrue("Error in uploading", respEnt2.getBody().getStatus()==HttpServletResponse.SC_OK);
		assertNull("Error exists", respEnt2.getBody().getError());
		
	}
	
	@Test
	public void testDownloadFile(){
		log.info("* Test File REST: /download - STARTING");
		/*log.info("Download txt file");
		ResponseObject respEnt = restTemplate.getForObject(BASE_URL+"/download/1", ResponseObject.class);
		
		log.info("Data: "+respEnt.getData()+", status: "+respEnt.getStatus()
				+", error: "+respEnt.getError());
		
		assertNotNull("File not found", respEnt.getData());
		assertTrue("Error in download", respEnt.getStatus()==HttpServletResponse.SC_OK);
		assertNull("Error exists", respEnt.getError());
		*/
		log.info("Download jpg file");
		ResponseObject respEnt2 = restTemplate.getForObject(BASE_URL+"/download/1", ResponseObject.class);
		
		log.info("Data: "+respEnt2.getData()+", status: "+respEnt2.getStatus()
				+", error: "+respEnt2.getError());
		
		assertNotNull("File not found", respEnt2.getData());
		assertTrue("Error in download", respEnt2.getStatus()==HttpServletResponse.SC_OK);
		assertNull("Error exists", respEnt2.getError());
		
	}
}