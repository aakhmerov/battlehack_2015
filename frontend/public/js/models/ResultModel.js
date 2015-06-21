define([
	'jquery',
	'Backbone'
], function DefineResultModel(
	$,
	Backbone
) {
	return Backbone.Model.extend({
		url: '/melder-api/services/amt/bookings/',
		name: 'ResultModel',
		initialize: function() {
			console.log(this.name + ': init');
		}
	});
});