package com.agh.mbulawa.model;

import java.sql.Date;

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

	private StringProperty status;

	private StringProperty addDate;
	private StringProperty editDate;

	// Used only for show in table purpose.
	private IntegerProperty rowNumber = new SimpleIntegerProperty(0);

	public Idea() {

	}

	public Idea(String category, String name, String content) {
		this.category = new SimpleStringProperty(category);
		this.name = new SimpleStringProperty(name);
		this.content = new SimpleStringProperty(content);
		this.addDate = new SimpleStringProperty();
		this.editDate = new SimpleStringProperty();
		this.status = new SimpleStringProperty();
		this.setStatus("Dodany");
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

	public StringProperty statusProperty() {
		return status;
	}

	public String getStatus() {
		return status.get();
	}

	public void setStatus(String status) {

		this.status.set(status);
	}

	public StringProperty addDateProperty() {
		return addDate;
	}

	public void setAddDate(Date date) {
		this.addDate.set(date.toString());
	}

	public String getAddDate() {
		return addDate.get();
	}

	public StringProperty editDateProperty() {
		return editDate;
	}

	public String getEditDate() {
		return editDate.get();
	}

	public void setEditDate(Date editDate) {
		this.editDate.set(editDate.toString());
	}

	public IntegerProperty rowNumberProperty() {
		return rowNumber;
	}

	public int getRowNumber() {
		return rowNumber.get();
	}

	public void setRowNumber(int rowNumber) {
		this.rowNumber.set(rowNumber);
	}

	@Override
	public String toString() {
		return "Idea [ id = " + id.get() + ", category = " + category.get() + ", name = " + name.get() + ", content = "
				+ content.get() + ", user Id = " + userId.get() + ", adding date = " + addDate.get()
				+ ", editing date = " + editDate.get() + ", idea status = " + status.get() + " ]";
	}

}
