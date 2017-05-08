# SpringData

Spring Data : Spring ��һ������Ŀ�����ڼ����ݿ���ʣ�֧��NoSQL �� ��ϵ���ݴ洢������ҪĿ����ʹ���ݿ�ķ��ʱ�÷����ݡ�

JPA Spring Data:�����ڼ������ݷ��ʲ� (DAO) �Ŀ�����. ������ΨһҪ���ģ���ֻ�������־ò�Ľӿڣ����������� Spring Data JPA ��������ɣ�

## HelloWord

**1��application.xml**
```

	<?xml version="1.0" encoding="UTF-8"?>
	<beans xmlns="http://www.springframework.org/schema/beans"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xmlns:context="http://www.springframework.org/schema/context"
		xmlns:tx="http://www.springframework.org/schema/tx"
		xmlns:jpa="http://www.springframework.org/schema/data/jpa"
		xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
			http://www.springframework.org/schema/data/jpa http://www.springframework.org/schema/data/jpa/spring-jpa-1.3.xsd
			http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-4.0.xsd
			http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.0.xsd">
		<!-- ɨ��base-package�µ��ཫ����ӵ�ioc������ -->
		<context:component-scan base-package="com.lw.springdata_jpa"></context:component-scan>
		
		<!-- 1����������Դ -->
		<context:property-placeholder location="classpath:db.properties"/>
		
		<bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
			<property name="user" value="${jdbc.user}"></property>
			<property name="password" value="${jdbc.password}"></property>
			<property name="driverClass" value="${jdbc.driverClass}"></property>
			<property name="jdbcUrl" value="${jdbc.jdbcUrl}"></property>	
			<!-- ������������������ -->
		</bean>
		<!-- 2������jpa��EntityManagerFactory====>�൱��session��SessionFactoryBean -->
		<bean id="entityManagerFactoryBean" class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
			<property name="dataSource" ref="dataSource"></property>
			<!-- jpaʵ�ֲ�Ʒ��ʵ���� -->
			<property name="jpaVendorAdapter">
				<bean class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter"></bean>
			</property>
			<!-- �������ɨ��entityע��İ� -->
			<property name="packagesToScan" value="com.lw.springdata_jpa"></property>
			
			<!-- ����jpa�����ԣ�������jpaʵ�ֲ�Ʒ�����ԣ��������hibernate -->
			<property name="jpaProperties">
				<props>
					<prop key="hibernate.dialect">org.hibernate.dialect.MySQL5InnoDBDialect</prop>
					<prop key="hibernate.show_sql">true</prop>
					<prop key="hibernate.format_sql">true</prop>
					<prop key="hibernate.hbm2ddl.auto">update</prop>
				</props>
			</property>
		</bean>
		<!-- 3��������������� -->
		<bean id="transactionManager" class="org.springframework.orm.jpa.JpaTransactionManager">
			<property name="entityManagerFactory" ref="entityManagerFactoryBean"></property>
		</bean>
		<!-- 4������֧��ע������� -->
		<tx:annotation-driven transaction-manager="transactionManager"/>
		<!-- 5������Springdata -->	
		<!-- ����jpa�������ռ� -->
		<!-- base-package:ɨ��Repository���ڵ�package -->
		<jpa:repositories base-package="com.lw.springdata_jpa"
			entity-manager-factory-ref="entityManagerFactoryBean"
			></jpa:repositories>
		
	</beans>

```
**2�������ӿ�**

```
	
	//�������ͣ���һ���־û�������ͣ��ڶ������������͡�
	public interface PersonRepository exetens JpaRepository<Person, Integer>{
		// �������ֲ�����
		Person getByLastName(String lastName);
	
	}

```

**3������**

```

	personRepository.getByLastName("aaa");
```


## ��������淶
```

	��ѯ��get/find/read��ͷ
	
```



## @Queryע��

```
	
	// ʹ��query�]��:ʹ��jpql����ѯ
	// �Ӳ�ѯ
	@Query("SELECT P FROM Person p WHERE p.id = (SELECT MAX(p2.id) FROM Person p2) ")
	Person getMaxIdPerson();
	// Queryע�⴫�ݲ����ķ�ʽ1��ʹ��ռλ��
	@Query("SELECT p FROM Person p WHERE p.lastName = ?1 AND p.email = ?2 ")
	List<Person> testQueryAnnotationParam1(String lastName,String email) ;
	
	//ģ����ѯʱ������ֱ����psql ��ռλ����ֱ�����%%
	@Query("SELECT p FROM Person p WHERE p.lastName like %?1% AND p.email like %?2% ")
	List<Person> testQueryAnnotationParam2(String lastName,String email) ;
	
	
	// Queryע�⴫�ݲ����ķ�ʽ2��ʹ����������
	@Query("SELECT P FROM Person p WHERE p.lastName = :lastName AND p.email = :email ")
	List<Person> testQueryAnnotationLikeParam(@Param("lastName") String lastName,@Param("email") String email);
	
	@Query("SELECT P FROM Person p WHERE p.lastName like %:lastName% AND p.email like %:email% ")
	List<Person> testQueryAnnotationLikeParam2(@Param("lastName") String lastName,@Param("email") String email);
	
	//ԭ��sql��ѯ
	//nativeQuery=true:����Ϊԭ����ѯ
	@Query(value="SELECT count(id) FROM springdata_person",nativeQuery=true)
	long getTotalCount() ;
	
```

