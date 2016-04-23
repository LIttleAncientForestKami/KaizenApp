package com.agh.mbulawa.dao;

import java.util.List;

import com.agh.mbulawa.model.Idea;

public interface IdeaDao {

	public boolean addIdea(Idea idea, int userId);
	public Idea getIdeaById(int id);
	public int getLastAddedIdeaId();
	public int getIdeaIdByName(String name);
	public List<Idea> getAllIdeasList();
	public List<Idea> getUserIdeasList(int userId);
	public boolean updateIdea(Idea idea);
	public boolean removeIdea(int id);
	public boolean changeIdeaStatus(int id, String status);
}
