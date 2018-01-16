#Conversational UI for Enterprise Information Systems

Please refer to the article : https://www.linkedin.com/pulse/conversational-interface-enterprise-information-sunil-vishnubhotla

##Author: Sunil Vishnubhotla

Tags : #Conversationa UI, #Alexa Skill, #AWS Lambda , #Apache Spark

Past couple of year has seen the emergence of mature conversational platforms. These platforms can understand the natural language spoken by humans while interacting with underlying software applications or passing on the messages in a translated format to the underlying software as commands. 

A conversational interface or UI is any human interaction with a machine that mimics a conversation with a real human being. It is the ability to transmit input to a machine or a computer via natural spoken commands that are no different from interacting with a real person. The result is a desired output from the machine or software without having to click a few buttons or icons on a screen.
Typically, there are two ways of interacting with conversational platforms. One is by typing a plain and meaningful sentence and passing it as input to the platform so that it can perform the desired task and produce the results based on what was asked. The other, a more natural way, is by having a real conversation.   

In this blog we will see how one can utilize conversational platforms for performing analytical operations and presenting the results in a spoken language to the end user. 

Some of the popular platforms that you may be familiar with are the Apple Siri, Amazon Alexa, Google Home and Microsoft Cortana. We will use Amazon Alexa to illustrate how conversational interface could be used for Analytics application.

Fortunately, adding conversational interfaces to existing business services that are exposed as web-services is fairly easy. Therefore, to illustrate this we will perform the following steps:

1.	Create our sample analytics application and expose as a web-service
2.	Create an AWS Lambda interface to call our web-service
3.	Create an Alexa Skill
4.	Use conversations to interact with our web-service

Below is the general architecture for our sample application:
 
https://github.com/sunilvb/ConversationalUI/blob/master/Conversational_UI.jpg

Sample Analytics Application

We will create a sample application that ingests a data file containing dummy data about credit card defaulters from a fictitious credit card company. This data contains information pertaining to the gender, marital status, age, education and other relevant information of each credit card user.
We start by running the sample data through a sequence of algorithms to process the raw data and to learn from it, also known as Machine Learning or ML.

Typically, the process of digesting raw data in stages to produce information that can be utilized in ML is called a Pipeline. A Pipeline may consist of one or more Transformers and Estimators applied to the raw data and run through a data-set to arrive at a Model. 

Based on the data provided, our application will produce a Model to answer some basic question like below:
•	How many of the credit card defaulters were Male?
•	Does the marital status and level of education effect the likelihood?
•	How many of the current customer are likely to default in the next month?
  
As illustrated in the diagram above, we will process our raw sample file and insert the data into an RDBMS to be queried by a service and present to the calling application. 

Our sample application consists of two independent executables or modules. The first one is a standalone Java application that is used to ingest, process and load the results into MySQL. The application processes the data using Apache Spark and creates appropriate Models for Machine Learning. We will be using the Decision Tree algorithm for our predictions in this sample.   
The second is a Spring Boot JAR file that can be executed to expose a set of web-services which in turn will be consumed by out AWS Lambda functions. These web-services are REST endpoints that query the data in MySQL (populated as a result of the data processing by our Spark application).
Please note, although the sample shows how to process a data file and present it as a web service, it is neither optimized for production use nor exemplifies the coding best practices. The sample code is located here.   
To run the application, build source using maven and use java command to execute main class in the jar file. Make sure you have Java 1.8 installed on your system.

```
java -cp jarfilename.jar your.package.Application
```

AWS Lambda Interface

The Lambda interface calls our web-service and converts the results in to an Alexa friendly format. Our sample Lambda interface is written in Node.js. The event-driven, non-blocking I/O model of Node.js is a great fit for an Alexa skill and Node.js is one of the largest ecosystems of open source libraries in the world. Also, AWS Lambda is free for the first one million calls per month, which is enough for most developers. If you think you need more than a million calls per month or the 750 hrs free tier limits then checkout the additional $100/month promotion credits for Alexa developers. Plus, when using AWS Lambda you don't need to manage any SSL certificates since the Alexa Skills Kit is a trusted trigger. 
The Node.js sample is also included in the sample code on the github repository. Refer to the online AWS tutorial on how to set up Lambda function for Alexa here.

Alexa Skill

Finally, we will build the Alexa Skill as the frontend application that interfaces with our users. A Skill to Alexa is like an app to your smart phone. It fastens the user interaction via voice to the underlying system that performs the job and returns the results. As a Skill developer, you define the Interaction Model that contains the elements to facilitate the use interaction and system response. 
A Skill’s Interaction Model consists of Intents that respond to a user’s request. An Intent’s schema is a JSON object that maps to specific functionality that we want to provide in a Skill. An Intent can further have optional parameters or arguments called Slots. Both Intents and Slots can be custom defined by a developer or built-in provided by Alexa platform.

We will define our Intent Schema like below:


```
{
  "intents": [
    {
      "intent": "GetEduSplit"
    },
    {
      "intent": "GetGenderSplit"
    },
    {
      "intent": "GetForecast"
    },
    {
      "intent": "AMAZON.HelpIntent"
    },
    {
      "intent": "AMAZON.StopIntent"
    },
    {
      "intent": "AMAZON.CancelIntent"
    }
  ]
}

```
https://github.com/sunilvb/ConversationalUI/blob/master/lambda_function/Interaction%20Models/IntentSchema.json

The next thing we do is glue our Intents with Utterances. This helps the Alexa to recognize the phrases that a user utters and associate them with specific Intents and thereby to the underlying functionality and response that is generated as a result of the phrase uttered. As you may have guessed, the utterances are tied to a specific language.

We will associate the following Utterances to the Intents:

https://github.com/sunilvb/ConversationalUI/blob/master/lambda_function/Interaction%20Models/SampleUtterances.txt

Our Alexa skill kit will respond to verbal commands and question as shown below:

User => Alexa, tell me the forecast.

Alexa => Based on the current month’s data, there are 4 defaulters forecasted for next month.

All the files needed for the Skill setup are located on github. Also refer to the AWS tutorial on setting up custom Alexa Skills here.
