package com.dav.dao;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import com.dav.entity.Document;

@Transactional
public interface DocumentDao extends CrudRepository<Document, Integer>{
	
	  public Document findById(Integer id);

}
