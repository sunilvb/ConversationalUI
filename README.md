Conversational UI for Enterprise Information Systems

Past couple of year has seen the emergence of mature conversational platforms. These platforms can understand the natural language spoken by humans while interacting with underlying software applications or passing on the messages in a translated format to the underlying software as commands. 

A conversational interface or UI is any human interaction with a machine that mimics a conversation with a real human being. It is the ability to transmit input to a machine or a computer vial natural spoken commands that are no different from interacting with a real person. The result is a desired output from the machine or software without having to click a few buttons or icons on a screen.
Typically, there are two ways of interacting with conversational platforms. One is by typing a plain and meaningful sentence and passing it as input to the platform so that it can perform the desired task and produce the results based on what was asked. The other, a more natural way, is by having a real conversation.   

In this blog we will see how one can utilize conversational platforms for performing analytical operations and presenting the results in a spoken language to the end user. 

Some of the popular platforms that you may be familiar with are the Apple Siri, Amazon Alexa, Google Home and Microsoft Cortana. We will use Amazon Alexa to illustrate how conversational interface could be used for Analytics application.
Fortunately, adding conversational interfaces to existing business services that are exposed as web-services is fairly easy. Therefore, to illustrate this we will perform the following steps:

1.	Create our sample analytics application and expose as a web-service
2.	Create an AWS Lambda interface to call our web-service
3.	Create an Alexa Skill
4.	Use conversations to interact with our web-service

Below is the general architecture for our sample application:
 


Sample Analytics application

We will create a sample application that ingests a data file containing dummy data about credit card defaulters from a fictitious credit card company. This data contains information pertaining to the gender, marital status, age, education and other relevant information of each credit card user.

Based on the data provided, our application will answer some basic question like below:

•	How many of the credit card defaulters were Male?

•	Do marital status and level of education effect the likelihood?

•	How many of the current customer are likely to default in the next month?

  





