package com.example.demo.service;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.ElementsStorage;
import com.example.demo.entity.ShoppingList;
import com.example.demo.entity.User;
import com.example.demo.repository.DebtRepository;
import com.example.demo.repository.ElementsStorageRepository;
import com.example.demo.repository.ShoppingListRepository;
import com.example.demo.repository.UserRepository;

@Service
public class ShoppingListService{

	@Autowired
	private ShoppingListRepository shoppingListRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ElementsStorageRepository  elementsStorageRepository;
	
	@Autowired 
	private DebtRepository debtRepository;
	
	
	List<String> listPaid;
	public static final String TYPES[]= {"Menjar","Restaurant/Bar","Gasolina","Oci","Productes casa"};
	
		
	public Double sumaShoppingListElements() {	
		Iterable<ShoppingList> iterableShoppingList = shoppingListRepository.findAll();
		List<Double> listElements = new ArrayList<>();
		iterableShoppingList.forEach(s->listElements.add(s.getPrice()));
		double totalPrice=0;
		for(Double price:listElements) 
			totalPrice += price;		
		return redondearDecimales(totalPrice,2);				
	}
	
	
	
	public Iterable<ShoppingList> getAllElements(){
		return shoppingListRepository.findAll();
	}
	
	//Save element in DB
	public boolean saveElement(ShoppingList shoppingList) {		
		if(shoppingList.getDate()==null) {
			ZoneId zoneId = ZoneId.of("Europe/Madrid");
			LocalDateTime ahora = LocalDateTime.now(zoneId); 	
			Date date = Date.valueOf(ahora.toLocalDate());
			shoppingList.setDate(date);
		}
		Iterable<User> listUser = userRepository.findAll();
		if(listUser!=null) {
			listUser.forEach(user ->{
				if(user.getName().equals(shoppingList.getUsername())) shoppingList.setUsername(shoppingList.getUsername());
			});
		}
		if(shoppingList.getUsername()!=null) {
			shoppingListRepository.save(shoppingList);		
			return true;
		}else{
			return false;
		}		
	}
	
	
	public HashMap<String,Double> getListNameAndPrice(){
		HashMap<String,Double> list = new HashMap<>();		
		Iterable<ShoppingList> iterableShoppingList = shoppingListRepository.findAll();
		iterableShoppingList.forEach(elementList->list.put(elementList.getName(), elementList.getPrice()));
		return list;		
	}
	
	
	public HashMap<String,Double> getListNameAndPriceByUser(String username){
		HashMap<String,Double> list = new HashMap<>();		
		Iterable<ShoppingList> iterableShoppingList = shoppingListRepository.findAll();
		iterableShoppingList.forEach(elementList->{
			if(elementList.getUsername().equals(username))
				list.put(elementList.getName(), elementList.getPrice());
		});
		return list;		
	}
	
	
	
	public boolean editElement(int id, String name, Double price, String type,  Date date) {
		ShoppingList shoppingList= new ShoppingList();
		Optional<ShoppingList> shoppingListOptional=shoppingListRepository.findById(id);
		if(shoppingListOptional.isPresent()) {
			shoppingList=shoppingListOptional.get();
			if(name.length()>0) shoppingList.setName(name);
			if(price!=0) shoppingList.setPrice(price);
			if(!type.equals("Undefined")) shoppingList.setType(type);
			Date datepast = new Date(70,0,1);
			if(date.getDate()!=datepast.getDate()) {
				shoppingList.setDate(date);
			}
		}			
		if(shoppingListRepository.save(shoppingList)!=null) return true;
		return false;
	}
	
	
	public boolean deletedElementById(int id) {
		shoppingListRepository.deleteById(id);
		return true;
	}
	
	

