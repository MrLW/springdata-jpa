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
	// 自定义Repository
	@Test
	public void testCustomerRepository(){
		personRepository.test();
	}
	
	// 查询id>10的记录
	@Test
	public void testSpecificationRepository(){
		// 查询条件相关
		//通常使用 Specification 的匿名内部类
		Specification<Person> specification = new Specification<Person>() {
			/**
			 * @param *root: 代表查询的实体类. 
			 * @param query: 可以从中可到 Root 对象, 即告知 JPA Criteria 查询要查询哪一个实体类. 还可以
			 * 来添加查询条件, 还可以结合 EntityManager 对象得到最终查询的 TypedQuery 对象. 
			 * @param *cb: CriteriaBuilder 对象. 用于创建 Criteria 相关对象的工厂. 当然可以从中获取到 Predicate 对象
			 * @return: *Predicate 类型, 代表一个查询条件. 
			 */
			@Override
			public Predicate toPredicate(Root<Person> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {
				Path path = root.get("id") ; // 获取Id
				Predicate predicate = cb.gt(path, 10) ; //获取id大于10的记录
				return predicate;
			}
		};
		
		// 分页相关
		int pageNo = 3 - 1;
		int pageSize = 5;
		PageRequest pageable = new PageRequest(pageNo, pageSize);
		/**
		 * 参数1：查询条件
		 * 参数2：分页
		 */
		Page<Person> page = personRepository.findAll(specification,pageable);
		System.out.println("size：" + page.getSize());
		System.out.println("当前页：" + (page.getNumber() + 1 ));
		System.out.println("List集合：" + page.getContent());
		System.out.println("总记录数：" + page.getTotalElements());
		System.out.println("总页数：" + page.getTotalPages());
		System.out.println("当前页面的记录数：" + page.getNumberOfElements());
	}
	
	
	@Test
	public void testJpaRepository(){
		Person person = new Person() ;
		person.setBirth(new Date());
		person.setEmail("liwen2@qq.com");
		person.setLastName("liwen2");
		person.setId(31); // 设置了id，先查询，如果有则update，没有则插入
		personRepository.saveAndFlush(person); // 没有设置Id，直接插入数据库
	}
	
	@Test // 测试分页和排序：但是不能有条件分页
	public void testPagingAndSort(){
		int pageNum = 3 - 1 ;// 第几页 ，从0开始
		int pageSize = 5; // 每页显示记录数
		Pageable pageable = new PageRequest(pageNum, pageSize);
		// 获取分页对象
		Page<Person> page = personRepository.findAll(pageable);
		System.out.println("size：" + page.getSize());
		System.out.println("当前页：" + (page.getNumber() + 1 ));
		System.out.println("List集合：" + page.getContent());
		System.out.println("总记录数：" + page.getTotalElements());
		System.out.println("总页数：" + page.getTotalPages());
		System.out.println("当前页面的记录数：" + page.getNumberOfElements());
	}
	
	//千万要注意：在使用CrudReposity接口时，当处理事务配置org.springframework.orm.jpa.JpaTransactionManager
	// 								id必须是：TransactionManager
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
		// 测试原生查询
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
	public void testJpa(){ // 会创建ac对象，于是会创建表
		
	}
	
	@Test
	public void testDataSource(){
		DataSource dataSource = ac.getBean(DataSource.class);
		System.out.println(dataSource); // success
	}
}
