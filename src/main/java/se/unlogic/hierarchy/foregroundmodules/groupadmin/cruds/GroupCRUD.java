package se.unlogic.hierarchy.foregroundmodules.groupadmin.cruds;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.beans.MutableGroup;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.exceptions.UnableToAddGroupException;
import se.unlogic.hierarchy.core.exceptions.UnableToDeleteGroupException;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.MutableAttributeHandler;
import se.unlogic.hierarchy.core.utils.IntegerBasedCRUD;
import se.unlogic.hierarchy.foregroundmodules.groupadmin.GroupAdminModule;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.BeanRequestPopulator;
import se.unlogic.webutils.http.URIParser;


public class GroupCRUD<CallbackType extends GroupAdminModule> extends IntegerBasedCRUD<MutableGroup,CallbackType> {

	public GroupCRUD(BeanRequestPopulator<MutableGroup> populator, String typeElementName, String typeLogName, CallbackType groupAdminModule) {

		super(null,populator, typeElementName, typeLogName, "", groupAdminModule);
	}

	@Override
	public MutableGroup getBean(Integer beanID) throws SQLException, AccessDeniedException {

		Group group = callback.getGroupHandler().getGroup(beanID, true);

		if(group == null || !(group instanceof MutableGroup)){

			return null;
		}

		return (MutableGroup) group;
	}

	@Override
	protected void addBean(MutableGroup bean, HttpServletRequest req, User user, URIParser uriParser) throws SQLException, UnableToAddGroupException {

		callback.getGroupHandler().addGroup(bean);
		callback.setUsers(bean, req);
	}

	@Override
	protected void updateBean(MutableGroup bean, HttpServletRequest req, User user, URIParser uriParser) throws Exception {

		callback.getGroupHandler().updateGroup(bean, callback.getSupportedAttributes() != null);
		callback.setUsers(bean, req);
	}

	@Override
	protected void deleteBean(MutableGroup bean, HttpServletRequest req, User user, URIParser uriParser) throws SQLException, UnableToDeleteGroupException {

		List<User> users = callback.getUserHandler().getUsersByGroup(bean.getGroupID(), true, false);

		if(users != null){

			callback.removeUsersFromGroup(users, bean);
		}

		callback.getGroupHandler().deleteGroup(bean);
	}

	@Override
	protected void appendAddFormData(Document doc, Element addTypeElement, User user, HttpServletRequest req, URIParser uriParser) throws SQLException {

		XMLUtils.append(doc, addTypeElement, "Users", callback.getUserHandler().getUsers(false, false));
		XMLUtils.append(doc, addTypeElement, "SupportedAttributes", "Attribute", callback.getSupportedAttributes());
	}


	@Override
	protected void appendUpdateFormData(MutableGroup bean, Document doc, Element updateTypeElement, User user, HttpServletRequest req, URIParser uriParser) throws SQLException {

		appendAddFormData(doc, updateTypeElement, user, req, uriParser);
		XMLUtils.append(doc, updateTypeElement, "GroupUsers", callback.getUserHandler().getUsersByGroup(bean.getGroupID(), false, false));
	}

	@Override
	public ForegroundModuleResponse list(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, ValidationError validationError) throws Exception {

		return callback.list(req, res, user, uriParser, validationError);
	}

	@Override
	protected MutableGroup populateFromAddRequest(HttpServletRequest req, User user, URIParser uriParser) throws ValidationException, Exception {

		MutableGroup group = super.populateFromAddRequest(req, user, uriParser);

		setAttributes(group, req);

		return group;
	}

	@Override
	protected MutableGroup populateFromUpdateRequest(MutableGroup bean, HttpServletRequest req, User user, URIParser uriParser) throws ValidationException, Exception {

		MutableGroup group = super.populateFromUpdateRequest(bean, req, user, uriParser);

		setAttributes(group, req);

		return group;
	}

	private void setAttributes(MutableGroup group, HttpServletRequest req) {

		if(callback.getSupportedAttributes() == null){

			return;
		}

		MutableAttributeHandler attributeHandler = group.getAttributeHandler();

		if(attributeHandler == null){

			return;
		}

		for(String attribute : callback.getSupportedAttributes()){

			String attributeValue = req.getParameter("attribute-" + attribute);

			if(!StringUtils.isEmpty(attributeValue)){

				attributeHandler.setAttribute(attribute, attributeValue.trim());
				
			}else{
			
				attributeHandler.removeAttribute(attribute);
			}
		}
	}

	@Override
	public void appendBean(MutableGroup bean, Element targetElement, Document doc) {

		Element groupElement = bean.toXML(doc);
		targetElement.appendChild(groupElement);

		MutableAttributeHandler attributeHandler = bean.getAttributeHandler();

		if(attributeHandler != null && !attributeHandler.isEmpty()){

			groupElement.appendChild(attributeHandler.toXML(doc));
		}
	}
}