	public void deletedAllElement() {
		Iterable<ShoppingList> iterableShoppingList = shoppingListRepository.findAll();
		for(ShoppingList shoppingList: iterableShoppingList) {
			ElementsStorage elementsStorage = new ElementsStorage();
			elementsStorage.setId(shoppingList.getId());
			elementsStorage.setName(shoppingList.getName());
			elementsStorage.setPrice(shoppingList.getPrice());
			elementsStorage.setUsername(shoppingList.getUsername());
			elementsStorage.setDate(shoppingList.getDate());
			elementsStorage.setType(shoppingList.getType());
			elementsStorageRepository.save(elementsStorage);
		}
		shoppingListRepository.deleteAll();
		debtRepository.deleteAll();
	}
	
	
	
	public HashMap<String,Double> calcularPaidAllUser(){	
		Double total = sumaShoppingListElements();		
		Double mediaTotal= redondearDecimales(total/userRepository.count(),2);
		HashMap<String,Double> paidForUser = new HashMap<String,Double>();
		Iterable<User> listUser = userRepository.findAll();		
		listUser.forEach(user -> {
			paidForUser.put(user.getName(), sumaElementsPaidByUser(user)-mediaTotal);
		});		
		return paidForUser; 
	}	
	public Double sumaElementsPaidByUser(User user) {
		Iterable<ShoppingList> iterableShoppingList = shoppingListRepository.findAll();
		Double totalByUser=0.0;
		for(ShoppingList element: iterableShoppingList) {
			if(element.getUsername().equals(user.getName())) totalByUser += element.getPrice();
		}		
		return totalByUser;
	}
	
	
	
	public List<String> calcularFinal() {
		listPaid = new ArrayList<String>();		
	    HashMap<String,Double> paidUser = calcularPaidAllUser();	    		   
	    int count=0;	    
	    while(count<paidUser.size()) {	 
	    	count=0;
	    	for(String user: paidUser.keySet()) {
	    		if (paidUser.get(user)>0.00) compareToUser(paidUser, user, paidUser.get(user));		
	    		if(paidUser.get(user)==0.00 || paidUser.get(user)<0.1) count++;	    		
	    	}   
	    }
	    return listPaid;
	}	
	public void compareToUser(HashMap<String,Double> paidUser, String user, Double paid) {			
		for(String userPaidYou: paidUser.keySet()) { 			
			if((!userPaidYou.equals(user)) && (paidUser.get(userPaidYou)<0.00)) {
				Double result = redondearDecimales(paid + paidUser.get(userPaidYou),2);	
				if(result > 0.00) {
					//System.out.println("r>0: El usuari "+userPaidYou+ " ha de pagar a "+user+": "+ redondearDecimales(-paidUser.get(userPaidYou),2));
					listPaid.add("El usuari "+userPaidYou+ " ha de pagar a "+user+": "+ redondearDecimales(-paidUser.get(userPaidYou),2));															
					paidUser.put(user, result);						
					paidUser.put(userPaidYou, 0.00);		
					break;
				}else if (result < 0.00){
					//System.out.println("r<0: El usuari "+userPaidYou+ " ha de pagar a "+user+": "+ paid);
					listPaid.add("El usuari "+userPaidYou+ " ha de pagar a "+user+": "+ redondearDecimales(paid,2));	
					paidUser.put(user, 0.00);
					paidUser.put(userPaidYou, result);
					break;
				}else if(result == 0.00) {
					//System.out.println("r=0: El usuari "+userPaidYou+ " ha de pagar a "+user+": "+ paidUser.get(user));
					listPaid.add("El usuari "+userPaidYou+ " ha de pagar a "+user+": "+ redondearDecimales(paidUser.get(user),2));					
					paidUser.put(user, 0.00);
					paidUser.put(userPaidYou, 0.00);
					break;
				}
			}
		}
	
	}

	
	//Rondeara dos decimales los precios
    public static double redondearDecimales(double valorInicial, int numeroDecimales) {
        double parteEntera, resultado;
        resultado = valorInicial;
        parteEntera = Math.floor(resultado);
        resultado=(resultado-parteEntera)*Math.pow(10, numeroDecimales);
        resultado=Math.round(resultado);
        resultado=(resultado/Math.pow(10, numeroDecimales))+parteEntera;
        return resultado;
    }
    
		
}


	
