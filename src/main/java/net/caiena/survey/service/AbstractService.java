package net.caiena.survey.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.List;

/**
 * @author bzumpano
 * @since 3/23/16
 */
public abstract class AbstractService<T, K extends Serializable> {

    @Autowired
    private CrudRepository<T, K> repository;


    public List<T> list() {
        return (List<T>) repository.findAll();
    }

    public T save(final T entity) {
        return repository.save(entity);
    }

    public T find(final K id) {
        return repository.findOne(id);
    }

    public void delete(final K id) {
        repository.delete(id);
    }
}
