package se.unlogic.hierarchy.core.utils.usergrouplist;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.handlers.GroupHandler;
import se.unlogic.hierarchy.core.handlers.UserHandler;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.json.JsonArray;
import se.unlogic.standardutils.json.JsonObject;
import se.unlogic.standardutils.json.JsonUtils;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.webutils.http.HTTPUtils;
import se.unlogic.webutils.http.URIParser;


public class UserGroupListConnector {
	
	private final Logger log = Logger.getLogger(this.getClass());
	
	private final UserHandler userHandler;
	private final GroupHandler groupHandler;
	
	
	public UserGroupListConnector(UserHandler userHandler, GroupHandler groupHandler) {

		super();
		this.userHandler = userHandler;
		this.groupHandler = groupHandler;
	}

	protected static void sendEmptyJSONResponse(HttpServletResponse res) throws IOException {

		JsonObject jsonObject = new JsonObject(1);
		jsonObject.putField("hitCount", "0");
		HTTPUtils.sendReponse(jsonObject.toJson(), JsonUtils.getContentType(), res);
	}
	
	protected void sendJSONResponse(JsonArray jsonArray, HttpServletResponse res) throws IOException{
		JsonObject jsonObject = new JsonObject(2);
		jsonObject.putField("hitCount", Integer.toString(jsonArray.size()));
		jsonObject.putField("hits", jsonArray);
		HTTPUtils.sendReponse(jsonObject.toJson(), JsonUtils.getContentType(), res);
	}
	
	@WebPublic(alias="users")
	public ForegroundModuleResponse getUsers(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {

		String query = req.getParameter("q");

		if(StringUtils.isEmpty(query)){

			sendEmptyJSONResponse(res);
			return null;
		}

		List<User> users = userHandler.searchUsers(query, false, false);

		log.info("User " + user + " searching for users using query " + query + ", found " + CollectionUtils.getSize(users) + " hits");

		if(users == null){

			sendEmptyJSONResponse(res);
			return null;
		}

		JsonArray jsonArray = new JsonArray();

		for(User currentUser : users){

			JsonObject instance = new JsonObject(2);
			instance.putField("ID", currentUser.getUserID().toString());
			instance.putField("Name", getUserNameString(currentUser));
			instance.putField("Email", currentUser.getEmail());
			
			jsonArray.addNode(instance);
		}
		
		sendJSONResponse(jsonArray, res);

		return null;
	}
	
	@WebPublic(alias="groups")
	public ForegroundModuleResponse getGroups(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {

		String query = req.getParameter("q");

		if(StringUtils.isEmpty(query)){

			sendEmptyJSONResponse(res);
			return null;
		}

		List<Group> groups = groupHandler.searchGroups(query, false);

		log.info("User " + user + " searching for groups using query " + query + ", found " + CollectionUtils.getSize(groups) + " hits");

		if(groups == null){

			sendEmptyJSONResponse(res);
			return null;
		}

		JsonArray jsonArray = new JsonArray();

		for(Group currentGroup : groups){

			JsonObject instance = new JsonObject(2);
			instance.putField("ID", currentGroup.getGroupID().toString());
			instance.putField("Name", currentGroup.getName());

			jsonArray.addNode(instance);
		}

		sendJSONResponse(jsonArray, res);

		return null;
	}
	
	protected String getUserNameString(User user) {

		if(user.getUsername() == null){

			return user.getFirstname() + " " + user.getLastname();
		}

		return user.getFirstname() + " " + user.getLastname() + " (" + user.getUsername() + ")";
	}

}
