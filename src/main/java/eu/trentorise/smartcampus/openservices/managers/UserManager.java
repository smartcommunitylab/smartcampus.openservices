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

import java.net.ConnectException;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.trentorise.smartcampus.openservices.UserRoles;
import eu.trentorise.smartcampus.openservices.beans.EmailMessage;
import eu.trentorise.smartcampus.openservices.dao.TemporaryLinkDao;
import eu.trentorise.smartcampus.openservices.dao.UserDao;
import eu.trentorise.smartcampus.openservices.entities.TemporaryLink;
import eu.trentorise.smartcampus.openservices.entities.User;
import eu.trentorise.smartcampus.openservices.support.ApplicationMailer;
import eu.trentorise.smartcampus.profileservice.model.AccountProfile;

/**
 * Manager that retrieves, adds, modifies and deletes user data calling dao
 * classes.
 * 
 * @author Giulia Canobbio
 * 
 */
@Component
@Transactional
public class UserManager {

	private static final Logger logger = LoggerFactory
			.getLogger(UserManager.class);
	/**
	 * Instance of {@link UserDao} to retrieve data of user.
	 */
	@Autowired
	private UserDao userDao;
	/**
	 * Instance of {@link TemporaryLinkManager} to retrieve data of temporary
	 * link.
	 */
	@Autowired
	private TemporaryLinkDao tlDao;
	/**
	 * Instance of {@link ApplicationMailer} to send email.
	 */
	@Autowired
	private ApplicationMailer mailer;

	@Value("${admin.username}")
	private String adminUsername;

	@Value("${admin.password}")
	private String adminPwd;

	@Value("${admin.email}")
	private String adminEmail;

	@Value("${oauth.active}")
	private boolean isOauthActive;

	@Value("${oauth.usermode.restricted}")
	private boolean restrictedMode;

	@Autowired
	Environment env;

	/**
	 * Get user data by id.
	 * 
	 * @param id
	 *            : int id of user
	 * @return a {@link User} instance
	 */
	public User getUserById(int id) {
		try {
			return userDao.getUserById(id);
		} catch (DataAccessException d) {
			return null;
		}
	}

	/**
	 * Get user by username.
	 * 
	 * @param username
	 *            : String username of user
	 * @return a {@link User} instance
	 */
	public User getUserByUsername(String username) {
		try {
			return userDao.getUserByUsername(username);
		} catch (DataAccessException d) {
			return null;
		}
	}

	@PostConstruct
	@SuppressWarnings("unused")
	private void initAdminAccount() {
		if (userDao.getUserByUsername(adminUsername) == null && !isOauthActive) {
			logger.info("Admin account not found...init with default account");
			User admin = new User();
			admin.setEmail(adminEmail);
			admin.setUsername(adminUsername);
			admin.setPassword(adminPwd);
			admin.setEnabled(1);
			admin.setRole(UserRoles.ROLE_ADMIN.toString());
			userDao.addUser(admin);
			logger.info("Default admin account created!");
		} else {
			logger.info("Admin account already present");
		}
	}

	/**
	 * Add a new user in database. Retrieves new user. Throw SecurityException
	 * if email address is already used.
	 * 
	 * @param user
	 *            : {@link User} instance
	 * @return a {@link User} instance
	 */
	public User createUser(User user, String host, String from, String object,
			String message) throws ConnectException {
		EmailMessage emailMsg = null;
		String s = addKeyVerifyEmail(user.getUsername());
		if (s != null) {
			// return link
			String link = host + "enable/" + s;
			String subject = object + "" + user.getUsername();
			String msg = message + " " + link;
			emailMsg = new EmailMessage(from, subject, msg);
		}
		try {
			return createUser(user, false, UserRoles.ROLE_NORMAL, emailMsg);
		} catch (Exception e) {
			logger.error("Error creating user: {}", e.getMessage());
			return null;
		}
	}

	public User createOauthUser(User user, AccountProfile profile) {
		try {
			UserRoles role = null;
			if (isOauthAdmin(profile)) {
				role = UserRoles.ROLE_ADMIN;
			} else {
				role = restrictedMode ? UserRoles.ROLE_USER
						: UserRoles.ROLE_NORMAL;
			}
			return createUser(user, true, role, null);
		} catch (Exception e) {
			logger.error("Error creating local oauth user: {}", e.getMessage());
			return null;
		}
	}

	public boolean isOauthAdmin(AccountProfile profile) {
		String adminPattern = env.getProperty("oauth.admin", ";;");
		String[] adminP = adminPattern.split(";");
		Map<String, String> attrs = profile.getAttributes().get(adminP[0]);
		if (attrs != null) {
			String value = attrs.get(adminP[1]);
			return StringUtils.equals(value, adminP[2]);
		}
		return false;
	}

	/**
	 * Add a new user in database. He/she has logged in with a social provider
	 * Retrieves new user. Throw SecurityException if email address is already
	 * used.
	 * 
	 * @param user
	 *            : {@link User} instance
	 * @return a {@link User} instance
	 */
	public User createSocialUser(User user) {
		if (!userDao.isEmailAlreadyUse(user.getEmail())) {
			try {
				userDao.addUser(user);
				return userDao.getUserByUsername(user.getUsername());
			} catch (DataAccessException d) {
				return null;
			}
		} else {
			throw new SecurityException();
		}
	}

