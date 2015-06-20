define([
	'underscore',
	'jquery',
	'Backbone',
	'Mustache',
	'models/FormModel',
	'text!/templates/form_view.html',
	'text!/resources/services.json',
	'select2',
	'bootstrap.slider'
], function DefineFormView(
	_,
	$,
	Backbone,
	Mustache,
	FormModel,
	template,
	services
) {
	return Backbone.View.extend({
		el: '#container',
		events: {
			'change #services-list': 'serviceSelected',
			'click #toggle-optional': 'toggleOptional',
			'click #submit-container': 'submitForm',
			'blur .required': 'requiredFiledChanged',
			'blur .optional': 'optionalFieldChanged'
		},
		initialize: function() {
			this.template = template;
			this.services = JSON.parse(services).services;
			this.model = new FormModel();
			this.render();
		},
		getTemplateData: function() {
			var data = {
				services: ''
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
				this.$el.find('#optional-information').addClass('hidden');
				this.$el.find('#submit-container').addClass('hidden');
			}
		},
		toggleOptional: function() {
			this.$el.find('#optional-information').toggleClass('hidden');
		},
		submitForm: function() {
			var optionalDay = this.$el.find('#optional-date').val(),
				optionalTime = this.$el.find('#optional-time').val(),
				optionalZipcodes = this.$el.find('#optional-zipcode').val();	
				
			this.model.set('date', optionalDay);
			this.model.set('time', optionalTime);
			this.model.set('zipcodes', optionalZipcodes);
			
			console.log(this.model.toJSON());
			//this.model.save();
		},
		requiredFiledChanged: function(e) {
			
			if (e.target.value.length === 0) {
				return;
			}
			
			this.model.set(e.target.name, e.target.value);
			
			if (this.model.requiredDataComplete()) {
				this.$el.find('#submit-container .btn').removeClass('disabled');
			}
			else {
				this.$el.find('#submit-container .btn').addClass('disabled');
			}
		},
		optionalFiledChanged: function(e) {
			
			if (e.target.value.length === 0) {
				return;
			}
			
			this.model.set(e.target.name, e.target.value);
		}
	});
});