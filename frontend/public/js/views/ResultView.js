define([
	'jquery',
	'underscore',
	'Backbone',
	'Mustache',
	'views/AbstractView',
	'text!/templates/result_view.html'
], function DefineResultView(
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
		name: 'ResultView',
		initialize: function(options) {
			this.template = template;
			this.resultID = options.resultID;
			this.results = [];
		},
		getTemplateData: function() {
			var data = {
				hasResults: this.results.length > 0
			};
			
			return data;
		},
		render: function() {
			this.$el.empty();
			this.$el.html(Mustache.render(this.template, this.getTemplateData()));
			return this;
		}
	});
});