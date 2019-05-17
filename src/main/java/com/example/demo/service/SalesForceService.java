package com.example.demo.service;


import com.example.demo.model.ATCModel;
import com.sforce.soap.enterprise.QueryResult;
import com.sforce.soap.enterprise.SaveResult;
import com.sforce.soap.enterprise.fault.UnexpectedErrorFault;
import com.sforce.soap.enterprise.sobject.ATC_Request__c;
import com.sforce.soap.enterprise.sobject.SObject;
import com.sforce.ws.ConnectorConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.sforce.soap.enterprise.EnterpriseConnection;

import java.util.logging.Logger;

@Service
public class SalesForceService {

    @Value("${salesforce.loginUrl}")
    String url;

    @Value("${salesforce.username}")
    String username;

    @Value("${salesforce.password}")
    String password;

    Logger logger = Logger.getLogger(SalesForceService.class.getName());

    EnterpriseConnection enterpriseConnection ;

    private EnterpriseConnection getEnterpriseConnection() throws  Exception{

        ConnectorConfig config = new ConnectorConfig();
        config.setAuthEndpoint(url);
        config.setUsername(username);
        config.setPassword(password);
        enterpriseConnection = new EnterpriseConnection(config);
        return enterpriseConnection;
    }

    public ATC_Request__c getATC(String id) throws Exception{
        enterpriseConnection = getEnterpriseConnection();
        logger.info("getting the id" +id);
        QueryResult result = null;

        try{
               if(id.length() == 15) {
                   String query = "Select Id,ATC_File_Name__c, name, Client_SSN__c, Is_Client_Found__c, Account_Number__c,CreatedById, Status__c From ATC_Request__c where id= '" + id + "'";
                   result = enterpriseConnection.query(query);
                   logger.info("getting the records for id " + id);
               }
           }
        catch (Exception e){
            System.out.println(e.getMessage());
            logger.info(" id length is not correct");
            }

        return (ATC_Request__c) result.getRecords()[0];
    }

    public SaveResult[] upsertATC(ATCModel atcModel) throws Exception{
        EnterpriseConnection enterpriseConnection= getEnterpriseConnection();
        ATC_Request__c atc_request__c = new ATC_Request__c() ;
        atc_request__c.setId(atcModel.getId());
        atc_request__c.setStatus__c(atcModel.getStatus__c());
        SaveResult[] saveResults= enterpriseConnection.update(new SObject[]{atc_request__c});
        logger.info("upsert record" + saveResults.toString());

        for(SaveResult result:saveResults){
            if(result.isSuccess()){
                logger.info("record upsert successfully");
            }
            else{
                 logger.info("failed");
            }
        }
        return saveResults;
    }

    //check given id valid or not..

    public boolean checkValidIDorNOT(String id) throws Exception {
        EnterpriseConnection enterpriseConnection= getEnterpriseConnection() ;
        QueryResult queryResult;

        // invalid id: if id's length is 16 then it will return from here and give the response 404.
        if(id.length()>15){
            return false;
        }

        String query= "select id from ATC_Request__c where id = '" +id +"'";
        queryResult= enterpriseConnection.query(query);

        boolean result ;

        //invalid id: checking id into the DB and if the id is exist then will get size = 1 meaning id is valid
        if (queryResult.getSize() == 1) {
               logger.info("given id found");
               result = true;
           } else {
               logger.info("not found");
               result = false;
           }
        return  result;
    }




}
