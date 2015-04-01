/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates;

import java.util.ArrayList;
import java.util.List;

import com.workday.revenue.CustomerRequestCriteriaType;
import com.workday.revenue.CustomerResponseGroupType;
import com.workday.revenue.GetCustomersRequestType;

/**
 * This class contains static methods to create Workday (Revenue Management web service) GetCustomersRequestType objects.
 * @author 
 *
 */
public class CustomerRequest {

	/**
	 * Creates request without any criteria. Will return all customers.
	 * @return GetCustomersRequestType - request for Get_Customers operation.
	 */
	public static GetCustomersRequestType createNoCriteria() {
		GetCustomersRequestType request = new GetCustomersRequestType();

		// no criteria - select everything
		CustomerResponseGroupType responseGroup = new CustomerResponseGroupType();
		responseGroup.setIncludeCustomerData(true);
		responseGroup.setIncludeReference(true);

		List<CustomerResponseGroupType> responseGroups = new ArrayList<>();
		request.setResponseGroup(responseGroups);
	
		return request;
	}
	
	/**
	 * Creates request based on the ID provided. This is the Customer Reference ID.
	 * @param id - Customer Reference ID. This is the external system ID.
	 * @return GetCustomersRequestType - request for Get_Customers operation.
	 */
	public static GetCustomersRequestType createByID(String id) {
		// GetCustomersRequestType -> CustomerRequestCriteriaType -> String id

		CustomerRequestCriteriaType criteria = new CustomerRequestCriteriaType();
		criteria.setCustomerReferenceID(id);
		
		GetCustomersRequestType request = createNoCriteria();
		List<CustomerRequestCriteriaType> listOfCriteria = new ArrayList<>();
		listOfCriteria.add(criteria);
		request.setRequestCriteria(listOfCriteria);

		return request;
	}

}
