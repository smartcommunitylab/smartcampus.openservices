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
import org.springframework.dao.DataAccessException;
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
		try{
			return serviceDao.showPublishedService();
			
		}catch(DataAccessException d){
			return null;
		}
	}
	
	/**
	 *  Get Service data, searching by id
	 * @param service_id
	 * @return a {@link Service} instance
	 */
	public Service catalogServiceById(int service_id){
		Service s=new Service();
		try{
			s = serviceDao.getServiceById(service_id);
			if(s!=null && s.getState().equalsIgnoreCase(SERVICE_STATE.UNPUBLISH.toString())){
				s = null;
			}
		}
		catch(DataAccessException d){
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
		try{
			return methodDao.getMethodByServiceId(service_id);			
		}catch(DataAccessException e){
			return null;
		}
	}
	
	/**
	 * Get list of service history for a given Service.
	 * Search by service id
	 * @param service_id
	 * @return all {@link ServiceHistory} instances
	 */
	public List<ServiceHistory> catalogServiceHistory(int service_id){
		try{
			return shDao.getServiceHistoryByServiceId(service_id);
		}catch(DataAccessException d){
			return null;
		}
	}
	
	/**
	 * Get list of all services.
	 * Their name contains token
	 * @param token
	 * @return all {@link Service} instances
	 */
	public List<Service> catalogServiceSimpleSearch(String token){
		try{
			return serviceDao.searchService(token);
		}catch(DataAccessException d){
			return null;
		}
	}
	
	/**
	 * Get list of all services, searching by category.
	 * @param category
	 * @return all {@link Service} instances
	 */
	public List<Service> catalogServiceBrowseByCategory(int category){
		try{
			return serviceDao.browseService(category, null);
		}catch(DataAccessException d){
			return null;
		}
	}
	
	/**
	 * @param org
	 * @return
	 */
	public List<Service> catalogServiceBrowseByOrg(int org) {
		List<Service> s = new ArrayList<Service>();
		try {
			s = serviceDao.getServiceByIdOrg(org);
			if (s != null) {
				for (Iterator<Service> iterator = s.iterator(); iterator.hasNext();) {
					Service service = iterator.next();
					if (SERVICE_STATE.UNPUBLISH.toString().equals(
							service.getState()))
						iterator.remove();
				}
			}
		} catch (DataAccessException d) {
			s = null;
		}
		return s;
	}

	/**
	 * Get list of all services, searching by tags
	 * @param tags
	 * @return all {@link Service} instances
	 */
	public List<Service> catalogServiceBrowseByTags( String tags) {
		try{
			return serviceDao.browseService(null, tags);
		}catch(DataAccessException d){
			return null;
		}
	}
	
	/**
	 * Get list of organizations
	 * @return all {@link Organization} instances
	 */
	public List<Organization> catalogOrg(){
		try{
			return orgDao.showOrganizations();
		}catch(DataAccessException d){
			return null;
		}
	}
	
	/**
	 * Simple search for organization.
	 * Their names contains token.
	 * @param token
	 * @return all {@link Organization} instances
	 */
	public List<Organization> catalogOrgSimpleSearch(@PathVariable String token){
		try{
			return orgDao.searchOrganization(token);
		}catch(DataAccessException d){
			return null;
		}
	}
	
	/**
	 * Get list of organization searching by category
	 * @param category
	 * @return all {@link Organization} instances
	 */
	public List<Organization> catalogOrgBrowse(@PathVariable int category){
		try{
			return orgDao.browseOrganization(category,null);
		}catch(DataAccessException d){
			return null;
		}
	}

	/**
	 * @param id
	 * @return
	 */
	public Organization catalogOrgById(int id) {
		try{
			return orgDao.getOrganizationById(id);
		}catch(DataAccessException d){
			return null;
		}
	}

	/**
	 * @return
	 */
	public CategoryServices getCategoryServices() {
		CategoryServices res = new CategoryServices();
		try {
			List<Category> list = categoryManager.getCategories();
			res.setCategories(list);
			if (res.getCategories() != null) {
				Map<Integer, Integer> counts = serviceDao
						.findCategoryServices();
				res.setServices(new ArrayList<Integer>());
				for (Category c : res.getCategories()) {
					Integer count = counts.get(c.getId());
					res.getServices().add(count == null ? 0 : count);
				}
			}
		} catch (DataAccessException d) {
			res = null;
		}
		
		return res;
	}

}
