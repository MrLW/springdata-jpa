# SpringData

Spring Data : Spring 的一个子项目。用于简化数据库访问，支持NoSQL 和 关系数据存储。其主要目标是使数据库的访问变得方便快捷。

JPA Spring Data:致力于减少数据访问层 (DAO) 的开发量. 开发者唯一要做的，就只是声明持久层的接口，其他都交给 Spring Data JPA 来帮你完成！

## HelloWord

**1、application.xml**
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
		<!-- 扫描base-package下的类将其添加到ioc容器中 -->
		<context:component-scan base-package="com.lw.springdata_jpa"></context:component-scan>
		
		<!-- 1、配置数据源 -->
		<context:property-placeholder location="classpath:db.properties"/>
		
		<bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
			<property name="user" value="${jdbc.user}"></property>
			<property name="password" value="${jdbc.password}"></property>
			<property name="driverClass" value="${jdbc.driverClass}"></property>
			<property name="jdbcUrl" value="${jdbc.jdbcUrl}"></property>	
			<!-- 还可以配置其它属性 -->
		</bean>
		<!-- 2、配置jpa的EntityManagerFactory====>相当于session的SessionFactoryBean -->
		<bean id="entityManagerFactoryBean" class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
			<property name="dataSource" ref="dataSource"></property>
			<!-- jpa实现产品的实现器 -->
			<property name="jpaVendorAdapter">
				<bean class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter"></bean>
			</property>
			<!-- 配置添加扫描entity注解的包 -->
			<property name="packagesToScan" value="com.lw.springdata_jpa"></property>
			
			<!-- 配置jpa的属性，具体是jpa实现产品的属性，这里就是hibernate -->
			<property name="jpaProperties">
				<props>
					<prop key="hibernate.dialect">org.hibernate.dialect.MySQL5InnoDBDialect</prop>
					<prop key="hibernate.show_sql">true</prop>
					<prop key="hibernate.format_sql">true</prop>
					<prop key="hibernate.hbm2ddl.auto">update</prop>
				</props>
			</property>
		</bean>
		<!-- 3、配置事务管理器 -->
		<bean id="transactionManager" class="org.springframework.orm.jpa.JpaTransactionManager">
			<property name="entityManagerFactory" ref="entityManagerFactoryBean"></property>
		</bean>
		<!-- 4、配置支持注解的事务 -->
		<tx:annotation-driven transaction-manager="transactionManager"/>
		<!-- 5、配置Springdata -->	
		<!-- 加入jpa的命名空间 -->
		<!-- base-package:扫描Repository所在的package -->
		<jpa:repositories base-package="com.lw.springdata_jpa"
			entity-manager-factory-ref="entityManagerFactoryBean"
			></jpa:repositories>
		
	</beans>

```
**2、申明接口**

```
	
	//两个泛型：第一个持久化类的类型，第二个是主键类型。
	public interface PersonRepository exetens JpaRepository<Person, Integer>{
		// 根据名字查找人
		Person getByLastName(String lastName);
	
	}

```

**3、测试**

```

	personRepository.getByLastName("aaa");
```


## 方法定义规范
```

	查询以get/find/read开头
	
```



## @Query注解

```
	
	// 使用query註解:使用jpql灵活查询
	// 子查询
	@Query("SELECT P FROM Person p WHERE p.id = (SELECT MAX(p2.id) FROM Person p2) ")
	Person getMaxIdPerson();
	// Query注解传递参数的方式1：使用占位符
	@Query("SELECT p FROM Person p WHERE p.lastName = ?1 AND p.email = ?2 ")
	List<Person> testQueryAnnotationParam1(String lastName,String email) ;
	
	//模糊查询时，可以直接在psql 的占位符上直接添加%%
	@Query("SELECT p FROM Person p WHERE p.lastName like %?1% AND p.email like %?2% ")
	List<Person> testQueryAnnotationParam2(String lastName,String email) ;
	
	
	// Query注解传递参数的方式2：使用命名参数
	@Query("SELECT P FROM Person p WHERE p.lastName = :lastName AND p.email = :email ")
	List<Person> testQueryAnnotationLikeParam(@Param("lastName") String lastName,@Param("email") String email);
	
	@Query("SELECT P FROM Person p WHERE p.lastName like %:lastName% AND p.email like %:email% ")
	List<Person> testQueryAnnotationLikeParam2(@Param("lastName") String lastName,@Param("email") String email);
	
	//原生sql查询
	//nativeQuery=true:表明为原声查询
	@Query(value="SELECT count(id) FROM springdata_person",nativeQuery=true)
	long getTotalCount() ;
	
