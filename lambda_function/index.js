var http = require('http');

exports.handler = function( event, context ) {
    var say = "";
    var shouldEndSession = false;
    var sessionAttributes = {};
    var myState = "";
    var pop = 0;
    var rank = 0;

    if (event.session.attributes) {
        sessionAttributes = event.session.attributes;
    }

    if (event.request.type === "LaunchRequest") {
        say = "Welcome to State Pop!  Say the name of a U.S. State.";
        context.succeed({sessionAttributes: sessionAttributes, response: buildSpeechletResponse(say, shouldEndSession) });

    } else {
        var IntentName = event.request.intent.name;

        if (IntentName === "GetForecast") {

            var post_options = {
                host:  '<YOUR HOST NAME>',
		path: '/forecast'
            };
                    
		var post_req = http.request(post_options, function(res) {
                res.setEncoding('utf8');
                var returnData = "";
                res.on('data', function (chunk) {
                    returnData += chunk;
                });
                
                res.on('end', function () {
                    
                    say = JSON.parse(returnData).text;
                    // add the state to a session.attributes array
                    if (!sessionAttributes.requestList) {
                        sessionAttributes.requestList = [];
                    }
                    sessionAttributes.requestList.push(myState);

                    // This line concludes the lambda call.  Move this line to within any asynchronous callbacks that return and use data.
                    context.succeed({sessionAttributes: sessionAttributes, response: buildSpeechletResponse(say, shouldEndSession) });

                });
                
            });
               
            post_req.end();
            

        } 
        else if (IntentName === "GetGenderSplit"){
            say = "You asked for stats based on Gender Type. Thanks for playing!";
            shouldEndSession = true;
            context.succeed({sessionAttributes: sessionAttributes, response: buildSpeechletResponse(say, shouldEndSession) });
        }
        else if (IntentName === "GetEduSplit"){
            say = "You asked for stats based on education and marital status. Thanks for playing!";
            shouldEndSession = true;
            context.succeed({sessionAttributes: sessionAttributes, response: buildSpeechletResponse(say, shouldEndSession) });
        }
        else if (IntentName === "AMAZON.StopIntent" || IntentName === "AMAZON.CancelIntent") {
            say = "You asked for " + sessionAttributes.requestList.toString() + ". Thanks for playing!";
            shouldEndSession = true;
            context.succeed({sessionAttributes: sessionAttributes, response: buildSpeechletResponse(say, shouldEndSession) });


        } else if (IntentName === "AMAZON.HelpIntent" ) {
            say = "Just say the name of a U.S. State, such as Massachusetts or California."
            context.succeed({sessionAttributes: sessionAttributes, response: buildSpeechletResponse(say, shouldEndSession) });

        }
    }
};

function buildSpeechletResponse(say, shouldEndSession) {
    return {
        outputSpeech: {
            type: "SSML",
            ssml: "<speak>" + say + "</speak>"
        },
        reprompt: {
            outputSpeech: {
                type: "SSML",
                ssml: "<speak>Please try again. " + say + "</speak>"
            }
        },
        card: {
            type: "Simple",
            title: "My Card Title",
            content: "My Card Content, displayed on the Alexa App or alexa.amazon.com"
        },
        shouldEndSession: shouldEndSession
    };
}
