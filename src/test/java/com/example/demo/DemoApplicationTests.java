package com.example.demo;

import com.sforce.soap.enterprise.SaveResult;
import com.sforce.soap.enterprise.UpsertResult;
import com.sforce.soap.enterprise.sobject.SObject;
import org.junit.Assert;
import com.sforce.ws.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.enterprise.QueryResult;
import com.sforce.soap.enterprise.sobject.ATC_Request__c;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {


	String url ="https://test.salesforce.com/services/Soap/c/32.0/";
	String username = "dfli_service_004@fdr.com.stage1";
	String password = "dfli004KgfcMzoLuRPQQlOcxMGea3po";


	@Test
	public void contextLoads() {
	}

	@Test
	public void testSalesforceConnectivity() throws Exception {
		EnterpriseConnection enterpriseConnection = getEnterpriseConnection();
		Assert.assertNotNull(enterpriseConnection);
		Assert.assertEquals(enterpriseConnection.getConfig().getUsername(),username);
		Assert.assertEquals(enterpriseConnection.getConfig().getPassword(),password);
	}

	@Test
	public void testSalesforceQueryExecution() throws Exception {
		EnterpriseConnection enterpriseConnection = getEnterpriseConnection();
		QueryResult result ;

		String query= "Select Id,ATC_File_Name__c, Client_SSN__c, Is_Client_Found__c, Account_Number__c,CreatedById, Status__c From ATC_Request__c where id='a3h2F000000CkPo'";
		result= enterpriseConnection.query(query);
		if(result.getSize()>0) {
			ATC_Request__c atc_request__c  = (ATC_Request__c)result.getRecords()[0];
			System.out.println(atc_request__c);
		}
		System.out.println(result);
	}

	private EnterpriseConnection getEnterpriseConnection() throws Exception {
		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(username);
		config.setPassword(password);
		config.setAuthEndpoint(url);
		EnterpriseConnection enterpriseConnection= new EnterpriseConnection(config);
		System.out.println(enterpriseConnection);
		return enterpriseConnection;
	}

	@Test
	public void testUpsertOperation() throws Exception{
		EnterpriseConnection enterpriseConnection= getEnterpriseConnection();
		ATC_Request__c atc_request__c = null;
		atc_request__c.setId("a3h2F000000Cml1");
		atc_request__c.setStatus__c("success");

		SaveResult[] upsertResults = enterpriseConnection.update( new SObject[]{atc_request__c});
		System.out.println("upsert result"+upsertResults.toString());
		for(SaveResult upsertResult: upsertResults){
			if(upsertResult.isSuccess()){
				System.out.println("upserted successfully");
			}
			else{ System.out.println("failed");
			}
		}
	}
}
