define([
	'Backbone'
], function DefineRouter(
	Backbone
) {
	return Backbone.Router.extend({
		routes: {
			'': 'welcome',
			'search': 'search',
			'result/:resultId': 'result',
			'booking/:bookingData': 'booking'
		},
		initialize: function() {
			console.log('Router: init');
		}
	});
});