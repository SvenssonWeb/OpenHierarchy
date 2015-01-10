/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import se.unlogic.hierarchy.core.enums.MenuItemType;
import se.unlogic.hierarchy.core.enums.URLType;
import se.unlogic.hierarchy.core.interfaces.AccessInterface;
import se.unlogic.hierarchy.core.interfaces.BundleDescriptor;
import se.unlogic.hierarchy.core.interfaces.MenuItemDescriptor;

public class SimpleBundleDescriptor implements BundleDescriptor {

	private String name;
	private String description;
	private String url;
	private URLType urlType;
	private MenuItemType itemType;
	private String uniqueID;

	protected boolean anonymousAccess;
	protected boolean userAccess;
	protected boolean adminAccess;
	protected Collection<Integer> allowedGroupIDs;
	protected Collection<Integer> allowedUserIDs;

	List<? extends MenuItemDescriptor> menuItemDescriptors;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public URLType getUrlType() {
		return urlType;
	}

	public void setUrlType(URLType urlType) {
		this.urlType = urlType;
	}

	public List<? extends MenuItemDescriptor> getMenuItemDescriptors() {
		return menuItemDescriptors;
	}

	public void setMenuItemDescriptors(ArrayList<? extends MenuItemDescriptor> menuItemDescriptors) {
		this.menuItemDescriptors = menuItemDescriptors;
	}

	public MenuItemType getItemType() {
		return itemType;
	}

	public void setItemType(MenuItemType itemType) {
		this.itemType = itemType;
	}

	public void setAdminAccess(boolean adminAccess) {
		this.adminAccess = adminAccess;
	}

	public boolean allowsAdminAccess() {
		return adminAccess;
	}

	public void setAnonymousAccess(boolean anonymousAccess) {
		this.anonymousAccess = anonymousAccess;
	}

	public boolean allowsAnonymousAccess() {
		return anonymousAccess;
	}

	public void setUserAccess(boolean userAccess) {
		this.userAccess = userAccess;
	}

	public boolean allowsUserAccess() {
		return userAccess;
	}

	public void setAllowedGroupIDs(Collection<Integer> allowedGroupIDs) {
		this.allowedGroupIDs = allowedGroupIDs;
	}

	public Collection<Integer> getAllowedGroupIDs() {
		return allowedGroupIDs;
	}

	public void setAllowedUserIDs(Collection<Integer> allowedUserIDs) {
		this.allowedUserIDs = allowedUserIDs;
	}

	public Collection<Integer> getAllowedUserIDs() {
		return allowedUserIDs;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public String getUniqueID() {
		return uniqueID;
	}

	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}

	public void setAccess(AccessInterface accessInterface) {

		this.allowedGroupIDs = accessInterface.getAllowedGroupIDs();
		this.allowedUserIDs = accessInterface.getAllowedUserIDs();
		this.adminAccess = accessInterface.allowsAdminAccess();
		this.userAccess = accessInterface.allowsUserAccess();
		this.anonymousAccess = accessInterface.allowsAnonymousAccess();
	}
}
