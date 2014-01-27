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
package eu.trentorise.smartcampus.openservices.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.trentorise.smartcampus.openservices.entities.UserRole;

/**
 * User Role Dao Implementation
 * Retrieve, Add and Delete user's role in an organization
 * @author Giulia
 *
 */
@Repository
public class UserRoleDaoImpl implements UserRoleDao{
	
	@PersistenceContext(unitName="JpaPersistenceUnit")
	protected EntityManager entityManager;

	/**
	 * 
	 * @return entity manager
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * Set entity manager
	 * @param entityManager
	 */
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * Retrieve all user role of a specified user
	 * @param int user id
	 * @return list of {@UserRole} instances
	 * @throws DataAccessException
	 */
	@Transactional
	@Override
	public List<UserRole> getUserRoleByIdUser(int id_user) throws DataAccessException{
		Query q = getEntityManager().createQuery("FROM UserRole UR WHERE id_user=:id_user")
				.setParameter("id_user", id_user);
		List<UserRole> ur = q.getResultList();
		return ur;
	}

	/**
	 * Retrieve all user role for a specific organization
	 * @param int organization id
	 * @return list of {@UserRole} instances
	 * @throws DataAccessException
	 */
	@Transactional
	@Override
	public List<UserRole> getUserRoleByIdOrg(int id_org) throws DataAccessException{
		Query q = getEntityManager().createQuery("FROM UserRole UR WHERE id_org=:id_org")
				.setParameter("id_org", id_org);
		List<UserRole> ur = q.getResultList();
		return ur;
	}

	/**
	 * Add a new role for a user in an organization
	 * @param int user id
	 * @param int organization id
	 * @param String role
	 * @throws DataAccessException
	 */
	@Transactional
	@Override
	public void createUserRole(int user_id, int org_id, String role) throws DataAccessException{
		UserRole ur = new UserRole(user_id, org_id, role);
		getEntityManager().persist(ur);
		
	}

	/**
	 * Retrieve all roles for a user
	 * @param int user id
	 * @return list of roles
	 * @throws DataAccessException
	 */
	@Transactional
	@Override
	public List<String> getRoleOfUser(int user_id) throws DataAccessException{
		Query q = getEntityManager().createQuery("SELECT UR.role FROM UserRole UR WHERE UR.id_user=:id_user")
				.setParameter("id_user", user_id);
		List<UserRole> ur = q.getResultList();
		if (ur.size() == 0) {
			return null;
		} else {
			List<String> roles = new ArrayList<String>();
			for (int i = 0; i < ur.size(); i++) {
				roles.add(ur.get(i).getRole());
			}
			return roles;
		}
	}
	
	/**
	 * Retrieve user's role in a specific organization
	 * @param int user id
	 * @param int organization id
	 * @return {@UserRole} instances
	 * @throws DataAccessException
	 */
	@Transactional
	@Override
	public UserRole getRoleOfUser(int user_id, int org_id) throws DataAccessException{
		Query q = getEntityManager().createQuery("FROM UserRole UR WHERE UR.id_user=:id_user AND UR.id_org=:id_org")
				.setParameter("id_user", user_id).setParameter("id_org", org_id);
		List<UserRole> ur = q.getResultList();
		if(ur.size()==0){
			return null;
		}
		else return ur.get(0);
	}

	/**
	 * Delete an existing user's role
	 * This can happend because organization is deleted
	 * or user is removed from an organization
	 * @param {@UserRole} instances
	 * @throws DataAccessException
	 */
	@Transactional
	@Override
	public void deleteUserRole(UserRole ur) throws DataAccessException{
		getEntityManager().remove(getEntityManager().merge(ur));
		
	}

	/**
	 * Retrieve all user's role data.
	 * Searching by user id and role.
	 * @param int user id
	 * @param String role
	 * @return list of {@UserRole} instances
	 * @throws DataAccessException
	 */
	@Transactional
	@Override
	public List<UserRole> getUserRoleByIdRole(int user_id, String role)
			throws DataAccessException {
		Query q = getEntityManager().createQuery("UserRole UR WHERE UR.id_user=:id_user AND UR.role=:role")
				.setParameter("id_user", user_id).setParameter("role", role);
		List<UserRole> ur = q.getResultList();
		return ur;
	}
}
