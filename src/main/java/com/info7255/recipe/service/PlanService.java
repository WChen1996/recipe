package com.info7255.recipe.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.info7255.recipe.controller.PlanController;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class PlanService {
   @Autowired
   private Jedis jedis;
   @Autowired
   private RedisTemplate<String, Object> template;
   public String addPlan(JSONObject plan) {
      String key = (String) plan.get("objectId");
      jedis.set(key,plan.toString());
      jedis.close();
      return key;
   }
   public JSONObject getPlan(String key ) {
      String plan = jedis.get(key);
      jedis.close();
      if(plan!=null){
         return new JSONObject(plan);
      }
      else
         return null;
   }
   public void deletePlan(String key ) {
      jedis.del(key);
      jedis.close();
   }
   public void verifyPlanWithSchema(JSONObject plan){
      JSONObject schemaJson= new JSONObject(new JSONTokener(PlanController.class.getResourceAsStream("/plan_schema.json")));
      Schema schema= SchemaLoader.load(schemaJson);
      schema.validate(plan);
   }
   public String getETag(JSONObject json) {

      String encoded=null;
      try {
         MessageDigest digest = MessageDigest.getInstance("SHA-256");
         byte[] hash = digest.digest(json.toString().getBytes(StandardCharsets.UTF_8));
         encoded = Base64.getEncoder().encodeToString(hash);
      } catch (NoSuchAlgorithmException e) {
         e.printStackTrace();
      }
      return "\""+encoded+"\"";
   }


}
