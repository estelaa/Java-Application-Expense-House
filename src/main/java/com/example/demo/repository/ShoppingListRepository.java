package com.example.demo.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.entity.ShoppingList;

public interface ShoppingListRepository extends CrudRepository<ShoppingList, Integer> {

}
