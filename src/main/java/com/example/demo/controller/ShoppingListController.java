package com.example.demo.controller;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.ShoppingList;
import com.example.demo.service.GraphicService;
import com.example.demo.service.ShoppingListService;



@Controller 
@RestController
@RequestMapping(path="/list") 
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST})
public class ShoppingListController {
	
	@Autowired
	private ShoppingListService shoppingListService;
	
	@Autowired 
	private GraphicService graphicService;
	
	@RequestMapping(value="/addelement", method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE) 
	public @ResponseBody boolean addElement (@RequestBody ShoppingList shoppingList) {
		if(!shoppingListService.saveElement(shoppingList)) return false;
		return true;
	}
	
	@GetMapping(path="/suma")
	public @ResponseBody Double getSumPriceElements() {
		return shoppingListService.sumaShoppingListElements();
	}
	
	@GetMapping(path="/allelements")
	public @ResponseBody Iterable<ShoppingList> getAllElements() {
		return shoppingListService.getAllElements();
	
	}
	
	@GetMapping(path="/elements")  
	public @ResponseBody HashMap<String,Double> getListNameAndPrice(){
		return shoppingListService.getListNameAndPrice();
	}
	
	@GetMapping(path="/elementsbyuser")  
	public @ResponseBody HashMap<String,Double> getListNameAndPriceByUsername(@RequestParam String username){
		return shoppingListService.getListNameAndPriceByUser(username);
	}
	
	@GetMapping(path="/delelement") 
	public @ResponseBody boolean deletedById(@RequestParam int id){		
		return shoppingListService.deletedElementById(id);
	}
	
	
	
	@RequestMapping(value ="/calcularlist", method = RequestMethod.GET)
	public @ResponseBody List<String> calcularList(){		
		return shoppingListService.calcularFinal();
	}
	
	
	@RequestMapping(value="/editelement", method = RequestMethod.GET)
	public @ResponseBody boolean editElement(@RequestParam int id, @RequestParam String name, 
			@RequestParam Double price, @RequestParam String type, @RequestParam Date date){	
		return shoppingListService.editElement(id, name, price, type ,date);
	}
	
	@RequestMapping(value="/delallelements", method = RequestMethod.GET)
	public void deletAllElements() {
		shoppingListService.deletedAllElement();
	}
	
	
	/*************GRAFIQUES****************/
	
	@RequestMapping(value="/calcularuserpaid", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<HashMap<String,Object>> getPaidUser(){
		return graphicService.getUserPaid();
	}
	
	@RequestMapping(value="/calculartypepaid", method = RequestMethod.GET)
	public @ResponseBody List<HashMap<String,Object>> getPaidType(){
		return graphicService.getTypePaid();
	}
	
	@RequestMapping(value="/calcularmonthpaid", method = RequestMethod.GET)
	public @ResponseBody List<HashMap<String,Object>> getPaidByMonth(){
		return graphicService.getPaidByMonth();
	}
	
}
