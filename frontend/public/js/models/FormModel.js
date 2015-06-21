define([
	'underscore',
	'Backbone'
], function DefineFormModel(
	_,
	Backbone
) {
	return Backbone.Model.extend({
		url: '/melder-api/services/amt/bookings/fetch',
		name: 'FormModel',
		requiredDataComplete: function() {
			console.log(this.name + ': requiredDataComplete');
			var firstName = this.get('firstName'),
				lastName = this.get('lastName'),
				email = this.get('email'),
				phone = this.get('phone'),
				serviceId = this.get('serviceId');
				
			return (firstName && firstName.length > 0) &&
				(lastName && lastName.length > 0) &&
				(email && email.length > 0) &&
				(phone && phone.length > 0) &&
				serviceId;
		}
	});
});