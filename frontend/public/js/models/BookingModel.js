define([
	'jquery',
	'Backbone'
], function DefineBookingModel(
	$,
	Backbone
) {
	return Backbone.Model.extend({
		url: '/melder-api/services/appointment/book',
		name: 'BookingModel',
		initialize: function() {
			console.log(this.name + ': init');
		}
	});
});