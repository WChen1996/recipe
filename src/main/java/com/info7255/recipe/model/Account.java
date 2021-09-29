package com.info7255.recipe.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import lombok.Data;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.stereotype.Component;

import java.io.Serializable;
@Component
@Data
@RedisHash
public class Account implements Serializable {
    @Id private String id;
    @Indexed private String username;
    private String password;
    public Account(String username,String password){
        this.username=username;
        this.password=password;
    }
}
