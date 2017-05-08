package com.lw.springdata_jpa.test;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.lw.springdata_jpa.Person;
import com.lw.springdata_jpa.PersonRepository;
import com.lw.springdata_jpa.PersonService;

public class SpringTestTest {

	private ApplicationContext ac = null ;
	PersonRepository personRepository = null ;
	PersonService personService = null ;
	
	{
		ac = new ClassPathXmlApplicationContext("applicationContext.xml");
		personRepository = ac.getBean(PersonRepository.class);
		personService = ac.getBean(PersonService.class) ;
	}
	// �Զ���Repository
	@Test
	public void testCustomerRepository(){
		personRepository.test();
	}
	
	// ��ѯid>10�ļ�¼
	@Test
	public void testSpecificationRepository(){
		// ��ѯ�������
		//ͨ��ʹ�� Specification �������ڲ���
		Specification<Person> specification = new Specification<Person>() {
			/**
			 * @param *root: �����ѯ��ʵ����. 
			 * @param query: ���Դ��пɵ� Root ����, ����֪ JPA Criteria ��ѯҪ��ѯ��һ��ʵ����. ������
			 * ����Ӳ�ѯ����, �����Խ�� EntityManager ����õ����ղ�ѯ�� TypedQuery ����. 
			 * @param *cb: CriteriaBuilder ����. ���ڴ��� Criteria ��ض���Ĺ���. ��Ȼ���Դ��л�ȡ�� Predicate ����
			 * @return: *Predicate ����, ����һ����ѯ����. 
			 */
			@Override
			public Predicate toPredicate(Root<Person> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {
				Path path = root.get("id") ; // ��ȡId
				Predicate predicate = cb.gt(path, 10) ; //��ȡid����10�ļ�¼
				return predicate;
			}
		};
		
		// ��ҳ���
		int pageNo = 3 - 1;
		int pageSize = 5;
		PageRequest pageable = new PageRequest(pageNo, pageSize);
		/**
		 * ����1����ѯ����
		 * ����2����ҳ
		 */
		Page<Person> page = personRepository.findAll(specification,pageable);
		System.out.println("size��" + page.getSize());
		System.out.println("��ǰҳ��" + (page.getNumber() + 1 ));
		System.out.println("List���ϣ�" + page.getContent());
		System.out.println("�ܼ�¼����" + page.getTotalElements());
		System.out.println("��ҳ����" + page.getTotalPages());
		System.out.println("��ǰҳ��ļ�¼����" + page.getNumberOfElements());
	}
	
	
	@Test
	public void testJpaRepository(){
		Person person = new Person() ;
		person.setBirth(new Date());
		person.setEmail("liwen2@qq.com");
		person.setLastName("liwen2");
		person.setId(31); // ������id���Ȳ�ѯ���������update��û�������
		personRepository.saveAndFlush(person); // û������Id��ֱ�Ӳ������ݿ�
	}
	
	@Test // ���Է�ҳ�����򣺵��ǲ�����������ҳ
	public void testPagingAndSort(){
		int pageNum = 3 - 1 ;// �ڼ�ҳ ����0��ʼ
		int pageSize = 5; // ÿҳ��ʾ��¼��
		Pageable pageable = new PageRequest(pageNum, pageSize);
		// ��ȡ��ҳ����
		Page<Person> page = personRepository.findAll(pageable);
		System.out.println("size��" + page.getSize());
		System.out.println("��ǰҳ��" + (page.getNumber() + 1 ));
		System.out.println("List���ϣ�" + page.getContent());
		System.out.println("�ܼ�¼����" + page.getTotalElements());
		System.out.println("��ҳ����" + page.getTotalPages());
		System.out.println("��ǰҳ��ļ�¼����" + page.getNumberOfElements());
	}
	
	//ǧ��Ҫע�⣺��ʹ��CrudReposity�ӿ�ʱ����������������org.springframework.orm.jpa.JpaTransactionManager
	// 								id�����ǣ�TransactionManager
	@Test
	public void testCrudReposity(){
		List<Person> persons = new ArrayList<>();
		for(int i = 'a'; i <= 'z'; i++){
			Person person = new Person();
			person.setBirth(new Date());
			person.setEmail((char)i + "" + (char)i + "@qq.com");
			person.setLastName((char)i + "" + (char)i);
			persons.add(person);
		}
		personService.savePersons(persons);
	}
	
	@Test
	public void testUpdate(){
		personService.updatePersonEmailById( "aaaa.com", 1); 
	}
	
	@Test
	public void testSpringDataQuery(){
		Person person = personRepository.getMaxIdPerson();
		System.out.println(person);
		List<Person> list = personRepository.testQueryAnnotationParam1("xBB", "bb.com");
		System.out.println(list);
		List<Person> list2 = personRepository.testQueryAnnotationLikeParam("xBB", "bb.com");
		System.out.println(list2);
		
		List<Person> list3 = personRepository.testQueryAnnotationParam2("xB", "bb.c");
		System.out.println(list3);
		
		List<Person> list4 = personRepository.testQueryAnnotationLikeParam2("xB", "bb.");
		System.out.println(list4);
		// ����ԭ����ѯ
		long totalCount = personRepository.getTotalCount();
		System.out.println(totalCount);
		
	}
	
	
	@Test
	public void testSpringData(){
		List<Person> list1 = personRepository.getByLastNameStartingWithAndIdLessThan("x", 3);
		System.out.println(list1);
		List<Person> list2 = personRepository.getByLastNameEndingWithAndIdLessThan("y", 4);
		System.out.println(list2);
		
		List<Person> list3 = personRepository.getByEmailInOrBirthLessThan(Arrays.asList("AA.com","BB.com","CC.com"), new Date());
		System.out.println(list3);
	}
	
	@Test
	public void testHelloWordSpringData(){
		System.out.println(personRepository.getByLastName("aaa"));
	}
	
	@Test
	public void testJpa(){ // �ᴴ��ac�������ǻᴴ����
		
	}
	
	@Test
	public void testDataSource(){
		DataSource dataSource = ac.getBean(DataSource.class);
		System.out.println(dataSource); // success
	}
}
