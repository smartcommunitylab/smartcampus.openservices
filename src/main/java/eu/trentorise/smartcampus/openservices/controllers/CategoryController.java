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

package eu.trentorise.smartcampus.openservices.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.openservices.entities.Category;
import eu.trentorise.smartcampus.openservices.managers.CategoryManager;
import eu.trentorise.smartcampus.openservices.support.ListCategory;

/**
 * @author raman
 *
 */
@Controller
@RequestMapping(value="/api/category")
public class CategoryController {

	@Autowired
	private CategoryManager categoryManager;
	
	@RequestMapping(value="/{category}", method=RequestMethod.GET)
	public @ResponseBody Category getCategoryById(@PathVariable int category) {
		return categoryManager.getCategoryById(category);
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody ListCategory getCategories() {
		return  new ListCategory(categoryManager.getCategories());
	}


	@RequestMapping(value="/add", method=RequestMethod.POST, consumes="application/json")
	public @ResponseBody Category createCategory(@RequestBody Category category) {
		return categoryManager.addCategory(category);
	}
	
	@RequestMapping(value="/modify", method=RequestMethod.PUT, consumes="application/json")
	public @ResponseBody Category modifyCategory(@RequestBody Category category) {
		return categoryManager.modifyCategory(category);
	}

	@RequestMapping(value="/delete/{id}", method=RequestMethod.DELETE)
	public void deleteCategory(@PathVariable int category) {
		categoryManager.deleteCategory(category);
	}

}