package com.lw.springdata_jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;

public class PersonRepositoryImpl implements PersonDao {

	@PersistenceContext
	private EntityManager entityManager;  // ����ಢû�д���Ioc������
	
	@Override
	public void test() {
		Person person = entityManager.find(Person.class, 11);
		System.out.println("�Զ���ķ���====>" + person );

	}

}
