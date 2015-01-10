/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.utils;

import org.apache.commons.dbcp.BasicDataSource;

import se.unlogic.hierarchy.core.beans.DataSourceDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBCPUtils {

	public static BasicDataSource createConnectionPool(DataSourceDescriptor dataSourceDescriptor){

		BasicDataSource basicDataSource = new BasicDataSource();

		basicDataSource.setDriverClassName	(parseUrl(dataSourceDescriptor.getDriver()));
		basicDataSource.setUsername			(parseUrl(dataSourceDescriptor.getUsername()));
		basicDataSource.setPassword			(parseUrl(dataSourceDescriptor.getPassword()));
		basicDataSource.setUrl				(parseUrl(dataSourceDescriptor.getUrl()));
		basicDataSource.setDefaultCatalog	(parseUrl(dataSourceDescriptor.getDefaultCatalog()));
		basicDataSource.setLogAbandoned		(dataSourceDescriptor.logAbandoned());
		basicDataSource.setRemoveAbandoned	(dataSourceDescriptor.removeAbandoned());

		if(dataSourceDescriptor.getRemoveTimeout() != null){
			basicDataSource.setRemoveAbandonedTimeout(dataSourceDescriptor.getRemoveTimeout());
		}

		basicDataSource.setTestOnBorrow		(dataSourceDescriptor.testOnBorrow());
		basicDataSource.setValidationQuery	(dataSourceDescriptor.getValidationQuery());
		basicDataSource.setMaxWait			(dataSourceDescriptor.getMaxWait());
		basicDataSource.setMaxActive		(dataSourceDescriptor.getMaxActive());
		basicDataSource.setMaxIdle			(dataSourceDescriptor.getMaxIdle());
		basicDataSource.setMinIdle			(dataSourceDescriptor.getMinIdle());

		return basicDataSource;
	}

	private static String parseUrl(String element){
		if (element == null || element.isEmpty()){
			return element;
		}
		List<String> variables = extractVariables(element);
		for (String variable : variables) {
			element = element.replace("${" + variable + "}", getenv(variable));
		}
		return element;
	}

	private static List<String> extractVariables(String element){
		List<String> findings = new ArrayList<String>();
		Matcher matcher = Pattern.compile("\\$\\{([A-Za-z0-9_]*?)\\}").matcher(element);
		while (matcher.find()) {
			findings.add(matcher.group());
		}
		return findings;
	}

	private static String getenv(String name){
		String val = System.getenv(name);
		return (val != null) ? val : name;
	}
}
