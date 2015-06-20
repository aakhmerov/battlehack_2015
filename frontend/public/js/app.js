require([
	'configuration'
],
function configurationLoaded() {
	'use strict';
	require([
		'Backbone',
		'Site',
		'Router',
		'bootstrap'
	],
	function appLoaded(Backbone, Site, Router) {
		var site,
			router;
			
		site = new Site();
		router = new Router();
		
		Backbone.history.start();
	});
});