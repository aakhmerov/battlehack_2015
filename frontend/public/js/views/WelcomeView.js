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
			console.log(this.name + ': init');
			this.template = template;
		},
		getTemplateData: function() {
			console.log(this.name + ': getTemplateData');
			var data = {};
			
			return data;
		},
		render: function() {
			console.log(this.name + ': render');
			this.$el.empty();
			this.$el.html(Mustache.render(this.template, this.getTemplateData()));
			return this;
		}
	});
});