package com.lostitem.dao;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lostitem.entities.Items;


public interface ItemRepository extends JpaRepository<Items, Integer>{
	//pagination...
	
	@Query("from Items as i where i.user.id =:userId")
	//currentPage-page
	//Items Per page - 5
	public Page<Items> findItemsByUser(@Param("userId")int userId, Pageable pePageable);
	
	
}
