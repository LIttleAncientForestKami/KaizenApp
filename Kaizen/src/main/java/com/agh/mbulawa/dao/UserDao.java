package com.agh.mbulawa.dao;

import java.util.List;

import com.agh.mbulawa.model.User;

public interface UserDao {
	
	public boolean addUser(User user);
	public User getUserById(int id);
	public List<User> getUsersList();
	public boolean updateUser(User user);
	public boolean removeUser(int id);
	public String getUserPassByLogin(String login);
	public int isUserAdmin(int id);
	public int getUserIdByLogin(String login);
}
