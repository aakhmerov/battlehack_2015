define([
	'underscore',
	'Backbone'
], function(
	_,
	Backbone
) {
	return Backbone.Model.extend({
		url: '/test',
		requiredDataComplete: function() {
			var firstname = this.get('firstname'),
				lastname = this.get('lastname'),
				email = this.get('email'),
				phone = this.get('phone');
				
			return (firstname && firstname.length > 0) &&
				(lastname && lastname.length > 0) &&
				(email && email.length > 0) &&
				(phone && phone.length > 0);
		}
	});
});