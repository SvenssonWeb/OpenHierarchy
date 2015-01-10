package se.unlogic.hierarchy.foregroundmodules.groupproviders;

import java.sql.SQLException;

import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.beans.MutableGroup;
import se.unlogic.hierarchy.core.interfaces.MutableGroupProvider;

public abstract class AnnotatedMutableGroupProviderModule<GroupType extends MutableGroup> extends AnnotatedGroupProviderModule<GroupType> implements MutableGroupProvider {

	public AnnotatedMutableGroupProviderModule(Class<GroupType> groupClass) {

		super(groupClass);
	}

	@SuppressWarnings("unchecked")
	public void addGroup(Group group) throws SQLException {

		groupDAO.add((GroupType) group);

		setGroupProviderID((GroupType)group);
	}

	@SuppressWarnings("unchecked")
	public void updateGroup(Group group, boolean updateAttributes) throws SQLException {

		groupDAO.update((GroupType) group, updateAttributes);
	}

	@SuppressWarnings("unchecked")
	public void deleteGroup(Group group) throws SQLException {

		groupDAO.delete((GroupType) group);
	}

	public boolean canAddGroupClass(Class<? extends Group> groupClass) {

		return this.groupClass.equals(groupClass);
	}
}
