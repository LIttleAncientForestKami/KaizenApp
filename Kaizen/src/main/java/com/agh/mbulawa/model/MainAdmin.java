package com.agh.mbulawa.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MainAdmin {

	private StringProperty firstName;
	private StringProperty lastName;
	private StringProperty login;
	private StringProperty password;

	public MainAdmin() {

	}

	public MainAdmin(String firstName, String lastName, String login, String faculty, String password) {

		this.firstName = new SimpleStringProperty(firstName);
		this.lastName = new SimpleStringProperty(lastName);
		this.login = new SimpleStringProperty(login);
		this.password = new SimpleStringProperty(password);
	}

	public StringProperty getFirstName() {
		return firstName;
	}

	public void setFirstName(StringProperty firstName) {
		this.firstName = firstName;
	}

	public StringProperty getLastName() {
		return lastName;
	}

	public void setLastName(StringProperty lastName) {
		this.lastName = lastName;
	}

	public StringProperty getLogin() {
		return login;
	}

	public void setLogin(StringProperty login) {
		this.login = login;
	}

	public StringProperty getPassword() {
		return password;
	}

	public void setPassword(StringProperty password) {
		this.password = password;
	}

 }
