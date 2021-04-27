var AWS = require("aws-sdk");
AWS.config.getCredentials(function(err) {
  if (err) {
    console.log(err.stack); // credentials not loaded
  } else {
    console.log("Access key:", AWS.config.credentials.accessKeyId);
  }
});

// Configuration: Put your region, tag key and value here
AWS.config.update({ region: 'ap-northeast-1' })
var keystr = "author";
var valuestr = "xinyuz";
var instanceFilter = {};

// Tag EC2 instances
var ec2 = new AWS.EC2();
var params = instanceFilter;
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
 * Tag given instances, if tag key already exists, will overwrite.
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

