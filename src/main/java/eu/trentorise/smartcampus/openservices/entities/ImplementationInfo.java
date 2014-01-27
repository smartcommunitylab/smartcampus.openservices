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
package eu.trentorise.smartcampus.openservices.entities;

import java.io.Serializable;
import java.util.List;

/**
 * Blob object for Implementation Info in table Service
 * executive environment
 * service dependencies
 * hosting
 * source code
 * @author Giulia Canobbio
 *
 */
public class ImplementationInfo implements Serializable{
	private static final long serialVersionUID = 1L;

	private String executionEnvironment;
	private List<Integer> serviceDependencies;
	private String hosting;
	private String sourceCode;
	
	/**
	 * new instance of {@Implementation Info}
	 */
	public ImplementationInfo(){
		
	}

	/**
	 * Get executive environment of {@Implementation Info} instance
	 * @return String executive environment
	 */
	public String getExecutionEnvironment() {
		return executionEnvironment;
	}

	/**
	 * Set executive environment in {@Implementation Info} instance
	 * @param executionEnvironment
	 */
	public void setExecutionEnvironment(String executionEnvironment) {
		this.executionEnvironment = executionEnvironment;
	}

	/**
	 * Get source code of {@Implementation Info} instance
	 * @return String source code
	 */
	public String getSourceCode() {
		return sourceCode;
	}

	/**
	 * Set source code in {@Implementation Info} instance
	 * @param sourceCode
	 */
	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	/**
	 * get list of service dependencies of {@Implementation Info} instance
	 * @return List<Integer> service dependencies
	 */
	public List<Integer> getServiceDependencies() {
		return serviceDependencies;
	}

	/**
	 * Set service dependencies in {@Implementation Info} instance
	 * @param serviceDependencies
	 */
	public void setServiceDependencies(List<Integer> serviceDependencies) {
		this.serviceDependencies = serviceDependencies;
	}

	/**
	 * Get hosting of {@Implementation Info} instance
	 * @return String hosting
	 */
	public String getHosting() {
		return hosting;
	}

	/**
	 * Set hosting in {@Implementation Info} instance
	 * @param hosting
	 */
	public void setHosting(String hosting) {
		this.hosting = hosting;
	}
}
