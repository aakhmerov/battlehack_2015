define([
	'Backbone'
], function DefineRouter(
	Backbone
) {
	return Backbone.Router.extend({
		routes: {
			'': 'welcome',
			'search': 'search',
			'result/:resultId': 'result'
		},
		initialize: function() {
			console.info('Router: init');
		}
	});
});