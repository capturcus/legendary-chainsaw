/*global cordova, module*/

module.exports = {
    greet: function (name, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "CordovaBt", "greet", [name]);
    },
    servicetest: function(successCallback, errorCallback) {
    	cordova.exec(successCallback, errorCallback, "CordovaBt", "servicetest", []);
    }
};
