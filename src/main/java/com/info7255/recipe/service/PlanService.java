package com.info7255.recipe.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.info7255.recipe.controller.PlanController;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class PlanService {
   @Autowired
   private Jedis jedis;
   @Autowired
   private RedisTemplate<String, Object> template;
   public String addPlan(JSONObject plan) {
      String key = (String) plan.get("objectId");
      jsonToMap(plan);

      return key;
   }

   public Map<String, Object> getPlan(String key ) {
      Map<String, Object> result = new HashMap<>();
      getOrDelete(key, result, false);
      return result;
   }
   public void deletePlan(String key ) {
      getOrDelete(key, null, true);
   }
   public void verifyPlanWithSchema(JSONObject plan){
      JSONObject schemaJson= new JSONObject(new JSONTokener(PlanController.class.getResourceAsStream("/plan_schema.json")));
      Schema schema= SchemaLoader.load(schemaJson);
      schema.validate(plan);
   }
   public String getETag(String key) {
      return jedis.hget(key, "eTag");
   }
   public String setETag(String key, JSONObject jsonObject) {
      String encoded=null;
      try {
         MessageDigest digest = MessageDigest.getInstance("SHA-256");
         byte[] hash = digest.digest(jsonObject.toString().getBytes(StandardCharsets.UTF_8));
         encoded = Base64.getEncoder().encodeToString(hash);
      } catch (NoSuchAlgorithmException e) {
         e.printStackTrace();
      }
      String eTag = "\""+encoded+"\"";
      jedis.hset(key, "eTag", eTag);
      return eTag;
   }
   public Map<String, Map<String, Object>> jsonToMap(JSONObject jsonObject) {
      Map<String, Map<String, Object>> map = new HashMap<>();
      Map<String, Object> contentMap = new HashMap<>();

      for (String key : jsonObject.keySet()) {
         String redisKey = jsonObject.get("objectType") + "_" + jsonObject.get("objectId");
         Object value = jsonObject.get(key);

         if (value instanceof JSONObject) {
            value = jsonToMap((JSONObject) value);
            jedis.sadd(redisKey + ":" + key, ((Map<String, Map<String, Object>>) value).entrySet().iterator().next().getKey());
         } else if (value instanceof JSONArray) {
            value = jsonToList((JSONArray) value);
            ((List<Map<String, Map<String, Object>>>) value)
                    .forEach((entry) -> {
                       entry.keySet()
                               .forEach((listKey) -> {
                                  jedis.sadd(redisKey + ":" + key, listKey);
                               });
                    });
         } else {
            jedis.hset(redisKey, key, value.toString());
            contentMap.put(key, value);
            map.put(redisKey, contentMap);
         }
      }
      return map;
   }
   public List<Object> jsonToList(JSONArray jsonArray) {
      List<Object> result = new ArrayList<>();
      for (Object value : jsonArray) {
         if (value instanceof JSONArray) value = jsonToList((JSONArray) value);
         else if (value instanceof JSONObject) value = jsonToMap((JSONObject) value);
         result.add(value);
      }
      return result;
   }
   private Map<String, Object> getOrDelete(String redisKey, Map<String, Object> resultMap, boolean isDelete) {
      Set<String> keys = jedis.keys(redisKey + ":*");
      keys.add(redisKey);

      for (String key : keys) {
         if (key.equals(redisKey)) {
            if (isDelete) jedis.del(new String[]{key});
            else {
               Map<String, String> object = jedis.hgetAll(key);
               for (String attrKey : object.keySet()) {
                  if (!attrKey.equalsIgnoreCase("eTag")) {
                     resultMap.put(attrKey, isInteger(object.get(attrKey)) ? Integer.parseInt(object.get(attrKey)) : object.get(attrKey));
                  }
               }
            }
         } else {
            String newKey = key.substring((redisKey + ":").length());
            Set<String> members = jedis.smembers(key);
            if (members.size() > 1 || newKey.equals("linkedPlanServices")) {
               List<Object> listObj = new ArrayList<>();
               for (String member : members) {
                  if (isDelete) {
                     getOrDelete(member, null, true);
                  } else {
                     Map<String, Object> listMap = new HashMap<>();
                     listObj.add(getOrDelete(member, listMap, false));
                  }
               }
               if (isDelete) jedis.del(new String[]{key});
               else resultMap.put(newKey, listObj);
            } else {
               if (isDelete) {
                  jedis.del(new String[]{members.iterator().next(), key});
               } else {
                  Map<String, String> object = jedis.hgetAll(members.iterator().next());
                  Map<String, Object> nestedMap = new HashMap<>();
                  for (String attrKey : object.keySet()) {
                     nestedMap.put(attrKey,
                             isInteger(object.get(attrKey)) ? Integer.parseInt(object.get(attrKey)) : object.get(attrKey));
                  }
                  resultMap.put(newKey, nestedMap);
               }
            }
         }
      }
      return resultMap;
   }
   private boolean isInteger(String str) {
      try {
         Integer.parseInt(str);
      } catch (Exception e) {
         return false;
      }
      return true;
   }
   public boolean isKeyPresent(String key) {
      Map<String, String> value = jedis.hgetAll(key);
      jedis.close();
      return !(value == null || value.isEmpty());
   }
}
