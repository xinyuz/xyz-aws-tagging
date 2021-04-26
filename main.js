var AWS = require("aws-sdk");
AWS.config.update({ region: 'cn-northwest-1' })
AWS.config.getCredentials(function(err) {
  if (err) {
    console.log(err.stack); // credentials not loaded
  } else {
    console.log("Access key:", AWS.config.credentials.accessKeyId);
  }
});

// TODO - Put your tag key and value here
var keystr = "author";
var valuestr = "xinyuz";

var ec2 = new AWS.EC2();
var params = {};
var instanceIds = [];
ec2.describeInstances(params, function(err, data) {
  if (err) {
    console.log(err, err.stack); // an error occurred
  } else {
    //console.log(data);           // successful response
    data.Reservations.forEach(function (resv) {
      //console.log(resv);
      resv.Instances.forEach(function (inst){
        //console.log(inst.InstanceId);
        instanceIds.push(inst.InstanceId);
      })
    });

    console.log(instanceIds);
    createTags(instanceIds, keystr, valuestr);
  }

});

/**
 * 
 * https://docs.aws.amazon.com/AWSJavaScriptSDK/latest/AWS/EC2.html#createTags-property
 * 
 * @param {*} instanceIds 
 * @param {*} key 
 * @param {*} value 
 */
function createTags (instanceIds, key, value) {
  params = {
    Resources: instanceIds,
    Tags: [
      {
        Key: key, 
        Value: value
      }
    ]
  };

  ec2.createTags(params, function(err, data) {
    if (err) {
      console.log(err, err.stack); // an error occurred
    } else {
      console.log("Tags created: " + data);           // successful response
    }
  });
}

