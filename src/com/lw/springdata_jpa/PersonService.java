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
	
	@Transactional // �����,������������֧�֣���personRepository���»�ʧ��
	public void updatePersonEmailById(String email,Integer id ){
		personRepository.updatePersonEmailById(id, email);
	}
	
}
