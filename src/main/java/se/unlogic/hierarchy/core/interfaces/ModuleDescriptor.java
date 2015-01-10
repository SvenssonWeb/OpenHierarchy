/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.interfaces;

import java.io.Serializable;
import java.sql.SQLException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.enums.ModuleType;
import se.unlogic.standardutils.xml.Elementable;

public interface ModuleDescriptor extends AccessInterface, Elementable, Serializable{
	
	boolean isEnabled();
	
	String getClassname();

	String getName();

	Element toXML(Document doc);

	Integer getModuleID();

	Integer getDataSourceID();

	MutableSettingHandler getMutableSettingHandler();
	
	/**
	 * This method is used to persist any changes made to the {@link MutableSettingHandler}
	 * 
	 * @param systemInterface
	 * @throws SQLException 
	 */
	public void saveSettings(SystemInterface systemInterface) throws SQLException;
	
	public ModuleType getType();
}
