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
 * 1��Repository��һ����ǽӿ�
 * 2��������Ƕ���Ľӿڼ̳���Repository����ýӿڻᱻIOC����ʶ��ΪRepository bean
 * 3������Ҳ����ʹ��ע������Ǽ̳нӿ���ʵ�ֹ��ܡ�
 * 4����Repository�������ӽӿڱ����ϸ�Ҫȥ
 * 4.1����ѯһfind��get��read��ͷ
 * 4.2���漰������ѯʱ�������������������ؼ������ӣ���������������ĸ��д��ͷ
 * 5��֧�����Եļ�����ѯ�������ǰ���з������������ԣ�������ʹ�õ�ǰ������ԡ�
 * 	 ���һ��Ҫʹ�ü������ԣ�������֮������_����
 * @author lw
 *
 */
// �������ͣ���һ���־û�������ͣ��ڶ������������͡�
//@RepositoryDefinition(domainClass=Person.class,idClass=Integer.class):ֻ����Repository
//public interface PersonRepository extends CrudRepository<Person,Integer> {
//public interface PersonRepository extends PagingAndSortingRepository<Person, Integer>{	// ʵ�ַ�ҳ������Ч��
public interface PersonRepository extends  JpaRepository<Person, Integer> 
								, JpaSpecificationExecutor<Person>,PersonDao{	// ���԰�����������������ķ�ҳ����

	Person getByLastName(String lastName);
	
	// WHERE lastName Like ?% AND id < ?
	List<Person> getByLastNameStartingWithAndIdLessThan(String lastName,Integer id);
	
	// WHERE lastName like %? AND ID < ? 
	List<Person> getByLastNameEndingWithAndIdLessThan(String lastName,Integer id ) ;
	
	// WHERE id in (?,?,?) OR birth < ? 
	List<Person> getByEmailInOrBirthLessThan(List<String> emailList,Date date);
	//	void test() ;error
	
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
	
	/************************Modifyingע��******************************/
	
	// ע�⣺jpql��֧��insert����
	// Modifying����Ҫ������֧��
	@Modifying // �����������ע�⽫�ᱨ�����ܽ���update��delete��������������ע��֮�󻹻ᱨ����Ϊ��Ҫ�����֧��
	// Ĭ������£�SpringData��ÿ����������������񣬵�����ֻ�����񣬲�������޸Ĳ���(���漸�����Ӿ���ֻ������)�����������޸Ĳ������������modifyingע��
	@Query("UPDATE Person p SET p.email = :email WHERE id = :id")
	void updatePersonEmailById(@Param("id") Integer id ,@Param("email") String email) ;
}
