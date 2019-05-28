package com.example.demo.controller;

import com.example.demo.model.ATCModel;
import com.example.demo.service.SalesForceService;
import com.sforce.soap.enterprise.sobject.ATC_Request__c;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
public class SalesForceController {

    @Value("${security.sourceid}")
    private String sourceId;

    @Value("${security.token}")
    private String token;

    @Value("${app.msg}")
    private String msg;

    @Autowired
    private SalesForceService salesForceService;

   private  Logger logger = Logger.getLogger(SalesForceController.class.getName());



    @GetMapping("/")
    public String msg(){
        return "hello" + msg;
    }

    @GetMapping("/greetings")
    public ResponseEntity<String> getGreeting(@RequestHeader("sourceId") String headerSourceId, @RequestHeader("token") String headerToken) {

        if (sourceId.equals(headerSourceId) && token.equals(headerToken)) {
            return new ResponseEntity<>("Hello, GOOD MORNING..!", HttpStatus.OK);
        }
        return new ResponseEntity<>("--- not valid user ---", HttpStatus.UNAUTHORIZED);
    }


    @GetMapping("/msg/{name}")
    public ResponseEntity<String> getUserName(@RequestHeader("sourceId") String headerSourceId, @RequestHeader("token") String headerToken,
                                              @PathVariable("name") String name) {
        if (sourceId.equals(headerSourceId) && token.equals(headerToken)) {
            return new ResponseEntity<>("Hi, " + name, HttpStatus.OK);
        }
        return new ResponseEntity<>(" --- not valid user --- ", HttpStatus.UNAUTHORIZED);
    }


    @ApiOperation("fetch the ATC using ID")
    @GetMapping("/atc/{id}")
    public ResponseEntity findById(@RequestHeader("sourceId") String headerSourceId, @RequestHeader("token") String headerToken,
                                   @PathVariable("id") String id) throws Exception {

        if (sourceId.equals(headerSourceId) && token.equals(headerToken)) {

            if(id == null || id.isEmpty() || id.length() > 15 ) {
                return new ResponseEntity("-- Something is wrong here --", HttpStatus.NOT_FOUND);
            }
            //checking the id is valid or not
            ATC_Request__c atc_request__c = salesForceService.getATC(id);
            if (atc_request__c == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("--- valid user --- "+atc_request__c , HttpStatus.OK);
        }
        return new ResponseEntity<>(" --- not valid user --- ", HttpStatus.UNAUTHORIZED);
    }

    @ApiOperation(value = "update atc")
    @PostMapping("/atc")
    public ResponseEntity updateATC(@RequestHeader("sourceId") String headerSourceId, @RequestHeader("token") String headerToken,
                                    @RequestBody ATCModel atcModel) throws Exception {
        if (sourceId.equals(headerSourceId) && token.equals(headerToken)) {
            logger.info(atcModel.toString());
            salesForceService.upsertATC(atcModel);
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity<>(" --- not valid user --- ", HttpStatus.UNAUTHORIZED);
    }
}
