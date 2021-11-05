package com.info7255.recipe.controller;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.info7255.recipe.service.JwtAuthService;
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
import java.util.Map;


@RestController
public class PlanController {
    @Autowired
    private PlanService planService;
    @Autowired
    private JwtAuthService jwtAuthService;
    @GetMapping(path = "/token",produces = "application/json")
    public ResponseEntity<?> generateToken() {
        String token = jwtAuthService.generateToken();
        return new ResponseEntity<>(new JSONObject().put("Token",token).toString(), HttpStatus.CREATED);
    }
    @PostMapping(path = "/plan",produces = "application/json")
    public ResponseEntity createPlan(@RequestBody String payload) {
        JSONObject plan=new JSONObject(payload);
        planService.verifyPlanWithSchema(plan);// if not validate, it will post exception to handler
        String key= "plan_" + plan.getString("objectId");
        if (planService.isKeyPresent(key)) {
            return new ResponseEntity(new JSONObject().put("Message",key+" exists").toString(), HttpStatus.BAD_REQUEST);
        }
        planService.addPlan(plan);
        String eTag=planService.setETag(key,plan);
        HttpHeaders headersToSend = new HttpHeaders();
        headersToSend.setETag(eTag);
        return new ResponseEntity(new JSONObject().put("Message",key+" created").toString(),headersToSend, HttpStatus.CREATED);
    }
    @GetMapping(path = "/{objectType}/{objectId}",produces = "application/json")
    public ResponseEntity getPlan(@PathVariable String objectId,
                                  @PathVariable String objectType,@RequestHeader HttpHeaders headers ) {
        String key = objectType + "_" + objectId;
        if (!planService.isKeyPresent(key)) return new ResponseEntity(new JSONObject().put("Message","plan not found").toString(),HttpStatus.NOT_FOUND);
        List<String> ifNoneMatch;
        ifNoneMatch = headers.getIfNoneMatch();
        String eTag = planService.getETag(key);
        HttpHeaders headersToSend = new HttpHeaders();
        headersToSend.setETag(eTag);
        if (objectType.equals("plan") && ifNoneMatch.contains(eTag))
            return new ResponseEntity<>(null, headersToSend, HttpStatus.NOT_MODIFIED);
        Map<String, Object> plan = planService.getPlan(key);
        if (objectType.equals("plan"))
            return new ResponseEntity<>(plan, headersToSend, HttpStatus.OK);
        return new ResponseEntity<>(plan, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{objectType}/{objectId}",produces = "application/json")
    public ResponseEntity<?> deletePlan(@PathVariable String objectId,
                                        @PathVariable String objectType,
                                        @RequestHeader HttpHeaders headers) {
        String key = objectType + "_" + objectId;
        if (!planService.isKeyPresent(key)) return new ResponseEntity(new JSONObject().put("Message",key+" not exists").toString(), HttpStatus.BAD_REQUEST);

        String eTag = planService.getETag(key);
        List<String> ifMatch;
        ifMatch = headers.getIfMatch();
        if (ifMatch.size() == 0) return new ResponseEntity(new JSONObject().put("Message","etag not provided").toString(), HttpStatus.BAD_REQUEST);
        if (!ifMatch.contains(eTag)){
            return preConditionFailed(eTag);
        }

        planService.deletePlan(key);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @PutMapping(value = "/plan/{objectId}", produces = "application/json")
    public ResponseEntity<?> updatePlan(@PathVariable String objectId,
                                        @RequestBody String planObject,
                                        @RequestHeader HttpHeaders headers) {

        String key = "plan" + "_" + objectId;
        if (!planService.isKeyPresent(key)) return new ResponseEntity(new JSONObject().put("Message","plan not found").toString(),HttpStatus.NOT_FOUND);

        String eTag = planService.getETag(key);
        List<String> ifMatch;
        ifMatch = headers.getIfMatch();
        if (ifMatch.size() == 0) return new ResponseEntity(new JSONObject().put("Message","etag not provided").toString(), HttpStatus.BAD_REQUEST);
        if (!ifMatch.contains(eTag)){
            return preConditionFailed(eTag);
        }
        JSONObject plan = new JSONObject(planObject);
        planService.verifyPlanWithSchema(plan);// if not validate, it will post exception to handler
        planService.deletePlan(key);
        planService.addPlan(plan);
        String updatedETag=planService.setETag(key,plan);
        HttpHeaders headersToSend = new HttpHeaders();
        headersToSend.setETag(updatedETag);
        return new ResponseEntity<>(new JSONObject().put("Message",key+" updated").toString(),
                headersToSend,
                HttpStatus.OK);
    }
    @PatchMapping(value = "/{objectType}/{objectId}", produces = "application/json")
    public ResponseEntity<?> patchPlan(@PathVariable String objectId,
                                       @RequestBody String planObject,
                                       @RequestHeader HttpHeaders headers) {
        String key = "plan" + "_" + objectId;
        if (!planService.isKeyPresent(key)) return new ResponseEntity(new JSONObject().put("Message","plan not found").toString(),HttpStatus.NOT_FOUND);

        String eTag = planService.getETag(key);
        List<String> ifMatch;
        ifMatch = headers.getIfMatch();
        if (ifMatch.size() == 0) return new ResponseEntity(new JSONObject().put("Message","etag not provided").toString(), HttpStatus.BAD_REQUEST);
        if (!ifMatch.contains(eTag)){
            return preConditionFailed(eTag);
        }
        JSONObject plan = new JSONObject(planObject);
        planService.addPlan(plan);
        String updatedETag=planService.setETag(key,plan);
        HttpHeaders headersToSend = new HttpHeaders();
        headersToSend.setETag(updatedETag);
        return new ResponseEntity<>(new JSONObject().put("Message",key+" patched").toString(),
                headersToSend,
                HttpStatus.OK);
    }
    private ResponseEntity preConditionFailed(String eTag) {
        HttpHeaders headersToSend = new HttpHeaders();
        headersToSend.setETag(eTag);
        return new ResponseEntity<>(new JSONObject().put("Message","plan has been changed").toString(), headersToSend, HttpStatus.PRECONDITION_FAILED);
    }

}
