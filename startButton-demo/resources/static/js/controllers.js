'use strict';

/* Controllers */


function TriggerAnomalyUICtl($scope, $http, $timeout) {

	$scope.trigger = function() {
		
		$http.post("../trigger", null);

	};
}

function StartSystemUICtl($scope, $http, $timeout) {

	$scope.trigger = function() {
		
		$http.post("../startSystem", null);

	};
}