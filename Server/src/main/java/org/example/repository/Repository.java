package org.example.repository;


import org.example.domain.Entity;

public interface Repository<ID, E extends Entity<ID>>{

    E findOne(ID id);

    Iterable<E> findAll();

    void save(E entity);

    void delete(ID id);

    void update(E entity);

}