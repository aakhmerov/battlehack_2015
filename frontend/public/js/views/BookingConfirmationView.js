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
	template
) {
	return AbstractView.extend({
		el: '#container',
		events: {},
		name: 'Booking',
		initialize: function(options) {
			console.log(this.name + ': init');
			this.template = template;
			this.bookingData = JSON.parse(options.bookingData);
			console.log(this.name + ': BookingData >', this.bookingData);
		},
		getTemplateData: function() {
			return {
				name: this.bookingData.name,
				time: this.bookingData.time,
				date: this.bookingData.date,
				number: this.bookingData.number,
				placeName: this.bookingData.place,
				placeAddress: this.bookingData.address
			};
		},
		render: function() {
			this.$el.empty();
			this.$el.html(Mustache.render(this.template, this.getTemplateData()));
			return this;
		}
	});
});