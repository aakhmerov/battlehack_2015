define([
	'jquery',
	'underscore',
	'Backbone',
	'Mustache',
	'text!/templates/welcome_view.html'
], function DefineWelcomeView(
	_,
	$,
	Backbone,
	Mustache,
	template
) {
	return Backbone.View.extend({
		el: '#container',
		events: {},
		initialize: function() {
			this.template = template;
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