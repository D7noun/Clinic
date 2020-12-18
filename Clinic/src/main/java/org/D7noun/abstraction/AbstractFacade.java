/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.D7noun.abstraction;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
public abstract class AbstractFacade<T> {
	public Class<T> entityClass;

	public AbstractFacade() {
		super();
	}

	public AbstractFacade(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	@PersistenceContext(unitName = "clinic-pu")
	protected EntityManager em;

	public T save(T entity) {

		beforeSave(entity);
		entity = em.merge(entity);
		afterSave(entity);
		return entity;
	}

	public void beforeSave(T entity) {

	}

	public void afterSave(T entity) {

	}

	public List<T> save(List<T> entities) {
		if (entities == null)
			return entities;

		for (int i = 0; i < entities.size(); i++) {
			entities.set(i, save(entities.get(i)));
		}

		return entities;
	}

	public void remove(T entity) {
		if (beforeRemove(entity)) {
			em.remove(em.merge(entity));
			afterRemove(entity);
		}

	}

	public boolean beforeRemove(T entity) {
		return true;
	}

	public void afterRemove(T entity) {

	}

	public void remove(T[] entity) {
		for (T t : entity) {
			remove(t);
		}

	}

	public T find(Object id) {
		return em.find(entityClass, id);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<T> findAll() {
		javax.persistence.criteria.CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
		cq.select(cq.from(entityClass));
		return em.createQuery(cq).getResultList();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<T> findRange(int[] range) {
		javax.persistence.criteria.CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
		cq.select(cq.from(entityClass));
		javax.persistence.Query q = em.createQuery(cq);
		q.setMaxResults(range[1] - range[0]);
		q.setFirstResult(range[0]);
		return q.getResultList();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int count() {
		javax.persistence.criteria.CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
		javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
		cq.select(em.getCriteriaBuilder().count(rt));
		javax.persistence.Query q = em.createQuery(cq);
		return ((Long) q.getSingleResult()).intValue();
	}

	protected T getSingleResult(TypedQuery<T> typedQuery) {

		List<T> t = typedQuery.getResultList();
		if (t == null || t.size() == 0)
			throw new EntityNotFoundException();
		return t.get(0);
	}

	public T createNewInstance() throws InstantiationException, IllegalAccessException {

		return entityClass.newInstance();
	}

	public void clearFromCache(Object id) {
		em.getEntityManagerFactory().getCache().evict(entityClass, id);
	}

}
