package com.example.demo.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.entity.Debt;

public interface DebtRepository  extends CrudRepository<Debt, Integer>{

}
