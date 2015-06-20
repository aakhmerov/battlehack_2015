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
		welcome: function() {
			Backbone.Events.trigger('page change', 'welcome');
		},
		search: function() {
			Backbone.Events.trigger('page change', 'search');
		},
		result: function(resultID) {
			Backbone.Events.trigger('page change', 'result', resultID);
		}
	});
});