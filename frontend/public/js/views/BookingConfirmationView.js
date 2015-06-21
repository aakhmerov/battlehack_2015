define([
	'underscore',
	'jquery',
	'Backbone',
	'Mustache',
	'views/AbstractView',
	'text!/templates/booking_confirmation.html'
], function DefineBookingConfirmationView(
	_,
	$,
	Backbone,
	Mustache,
	AbstractView,
	tempalte
) {
	return AbstractView.extend({
		el: 'container',
		events: {},
		name: 'Booking',
		initialize: function(options) {
			console.log(this.name + ': init');
			this.bookingData = JSON.parse(options.bookingData);
			console.log(this.name + ': BookingData >', this.bookingData);
		},
		getTemplateData: function() {
			var data = {};
			
			return data;
		},
		render: function() {
			this.$el.empty();
			this.$el.html(Mustache.render(this.template, this.getTemplateData()));
			return this;
		}
	});
});