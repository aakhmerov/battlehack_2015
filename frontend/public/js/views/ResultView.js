/* global . */
define([
	'jquery',
	'underscore',
	'Backbone',
	'Mustache',
	'views/AbstractView',
	'models/ResultModel',
	'models/BookingModel',
	'text!/templates/result_view.html',
	'text!/templates/result_table_row.html'
], function DefineResultView(
	_,
	$,
	Backbone,
	Mustache,
	AbstractView,
	ResultModel,
	BookingModel,
	templateTable,
	templateTableRow
) {
	return AbstractView.extend({
		el: '#container',
		events: {
			'click .book': 'doBooking'
		},
		name: 'ResultView',
		initialize: function(options) {
			console.log(this.name + ': init');
			this.templateTable = templateTable;
			this.templateTableRow = templateTableRow;			
			this.pollingInterval = 5000;
			this.maxTries = 10;
			this.currentTry = 0;
			this.results = [];
			this.options = JSON.parse(options.response);
			
			this.resultModel = new ResultModel();
			this.bookingModel = new BookingModel();
			
			this.interval = window.setInterval(this.fetch.bind(this), this.pollingInterval);
		},
		getTemplateData: function() {
			console.log(this.name + ': getTemplateData');
			var data = {
					rows: ''
				};

			this.results.forEach(function forEachResult(result) {
				data.rows += Mustache.render(this.templateTableRow, {
					name: result.placeName,
					address: result.placeAddress,
					date: result.date,
					time: result.bookingTime,
					bookingUrl: result.bookingUrl,
					dateUrl: result.dateUrl
				});
			}, this);

			data.hasResults = this.results.length > 0;

			return data;
		},
		render: function() {
			console.log(this.name + ': Render');
			this.$el.empty();
			this.$el.html(Mustache.render(this.templateTable, this.getTemplateData()));
			return this;
		},
		fetch: function() {
			this.currentTry++;
			console.log(this.name + ': Saveing data... %d try', this.currentTry);
			this.resultModel.save(this.options, {
				success: this.fetchSuccess.bind(this),
				error: this.fetchError.bind(this)
			});
		},
		fetchSuccess: function(model, response, options) {
			console.log(this.name + ': Save-Results >', response);
			
			if (response) {
				window.clearInterval(this.interval);	
				this.results = response.possibleBookings;
				this.render();
			}
		},
		fetchError: function() {
			console.log(this.name + ': Save failed!')
			if (this.currentTry >= this.maxTries) {
				console.warn(this.name + ': Stop polling after %d fails!', this.maxTries);
				window.clearInterval(this.interval);
			}
		},
		doBooking: function(e) {
			console.log(this.name + ': doBooking!');
			var booking = e.target,
				bookingData = {
					user: this.options,
					desiredBooking: {
						bookingTime: booking.dataset.bookingTime,
						bookingUrl: booking.dataset.bookingUrl,
						date: booking.dataset.date,
						dateUrl: booking.dataset.dateUrl,
						placeAddress: booking.dataset.placeAddress,
						placeName: booking.dataset.placeName 
					}
				};
			
			console.log(this.name + ': BookingData >', bookingData);
			
			this.bookingModel.save(bookingData, {
				success: this.bookingSuccess.bind(this),
				error: this.bookingError.bind(this)
			});			
		},
		bookingSuccess: function(response) {
			console.log(this.name + ': bookingSuccess!', response);
		},
		bookingError: function(response) {
			console.log(this.name + ': bookingError!', response);
		}
		
	});
});