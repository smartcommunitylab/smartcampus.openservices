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
package eu.trentorise.smartcampus.openservices.support;

/**
 * Class for cookie user.
 * 
 * @author Giulia Canobbio
 *
 */
public class CookieUser {
	
	private String username;
	private String role;
	/**
	 * Get username.
	 * 
	 * @return String username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * Set username.
	 * 
	 * @param username 
	 * 			: String
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * Get role.
	 * 
	 * @return String role
	 */
	public String getRole() {
		return role;
	}
	/**
	 * Set role.
	 * 
	 * @param role 
	 * 			: String
	 */
	public void setRole(String role) {
		this.role = role;
	}
	 /**
	  * toString function
	  */
	@Override
	public String toString(){
		return "DataObject [username="+username+", role="+role+"]";
	}

}
