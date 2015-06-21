/*global require*/

require.config({
	baseUrl: 'js',
	paths: {
		'underscore': 'libs/underscore-min',
		'Backbone': 'libs/backbone-min',
		'jquery': 'libs/jquery-min',
		'Mustache': 'libs/mustache',
		'text': 'libs/text',
		'json2': 'libs/json2-min',
		'bootstrap': 'libs/bootstrap-min',
		'select2': 'libs/select2-min',
		'bootstrap.slider': 'libs/bootstrap.slider',
		'braintree': 'libs/braintree.min'
	},
	shim: {
		'bootstrap': {
			deps: ['jquery'],
			exports: 'bootstrap'
		},
		'select2': {
			deps: ['jquery']
		},
		'bootstrap.slider': {
			deps: ['jquery']
		}
	}
});