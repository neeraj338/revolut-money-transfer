package com.revolut.repository;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

public interface JpaRepository<E extends Serializable, P> {
	
	/**
	 * refresh the indicated entity to database
	 * @param entity
	 */
	void refresh(E entity);

	/**
	 * Persist the indicated entity to database
	 * 
	 * @param entity
	 * @return the primary key
	 */
	E persist(E entity);
	
	/**
	 * save all the indicated entities to database
	 * @param entities
	 * @return entities
	 */
	Iterable<E> saveAll(Iterable<E> entities);
	
	/**
	 * Retrieve an object using indicated ID
	 * 
	 * @param id
	 * @return
	 */
	E find(P id);

	/**
	 * Update indicated entity to database
	 * 
	 * @param entity
	 */
	void update(E entity);

	/**
	 * Delete indicated entity from database
	 * 
	 * @param entity
	 */
	void delete(E entity);

	/**
	 * Return the entity class
	 * 
	 * @return
	 */
	Class<E> getEntityClass();

	/**
	 * Get the entity manager
	 * 
	 * @return
	 */
	EntityManager getEntityManager();

	/**
	 * 
	 * @return
	 */
	List<E> findAll();
}