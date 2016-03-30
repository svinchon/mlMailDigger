'use strict';

/* App Module */

// create module
// with dependencies on other modules
var mlMailDiggerApp = angular.module('mlMailDiggerApp', [
  'ngRoute',
  'mlMailDiggerControllers',
]);
/*
'phonecatAnimations',
'phonecatFilters',
'phonecatServices'
*/

// configure route provider module
mlMailDiggerApp.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      when('/test', {
        templateUrl: 'partials/check-local-storage.html',
        controller: 'CheckLocalStorageController'
      }).
      when('/mails', {
        templateUrl: 'partials/mails-list.html',
        controller: 'MailsListController'
      }).
      when('/mails/:mailHref', {
        templateUrl: 'partials/mail-detail.html',
        controller: 'MailDetailController'
      }).
	  //when('searchResults', { //contains_:Contains/from_:From/tags_:Tags', {
      when('/searchResults/contains:contains/from:from/tags:tags', {
          templateUrl: 'partials/mails-search-results.html',
          controller: 'MailsSearchResultsController'
        }).
      otherwise({
        redirectTo: '/mails'
      });
  }]);
