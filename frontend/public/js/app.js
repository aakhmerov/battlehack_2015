require([
	'configuration'
],
function configurationLoaded() {
	'use strict';
	require([
		'views/FormView',
		'bootstrap'
	],
	function appLoaded(FormView) {
		new FormView();
	});
});