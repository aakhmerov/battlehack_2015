define([
	'jquery',
	'underscore',
	'Backbone',
	'Mustache',
	'views/AbstractView',
	'text!/templates/welcome_view.html'
], function DefineWelcomeView(
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
		name: 'WelcomeView',
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