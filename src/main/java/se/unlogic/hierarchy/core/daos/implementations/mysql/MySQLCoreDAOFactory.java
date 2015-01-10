/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.daos.implementations.mysql;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import se.unlogic.hierarchy.core.daos.factories.CoreDaoFactory;
import se.unlogic.hierarchy.core.daos.interfaces.FilterModuleDAO;
import se.unlogic.hierarchy.core.daos.interfaces.FilterModuleSettingDAO;
import se.unlogic.standardutils.db.tableversionhandler.TableUpgradeException;
import se.unlogic.standardutils.db.tableversionhandler.TableVersionHandler;
import se.unlogic.standardutils.db.tableversionhandler.UpgradeResult;
import se.unlogic.standardutils.db.tableversionhandler.XMLDBScriptProvider;

public class MySQLCoreDAOFactory extends CoreDaoFactory {

	private MySQLDataSourceDAO dataSourceDAO;
	private MySQLMenuIndexDAO menuIndexDAO;
	private MySQLForegroundModuleDAO foregroundModuleDAO;
	private MySQLBackgroundModuleDAO backgroundModuleDAO;
	private MySQLFilterModuleDAO filterModuleDAO;
	private MySQLFilterModuleSettingDAO filterModuleSettingDAO;
	private MySQLForegroundModuleSettingDAO foregroundModuleSettingDAO;
	private MySQLBackgroundModuleSettingDAO backgroundModuleSettingDAO;
	private MySQLSectionDAO sectionDAO;
	private MySQLVirtualMenuItemDAO virtualMenuItemDAO;

	@Override
	public void init(DataSource dataSource) throws TableUpgradeException, SQLException, SAXException, IOException, ParserConfigurationException {

		//New automatic table version handling
		UpgradeResult upgradeResult = TableVersionHandler.upgradeDBTables(dataSource, this.getClass().getName(), new XMLDBScriptProvider(this.getClass().getResourceAsStream("MySQL DB script.xml")));

		if(upgradeResult.isUpgrade()){

			log.info(upgradeResult.toString());
		}

		this.dataSourceDAO = new MySQLDataSourceDAO(dataSource);
		this.menuIndexDAO = new MySQLMenuIndexDAO(dataSource);
		this.virtualMenuItemDAO = new MySQLVirtualMenuItemDAO(dataSource);
		this.sectionDAO = new MySQLSectionDAO(dataSource);

		this.backgroundModuleSettingDAO = new MySQLBackgroundModuleSettingDAO(dataSource);
		this.filterModuleSettingDAO = new MySQLFilterModuleSettingDAO(dataSource);
		this.foregroundModuleSettingDAO = new MySQLForegroundModuleSettingDAO(dataSource);

		this.backgroundModuleDAO = new MySQLBackgroundModuleDAO(dataSource, backgroundModuleSettingDAO);
		this.filterModuleDAO = new MySQLFilterModuleDAO(dataSource, filterModuleSettingDAO);
		this.foregroundModuleDAO = new MySQLForegroundModuleDAO(dataSource, foregroundModuleSettingDAO);
	}

	@Override
	public MySQLDataSourceDAO getDataSourceDAO() {

		return dataSourceDAO;
	}

	@Override
	public MySQLMenuIndexDAO getMenuIndexDAO() {

		return menuIndexDAO;
	}

	@Override
	public MySQLForegroundModuleDAO getForegroundModuleDAO() {

		return foregroundModuleDAO;
	}

	@Override
	public MySQLBackgroundModuleDAO getBackgroundModuleDAO() {

		return backgroundModuleDAO;
	}

	@Override
	public MySQLForegroundModuleSettingDAO getForegroundModuleSettingDAO() {

		return foregroundModuleSettingDAO;
	}

	@Override
	public MySQLBackgroundModuleSettingDAO getBackgroundModuleSettingDAO() {

		return backgroundModuleSettingDAO;
	}

	@Override
	public MySQLSectionDAO getSectionDAO() {

		return sectionDAO;
	}

	@Override
	public MySQLVirtualMenuItemDAO getVirtualMenuItemDAO() {

		return virtualMenuItemDAO;
	}

	@Override
	public FilterModuleSettingDAO getFilterModuleSettingDAO() {

		return this.filterModuleSettingDAO;
	}

	@Override
	public FilterModuleDAO getFilterModuleDAO() {

		return this.filterModuleDAO;
	}
}