	private User createUser(User user, boolean enabled, UserRoles role,
			EmailMessage activationEmail) throws Exception {
		if (user != null) {
			if (userDao.isEmailAlreadyUse(user.getEmail())
					|| userDao.getUserByUsername(user.getUsername()) != null) {
				return null;
			}
			user.setEnabled(enabled ? 1 : 0);
			user.setRole(role.toString());
			user = userDao.addUser(user);

			if (activationEmail != null) {
				mailer.sendMail(activationEmail.getFrom(), user.getEmail(),
						activationEmail.getSubject(),
						activationEmail.getMessage());
			}
		}

		return user;
	}

	/**
	 * Modify user data. User can modify only profile data and email.
	 * 
	 * @param username
	 *            : String username of user
	 * @param user
	 *            : {@link User} instance
	 * @return a {@link User} instance
	 */
	public User modifyUserData(String username, User user) {
		try {
			User sessionU = userDao.getUserByUsername(username);
			userDao.modifyUser(sessionU.getId(), user);
			User userN = userDao.getUserById(sessionU.getId());
			return userN;
		} catch (DataAccessException d) {
			return null;
		}

	}

	/**
	 * Enable user. This can be done by verifying email.
	 * 
	 * @param username
	 *            : String username of user
	 * @return a {@link User} instance of enabled user
	 */
	public User enableUser(String username) {
		try {
			User user = userDao.getUserByUsername(username);
			userDao.enableUser(user.getId());
			User userN = userDao.getUserById(user.getId());
			return userN;
		} catch (DataAccessException d) {
			return null;
		}
	}

	/**
	 * Disable a user. Only admin user can disable a user.
	 * 
	 * @param username
	 *            : String username of user that admin wants to disable.
	 * @return a {@link User} instance of disabled user
	 */
	public User disabledUser(String username) {
		try {
			User user = userDao.getUserByUsername(username);
			userDao.disableUser(user.getId());

			User userN = userDao.getUserById(user.getId());
			return userN;
		} catch (DataAccessException d) {
			return null;
		}
	}

	/**
	 * Save in temporary link table, key and email address of user. After doing
	 * registration, link + key value to enable his/her account is sent by
	 * email.
	 * 
	 * @param username
	 *            : String username of user that we want to verify email address
	 * @return String value, it is key value if it is ok, else it is null. In
	 *         case user is enable then a security exception is threw.
	 */
	public String addKeyVerifyEmail(String username) {
		String key;
		try {
			User user = userDao.getUserByUsername(username);
			// check if user is enable or not
			if (user.getEnabled() == 0) {
				// Generate a key
				key = UUID.randomUUID().toString();
				// save in temporary link table
				TemporaryLink tl = new TemporaryLink();
				tl.setId_org(0);
				tl.setKey(key);
				tl.setEmail(user.getEmail());
				tlDao.save(tl);
				return key;

			} else {
				throw new SecurityException();
			}

		} catch (DataAccessException d) {
			return null;
		}
	}

	/**
	 * Enable user account by checking user email and key. After finding key in
	 * database, this object is deleted.
	 * 
	 * @param key
	 *            : String key sent by email for verifying email address
	 * @return {@link User} instance of enabled account otherwise if key does
	 *         not exist then EntityNotFoundException
	 */
	public User enableUserAfterVerification(/* String username, */String key) {
		try {
			// User user = userDao.getUserByUsername(username);
			TemporaryLink tl = tlDao.getTLByKey(key);
			if (tl != null /*
							 * &&
							 * user.getEmail().equalsIgnoreCase(tl.getEmail())
							 */
					&& tl.getId_org() == 0 && tl.getRole() == null) {
				User user = userDao.getUserByEmail(tl.getEmail());
				tlDao.delete(key);
				return enableUser(/* username */user.getUsername());
			} else {
				throw new EntityNotFoundException();
			}
		} catch (DataAccessException d) {
			return null;
		}
	}

	/**
	 * Modify user password. This function retrieves user data and check if
	 * password is the same. If it is correct then user password is modify with
	 * new one, otherwise an exception is thrown.
	 * 
	 * @param username
	 *            : String
	 * @param oldPassw
	 *            : String, old password
	 * @param newPassw
	 *            : String, new password
	 * @return a boolean value: true if there is no error, false otherwise.
	 *         Throw a SecurityException if user's old password is wrong
	 */
	public boolean modifyUserPassword(String username, String oldPassw,
			String newPassw) {
		try {
			// get user data
			User user = userDao.getUserByUsername(username);
			// check if email are equals
			// bcrypt old passw
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			if (oldPassw != null
					&& passwordEncoder.matches(oldPassw, user.getPassword())) {
				// commit
				userDao.modifyPassword(username, newPassw);
				return true;
			} else {
				throw new SecurityException();
			}
		} catch (DataAccessException d) {
			return false;
		}
	}

	/**
	 * Reset user password, and change it with a random string.
	 * 
	 * @param email
	 *            : String
	 * @return temporary password : String
	 */
	public String resetPassword(String email) {
		try {
			User user = userDao.getUserByEmail(email);
			String tPassw = UUID.randomUUID().toString();
			userDao.modifyPassword(user.getUsername(), tPassw);
			return tPassw;
		} catch (DataAccessException d) {
			return null;
		}
	}
}
