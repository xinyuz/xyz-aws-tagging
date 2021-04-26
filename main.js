var AWS = require("aws-sdk");

AWS.config.getCredentials(function(err) {
  if (err) console.log(err.stack);
  // credentials not loaded
  else {
    console.log("Access key:", AWS.config.credentials.accessKeyId);
  }
});

AWS.config.update({ region: 'cn-northwest-1' })

var tagValueMapMigrated = "abcd";

var ec2 = new AWS.EC2();

// var params = {
//   Filters: [
//      {
//     Name: "tag:Purpose", 
//     Values: [
//        "test"
//     ]
//    }
//   ]
//  };

// var params = {
//   InstanceIds: [
//      "i-1234567890abcdef0"
//   ]
//  };

var params = {

}

ec2.describeInstances(params, function(err, data) {
   if (err) console.log(err, err.stack); // an error occurred
   else     console.log(data);           // successful response
 });