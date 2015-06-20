define([
	'jquery',
	'underscore',
	'Backbone',
	'Mustache',
	'text!/templates/result_view.html'
], function DefineResultView(
	_,
	$,
	Backbone,
	Mustache,
	template
) {
	return Backbone.View.extend({
		el: '#container',
		events: {},
		initialize: function(options) {
			this.template = template;
			this.resultID = options.resultID;
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