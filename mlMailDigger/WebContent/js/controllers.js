'use strict';

var mlMailDiggerControllers = angular.module('mlMailDiggerControllers', ['ngSanitize']);

mlMailDiggerControllers.controller('ToolbarsController', ['$scope', '$location', function($scope, $location) {
	$scope.disconnect = function() {
		console.log("disconnect");
		localStorage.removeItem("settings");
		$("#divSideMenu").hide();
		$location.path("/");
	};
	$scope.connect = function() {
		console.log("connect");
		$("#divSideMenu").hide();
		$location.path("/login");
	}
}]);

mlMailDiggerControllers.controller('MailsListController', ['$scope', '$http', '$location', function ($scope, $http, $location) {
	var settings = JSON.parse(localStorage.getItem("settings"));
	if (!settings) {
		$location.path("/login");
	} else {
		$scope.go = function(search) {
			var query = "";
			if (search) {
				if (search.contains) { query += "/contains:"+ search.contains; } else { query += "/contains:"; }
				if (search.from) { query += "/from:"+ search.from; } else { query += "/from:"; }
				if (search.tags) { query += "/tags:"+ search.tags; } else { query += "/tags:"; }
		  	    $location.path("searchResults"+query);
		  	} else {
				alert("empty search");
			}
	   	};
	   	var url = 'http://';
	   	//url	+= 'localhost:18080';
	   	url	+= localStorage.getItem("server");
	   	url += '/mlMailDigger/rest/services/getMostRecentMails.json';
	    $http.get(url).success(function(data) {
	    	$scope.response = data;
		});
	}
}]);

mlMailDiggerControllers.controller('MailDetailController', ['$scope', '$routeParams', '$http', function($scope, $routeParams, $http) {
   	var url = 'http://';
   	//url	+= 'localhost:18080';
   	url	+= localStorage.getItem("server");
   	url += '/mlMailDigger/rest/services/getMail.json?id='+$routeParams.mailHref
	$http.get(url).success(function(data) {
		$scope.response = data;
		$scope.myHTML = $scope.response.Metadata.HTMLBody;
	});
}]);

mlMailDiggerControllers.controller('MailsSearchResultsController', ['$scope', '$routeParams', '$http', function($scope, $routeParams, $http) {
   	var url = 'http://';
   	//url	+= 'localhost:18080';
   	url	+= localStorage.getItem("server");
   	url += '/mlMailDigger/rest/services/getMailsSearchResults.json';
	url += 		'?';	url += 		'Contains='	+$routeParams.contains.substring(1);
	url += 		'&';	url +=		'From='		+$routeParams.from.substring(1);
	url += 		'&';	url += 		'Tags='		+$routeParams.tags.substring(1);
	$http.get(url).success(function(data) {
	  	$scope.response = data;
	});
}]);

mlMailDiggerControllers.controller('LoginController', [ '$scope', '$routeParams', '$http', '$location', function($scope, $routeParams, $http, $location) {
	var settings = JSON.parse(localStorage.getItem("settings"));
	if (settings) {
		console.log("settings are set");
		$scope.login = settings.login; $scope.password = settings.password; $scope.server = settings.server;
	} else {
		console.log("settings not set");
	}
    $scope.storeCredentials = function() {
		var jsonSettings = {}; jsonSettings.login = $scope.login; jsonSettings.password = $scope.password; jsonSettings.server = $scope.server;
		localStorage.setItem("settings", JSON.stringify(jsonSettings));
		localStorage.setItem("server", $scope.server);
    	$location.path("loginResult");
    }
}]);

mlMailDiggerControllers.controller('LoginResultController', ['$scope', '$routeParams', '$http', function($scope, $routeParams, $http) {
	//console.log(JSON.parse(localStorage.getItem("settings")));
}]);
