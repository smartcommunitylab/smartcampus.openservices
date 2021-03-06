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

package eu.trentorise.smartcampus.openservices.controllers.exec;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Container for the {@link TestBoxAuthHandler} implementations
 * @author raman
 *
 */
@Component
public class TestBoxAuthHandlerFactory {

	@Autowired
	private Map<String, TestBoxAuthHandler> handlers;
	/**
	 * @return the handlers
	 */
	public Map<String, TestBoxAuthHandler> getHandlers() {
		return handlers;
	}

	/**
	 * @param handlers the handlers to set
	 */
	public void setHandlers(Map<String, TestBoxAuthHandler> handlers) {
		this.handlers = handlers;
	}

	public TestBoxAuthHandler getHandler(String name) {
		return handlers.get(name);
	}

}
