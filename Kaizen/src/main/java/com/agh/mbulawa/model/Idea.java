package com.agh.mbulawa.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Idea {

	private IntegerProperty id = new SimpleIntegerProperty(0);
	private StringProperty category;
	private StringProperty name;
	private StringProperty content;
	private IntegerProperty userId = new SimpleIntegerProperty(0);

	public Idea() {

	}

	public Idea(String category, String name, String content) {
		this.category = new SimpleStringProperty(category);
		this.name = new SimpleStringProperty(name);
		this.content = new SimpleStringProperty(content);
	}

	public IntegerProperty idProperty() {
		return id;
	}

	public int getId() {
		return id.get();
	}

	public void setId(int id) {
		this.id.set(id);
	}

	public StringProperty nameProperty() {
		return name;
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public StringProperty categoryProperty() {
		return category;
	}

	public String getCategory() {
		return category.get();
	}

	public void setCategory(String category) {
		this.category.set(category);
	}

	public StringProperty contentProperty() {
		return content;
	}

	public String getContent() {
		return content.get();
	}

	public void setContent(String content) {
		this.content.set(content);
	}

	public IntegerProperty userIdProperty() {
		return userId;
	}

	public int getUserId() {
		return userId.get();
	}

	public void setUserId(int userId) {
		this.userId.set(userId);
	}

	@Override
	public String toString() {
		return "Idea [ id = " + id.get() + ", category = " + category.get() + ", name = " + name.get() + ", content = "
				+ content.get() + ", user Id = " + userId.get() + " ]";
	}

}
