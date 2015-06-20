define([
	'jquery',
	'underscore',
	'Backbone',
	'Mustache',
	'views/AbstractView',
	'models/ResultModel',
	'text!/templates/result_view.html',
	'text!/templates/result_table_row.html',
	'text!/resources/fakeServiceReponse.json'
], function DefineResultView(
	_,
	$,
	Backbone,
	Mustache,
	AbstractView,
	ResultModel,
	templateTable,
	templateTableRow,
	fakeResponse
) {
	return AbstractView.extend({
		el: '#container',
		events: {},
		name: 'ResultView',
		initialize: function(options) {
			console.log(this.name + ': init');
			this.templateTable = templateTable;
			this.templateTableRow = templateTableRow;			
			this.pollingInterval = 5000;
			this.userToken = options.userToken;
			this.results = [];
			
			this.model = new ResultModel(this.userToken);
			this.model.on('change', this.fetchSuccess.bind(this));
			
//			this.interval = window.setInterval(this.fetch.bind(this), this.pollingInterval);
			
//			this.resultID = options.resultID;
//			this.results = JSON.parse(fakeResponse).possibleBookings;
		},
		getTemplateData: function() {
			console.log(this.name + ': getTemplateData');
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
			
			data.rows = rows.join();

			data.hasResult = data.rows.length > 0;

			return data;
		},
		render: function() {
			console.log(this.name + ': Render');
			this.$el.empty();
			this.$el.html(Mustache.render(this.templateTable, this.getTemplateData()));
			return this;
		},
		fetch: function() {
			console.log(this.name + ': Fetching data...');
			this.model.fetch();
		},
		fetchSuccess: function(model, response, options) {
			console.log(this.name + ': Fetch-Results >', response);
			window.clearInterval(this.interval);
		}
	});
});