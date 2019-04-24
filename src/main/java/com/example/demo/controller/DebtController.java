package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Debt;
import com.example.demo.service.DebtService;

@Controller 
@RestController
@RequestMapping(path="/debt") 
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST})
public class DebtController{

	@Autowired
	private DebtService debtService;
	
	
	@RequestMapping(value ="/calcularlist", method = RequestMethod.GET)
	public @ResponseBody List<String> calcularList(){		
		return debtService.calcularFinal();
	}
	
	@RequestMapping(value ="/listall", method = RequestMethod.GET)
	public @ResponseBody Iterable<Debt> getAll(){		
		return debtService.getAll();
	}
	
	@RequestMapping(value="/adddebt", method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE) 
	public @ResponseBody boolean addElement (@RequestBody Debt debt) {
		if(!debtService.saveElement(debt)) return false;
		return true;
	}
	
	@RequestMapping(value="/editelement", method = RequestMethod.GET)
	public @ResponseBody boolean editElement(@RequestParam String user, @RequestParam String userToPaid, @RequestParam Double cost){	
		return debtService.editElement(user, userToPaid, cost);
	}
	
	@RequestMapping(value="/delelement", method = RequestMethod.GET)
	public @ResponseBody boolean editElement(@RequestParam int id){	
		return debtService.delElemenet(id);
	}
}
