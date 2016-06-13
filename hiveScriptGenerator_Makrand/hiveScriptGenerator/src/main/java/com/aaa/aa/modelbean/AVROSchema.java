/**
 * 
 */
package com.aaa.aa.modelbean;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author root
 *
 */
public class AVROSchema {
	@JsonProperty("type")
	private String type;
	@JsonProperty("name")
	private String name;
	@JsonProperty("namespace")
	private String namespace;
	@JsonProperty("doc")
	private String doc;
	@JsonProperty("fields")
	private List<Field> fields = new ArrayList<Field>();

	/**
	* 
	* @return
	* The type
	*/
	@JsonProperty("type")
	public String getType() {
	return type;
	}

	/**
	* 
	* @param type
	* The type
	*/
	@JsonProperty("type")
	public void setType(String type) {
	this.type = type;
	}

	/**
	* 
	* @return
	* The name
	*/
	@JsonProperty("name")
	public String getName() {
	return name;
	}

	/**
	* 
	* @param name
	* The name
	*/
	@JsonProperty("name")
	public void setName(String name) {
	this.name = name;
	}

	/**
	* 
	* @return
	* The namespace
	*/
	@JsonProperty("namespace")
	public String getNamespace() {
	return namespace;
	}

	/**
	* 
	* @param namespace
	* The namespace
	*/
	@JsonProperty("namespace")
	public void setNamespace(String namespace) {
	this.namespace = namespace;
	}

	/**
	* 
	* @return
	* The doc
	*/
	@JsonProperty("doc")
	public String getDoc() {
	return doc;
	}

	/**
	* 
	* @param doc
	* The doc
	*/
	@JsonProperty("doc")
	public void setDoc(String doc) {
	this.doc = doc;
	}

	/**
	* 
	* @return
	* The fields
	*/
	@JsonProperty("fields")
	public List<Field> getFields() {
	return fields;
	}

	/**
	* 
	* @param fields
	* The fields
	*/
	@JsonProperty("fields")
	public void setFields(List<Field> fields) {
	this.fields = fields;
	}
}
