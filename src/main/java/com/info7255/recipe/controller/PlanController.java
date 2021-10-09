package com.info7255.recipe.controller;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.info7255.recipe.service.PlanService;
import org.everit.json.schema.loader.SchemaLoader;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class PlanController {
    @Autowired
    private PlanService planService;

    @PostMapping(path = "/plan",produces = "application/json")
    public ResponseEntity createPlan(@RequestBody String payload) {
        JSONObject plan=new JSONObject(payload);
        planService.verifyPlanWithSchema(plan);// if not validate, it will post exception to handler
        String id=(String)plan.get("objectId");
        if(planService.getPlan(id) != null){
            return new ResponseEntity(new JSONObject().put("Message","plan ("+id+") exsits").toString(), HttpStatus.BAD_REQUEST);
        }
        planService.addPlan(plan);
        return new ResponseEntity(new JSONObject().put("Message","plan ("+id+") created").toString(), HttpStatus.CREATED);
    }
    @GetMapping(path = "/plan/{id}",produces = "application/json")
    public ResponseEntity getPlan(@PathVariable String id,@RequestHeader HttpHeaders headers ) {
        JSONObject plan=planService.getPlan(id);
        if(plan==null) return new ResponseEntity(new JSONObject().put("Message","plan not found").toString(),HttpStatus.NOT_FOUND);
        String eTag=planService.getETag(plan);
//        System.out.println(eTag);
//        System.out.println(plan);
        HttpHeaders headersToSend = new HttpHeaders();
        headersToSend.setETag(eTag);
        List<String> ifNoneMatch = headers.getIfNoneMatch();//get from request header then use for compare
        if(!ifNoneMatch.isEmpty()&&ifNoneMatch.contains(eTag)){
            return new ResponseEntity(null, headersToSend, HttpStatus.NOT_MODIFIED);
        }else{
            return new ResponseEntity(plan.toString(), headersToSend, HttpStatus.OK);
        }


    }

    @DeleteMapping(path = "/plan/{id}",produces = "application/json")
    public ResponseEntity<?> deletePlan( @PathVariable String id ) {
        if ( planService.getPlan(id) ==null ) return new ResponseEntity(new JSONObject().put("Message","plan ("+id+") not exsit").toString(), HttpStatus.BAD_REQUEST);
        planService.deletePlan(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
