package com.revolut.repository;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import com.google.inject.Inject;
import com.google.inject.Provider;

public abstract class JpaRepositotyImpl<E extends Serializable, P> implements JpaRepository<E, P> {
	
	@Inject
	protected Provider<EntityManager> entityManagerProvider;
	
	public void refresh(E entity) {
		this.getEntityManager().refresh(entity);
	}
	
	public E persist(E entity) {
		this.getEntityManager().getTransaction().begin();
		getEntityManager().persist(entity);
		this.getEntityManager().getTransaction().commit();
		return entity;
	}
	
	public Iterable<E> saveAll(Iterable<E> entities){
		this.getEntityManager().getTransaction().begin();
		
		for(E entity: entities) {
			getEntityManager().persist(entity);
		}
		
		this.getEntityManager().getTransaction().commit();
		return entities;
	}
	
	public E find(P id) {
		return getEntityManager().find(getEntityClass(), id);
	}

	public void update(E entity) {
		this.getEntityManager().getTransaction().begin();
		getEntityManager().merge(entity);
		this.getEntityManager().getTransaction().commit();
	}

	public void delete(E entity) {
		this.getEntityManager().getTransaction().begin();
		getEntityManager().remove(entity);
		this.getEntityManager().getTransaction().commit();
	}

	public EntityManager getEntityManager() {
		return entityManagerProvider.get();
	}

	@SuppressWarnings("unchecked")
	public Class<E> getEntityClass() {

		Class<E> entityClass = null;
		Type type = getClass().getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			ParameterizedType paramType = (ParameterizedType) type;

			entityClass = (Class<E>) paramType.getActualTypeArguments()[0];

		} else {
			throw new IllegalArgumentException("Could not guess entity class by reflection");
		}

		return entityClass;
	}

	@Override
	public List<E> findAll() {
		Class<E> entityClass = getEntityClass();
		CriteriaBuilder criteriaBuilder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(entityClass);
		criteriaQuery.from(entityClass);
		return this.getEntityManager().createQuery(criteriaQuery).getResultList();
	}

}