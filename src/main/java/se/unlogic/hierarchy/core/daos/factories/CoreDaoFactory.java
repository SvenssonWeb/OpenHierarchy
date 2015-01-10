/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.daos.factories;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import se.unlogic.hierarchy.core.daos.interfaces.BackgroundModuleDAO;
import se.unlogic.hierarchy.core.daos.interfaces.BackgroundModuleSettingDAO;
import se.unlogic.hierarchy.core.daos.interfaces.DataSourceDAO;
import se.unlogic.hierarchy.core.daos.interfaces.FilterModuleDAO;
import se.unlogic.hierarchy.core.daos.interfaces.FilterModuleSettingDAO;
import se.unlogic.hierarchy.core.daos.interfaces.ForegroundModuleDAO;
import se.unlogic.hierarchy.core.daos.interfaces.ForegroundModuleSettingDAO;
import se.unlogic.hierarchy.core.daos.interfaces.MenuIndexDAO;
import se.unlogic.hierarchy.core.daos.interfaces.SectionDAO;
import se.unlogic.hierarchy.core.daos.interfaces.VirtualMenuItemDAO;

public abstract class CoreDaoFactory {

	protected static Logger log = Logger.getLogger(CoreDaoFactory.class);

	public abstract void init(DataSource dataSource) throws Exception;

	public abstract DataSourceDAO getDataSourceDAO();

	public abstract MenuIndexDAO getMenuIndexDAO();

	public abstract ForegroundModuleDAO getForegroundModuleDAO();

	public abstract BackgroundModuleDAO getBackgroundModuleDAO();

	public abstract ForegroundModuleSettingDAO getForegroundModuleSettingDAO();

	public abstract BackgroundModuleSettingDAO getBackgroundModuleSettingDAO();

	public abstract FilterModuleSettingDAO getFilterModuleSettingDAO();

	public abstract SectionDAO getSectionDAO();

	public abstract VirtualMenuItemDAO getVirtualMenuItemDAO();

	public abstract FilterModuleDAO getFilterModuleDAO();
}
