define([
	'jquery',
	'underscore',
	'Backbone',
	'Mustache',
	'views/AbstractView',
	'text!/templates/result_view.html',
	'text!/templates/result_table_row.html',
	'text!/resources/fakeServiceReponse.json'
], function DefineResultView(
	_,
	$,
	Backbone,
	Mustache,
	AbstractView,
	templateTable,
	templateTableRow,
	fakeResponse
) {
	return AbstractView.extend({
		el: '#container',
		events: {},
		name: 'ResultView',
		initialize: function(options) {
			this.templateTable = templateTable;
			this.templateTableRow = templateTableRow;
			this.resultID = options.resultID;
			this.results = JSON.parse(fakeResponse).possibleBookings;
		},
		getTemplateData: function() {
			var data = {},
				rows = [];

			this.results.forEach(function forEachResult(result) {
				rows.push(Mustache.render(this.templateTableRow, {
					name: result.placeName,
					address: result.placeAddress,
					date: result.date,
					time: result.bookingTime,
					bookingUrl: result.bookingUrl
				}));
			}, this);
			
			data.rows = rows.join('');
			console.log(data);
			return data;
		},
		render: function() {
			this.$el.empty();
			this.$el.html(Mustache.render(this.templateTable, this.getTemplateData()));
			return this;
		}
	});
});