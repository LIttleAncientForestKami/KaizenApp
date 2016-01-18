package com.agh.mbulawa.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {

	private IntegerProperty id = new SimpleIntegerProperty(0);
	private IntegerProperty isAdmin = new SimpleIntegerProperty(0);
	private IntegerProperty amountOfIdeas = new SimpleIntegerProperty(0);
	private StringProperty firstName;
	private StringProperty lastName;
	private StringProperty login;
	private StringProperty password;

	public User() {

	}

	public User(String firstName, String lastName, String login, String password) {

		this.firstName = new SimpleStringProperty(firstName);
		this.lastName = new SimpleStringProperty(lastName);
		this.login = new SimpleStringProperty(login);
		this.password = new SimpleStringProperty(password);
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

	public StringProperty loginProperty() {
		return login;
	}

	public String getLogin() {
		return login.get();
	}

	public void setLogin(String login) {
		this.login.set(login);
		;
	}

	public StringProperty passwordProperty() {
		return password;
	}

	public String getPassword() {
		return password.get();
	}

	public void setPassword(String password) {
		this.password.set(password);
	}

	public StringProperty firstNameProperty() {
		return firstName;
	}

	public String getFirstName() {
		return firstName.get();
	}

	public void setFirstName(String firstName) {
		this.firstName.set(firstName);
	}

	public StringProperty lastNameProperty() {
		return lastName;
	}

	public String getLastName() {
		return lastName.get();
	}

	public void setLastName(String lastName) {
		this.lastName.set(lastName);
	}

	public IntegerProperty amountOfIdeasProperty() {
		return amountOfIdeas;
	}

	public int getAmountOfIdeas() {
		return amountOfIdeas.get();
	}

	public void setAmountOfIdeas(int amountOfIdeas) {
		this.amountOfIdeas.set(amountOfIdeas);
	}

	public IntegerProperty isAdminProperty() {
		return isAdmin;
	}
	
	public int getIsAdmin() {
		return isAdmin.get();
	}

	public void setIsAdmin(int isAdmin) {
		this.isAdmin.set(isAdmin);
	}

	@Override
	public String toString() {
		return "User [id=" + id.get() + ", firstName=" + firstName.get() + ", lastName=" + lastName.get() + ", login="
				+ login.get() + ", password=" + password.get() + "]";
	}

}
