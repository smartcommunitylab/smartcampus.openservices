/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 ******************************************************************************/

package eu.trentorise.smartcampus.openservices.entities;

import java.io.Serializable;
import java.util.Map;

/**
 * @author raman
 *
 */
public class Authentication implements Serializable {
	private static final long serialVersionUID = 1L;

	private String accessProtocol;
	private Map<String,Object> accessAttributes;
	public String getAccessProtocol() {
		return accessProtocol;
	}
	public void setAccessProtocol(String accessProtocol) {
		this.accessProtocol = accessProtocol;
	}
	public Map<String, Object> getAccessAttributes() {
		return accessAttributes;
	}
	public void setAccessAttributes(Map<String, Object> accessAttributes) {
		this.accessAttributes = accessAttributes;
	}
}