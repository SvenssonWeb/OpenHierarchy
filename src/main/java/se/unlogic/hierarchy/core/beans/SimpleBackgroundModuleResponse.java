/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.beans;

import java.util.Collection;

import javax.xml.transform.Transformer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.enums.ResponseType;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleResponse;


public final class SimpleBackgroundModuleResponse extends BaseModuleResponse implements BackgroundModuleResponse {

	private BackgroundModuleDescriptor moduleDescriptor;
	private Collection<String> slots;

	public SimpleBackgroundModuleResponse(Element response) {
		super(response);
	}

	public SimpleBackgroundModuleResponse(String response) {
		super(response);
	}

	public SimpleBackgroundModuleResponse(Document response) {
		super(response);
	}

	public SimpleBackgroundModuleResponse(Document response, Transformer transformer) {
		super(response, transformer);
	}

	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = element;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public ResponseType getResponseType() {
		return responseType;
	}

	public void setResponseType(ResponseType responseType) {
		this.responseType = responseType;
	}

	public Transformer getTransformer() {
		return transformer;
	}


	public void setTransformer(Transformer transformer) {
		this.transformer = transformer;
	}


	public BackgroundModuleDescriptor getModuleDescriptor() {
		return moduleDescriptor;
	}

	public void setModuleDescriptor(BackgroundModuleDescriptor moduleDescriptor) {
		this.moduleDescriptor = moduleDescriptor;
	}

	public Collection<String> getSlots() {
		return this.slots;
	}


	public void setSlots(Collection<String> slots) {
		this.slots = slots;
	}

}
