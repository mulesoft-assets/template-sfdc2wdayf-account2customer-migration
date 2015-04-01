
# Anypoint Template: Salesforce to Workday Account to Customer Migration

+ [License Agreement](#licenseagreement)
+ [Use Case](#usecase)
+ [Considerations](#considerations)
	* [Salesforce Considerations](#salesforceconsiderations)
	* [Workday Financials Considerations](#workdayfinancialsconsiderations)
+ [Run it!](#runit)
	* [Running on premise](#runonopremise)
	* [Running on Studio](#runonstudio)
	* [Running on Mule ESB stand alone](#runonmuleesbstandalone)
	* [Running on CloudHub](#runoncloudhub)
	* [Deploying your Anypoint Template on CloudHub](#deployingyouranypointtemplateoncloudhub)
	* [Properties to be configured (With examples)](#propertiestobeconfigured)
+ [API Calls](#apicalls)
+ [Customize It!](#customizeit)
	* [config.xml](#configxml)
	* [businessLogic.xml](#businesslogicxml)
	* [endpoints.xml](#endpointsxml)
	* [errorHandling.xml](#errorhandlingxml)


# License Agreement <a name="licenseagreement"/>
Note that using this template is subject to the conditions of this [License Agreement](AnypointTemplateLicense.pdf).
Please review the terms of the license before downloading and using this template. In short, you are allowed to use the template for free with Mule ESB Enterprise Edition, CloudHub, or as a trial in Anypoint Studio.

# Use Case <a name="usecase"/>
As an admin I want to migrate Salesforce accounts to Workday customers.

This template should serve as a foundation for the process of migrating accounts from Salesforce, where you can specify the filtering criteria 
and desired behavior when the corresponding Customer already exists in Workday.

As implemented, this template leverages the [Batch Module](http://www.mulesoft.org/documentation/display/current/Batch+Processing).
The batch job is divided into *Input*, *Process* and *On Complete* stages.

During the *Input* stage the template will query all the existing Accounts at Salesforce that match the filtering criteria.
In the *Process* stage, the template queries the Workday for already existing customers based on the Salesforce IDs retrieved in the *Input* stage.
The customers get inserted or updated in the Workday system based on the results of these queries.
Lastly, the information about the migration results is sent to pre-configured e-mail recipient and output to the console as well.

# Considerations <a name="considerations"/>

There are certain pre-requisites that must be considered to run this Anypoint Template. 
All of them deal with the preparations in both source and destination systems, that must be made in order for all to run smoothly. 
**Failling to do so could lead to unexpected behavior of the template.**



## Salesforce Considerations <a name="salesforceconsiderations"/>

There may be a few things that you need to know regarding Salesforce, in order for this template to work.

In order to have this template working as expected, you should be aware of your own Salesforce field configuration.

###FAQ

 - Where can I check that the field configuration for my Salesforce instance is the right one?

    [Salesforce: Checking Field Accessibility for a Particular Field][1]

- Can I modify the Field Access Settings? How?

    [Salesforce: Modifying Field Access Settings][2]


[1]: https://help.salesforce.com/HTViewHelpDoc?id=checking_field_accessibility_for_a_particular_field.htm&language=en_US
[2]: https://help.salesforce.com/HTViewHelpDoc?id=modifying_field_access_settings.htm&language=en_US

### As source of data

If the user configured in the template for the source system does not have at least *read only* permissions for the fields that are fetched, then a *InvalidFieldFault* API fault will show up.

```
java.lang.RuntimeException: [InvalidFieldFault [ApiQueryFault [ApiFault  exceptionCode='INVALID_FIELD'
exceptionMessage='
Account.Phone, Account.Rating, Account.RecordTypeId, Account.ShippingCity
^
ERROR at Row:1:Column:486
No such column 'RecordTypeId' on entity 'Account'. If you are attempting to use a custom field, be sure to append the '__c' after the custom field name. Please reference your WSDL or the describe call for the appropriate names.'
]
row='1'
column='486'
]
]
```









## Workday Financials Considerations <a name="workdayfinancialsconsiderations"/>


### As destination of data

When creating new Workday customer, one of existing customer categories must be chosen. In order to do this, this template uses lookup table with pre-defined mappings between Salesforce data (Industry) and Workday Customer Category ID.
Workday customer categories can be user defined, therefore the mapping must be edited by user manually before running this template.

To view all the Workday Customer Categories, please type "customer categories" into search field in Workday system and select Customer Categories report.
In the report you should see all the customer categories available. Select Integration IDs -> View Integration IDs from available actions of each customer 
category and update the user-defined lookup table in the template's datamapper component manually.
The default customer category ID can be edited in the Script view inside datamapper component. This category will be used if the source Account's Industry 
value does not match any defined mappings in the lookup table.

To find out more about DataMapper Lookup Tables, please read this [documentation page](http://www.mulesoft.org/documentation/display/current/Using+DataMapper+Lookup+Tables).

It is not possible to delete customer in Workday system. Therefore, integration test is based on update of test Account in Salesforce identified by property `sfdc.testaccount.id`. 
Use the ID actually returned by the Salesforce connector. 
You can find the proper ID of an Account by using Salesforce Developer Console. Example query: `SELECT Id FROM Account WHERE Name = 'TestAccount'`.
# Run it! <a name="runit"/>
Simple steps to get Salesforce to Workday Account to Customer Migration running.
Whichever way you choose to run this template, this this is an example of the output you'll see after hitting the HTTP endpoint:

<pre>
<h1>Batch Process initiated</h1>
<b>ID:</b>6eea3cc6-7c96-11e3-9a65-55f9f3ae584e<br/>
<b>Records to be processed: </b>3<br/>
<b>Started execution on: </b>Tue Mar 24 16:08:16 CET 2015
</pre>

## Running on premise <a name="runonopremise"/>
In this section we detail the way you should run your Anypoint Template on your computer.


### Where to Download Mule Studio and Mule ESB
First thing to know if you are a newcomer to Mule is where to get the tools.

+ You can download Mule Studio from this [Location](http://www.mulesoft.com/platform/mule-studio)
+ You can download Mule ESB from this [Location](http://www.mulesoft.com/platform/soa/mule-esb-open-source-esb)


### Importing an Anypoint Template into Studio
Mule Studio offers several ways to import a project into the workspace, for instance: 

+ Anypoint Studio generated Deployable Archive (.zip)
+ Anypoint Studio Project from External Location
+ Maven-based Mule Project from pom.xml
+ Mule ESB Configuration XML from External Location

You can find a detailed description on how to do so in this [Documentation Page](http://www.mulesoft.org/documentation/display/current/Importing+and+Exporting+in+Studio).


### Running on Studio <a name="runonstudio"/>
Once you have imported you Anypoint Template into Anypoint Studio you need to follow these steps to run it:

+ Locate the properties file `mule.dev.properties`, in src/main/resources
+ Complete all the properties required as per the examples in the section [Properties to be configured](#propertiestobeconfigured)
+ Once that is done, right click on you Anypoint Template project folder 
+ Hover you mouse over `"Run as"`
+ Click on  `"Mule Application"`


### Running on Mule ESB stand alone <a name="runonmuleesbstandalone"/>
Complete all properties in one of the property files, for example in [mule.prod.properties] (../master/src/main/resources/mule.prod.properties) and run your app with the corresponding environment variable to use it. To follow the example, this will be `mule.env=prod`. 


## Running on CloudHub <a name="runoncloudhub"/>
While [creating your application on CloudHub](http://www.mulesoft.org/documentation/display/current/Hello+World+on+CloudHub) (Or you can do it later as a next step), you need to go to Deployment > Advanced to set all environment variables detailed in **Properties to be configured** as well as the **mule.env**.
Once your app is all set up and started, supposing you choose as domain name `sfdcaccountmigration` to trigger the use 
case you just need to hit `http://sfdcaccountmigration.cloudhub.io/migrateAccounts` and report will be sent to the emails configured.

### Deploying your Anypoint Template on CloudHub <a name="deployingyouranypointtemplateoncloudhub"/>
Mule Studio provides you with really easy way to deploy your Template directly to CloudHub, for the specific steps to do so please check this [link](http://www.mulesoft.org/documentation/display/current/Deploying+Mule+Applications#DeployingMuleApplications-DeploytoCloudHub)


## Properties to be configured (With examples) <a name="propertiestobeconfigured"/>
In order to use this Mule Anypoint Template you need to configure properties (Credentials, configurations, etc.) either in properties file or in CloudHub as Environment Variables. Detail list with examples:
### Application configuration
**Application configuration**

 + http.port `9090` 

**Salesforce Connector configuration**

 + sfdc.username `bob.dylan@orga`
 + sfdc.password `DylanPassword123`
 + sfdc.securityToken `avsfwCUl7apQs56Xq2AKi3X`
 + sfdc.url `https://login.salesforce.com/services/Soap/u/32.0`

**Workday Connector configuration**

 + wday.user `wday_user`
 + wday.password `wday_password`
 + wday.endpoint `https://{your Workday domain}/ccx/service/{your tenant name}/Revenue_Management/v23.2`

**SMTP Services configuration**

 + smtp.host `smtp.gmail.com`
 + smtp.port `587`
 + smtp.user `gmail_user`
 + smtp.password `gmail_password`

**EMail Details**

 + mail.from `batch.migrateaccounts.migration%40mulesoft.com`
 + mail.to `your.email@example.com`
 + mail.subject `Batch Job Finished Report`

# API Calls <a name="apicalls"/>
Salesforce imposes limits on the number of API Calls that can be made. However, we make API call to Salesforce only once during migration, so this is not something to worry about.


# Customize It!<a name="customizeit"/>
This brief guide intends to give a high level idea of how this Anypoint Template is built and how you can change it according to your needs.
As mule applications are based on XML files, this page will be organized by describing all the XML that conform the Anypoint Template.
Of course more files will be found such as Test Classes and [Mule Application Files](http://www.mulesoft.org/documentation/display/current/Application+Format), but to keep it simple we will focus on the XMLs.

Here is a list of the main XML files you'll find in this application:

* [config.xml](#configxml)
* [endpoints.xml](#endpointsxml)
* [businessLogic.xml](#businesslogicxml)
* [errorHandling.xml](#errorhandlingxml)


## config.xml<a name="configxml"/>
Configuration for Connectors and [Properties Place Holders](http://www.mulesoft.org/documentation/display/current/Configuring+Properties) are set in this file. **Even you can change the configuration here, all parameters that can be modified here are in properties file, and this is the recommended place to do it so.** Of course if you want to do core changes to the logic you will probably need to modify this file.

In the visual editor they can be found on the *Global Element* tab.


## businessLogic.xml<a name="businesslogicxml"/>
Functional aspect of the template is implemented on this XML, directed by one flow responsible of excecuting the logic.
For the pourpose of this particular template the *mainFlow* just excecutes a [Batch Job](http://www.mulesoft.org/documentation/display/current/Batch+Processing) which handles all the migration logic.
This flow has Exception Strategy that basically consists of invoking the *defaultChoiceExceptionStrategy* defined in *errorHandling.xml* file.



## endpoints.xml<a name="endpointsxml"/>
This is the file where you will find the inbound side of your integration app.
This template has only a [HTTP Connector](http://www.mulesoft.org/documentation/display/current/HTTP+Connector) as the way to trigger the use case.

**HTTP Connector** - Start Report Generation

 + `${http.port}` is set as a property to be defined either on a property file or in CloudHub environment variables.
 + The path configured by default is `migrateAccounts` and you are free to change it for the one that you prefer.
 + The host name for all endpoints in your CloudHub configuration should be defined as `0.0.0.0`. CloudHub will then route requests from your application domain URL to the endpoint.
 + The endpoint is configured as a *request-response* by placing it in the source section of the flow. The response will be the summary of Batch Input stage - number of Accounts loaded by the criteria specified.



## errorHandling.xml<a name="errorhandlingxml"/>
This is the right place to handle how your integration will react depending on the different exceptions. 
This file holds a [Choice Exception Strategy](http://www.mulesoft.org/documentation/display/current/Choice+Exception+Strategy) that is referenced by the main flow in the business logic.



