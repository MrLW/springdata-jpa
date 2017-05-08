package com.lw.springdata_jpa;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * 1、Repository是一个标记接口
 * 2、如果我们定义的接口继承了Repository，则该接口会被IOC容器识别为Repository bean
 * 3、我们也可以使用注解而不是继承接口来实现功能。
 * 4、在Repository中申明子接口必须严格要去
 * 4.1、查询一find、get、read开头
 * 4.2、涉及条件查询时，条件的属性用条件关键字连接，条件属性以首字母大写开头
 * 5、支持属性的级联查询，如果当前类有符合条件的属性，则优先使用当前类的属性。
 * 	 如果一定要使用级联属性，则属性之间是有_连接
 * @author lw
 *
 */
// 两个泛型：第一个持久化类的类型，第二个是主键类型。
//@RepositoryDefinition(domainClass=Person.class,idClass=Integer.class):只适用Repository
//public interface PersonRepository extends CrudRepository<Person,Integer> {
//public interface PersonRepository extends PagingAndSortingRepository<Person, Integer>{	// 实现分页和排序效果
public interface PersonRepository extends  JpaRepository<Person, Integer> 
								, JpaSpecificationExecutor<Person>,PersonDao{	// 可以帮组我们完成有条件的分页排序

	Person getByLastName(String lastName);
	
	// WHERE lastName Like ?% AND id < ?
	List<Person> getByLastNameStartingWithAndIdLessThan(String lastName,Integer id);
	
	// WHERE lastName like %? AND ID < ? 
	List<Person> getByLastNameEndingWithAndIdLessThan(String lastName,Integer id ) ;
	
	// WHERE id in (?,?,?) OR birth < ? 
	List<Person> getByEmailInOrBirthLessThan(List<String> emailList,Date date);
	//	void test() ;error
	
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
	
	/************************Modifying注解******************************/
	
	// 注意：jpql不支持insert操作
	// Modifying必须要有事务支持
	@Modifying // 如果不添加这个注解将会报错，不能进行update、delete操作，添加了这个注解之后还会报错，因为需要事务的支持
	// 默认情况下：SpringData的每个方法都会添加事务，但都是只读事务，不能完成修改操作(上面几个例子就是只读事务)。而这里是修改操作，必须添加modifying注解
	@Query("UPDATE Person p SET p.email = :email WHERE id = :id")
	void updatePersonEmailById(@Param("id") Integer id ,@Param("email") String email) ;
}
