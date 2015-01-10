package se.unlogic.hierarchy.core.interfaces;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.standardutils.enums.Order;


public interface GroupProvider extends Prioritized{

	public Group getGroup(Integer groupID, boolean attributes) throws SQLException;

	public List<? extends Group> searchGroups(String query, boolean attributes) throws SQLException;

	public List<? extends Group> getGroups(boolean attributes) throws SQLException;

	public List<? extends Group> getGroups(List<Integer> groupIDs, boolean attributes) throws SQLException;

	public int getGroupCount() throws SQLException;

	public int getDisabledGroupCount() throws SQLException;

	public List<? extends Group> getGroups(Order order, char startsWith, boolean attributes) throws SQLException;

	public List<Character> getGroupFirstLetterIndex() throws SQLException;

	public boolean isProviderFor(Group group);

	public DataSource getDataSource();
}
