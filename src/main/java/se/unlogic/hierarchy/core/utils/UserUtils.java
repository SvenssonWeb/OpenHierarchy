package se.unlogic.hierarchy.core.utils;

import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.AttributeHandler;


public class UserUtils {

	public static Group getGroupByAttribute(User user, String attributeName, Object attributeValue){
		
		return getGroupByAttribute(user, attributeName, attributeValue.toString());
	}
	
	public static Group getGroupByAttribute(User user, String attributeName, String attributeValue){
		
		if(user == null || user.getGroups() == null){
			
			return null;
		}
		
		for(Group group : user.getGroups()){
			
			AttributeHandler attributeHandler = group.getAttributeHandler();
			
			if(attributeHandler != null){
				
				String value = attributeHandler.getString(attributeName);
				
				if(value != null && value.equals(attributeValue)){
					
					return group;
				}
			}
		}
		
		return null;
	}
}
