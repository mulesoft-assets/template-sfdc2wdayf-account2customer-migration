/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates.integration;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleEvent;
import org.mule.processor.chain.SubflowInterceptingChainLifecycleWrapper;
import org.mule.tck.junit4.rule.DynamicPort;
import org.mule.templates.builders.SfdcObjectBuilder;

import com.mulesoft.module.batch.BatchTestHelper;
import com.workday.revenue.GetCustomersResponseType;

/**
 * The objective of this class is to validate the correct behavior of the Mule
 * Template that make calls to external systems.
 * 
 * The test will update the SFDC test account, then invoke the migration batch
 * process and finally check that the corresponding customer in Workday is
 * correctly updated.
 */
public class BusinessLogicIT extends AbstractTemplateTestCase {

	private static final String PATH_TO_TEST_PROPERTIES = "./src/test/resources/mule.test.properties";
	protected static final int TIMEOUT_SECONDS = 300;
	private SubflowInterceptingChainLifecycleWrapper UPDATE_ACCOUNT_FLOW;
	private SubflowInterceptingChainLifecycleWrapper RETRIEVE_CUSTOMER_FLOW;
	private String SFDC_TEST_ACCOUNT_ID;
	private BatchTestHelper helper;

	@Rule
	public DynamicPort port = new DynamicPort("http.port");

	/**
	 * Sets up the test prerequisites.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		helper = new BatchTestHelper(muleContext);

		final Properties props = new Properties();
		try {
			props.load(new FileInputStream(PATH_TO_TEST_PROPERTIES));
		} catch (Exception e) {
			logger.error("Error occured while reading mule.test.properties", e);
		}

		SFDC_TEST_ACCOUNT_ID = props.getProperty("sfdc.testaccount.id");
		UPDATE_ACCOUNT_FLOW = getSubFlow("updateAccountFlow");
		UPDATE_ACCOUNT_FLOW.initialise();
		RETRIEVE_CUSTOMER_FLOW = getSubFlow("retrieveCustomersFlow");
		RETRIEVE_CUSTOMER_FLOW.initialise();

	}

	/**
	 * Performs update on the SFDC test account.
	 * 
	 * @param name
	 *            String - new name
	 * @param website
	 *            String - new website
	 * @throws Exception
	 */
	public void editTestDataInSandbox(String name, String website)
			throws Exception {
		// create object to edit the test account with
		Map<String, Object> account = SfdcObjectBuilder.anAccount()
				.with("Id", SFDC_TEST_ACCOUNT_ID).with("Name", name)
				.with("Website", website).build();
		List<Map<String, Object>> payload = new ArrayList<>();
		payload.add(account);

		UPDATE_ACCOUNT_FLOW.process(getTestEvent(payload,
				MessageExchangePattern.REQUEST_RESPONSE));
	}

	/**
	 * Tests if update of a SFDC test Account results in Workday Prospect update
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMainFlow() throws Exception {
		// edit test data
		String name = generateUniqueName();
		String website = generateUniqueWebsite();
		editTestDataInSandbox(name, website);

		// run the main migration flow
		runFlow("mainFlow");
		helper.awaitJobTermination(TIMEOUT_SECONDS * 1000, 500);
		helper.assertJobWasSuccessful();

		MuleEvent event = RETRIEVE_CUSTOMER_FLOW.process(getTestEvent(
				SFDC_TEST_ACCOUNT_ID, MessageExchangePattern.REQUEST_RESPONSE));
		GetCustomersResponseType response = (GetCustomersResponseType) event
				.getMessage().getPayload();

		// assertions
		assertEquals("Workday should return one result", 1, response
				.getResponseResults().get(0).getTotalResults().intValue());
		assertEquals("The website should be the same", website, response
				.getResponseData().get(0).getCustomer().get(0)
				.getCustomerData().getBusinessEntityData().getContactData()
				.getWebAddressData().get(0).getWebAddress());
		assertEquals("The name should be the same", name, response
				.getResponseData().get(0).getCustomer().get(0)
				.getCustomerData().getCustomerName());
	}

	/**
	 * Generates unique name based on current time.
	 * 
	 * @return String - name
	 */
	public String generateUniqueName() {
		return "TestCustomer" + System.currentTimeMillis();
	}

	/**
	 * Generates unique website based on current time.
	 * 
	 * @return String - Website
	 */
	public String generateUniqueWebsite() {
		return "http://test." + System.currentTimeMillis() + ".com";
	}

}
