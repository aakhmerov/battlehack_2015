require([
	'configuration'
],
function configurationLoaded() {
	'use strict';
	require([
		'Backbone',
		'Site',
		'bootstrap'
	],
	function appLoaded(
		Backbone,
		Site
	) {
		new Site();
		Backbone.history.start();
	});
});