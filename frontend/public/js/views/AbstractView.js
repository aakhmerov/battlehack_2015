define([
	'underscore',
	'jquery',
	'Backbone'
], function DefineAbstractView(
	_,
	$,
	Backbone
) {
	return Backbone.View.extend({
		close: function() {			
			console.log(this.name + ': close');
			
			this.unbind();
			this.stopListening();
			this.$el.empty();
		}
	});
});