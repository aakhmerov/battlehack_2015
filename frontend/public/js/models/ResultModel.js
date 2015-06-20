define([
	'Backbone'
], function DefineResultModel(
	Backbone
) {
	return Backbone.Model.extend({
		url: '/melder-api/services/amt/bookings/',
		name: 'ResultModel',
		initialize: function(userToken) {
			console.log(this.name + ': init');
			this.url += userToken.toString();
		}
	});
});