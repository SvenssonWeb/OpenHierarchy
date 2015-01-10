package se.unlogic.hierarchy.core.daos.implementations.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.beans.SimpleFilterModuleDescriptor;
import se.unlogic.hierarchy.core.daos.interfaces.FilterModuleDAO;
import se.unlogic.standardutils.dao.AnnotatedDAO;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.dao.QueryParameterFactory;
import se.unlogic.standardutils.dao.SimpleAnnotatedDAOFactory;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.db.DBUtils;


public class MySQLFilterModuleDAO extends AnnotatedDAO<SimpleFilterModuleDescriptor> implements FilterModuleDAO {

	protected MySQLFilterModuleSettingDAO moduleSettingDAO;
	protected QueryParameterFactory<SimpleFilterModuleDescriptor, Integer> moduleIDQueryParameterFactory;
	
	protected final HighLevelQuery<SimpleFilterModuleDescriptor> ENABLED_MODULES_QUERY;
	
	public MySQLFilterModuleDAO(DataSource dataSource, MySQLFilterModuleSettingDAO moduleSettingDAO) {

		super(dataSource, SimpleFilterModuleDescriptor.class, new SimpleAnnotatedDAOFactory());
		this.moduleSettingDAO = moduleSettingDAO;
		
		ENABLED_MODULES_QUERY = new HighLevelQuery<SimpleFilterModuleDescriptor>();
		ENABLED_MODULES_QUERY.addParameter(this.getParamFactory("enabled", boolean.class).getParameter(true));
		
		moduleIDQueryParameterFactory = this.getParamFactory("moduleID", Integer.class);
	}

	@Override
	public void add(SimpleFilterModuleDescriptor moduleDescriptor) throws SQLException {

		TransactionHandler transactionHandler = null;
		
		try{
			transactionHandler = this.createTransaction();
		
			this.add(moduleDescriptor,transactionHandler,null);

			this.moduleSettingDAO.set(moduleDescriptor,transactionHandler);
			
			transactionHandler.commit();
			
		}finally{
			
			TransactionHandler.autoClose(transactionHandler);
		}
	}

	@Override
	public void update(SimpleFilterModuleDescriptor moduleDescriptor) throws SQLException {

		TransactionHandler transactionHandler = null;
		
		try{
			transactionHandler = this.createTransaction();
		
			this.update(moduleDescriptor,transactionHandler,null);

			this.moduleSettingDAO.set(moduleDescriptor,transactionHandler);
			
			transactionHandler.commit();
			
		}finally{
			
			TransactionHandler.autoClose(transactionHandler);
		}
	}

	public List<SimpleFilterModuleDescriptor> getEnabledModules() throws SQLException {

		Connection connection = null;
		
		try{
			connection = this.dataSource.getConnection();
			
			List<SimpleFilterModuleDescriptor> modules = this.getAll(ENABLED_MODULES_QUERY, connection);
			
			if(modules != null){
				
				getSettingHandlers(modules, connection);
			}
			
			return modules;
			
		}finally{
			
			DBUtils.closeConnection(connection);
		}
	}

	public List<SimpleFilterModuleDescriptor> getModules() throws SQLException {

		Connection connection = null;
		
		try{
			connection = this.dataSource.getConnection();
			
			List<SimpleFilterModuleDescriptor> modules = this.getAll((HighLevelQuery<SimpleFilterModuleDescriptor>)null, connection);
			
			if(modules != null){
				
				getSettingHandlers(modules, connection);
			}
			
			return modules;
			
		}finally{
			
			DBUtils.closeConnection(connection);
		}
	}
	
	public SimpleFilterModuleDescriptor getModule(Integer moduleID) throws SQLException {

		Connection connection = null;
		
		try{
			connection = this.dataSource.getConnection();
			
			HighLevelQuery<SimpleFilterModuleDescriptor> query = new HighLevelQuery<SimpleFilterModuleDescriptor>();
			
			query.addParameter(moduleIDQueryParameterFactory.getParameter(moduleID));
			
			SimpleFilterModuleDescriptor moduleDescriptor = this.get(query, connection);
			
			if(moduleDescriptor != null){
				
				getSettingHandlers(Collections.singletonList(moduleDescriptor), connection);
			}
			
			return moduleDescriptor;
			
		}finally{
			
			DBUtils.closeConnection(connection);
		}
	}
	
	protected void getSettingHandlers(List<SimpleFilterModuleDescriptor> modules, Connection connection) throws SQLException{
		
		for(SimpleFilterModuleDescriptor moduleDescriptor : modules){
			
			this.moduleSettingDAO.getSettingsHandler(moduleDescriptor, connection);
		}
	}	
}