```

## @Modifying注解

如果只有@Query注解的话,只能做查询操作,想要实现修改操作的话,需要再添加@Modifying注解

```
	
	// 注意：jpql不支持insert操作,因此想要实现insert操作,可以使用本地sql查询
	// Modifying必须要有事务支持
	@Modifying // 如果不添加这个注解将会报错，不能进行update、delete操作，添加了这个注解之后还会报错，因为需要事务的支持
	// 默认情况下：SpringData的每个方法都会添加事务，但都是只读事务，不能完成修改操作(上面几个例子就是只读事务)。而这里是修改操作，必须添加modifying注解
	@Query("UPDATE Person p SET p.email = :email WHERE id = :id")
	void updatePersonEmailById(@Param("id") Integer id ,@Param("email") String email) ;
```




## repository子接口

SpringData除了提供默认的空接口repository外,还提供了已经有了很多功能的子接口

### CrudRepository(Repository子接口)

主要实现增删改查的操作

```

	T save(T entity); // 保存单个实体
	// 具体查看源码
```

### PagingAndSortingRepository(CrudRepository子接口)

主要实现分页和排序,一共只有两个方法

```
	
	// 排序
	Iterable findAll(Sort sort);
	// 分页
	Page findAll(Pageable pageable);
	
```

**测试**
```

	@Test // 测试分页和排序：但是不能有条件分页
	public void testPagingAndSort(){
		int pageNum = 3 - 1 ;// 第几页 ，从0开始
		int pageSize = 5; // 每页显示记录数
		//Order:对某一个属性是圣墟还是降序
		Order order1 = new Order(Direction.DESC	, "id"); 
		Sort sort = new Sort(Direction.ASC	, "email");
		Pageable pageable = new PageRequest(pageNum, pageSize,sort);
		// 获取分页对象 
		Page<Person> page = personRepository.findAll(pageable);
		System.out.println("size：" + page.getSize());
		System.out.println("当前页：" + (page.getNumber() + 1 ));
		System.out.println("List集合：" + page.getContent());
		System.out.println("总记录数：" + page.getTotalElements());
		System.out.println("总页数：" + page.getTotalPages());
		System.out.println("当前页面的记录数：" + page.getNumberOfElements());
	}
```

## JpaRepository(PagingAndSortingRepository子接口)

添加了批量添加和批量删除等的方法

**方法**

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

**测试**

```

	@Test
	public void testJpaRepository(){
		Person person = new Person() ;
		person.setBirth(new Date());
		person.setEmail("liwen2@qq.com");
		person.setLastName("liwen2");
		person.setId(31); // 设置了id，先查询，如果有则update，没有则插入
		Person person2 = personRepository.saveAndFlush(person); // 没有设置Id，直接插入数据库
		System.out.println(person == person ); // false：类似jpa中的merger方法,完成了对象属性的复制
	}
```

## JpaSpecificationExecutor

这个接口不属于Repository体系,由于PagingAndSortingRepository不能带条件分页,这个接口做了补充


**方法**

```

	  public abstract java.lang.Object findOne(Specification spe);
	  
	  public abstract java.util.List findAll(Specification spe);
	  // 带条件查询的方法
	  public abstract Page findAll(Specification specification, Pageable pageable);
	  
	  public abstract List findAll(Specification arg0,Sort arg1);
	  
	  public abstract long count(Specification arg0);
	
```

**Specification**：封装了查询条件

```
	
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
	

```








