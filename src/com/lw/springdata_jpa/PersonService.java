package com.lw.springdata_jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonService {

	@Autowired
	private PersonRepository personRepository ;
	
	@Transactional
	public void savePersons(List<Person> persons) {
		personRepository.save(persons);
	}
	
	@Transactional // 添加事務,如果不添加事务支持，则personRepository更新会失败
	public void updatePersonEmailById(String email,Integer id ){
		personRepository.updatePersonEmailById(id, email);
	}
	
}
