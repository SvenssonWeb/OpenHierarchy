/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.daos.implementations.mysql;

import java.sql.SQLException;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.daos.interfaces.ForegroundModuleDAO;
import se.unlogic.hierarchy.core.populators.ForegroundModuleDescriptorPopulator;
import se.unlogic.standardutils.dao.BeanResultSetPopulator;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.dao.querys.IntegerKeyCollector;
import se.unlogic.standardutils.dao.querys.UpdateQuery;

public class MySQLForegroundModuleDAO extends MySQLModuleDAO<SimpleForegroundModuleDescriptor> implements ForegroundModuleDAO {

	private static final ForegroundModuleDescriptorPopulator POPULATOR = new ForegroundModuleDescriptorPopulator();


	protected MySQLForegroundModuleDAO(DataSource ds, MySQLForegroundModuleSettingDAO mySQLForegroundModuleSettingDAO){

		super(mySQLForegroundModuleSettingDAO,ds,"openhierarchy_foreground_modules","openhierarchy_foreground_module_users","openhierarchy_foreground_module_groups");
	}

	public void add(SimpleForegroundModuleDescriptor SimpleForegroundModuleDescriptor) throws SQLException {

		TransactionHandler transactionHandler = null;

		try {
			transactionHandler = new TransactionHandler(dataSource);

			UpdateQuery query = transactionHandler.getUpdateQuery("INSERT INTO openhierarchy_foreground_modules VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			query.setObject(1, SimpleForegroundModuleDescriptor.getModuleID());
			query.setString(2, SimpleForegroundModuleDescriptor.getClassname());
			query.setString(3, SimpleForegroundModuleDescriptor.getName());
			query.setString(4, SimpleForegroundModuleDescriptor.getAlias());
			query.setString(5, SimpleForegroundModuleDescriptor.getDescription());
			query.setString(6, SimpleForegroundModuleDescriptor.getXslPath());

			if (SimpleForegroundModuleDescriptor.getXslPathType() != null) {
				query.setString(7, SimpleForegroundModuleDescriptor.getXslPathType().toString());
			} else {
				query.setString(7, null);
			}

			query.setBoolean(8, SimpleForegroundModuleDescriptor.allowsAnonymousAccess());
			query.setBoolean(9, SimpleForegroundModuleDescriptor.allowsUserAccess());
			query.setBoolean(10, SimpleForegroundModuleDescriptor.allowsAdminAccess());
			query.setBoolean(11, SimpleForegroundModuleDescriptor.isEnabled());
			query.setBoolean(12, SimpleForegroundModuleDescriptor.isVisibleInMenu());
			query.setObject(13, SimpleForegroundModuleDescriptor.getSectionID());
			query.setObject(14, SimpleForegroundModuleDescriptor.getDataSourceID());
			query.setObject(15, SimpleForegroundModuleDescriptor.getStaticContentPackage());
			if(SimpleForegroundModuleDescriptor.getRequiredProtocol() != null) {
				query.setString(16, SimpleForegroundModuleDescriptor.getRequiredProtocol().toString());
			} else {
				query.setString(16, null);
			}

			IntegerKeyCollector keyCollector = new IntegerKeyCollector();

			query.executeUpdate(keyCollector);

			SimpleForegroundModuleDescriptor.setModuleID(keyCollector.getKeyValue());

			if (SimpleForegroundModuleDescriptor.getAllowedUserIDs() != null && !SimpleForegroundModuleDescriptor.getAllowedUserIDs().isEmpty()) {
				this.setUsers(transactionHandler, SimpleForegroundModuleDescriptor);
			}

			if (SimpleForegroundModuleDescriptor.getAllowedGroupIDs() != null && !SimpleForegroundModuleDescriptor.getAllowedGroupIDs().isEmpty()) {
				this.setGroups(transactionHandler, SimpleForegroundModuleDescriptor);
			}

			if (SimpleForegroundModuleDescriptor.getMutableSettingHandler() != null && !SimpleForegroundModuleDescriptor.getMutableSettingHandler().isEmpty()) {
				this.mySQLModuleSettingDAO.set(SimpleForegroundModuleDescriptor, transactionHandler);
			}

			transactionHandler.commit();
		} finally {
			if (transactionHandler != null && !transactionHandler.isClosed()) {
				transactionHandler.abort();
			}
		}
	}

	public void update(SimpleForegroundModuleDescriptor SimpleForegroundModuleDescriptor) throws SQLException {

		TransactionHandler transactionHandler = null;

		try {
			transactionHandler = new TransactionHandler(dataSource);

			UpdateQuery query = transactionHandler.getUpdateQuery("UPDATE openhierarchy_foreground_modules SET name = ?, alias = ?, description = ?, xslPath = ?, xslPathType = ?, anonymousAccess = ?, userAccess = ?, adminAccess = ?, enabled = ?, visibleInMenu = ?, sectionID = ?, classname = ?, dataSourceID = ?, staticContentPackage = ?, requiredProtocol = ? WHERE moduleID = ?");

			query.setString(1, SimpleForegroundModuleDescriptor.getName());
			query.setString(2, SimpleForegroundModuleDescriptor.getAlias());
			query.setString(3, SimpleForegroundModuleDescriptor.getDescription());
			query.setString(4, SimpleForegroundModuleDescriptor.getXslPath());

			if (SimpleForegroundModuleDescriptor.getXslPathType() != null) {
				query.setString(5, SimpleForegroundModuleDescriptor.getXslPathType().toString());
			} else {
				query.setString(5, null);
			}

			query.setBoolean(6, SimpleForegroundModuleDescriptor.allowsAnonymousAccess());
			query.setBoolean(7, SimpleForegroundModuleDescriptor.allowsUserAccess());
			query.setBoolean(8, SimpleForegroundModuleDescriptor.allowsAdminAccess());
			query.setBoolean(9, SimpleForegroundModuleDescriptor.isEnabled());
			query.setBoolean(10, SimpleForegroundModuleDescriptor.isVisibleInMenu());
			query.setObject(11, SimpleForegroundModuleDescriptor.getSectionID());
			query.setString(12, SimpleForegroundModuleDescriptor.getClassname());
			query.setObject(13, SimpleForegroundModuleDescriptor.getDataSourceID());
			query.setObject(14, SimpleForegroundModuleDescriptor.getStaticContentPackage());
			if (SimpleForegroundModuleDescriptor.getRequiredProtocol() != null) {
				query.setString(15, SimpleForegroundModuleDescriptor.getRequiredProtocol().toString());
			} else {
				query.setString(15, null);
			}
			query.setInt(16, SimpleForegroundModuleDescriptor.getModuleID());

			query.executeUpdate();

			this.deleteModuleUsers(transactionHandler, SimpleForegroundModuleDescriptor);

			if (SimpleForegroundModuleDescriptor.getAllowedUserIDs() != null && !SimpleForegroundModuleDescriptor.getAllowedUserIDs().isEmpty()) {
				this.setUsers(transactionHandler, SimpleForegroundModuleDescriptor);
			}

			this.deleteModuleGroups(transactionHandler, SimpleForegroundModuleDescriptor);

			if (SimpleForegroundModuleDescriptor.getAllowedGroupIDs() != null && !SimpleForegroundModuleDescriptor.getAllowedGroupIDs().isEmpty()) {
				this.setGroups(transactionHandler, SimpleForegroundModuleDescriptor);
			}

			this.mySQLModuleSettingDAO.set(SimpleForegroundModuleDescriptor, transactionHandler);

			transactionHandler.commit();
		} finally {
			if (transactionHandler != null && !transactionHandler.isClosed()) {
				transactionHandler.abort();
			}
		}
	}

	@Override
	protected BeanResultSetPopulator<SimpleForegroundModuleDescriptor> getPopulator() {

		return POPULATOR;
	}
}
