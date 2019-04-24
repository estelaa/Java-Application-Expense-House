package com.example.demo.controller;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.ElementsStorage;
import com.example.demo.service.ElementsStorageService;


@Controller 
@RestController
@RequestMapping(path="/moviment") 
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST})
public class ElementsStorageController {
	
	@Autowired
	private ElementsStorageService elementsStorageService;
	

	@GetMapping(path="/allelements")
	public @ResponseBody Iterable<ElementsStorage> getAllElements() {
		return elementsStorageService.getAllElemets();
	}
	
	@RequestMapping(value="/estadistiques", method = RequestMethod.GET)
	public @ResponseBody List<HashMap<String,Object>> getPaidUser(@RequestParam Date dateInici, @RequestParam Date dateFinal, @RequestParam String type){
		switch(type) {
			case "pie":
				return elementsStorageService.getUserPaid(dateInici, dateFinal);
			case "bar-vertical":
				return elementsStorageService.getTypePaid(dateInici, dateFinal);			
		}
		return null;	
	}

}
