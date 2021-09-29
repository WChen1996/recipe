package com.info7255.recipe.controller;


import com.info7255.recipe.service.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.info7255.recipe.model.Account;

@RestController
public class AccountController {
    @Autowired
    private AccountServiceImpl accountService;

    @PostMapping(path = "account/self", produces = "application/json")
    public ResponseEntity getAccount(@RequestBody Account info) {
        System.out.println(info.getUsername());
        Account account=accountService.findAccountByUsername(info.getUsername());
        System.out.println(account);
        return new ResponseEntity(account, HttpStatus.OK);
    }

    @GetMapping(path = "account/{id}", produces = "application/json")
    public ResponseEntity getAccount(@PathVariable String id) {

        Account account=accountService.findAccountById(id);
       if(account==null){
           return new ResponseEntity(HttpStatus.NOT_FOUND);
       }
        return new ResponseEntity(account, HttpStatus.OK);
    }
    @GetMapping(path = "account/all", produces = "application/json")
    public ResponseEntity getAccount() {

        Iterable<Account> accounts=accountService.findAllAccount();
        if(accounts==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(accounts, HttpStatus.OK);
    }

    @PostMapping(path = "account/create", produces = "application/json") // Map ONLY POST Request
    public ResponseEntity createUser(@RequestBody Account info) {

        if (info.getPassword() == null || info.getUsername() == null) {
            //System.out.println("1");
            return new ResponseEntity<>(HttpStatus.valueOf(400));
        }
        if (info.getPassword().equals("") || info.getUsername().equals("")) {
            //System.out.println("2");
            return new ResponseEntity<>(HttpStatus.valueOf(400));
        }
        if(accountService.findAccountByUsername(info.getUsername())!=null){
            return new ResponseEntity<>(HttpStatus.valueOf(400));
        }

        Account n = new Account(info.getUsername(),info.getPassword());


        accountService.addAccount(n);
        return new ResponseEntity<>(n, HttpStatus.CREATED);
    }


}
