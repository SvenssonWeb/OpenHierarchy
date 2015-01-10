package se.unlogic.hierarchy.core.handlers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.interfaces.AttributeHandler;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.xml.XMLUtils;


public class SimpleAttributeHandler implements AttributeHandler {

	private static final long serialVersionUID = -8731009004825362810L;

	protected final HashMap<String,String> attributeMap;

	public SimpleAttributeHandler(){

		this.attributeMap = new HashMap<String,String>();
	}

	public SimpleAttributeHandler(HashMap<String,String> attributeMap){

		this.attributeMap = attributeMap;
	}

	public boolean isSet(String name) {

		return attributeMap.containsKey(name);
	}

	public String getString(String name) {

		return attributeMap.get(name);
	}

	public Integer getInt(String name) {

		return NumberUtils.toInt(attributeMap.get(name));
	}

	public Long getLong(String name) {

		return NumberUtils.toLong(attributeMap.get(name));
	}

	public Double getDouble(String name) {

		return NumberUtils.toDouble(attributeMap.get(name));
	}

	public Boolean getBoolean(String name) {

		String value = attributeMap.get(name);

		if(value == null){

			return null;
		}

		return Boolean.parseBoolean(value);
	}

	public boolean isEmpty() {

		return attributeMap.isEmpty();
	}

	public Set<String> getNames() {

		return new HashSet<String>(attributeMap.keySet());
	}

	public int size() {

		return attributeMap.size();
	}

	public boolean getPrimitiveBoolean(String name) {

		return Boolean.parseBoolean(attributeMap.get(name));
	}

	public Map<String, String> getAttributeMap() {

		return new HashMap<String, String>(attributeMap);
	}

	public Element toXML(Document doc) {

		Element attributesElement = doc.createElement("Attributes");

		for(Entry<String,String> entry : attributeMap.entrySet()){

			Element attributeElement = doc.createElement("Attribute");
			XMLUtils.appendNewCDATAElement(doc, attributeElement, "Name", entry.getKey());
			XMLUtils.appendNewCDATAElement(doc, attributeElement, "Value", entry.getValue());
			attributesElement.appendChild(attributeElement);
		}

		return attributesElement;
	}
}
