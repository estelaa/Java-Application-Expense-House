package com.example.demo.service;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.ElementsStorage;
import com.example.demo.entity.ShoppingList;
import com.example.demo.entity.User;
import com.example.demo.repository.ElementsStorageRepository;
import com.example.demo.repository.ShoppingListRepository;
import com.example.demo.repository.UserRepository;

@Service
public class GraphicService {

	@Autowired
	private ElementsStorageRepository elementsStorageRepository;
	
	@Autowired
	private ShoppingListRepository shoppingListRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	ShoppingListService shoppingListService;
	
	private static final String MONTH[] = {"Gener","Febrer","Marc","Abril","Maig","Juny","Juliol","Agost","Setembre","Octubre","Novembre","Decembre"};
	
	public List<HashMap<String,Object>> getUserPaid(){
		Iterable<User> userList = userRepository.findAll();	
		List<HashMap<String,Object>> listJson = new ArrayList<HashMap<String,Object>>();
		userList.forEach(user -> {
			HashMap<String,Object> userPaid  = new HashMap<>();
			userPaid.put("name", user.getName());
			userPaid.put("value", shoppingListService.redondearDecimales(shoppingListService.sumaElementsPaidByUser(user), 2));
			listJson.add(userPaid);
		});		
		return listJson;
	}
	
	public List<HashMap<String,Object>> getTypePaid(){
		Iterable<ShoppingList> shoppingListIterable = shoppingListRepository.findAll();
		List<HashMap<String,Object>> listJson = new ArrayList<HashMap<String,Object>>();
		for(int i=0; i<ShoppingListService.TYPES.length; i++) {
			double suma=0.00;		
			for(ShoppingList item: shoppingListIterable) {
				if(item.getType()!=null&&item.getType().equals(ShoppingListService.TYPES[i])) suma+=item.getPrice();
			}
			HashMap<String,Object> typePaid  = new HashMap<>();
			typePaid.put("name", ShoppingListService.TYPES[i]);
			typePaid.put("value", shoppingListService.redondearDecimales(suma,2));
			listJson.add(typePaid);
		}	
		return listJson;
	}
	
	
	public List<HashMap<String,Object>> getPaidByMonth(){
		Iterable<ElementsStorage> shoppingListIterable = elementsStorageRepository.findAll();
		List<HashMap<String,Object>> listJson = new ArrayList<HashMap<String,Object>>();		
		LocalDateTime ahora = LocalDateTime.now(); 
		Date dateNow = Date.valueOf(ahora.toLocalDate());
		Calendar calendarNow = Calendar.getInstance();
		calendarNow.setTime(dateNow);
		int monthNow = calendarNow.get(Calendar.MONTH);	
		int yearNow = calendarNow.get(Calendar.YEAR);
		for(int month=0;month<=monthNow;month++) {		
			double sumMonth=0;
			for(ElementsStorage itemShoppingList: shoppingListIterable) {			
				Date date = itemShoppingList.getDate(); // your date
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				int yearCalendar = calendar.get(Calendar.YEAR);
				int monthCalendar = calendar.get(Calendar.MONTH);		
				if(month==monthCalendar && yearNow==yearCalendar) {
					sumMonth+=	itemShoppingList.getPrice();
				}		
			}
			HashMap<String,Object> typePaid  = new HashMap<>();
			typePaid.put("name", MONTH[month]);
			typePaid.put("value", shoppingListService.redondearDecimales(sumMonth,2));
			listJson.add(typePaid);
		}
							
		return listJson;
	}
}
