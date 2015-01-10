package se.unlogic.hierarchy.core.handlers;

import java.util.HashMap;

import se.unlogic.hierarchy.core.interfaces.MutableAttributeHandler;


public class SimpleMutableAttributeHandler extends SimpleAttributeHandler implements MutableAttributeHandler {

	private static final long serialVersionUID = -308921362713744890L;

	private final int maxNameLength;
	private final int maxValueLength;

	public SimpleMutableAttributeHandler(int maxNameLength, int maxValueLength) {

		super();
		this.maxNameLength = maxNameLength;
		this.maxValueLength = maxValueLength;
	}

	public SimpleMutableAttributeHandler(HashMap<String, String> attributeMap, int maxNameLength, int maxValueLength) {

		super(attributeMap);
		this.maxNameLength = maxNameLength;
		this.maxValueLength = maxValueLength;
	}

	public boolean setAttribute(String name, Object value) {

		String valueString = value.toString();

		if(name.length() > maxNameLength || valueString.length() > maxValueLength){

			return false;
		}

		this.attributeMap.put(name, valueString);

		return true;
	}

	public void removeAttribute(String name) {

		this.attributeMap.remove(name);
	}

	public void clear() {

		this.attributeMap.clear();
	}

	public int getMaxNameLength() {

		return maxNameLength;
	}

	public int getMaxValueLength() {

		return maxValueLength;
	}
}
