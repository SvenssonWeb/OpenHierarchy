package se.unlogic.hierarchy.foregroundmodules.useradmin;

import java.util.List;

import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.interfaces.UserFormCallback;


public class GroupListUserFormCallback implements UserFormCallback {

	private final List<Group> groups;
	
	public GroupListUserFormCallback(List<Group> groups) {

		super();
		this.groups = groups;
	}

	public boolean allowGroupAdministration() {

		return true;
	}

	public List<Group> getAvailableGroups() {

		return groups;
	}

	public Group getGroup(Integer groupID) {


		for(Group group : groups){
			
			if(group.getGroupID().equals(groupID)){
				
				return group;
			}
		}
		
		return null;
	}

	public boolean allowAdminFlagAccess() {

		return false;
	}
}
