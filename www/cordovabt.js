/*global cordova, module*/

module.exports = {
    init: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "CordovaBt", "initBt", []);
    },
    startDiscovery: function(successCallback, errorCallback) {
    	cordova.exec(successCallback, errorCallback, "CordovaBt", "startDiscovery", []);
    },
    advertiseUuid: function(uuid, successCallback, errorCallback) {
    	cordova.exec(successCallback, errorCallback, "CordovaBt", "advertiseUuid", [uuid])
    }
};
