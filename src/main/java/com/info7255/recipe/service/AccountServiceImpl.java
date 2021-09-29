package com.info7255.recipe.service;

import com.info7255.recipe.model.Account;
import com.info7255.recipe.model.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AccountServiceImpl {
    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public void addAccount(Account acc){
        accountRepository.save(acc);
    }

    public Account findAccountByUsername(String username){
        return accountRepository.findAccountByUsername(username).orElse(null);
    }

    public Account findAccountById(String id){
        return accountRepository.findById(id).orElse(null);
    }

    public Iterable<Account> findAllAccount(){
        return accountRepository.findAll();
    }
}
