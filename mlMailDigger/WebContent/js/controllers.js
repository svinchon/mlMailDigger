'use strict';

/* Controllers */

// create module
var mlMailDiggerControllers = angular.module(
		'mlMailDiggerControllers',
		[
		 	'ngSanitize'
		]
);

// add controller to module
// called mlMailDiggerController
// provide $http service and route parameters
// provide constructor
mlMailDiggerControllers.controller(
	'MailsListController',
	[
		'$scope', '$http', '$location',
		function ($scope, $http, $location) {
			$scope.go = function(search) {
				//alert("contains: "+search.contains+", from: "+search.from+", tags: "+search.tags);
				var query = "";
				if (search) {
					//console.log(search);
					if (search.contains) { query += "/contains:"+ search.contains; } else { query += "/contains:"; }
					if (search.from) { query += "/from:"+ search.from; } else { query += "/from:"; }
					if (search.tags) { query += "/tags:"+ search.tags; } else { query += "/tags:"; }
					//console.log(query);
			  	    $location.path("searchResults"+query);///contains:test/from:/tags:");
			  	} else {
					alert("empty search");
				}
	    	    //$location.path("searchResults/contains_"+search.contains+"/from_"+search.from+"/tags_"+search.tags);
	    	    //alert("searchResults/contains_"+search.contains+"/from_"+search.from+"/tags_"+search.tags);
	    	};
	    	var url = 'http://localhost:18080/mlMailDigger/rest/services/getMostRecentMails.json';
		    $http.get(url).success(function(data) {
		    	$scope.response = data;
		    });
		}
	]
);

mlMailDiggerControllers.controller(
	'MailDetailController',
	[
	 	'$scope', '$routeParams', '$http',
        function($scope, $routeParams, $http) {
	 		//console.log("test: "+$routeParams.mailHref);
	 		$scope.mailHref = $routeParams.mailHref;
	 		//console.log('http://localhost:18080/mlMailDigger/rest/services/getMail.json?id='+$routeParams.mailHref);
			$http.get('http://localhost:18080/mlMailDigger/rest/services/getMail.json?id='+$routeParams.mailHref).success(
					function(data) {
						$scope.response = data;
						$scope.response.Metadata.Body = $scope.response.Metadata.Body.replace(/\r\n/g, "<br/>");
						$scope.myHTML = $scope.response.Metadata.HTMLBody;
						//console.log("test: "+data);
					}
			);
		}
    ]
);

mlMailDiggerControllers.controller(
		'MailsSearchResultsController',
		[
		 	'$scope', '$routeParams', '$http',
	        function($scope, $routeParams, $http) {
		 				//console.log($routeParams);
			    	var url = 	'http://localhost:18080/mlMailDigger/rest/services/getMailsSearchResults.json';
			    	url += 		'?';
			    	url += 		'Contains='	+$routeParams.contains.substring(1);
			    	url += 		'&';
			    	url +=		'From='		+$routeParams.from.substring(1);
			    	url += 		'&';
			    	url += 		'Tags='		+$routeParams.tags.substring(1);
				    $http.get(url).success(function(data) {
				    	$scope.response = data;
				    	//console.log(data);
				    });
					}
	    ]
	);

mlMailDiggerControllers.controller(
	'CheckLocalStorageController',
	[
		'$scope', '$routeParams', '$http',
	  function($scope, $routeParams, $http) {
			//console.log("hello");
			var settings = JSON.parse(localStorage.getItem("settings"));
			//console.log(settings);
			if (settings) {
				console.log("settings are set");
				$scope.login = settings.login;
				$scope.password = settings.password;
				$scope.server = settings.server;
			} else {
				console.log("set settings");
				$scope.login = "<enter login>"
			}
		}
	]
);
