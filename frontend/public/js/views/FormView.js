define([
	'underscore',
	'jquery',
	'Backbone',
	'Mustache',
	'text!/templates/form_view.html',
	'text!/resources/services.json',
	'select2',
	'bootstrap.slider'
], function DefineFormView(
	_,
	$,
	Backbone,
	Mustache,
	template,
	services
) {
	return Backbone.View.extend({
		el: '#form-container',
		events: {
			'change #services-list': 'serviceSelected',
			'click #toggle-optional': 'toggleOptional' 
		},
		initialize: function() {
			this.template = template;
			this.services = JSON.parse(services).services;
			console.log(this.services.length);
			this.render();
		},
		getTemplateData: function() {
			var data = {
				services: '<option></option>'
			};
			
			this.services.forEach(function forEachService(service) {
				data.services += '<option value="' + service.id + '">' +
					service.name + '</option>';
			}, this);
			
			return data;
		},
		render: function() {
			this.$el.empty();
			this.$el.html(Mustache.render(this.template, this.getTemplateData()));
			
			this.$el.find('#services-list').select2({
				placeholder: 'Select a service',
				allowClear: true
			});	
					
			this.$el.find('#optional-date').select2({
				placeholder: 'Select preferred days'
			});
					
			this.$el.find('#optional-zipcode').select2({
				placeholder: 'Zipcodes',
				tags: true
			});
			
			$("#optional-time").slider({});
			
			return this;
		},
		serviceSelected: function(e) {
			var serviceId = e.target.value;
			
			if (serviceId) {
				this.$el.find('#personal-data').removeClass('hidden');
				this.$el.find('#toggle-optional').removeClass('hidden');
				this.$el.find('#submit-container').removeClass('hidden');
			}
			else {
				this.$el.find('#personal-data').addClass('hidden');
				this.$el.find('#toggle-optional').addClass('hidden');
				this.$el.find('#submit-container').addClass('hidden');
			}
		},
		toggleOptional: function() {
			this.$el.find('#optional-information').toggleClass('hidden');
		}
	});
});