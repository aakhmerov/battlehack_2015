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
		}//,
//		fetch: function(data) {
//			console.log(data);
//			$.ajax({
//				headers: { 
//					Accept: 'application/json',
//					'Content-Type': 'application/json' 
//				},
//				type: 'POST',
//				url: this.url,
//				data: data,
//				success: this.fetchSuccess.bind(this),
//				error: this.catchError.bind(this),
//				dataType: 'json'
//			});
//		},
//		fetchSuccess: function(data, status) {
//			console.log(this.name + ': fetch success!', status, data);
//			this.trigger('fetched');
//		},
//		catchError: function() {
//			console.warn(this.name + ': Error fetching data!');
//			this.trigger('error');
//		}
	});
});