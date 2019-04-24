package com.example.demo.service;


import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.ElementsStorage;
import com.example.demo.entity.User;
import com.example.demo.repository.ElementsStorageRepository;
import com.example.demo.repository.UserRepository;

@Service
public class ElementsStorageService {
	
	@Autowired
	private ElementsStorageRepository elementsStorageRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	public static final String TYPES[]= {"Menjar","Restaurant/Bar","Gasolina","Oci","Productes casa"};
	
	
	public Iterable<ElementsStorage> getAllElemets(){
		Iterable<ElementsStorage> iterableElementsStorage = elementsStorageRepository.findAll();
		return iterableElementsStorage;		
	}
	

	/****************ESTADISTIQUES*******************/
	public List<HashMap<String,Object>> getUserPaid(Date dateInici, Date dateFinal){
		Iterable<User> userList = userRepository.findAll();	
		List<HashMap<String,Object>> listJson = new ArrayList<HashMap<String,Object>>();	
		userList.forEach(user -> {
			HashMap<String,Object> userPaid  = new HashMap<>();
			userPaid.put("name", user.getName());
			userPaid.put("value", sumaElementsPaidUserByDate(user, dateInici, dateFinal));
			listJson.add(userPaid);
		});		
		return listJson;
	}
	
	
	private Double sumaElementsPaidUserByDate(User user, Date dateInici, Date dateFinal) {
		Iterable<ElementsStorage> iterableElementsStorage = elementsStorageRepository.findAll();
		Double totalByUser=0.0;
		for(ElementsStorage element: iterableElementsStorage) {
			if(element.getUsername().equals(user.getName()) && element.getDate().after(dateInici) && element.getDate().before(dateFinal)) 
				totalByUser += element.getPrice();
		}		
		return totalByUser;
	}
	
	public List<HashMap<String,Object>> getTypePaid(Date dateInici, Date dateFinal){
		Iterable<ElementsStorage> iterableElementsStorage = elementsStorageRepository.findAll();
		List<HashMap<String,Object>> listJson = new ArrayList<HashMap<String,Object>>();
		
		for(int i=0; i<TYPES.length; i++) {
			double suma=0.00;		
			for(ElementsStorage item: iterableElementsStorage) {
				if(item.getType()!=null) {
					if(item.getType().equals(TYPES[i]) && 
							item.getDate().after(dateInici) && item.getDate().before(dateFinal)) 
						suma+=item.getPrice();
				}
	
			}
			HashMap<String,Object> typePaid  = new HashMap<>();
			typePaid.put("name",TYPES[i]);
			typePaid.put("value", suma );
			listJson.add(typePaid);
		}
		
		return listJson;
		
	}
		
	
	
}
