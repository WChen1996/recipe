package com.info7255.recipe.service;

import com.info7255.recipe.model.Account;
import org.springframework.stereotype.Service;

@Service
public interface AccountService {
    void addAccount(Account acc);
    Account findAccountByUsename(String username);
    Account findAccountById(String id);
    Account[] findAllAccount();
}
