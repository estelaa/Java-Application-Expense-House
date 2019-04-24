package com.example.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Debt;
import com.example.demo.repository.DebtRepository;

@Service
public class DebtService {

	
	@Autowired
	private ShoppingListService shoppingListService;
	
	@Autowired
	private DebtRepository debtRepository;
	
	
	List<String> listPaid;
	
	
	public Iterable<Debt> getAll(){
		return debtRepository.findAll();
	}
	
	public List<String> calcularFinal() {
		listPaid = new ArrayList<String>();		
	    HashMap<String,Double> paidUser = shoppingListService.calcularPaidAllUser();	    		   
	    int count=0;	    
	    while(count<paidUser.size()) {	 
	    	count=0;
	    	for(String user: paidUser.keySet()) {
	    		if (paidUser.get(user)>0.00) compareToUser(paidUser, user, paidUser.get(user));		
	    		if (paidUser.get(user)==0.00 || paidUser.get(user)<0.1) count++;	    		
	    	}   
	    }
		Iterable<Debt> iterableDebt= debtRepository.findAll();
		iterableDebt.forEach(item -> {
			if(item.getCost()!=0.00) {
				listPaid.add("El usuari "+item.getUser()+ " ha de pagar a "+item.getUserToPaid()+": "+redondearDecimales(item.getCost(),2));
			}
		});
	    return listPaid;
	}
	
	public boolean saveElement(Debt debt) {
		Iterable<Debt> iterableDebt= debtRepository.findAll();
		boolean isExist=false;
		if(debtRepository.count()==0) {
			debtRepository.save(debt);
		}else {
			for(Debt item: iterableDebt) {
				if(item.getUserToPaid().equals(debt.getUserToPaid())&&item.getUser().equals(debt.getUser())) {
					item.setCost(item.getCost()+debt.getCost());
					debtRepository.save(item);
					isExist=true;
				}
			}
			if(!isExist) debtRepository.save(debt);
		}
		return true;
	}
	
	public boolean editElement(String user, String userToPaid, Double cost) {
		Iterable<Debt> iterableDebt= debtRepository.findAll();
		iterableDebt.forEach(item -> {
			if(item.getUserToPaid().equals(userToPaid)&&item.getUser().equals(user)) {
				item.setCost(cost);
				debtRepository.save(item);
			}
		});
		return true;
	}
	
	public boolean delElemenet(int id) {
		debtRepository.deleteById(id);
		return true;
	}
	
	public void compareToUser(HashMap<String,Double> paidUser, String user, Double paid) {			
		for(String userPaidYou: paidUser.keySet()) { 			
			if((!userPaidYou.equals(user)) && (paidUser.get(userPaidYou)<0.00)) {
				Double result = redondearDecimales(paid + paidUser.get(userPaidYou),2);	
				Double paidFinal = 0.00;
				if(result > 0.00) {				
					paidFinal=-paidUser.get(userPaidYou);
					paidUser.put(user, result);						
					paidUser.put(userPaidYou, 0.00);					
				}else if (result < 0.00){
					paidFinal=paid;
					paidUser.put(user, 0.00);
					paidUser.put(userPaidYou, result);			
				}else if(result == 0.00) {
					paidFinal=paidUser.get(user);
					paidUser.put(user, 0.00);
					paidUser.put(userPaidYou, 0.00);			
				}
				
				//Afegim el deute
				Iterable<Debt> iterableDebt= debtRepository.findAll();
				double debtUser=0; 
				double debtUserPaidYou=0; 
				for(Debt debt: iterableDebt) {	
					if(debt.getUser().equals(user) && userPaidYou.equals(debt.getUserToPaid())) {
						debtUser=debt.getCost();
						debt.setCost(0.00);
					}
					if(userPaidYou.equals(debt.getUser()) && debt.getUserToPaid().equals(user)) {
						debtUserPaidYou=debt.getCost();
						debt.setCost(0.00);
					}
				}
				Double PaidFinalWithDebt=paidFinal-debtUser+debtUserPaidYou;
				//Double PaidFinalWithDebt=paidFinal-debtUser;
				if(PaidFinalWithDebt<0.00) {
					listPaid.add("El usuari "+user+ " ha de pagar a "+userPaidYou+": "+-redondearDecimales(PaidFinalWithDebt,2));
					break;
				}else {
					listPaid.add("El usuari "+userPaidYou+ " ha de pagar a "+user+": "+redondearDecimales(PaidFinalWithDebt,2));
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
