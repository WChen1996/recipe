package com.info7255.recipe.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends CrudRepository<Account,String> {
    Optional<Account> findAccountByUsername(String username);
}
