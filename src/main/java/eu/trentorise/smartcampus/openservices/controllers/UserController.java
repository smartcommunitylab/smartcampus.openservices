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
package eu.trentorise.smartcampus.openservices.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import eu.trentorise.smartcampus.openservices.dao.UserDao;
import eu.trentorise.smartcampus.openservices.entities.User;
import eu.trentorise.smartcampus.openservices.support.ApplicationMailer;

@Controller
@RequestMapping(value="/api/user")
public class UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserDao userDao;
	
	/*
	 * USER REST WEB SERVICE
	 */
	
	/**
	 * Retrieve User data by user id
	 * user id is a primary key
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces="application/json") 
	@ResponseBody
	public User getUserById(@PathVariable int id){
		logger.info("-- User Data by Id --");
		return userDao.getUserById(id);
	}
	
	/**
	 * Retrieve User data by username
	 * username is unique
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/my", method = RequestMethod.GET, produces="application/json") 
	@ResponseBody
	public User getUserByUsername(){
		logger.info("-- My User Data--");
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		return userDao.getUserByUsername(username);
	}
	
	/**
	 * Get in input a User data and add it to User table.
	 * Return saved user.
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes="application/json") 
	@ResponseBody
	public User createUser(@RequestBody User user){
		logger.info("-- Add user data --");
		user.setEnabled(0);
		user.setRole("ROLE_NORMAL");
		userDao.addUser(user);
		return userDao.getUserByUsername(user.getUsername());
	}
	
	/**
	 * Verify user email, sending an email with a link to 
	 * a rest service which enable user's account
	 * @param user
	 */
	@RequestMapping(value = "/add/verify", method = RequestMethod.POST, consumes="application/json") 
	@ResponseBody
	public void verifyEmail(@RequestBody User user){
		logger.info("-- User verify email --");
		ApplicationMailer mailer = new ApplicationMailer();
		mailer.sendMail(user.getEmail(), "[OpenService] Welcome!", "For activating your account goes to following link: "
				+ "<choseLink>");
	}
	
	/**
	 * Enable user account
	 */
	@RequestMapping(value = "/add/enable", method = RequestMethod.POST, consumes="application/json") 
	@ResponseBody
	public void enableUser(){
		logger.info("-- User enable --");
		//TODO
	}
	
	/**
	 * Modify user account and update data in db
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/modify", method = RequestMethod.POST, consumes="application/json") 
	@ResponseBody
	public User modifyUserData(@RequestBody User user){
		logger.info("-- User modify --");
		userDao.modifyUser(user);
		return userDao.getUserByUsername(user.getUsername());
	}
	
	/**
	 * Disabled a user by his/her username.
	 * Therefore user cannot login.
	 * Then retrieve disabled user.
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/disable/{username}", method = RequestMethod.GET, produces="application/json") 
	@ResponseBody
	public User disabledUser(@PathVariable String username){
		logger.info("-- User disable --");
		User user = userDao.getUserByUsername(username);
		userDao.disableUser(user);
		return userDao.getUserById(user.getId());
	}
	
}