## @Modifyingע��

���ֻ��@Queryע��Ļ�,ֻ������ѯ����,��Ҫʵ���޸Ĳ����Ļ�,��Ҫ�����@Modifyingע��

```
	
	// ע�⣺jpql��֧��insert����,�����Ҫʵ��insert����,����ʹ�ñ���sql��ѯ
	// Modifying����Ҫ������֧��
	@Modifying // �����������ע�⽫�ᱨ�����ܽ���update��delete��������������ע��֮�󻹻ᱨ����Ϊ��Ҫ�����֧��
	// Ĭ������£�SpringData��ÿ����������������񣬵�����ֻ�����񣬲�������޸Ĳ���(���漸�����Ӿ���ֻ������)�����������޸Ĳ������������modifyingע��
	@Query("UPDATE Person p SET p.email = :email WHERE id = :id")
	void updatePersonEmailById(@Param("id") Integer id ,@Param("email") String email) ;
```




## repository�ӽӿ�

SpringData�����ṩĬ�ϵĿսӿ�repository��,���ṩ���Ѿ����˺ܶ๦�ܵ��ӽӿ�

### CrudRepository(Repository�ӽӿ�)

��Ҫʵ����ɾ�Ĳ�Ĳ���

```

	T save(T entity); // ���浥��ʵ��
	// ����鿴Դ��
```

### PagingAndSortingRepository(CrudRepository�ӽӿ�)

��Ҫʵ�ַ�ҳ������,һ��ֻ����������

```
	
	// ����
	Iterable findAll(Sort sort);
	// ��ҳ
	Page findAll(Pageable pageable);
	
```

**����**
```

	@Test // ���Է�ҳ�����򣺵��ǲ�����������ҳ
	public void testPagingAndSort(){
		int pageNum = 3 - 1 ;// �ڼ�ҳ ����0��ʼ
		int pageSize = 5; // ÿҳ��ʾ��¼��
		//Order:��ĳһ��������ʥ�滹�ǽ���
		Order order1 = new Order(Direction.DESC	, "id"); 
		Sort sort = new Sort(Direction.ASC	, "email");
		Pageable pageable = new PageRequest(pageNum, pageSize,sort);
		// ��ȡ��ҳ���� 
		Page<Person> page = personRepository.findAll(pageable);
		System.out.println("size��" + page.getSize());
		System.out.println("��ǰҳ��" + (page.getNumber() + 1 ));
		System.out.println("List���ϣ�" + page.getContent());
		System.out.println("�ܼ�¼����" + page.getTotalElements());
		System.out.println("��ҳ����" + page.getTotalPages());
		System.out.println("��ǰҳ��ļ�¼����" + page.getNumberOfElements());
	}
```

## JpaRepository(PagingAndSortingRepository�ӽӿ�)

�����������Ӻ�����ɾ���ȵķ���

**����**

```

	  public abstract java.util.List findAll();
	  
	  public abstract java.util.List findAll(Sort sort);
	  
	  public abstract java.util.List findAll(Iterable iterable);
	  
	  public abstract java.util.List save(Iterable iterable);
	  
	  public abstract void flush();
	  
	  public abstract java.lang.Object saveAndFlush(Object obj);
	  
	  public abstract void deleteInBatch(Iterable iterable);
	  
	  public abstract void deleteAllInBatch();
```

**����**

```

	@Test
	public void testJpaRepository(){
		Person person = new Person() ;
		person.setBirth(new Date());
		person.setEmail("liwen2@qq.com");
		person.setLastName("liwen2");
		person.setId(31); // ������id���Ȳ�ѯ���������update��û�������
		Person person2 = personRepository.saveAndFlush(person); // û������Id��ֱ�Ӳ������ݿ�
		System.out.println(person == person ); // false������jpa�е�merger����,����˶������Եĸ���
	}
```

## JpaSpecificationExecutor

����ӿڲ�����Repository��ϵ,����PagingAndSortingRepository���ܴ�������ҳ,����ӿ����˲���


**����**

```

	  public abstract java.lang.Object findOne(Specification spe);
	  
	  public abstract java.util.List findAll(Specification spe);
	  // ��������ѯ�ķ���
	  public abstract Page findAll(Specification specification, Pageable pageable);
	  
	  public abstract List findAll(Specification arg0,Sort arg1);
	  
	  public abstract long count(Specification arg0);
	
```

**Specification**����װ�˲�ѯ����

```
	
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
	

```








