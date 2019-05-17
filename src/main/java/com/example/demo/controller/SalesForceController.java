package com.example.demo.controller;

import com.example.demo.model.ATCModel;
import com.example.demo.service.SalesForceService;
import com.sforce.soap.enterprise.sobject.ATC_Request__c;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
public class SalesForceController {

    @Autowired
    private SalesForceService salesForceService;

    Logger logger = Logger.getLogger(SalesForceController.class.getName());

    @RequestMapping("/greetings")
    public String getGreeting() {
        return "Hello Spring World!";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/msg/{name}")
    public String sayHi(@PathVariable("name") String name) {
        return "Hi, " + name;
    }



    @GetMapping("/atc/{id}")
    public ResponseEntity<ATC_Request__c> findById(@PathVariable("id") String id) throws Exception {

       //checking the id is valid or not
        if(!salesForceService.checkValidIDorNOT(id)){
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if(id == null || id.isEmpty()){
            return new ResponseEntity("-- Something is wrong here --", HttpStatus.NOT_FOUND) ;
        }

        return new ResponseEntity<>(salesForceService.getATC(id) ,HttpStatus.OK );
    }

    @PostMapping("/atc/{id}")
    public ResponseEntity updateATC(@RequestBody ATCModel atcModel) throws Exception{
        logger.info(atcModel.toString());
       salesForceService.upsertATC(atcModel);

        return new ResponseEntity(HttpStatus.OK);
    }


}
