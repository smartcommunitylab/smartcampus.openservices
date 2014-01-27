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
package eu.trentorise.smartcampus.openservices.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * Organization Entity for organization table
 * primary key, not null, auto increment int(11) id
 * not null, unique index varchar(45) name
 * varchar(255) description
 * varchar(45) activityArea
 * int(11) category
 * MediumBlob contacts
 * int(11) creator id
 * varchar(255) logo
 * 
 * @author Giulia Canobbio
 *
 */
@Entity
@Table(name="Organization")
public class Organization{
	
	@Id
	@GeneratedValue
	private int id;
	@Column(name="name", unique=true, nullable=false)
	private String name;
	@Column(name="description")
	private String description;
	@Column(name="activityArea")
	private String activityArea;
	@Column(name="category")
	private int category;
	@Column(name="contacts")
	@Lob
	private Contact contacts;
	@Column(name="creator_id")
	private int creatorId;

	@Column(name="logo")
	private String logo;
	
	/**
	 * New {@Organization} instance
	 */
	public Organization(){
		
	}

	/**
	 * Get id of {@Organization} instance
	 * @return int id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Set id in {@Organization} instance
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Get name of {@Organization} instance
	 * @return String name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set name in {@Organization} instance
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get activity area of {@Organization} instance
	 * @return
	 */
	public String getActivityArea() {
		return activityArea;
	}

	/**
	 * Set activity area in {@Organization} instance
	 * @param activityArea
	 */
	public void setActivityArea(String activityArea) {
		this.activityArea = activityArea;
	}

	/**
	 * Get category of {@Organization} instance
	 * @return
	 */
	public int getCategory() {
		return category;
	}

	/**
	 * Set category in {@Organization} instance
	 * @param category
	 */
	public void setCategory(int category) {
		this.category = category;
	}

	/**
	 * Get {@Contact} instance of {@Organization} instance
	 * @return {@Contact} instance
	 */
	public Contact getContacts() {
		return contacts;
	}

	/**
	 * Set {@Contact} instance in {@Organization} instance
	 * @param contacts
	 */
	public void setContacts(Contact contacts) {
		this.contacts = contacts;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the creatorId
	 */
	public int getCreatorId() {
		return creatorId;
	}

	/**
	 * @param creatorId the creatorId to set
	 */
	public void setCreatorId(int creatorId) {
		this.creatorId = creatorId;
	}

	/**
	 * @return the logo
	 */
	public String getLogo() {
		return logo;
	}

	/**
	 * @param logo the logo to set
	 */
	public void setLogo(String logo) {
		this.logo = logo;
	}
}
