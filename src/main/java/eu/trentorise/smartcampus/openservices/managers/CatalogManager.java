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
package eu.trentorise.smartcampus.openservices.managers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import eu.trentorise.smartcampus.openservices.Constants.SERVICE_STATE;
import eu.trentorise.smartcampus.openservices.dao.MethodDao;
import eu.trentorise.smartcampus.openservices.dao.OrganizationDao;
import eu.trentorise.smartcampus.openservices.dao.ServiceDao;
import eu.trentorise.smartcampus.openservices.dao.ServiceHistoryDao;
import eu.trentorise.smartcampus.openservices.entities.Category;
import eu.trentorise.smartcampus.openservices.entities.Method;
import eu.trentorise.smartcampus.openservices.entities.Organization;
import eu.trentorise.smartcampus.openservices.entities.Service;
import eu.trentorise.smartcampus.openservices.entities.ServiceHistory;
import eu.trentorise.smartcampus.openservices.support.CategoryServices;

/**
 * 
 * @author Giulia
 *
 */
@Component
@Transactional
public class CatalogManager {
	
	@Autowired
	private ServiceDao serviceDao;
	@Autowired
	private OrganizationDao orgDao;
	@Autowired
	private MethodDao methodDao;
	@Autowired
	private ServiceHistoryDao shDao;
	
	@Autowired
	private CategoryManager categoryManager;
	
	/**
	 * Get list of published and deprecated services
	 * @return all {@link Service} instances
	 */
	public List<Service> catalogServices(){
		List<Service> s = serviceDao.showPublishedService();
		return s;
	}
	
	/**
	 *  Get Service data, searching by id
	 * @param service_id
	 * @return a {@link Service} instance
	 */
	public Service catalogServiceById(int service_id){
		Service s = serviceDao.getServiceById(service_id);
		if(s.getState().equalsIgnoreCase(SERVICE_STATE.UNPUBLISH.toString())){
			s = null;
		}
		return s;
	}
	
	/**
	 * Get list of methods for a given Service.
	 * Search by service id
	 * @param service_id
	 * @return all {@link Method} instances
	 */
	public List<Method> catalogServiceMethods(int service_id){
		List<Method> meth = methodDao.getMethodByServiceId(service_id);
		return meth;
	}
	
	/**
	 * Get list of service history for a given Service.
	 * Search by service id
	 * @param service_id
	 * @return all {@link ServiceHistory} instances
	 */
	public List<ServiceHistory> catalogServiceHistory(int service_id){
		List<ServiceHistory> sh = shDao.getServiceHistoryByServiceId(service_id);
		return sh;
	}
	
	/**
	 * Get list of all services.
	 * Their name contains token
	 * @param token
	 * @return all {@link Service} instances
	 */
	public List<Service> catalogServiceSimpleSearch(String token){
		List<Service> s = serviceDao.searchService(token);
		return s;
	}
	
	/**
	 * Get list of all services, searching by category.
	 * @param category
	 * @return all {@link Service} instances
	 */
	public List<Service> catalogServiceBrowseByCategory(int category){
		List<Service> s = serviceDao.browseService(category, null);
		return s;
	}
	
	/**
	 * @param org
	 * @return
	 */
	public List<Service> catalogServiceBrowseByOrg(int org) {
		List<Service> s = serviceDao.getServiceByIdOrg(org);
		for (Iterator<Service> iterator = s.iterator(); iterator.hasNext();) {
			Service service = iterator.next();
			if (SERVICE_STATE.UNPUBLISH.toString().equals(service.getState())) iterator.remove();
		}
		return s;
	}

	/**
	 * Get list of all services, searching by tags
	 * @param tags
	 * @return all {@link Service} instances
	 */
	public List<Service> catalogServiceBrowseByTags( String tags) {
		List<Service> s = serviceDao.browseService(null, tags);
		return s;
	}
	
	/**
	 * Get list of organizations
	 * @return all {@link Organization} instances
	 */
	public List<Organization> catalogOrg(){
		List<Organization> orgs = orgDao.showOrganizations();
		return orgs;
	}
	
	/**
	 * Simple search for organization.
	 * Their names contains token.
	 * @param token
	 * @return all {@link Organization} instances
	 */
	public List<Organization> catalogOrgSimpleSearch(@PathVariable String token){
		List<Organization> orgs = orgDao.searchOrganization(token);
		return orgs;
	}
	
	/**
	 * Get list of organization searching by category
	 * @param category
	 * @return all {@link Organization} instances
	 */
	public List<Organization> catalogOrgBrowse(@PathVariable int category){
		List<Organization> orgs = orgDao.browseOrganization(category,null);
		return orgs;
	}

	/**
	 * @param id
	 * @return
	 */
	public Organization catalogOrgById(int id) {
		return orgDao.getOrganizationById(id);
	}

	/**
	 * @return
	 */
	public CategoryServices getCategoryServices() {
		CategoryServices res = new CategoryServices();
		List<Category> list = categoryManager.getCategories();
		res.setCategories(list);
		if (res.getCategories() != null) {
			Map<Integer, Integer> counts = serviceDao.findCategoryServices();
			res.setServices(new ArrayList<Integer>());
			for (Category c : res.getCategories()) {
				Integer count = counts.get(c.getId());
				res.getServices().add(count == null ? 0 : count);
			}
		}
		
		return res;
	}

}
