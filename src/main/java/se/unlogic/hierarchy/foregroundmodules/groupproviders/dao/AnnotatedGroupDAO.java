package se.unlogic.hierarchy.foregroundmodules.groupproviders.dao;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.standardutils.dao.AnnotatedDAO;
import se.unlogic.standardutils.dao.AnnotatedDAOFactory;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.dao.QueryOperators;
import se.unlogic.standardutils.dao.QueryParameterFactory;
import se.unlogic.standardutils.dao.RelationQuery;
import se.unlogic.standardutils.dao.querys.ArrayListQuery;
import se.unlogic.standardutils.dao.querys.ObjectQuery;
import se.unlogic.standardutils.enums.Order;
import se.unlogic.standardutils.populators.CharacterPopulator;
import se.unlogic.standardutils.populators.IntegerPopulator;


public class AnnotatedGroupDAO<GroupType extends Group> extends AnnotatedDAO<GroupType>{

	private final QueryParameterFactory<GroupType, Integer> groupIDParamFactory;
	private final QueryParameterFactory<GroupType, String> nameParamFactory;

	private final Field attributesRelation;	
	
	public AnnotatedGroupDAO(DataSource dataSource, Class<GroupType> beanClass, AnnotatedDAOFactory daoFactory, Field attributesRelation) {

		super(dataSource, beanClass, daoFactory);

		groupIDParamFactory = this.getParamFactory("groupID", Integer.class);
		nameParamFactory = this.getParamFactory("name", String.class);
		this.attributesRelation = attributesRelation; 
	}

	public GroupType getGroup(int groupID, boolean attributes) throws SQLException {

		HighLevelQuery<GroupType> query = new HighLevelQuery<GroupType>();

		query.addParameter(groupIDParamFactory.getParameter(groupID));
		
		setQueryRelations(query, attributes);

		return this.get(query);
	}

	public List<GroupType> getGroups(boolean attributes) throws SQLException {

		if(attributes){
			
			HighLevelQuery<GroupType> query = new HighLevelQuery<GroupType>();
			
			setQueryRelations(query, attributes);
			
			return this.getAll(query);
		}
		
		return this.getAll();
	}

	public List<GroupType> searchGroups(String queryTerm, boolean attributes) throws SQLException {

		HighLevelQuery<GroupType> query = new HighLevelQuery<GroupType>();

		query.addParameter(nameParamFactory.getParameter("%" + queryTerm + "%", QueryOperators.LIKE));

		setQueryRelations(query, attributes);
		
		return this.getAll(query);
	}

	public List<GroupType> getGroups(List<Integer> groupIDList, boolean attributes) throws SQLException {

		HighLevelQuery<GroupType> query = new HighLevelQuery<GroupType>();

		query.addParameter(groupIDParamFactory.getWhereInParameter(groupIDList));

		setQueryRelations(query, attributes);
		
		return this.getAll(query);
	}

	public Integer getGroupCount() throws SQLException {

		return new ObjectQuery<Integer>(dataSource, true, "SELECT COUNT(groupID) FROM " + this.getTableName(), IntegerPopulator.getPopulator()).executeQuery();
	}

	public Integer getDisabledGroupCount() throws SQLException {

		return new ObjectQuery<Integer>(dataSource, true, "SELECT COUNT(groupID) FROM " + this.getTableName() + " WHERE enabled = false", IntegerPopulator.getPopulator()).executeQuery();
	}

	public List<GroupType> getGroups(Order order, char startsWith, boolean attributes) throws SQLException {

		HighLevelQuery<GroupType> query = new HighLevelQuery<GroupType>();

		query.addParameter(nameParamFactory.getParameter(startsWith + "%", QueryOperators.LIKE));

		query.addOrderByCriteria(this.getOrderByCriteria("name", order));

		setQueryRelations(query, attributes);
		
		return this.getAll(query);
	}

	public List<Character> getGroupFirstLetterIndex() throws SQLException {

		return new ArrayListQuery<Character>(dataSource, true, "SELECT DISTINCT UPPER(LEFT(name, 1)) as letter FROM " + this.getTableName() + " ORDER BY letter ", CharacterPopulator.getPopulator()).executeQuery();
	}
	
	private void setQueryRelations(RelationQuery query, boolean attributes) {

		if(attributes && attributesRelation != null){
			
			query.addRelation(attributesRelation);
		}
	}
	
	@Override
	public void add(GroupType group) throws SQLException {
		
		RelationQuery query = new RelationQuery();
		
		this.setQueryRelations(query, true);
		
		this.add(group, query);
	}
	
	public void update(GroupType group, boolean updateAttributes) throws SQLException {
		
		RelationQuery query = new RelationQuery();
		
		this.setQueryRelations(query, updateAttributes);
		
		this.update(group, query);
	}
}
