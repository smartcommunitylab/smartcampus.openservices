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

package eu.trentorise.smartcampus.openservices;

import javax.servlet.http.HttpServletRequest;

/**
 * Helper methods
 * @author raman
 *
 */
public class Utils {

	/**
	 * Construct the root address of the app using virtual host if any
	 * @param req : instance of {@link HttpServletRequest}
	 * @return root address
	 */
	public static String getAppURL(HttpServletRequest req) {
		String res = req.getScheme()+"://"+ req.getServerName();
		if (req.getServerPort() != 80  && req.getServerPort() != 443) {
			res +=":"+req.getServerPort();
		}
		res += req.getContextPath();//"/"+
		if (!res.endsWith("/")) res += "/";
		return res;
	}

}
