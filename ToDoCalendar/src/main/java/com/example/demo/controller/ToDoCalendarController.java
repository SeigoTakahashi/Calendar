package com.example.demo.controller;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ToDoCalendarController {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@RequestMapping(path = "/home", method = RequestMethod.GET)
	public String index(Model model) throws IOException {
		List<Map<String, Object>> goals = jdbcTemplate.queryForList("SELECT * FROM goals;");
		model.addAttribute("goals", goals);
		return "home";
	}

	@RequestMapping(path = "/addGoal", method = RequestMethod.GET)
	public String form(Model model) throws IOException {
		return "form";
	}

	@RequestMapping(path = "/detail/{id}", method = RequestMethod.GET)
	public String detail(@PathVariable("id") String id, Model model) throws IOException {
		List<Map<String, Object>> goals = jdbcTemplate.queryForList("SELECT * FROM goals WHERE id = ?", id);
		List<Map<String, Object>> events = jdbcTemplate.queryForList("SELECT * FROM events WHERE goal_id = ?", id);
		model.addAttribute("events", events);
		model.addAttribute("goals", goals);
		return "index";
	}

	@RequestMapping(path = "/addGoal", method = RequestMethod.POST)
	public String addGoal(String title, Date start_date, Date end_date) throws IOException {
		jdbcTemplate.update("INSERT INTO goals (name,start_date,end_date) VALUES(?,?,?)", title, start_date, end_date);
		return "redirect:/home";
	}

	@RequestMapping(path = "/addEvent", method = RequestMethod.POST)
	public String addEvent(String id, String event_title, Date start_date, Date end_date) throws IOException {
		jdbcTemplate.update("INSERT INTO events (title,start_date,completed,goal_id,end_date) VALUES(?,?,?,?,?)", event_title,
				start_date, false, id, end_date);
		return "redirect:/detail/" + id;
	}
	@RequestMapping(path = "/updateEvent", method = RequestMethod.POST)
	public String updateEvent(String eventid, String goalid, String event_title, Date start_date, Date end_date, String completed) throws IOException {
		int c = 0;
		if ("on".equals(completed)) {
			c = 1;
		} 
		jdbcTemplate.update("UPDATE events SET title = ?, start_date = ?, completed = ?, goal_id = ?, end_date = ? WHERE id = ?", event_title,
				start_date, c, goalid, end_date, eventid);
		return "redirect:/detail/" + goalid;
	}
	
	@RequestMapping(path = "/deleteEvent", method = RequestMethod.POST)
	public String deleteEvent(String eventid, String goalid) throws IOException {
		jdbcTemplate.update("DELETE FROM events WHERE id = ?", eventid);
		return "redirect:/detail/" + goalid;
	}



}
