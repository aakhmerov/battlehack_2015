define([
	'underscore',
	'jquery',
	'Backbone',
	'Mustache',
	'views/AbstractView',
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
	AbstractView,
	FormModel,
	template,
	services
) {
	return AbstractView.extend({
		el: '#container',
		events: {
			'change #services-list': 'serviceSelected',
			'click button#toggle-optional': 'toggleOptional',
			'click #submit-container': 'submitForm',
			'click .day': 'changeDay',
			'blur .required': 'requiredFieldChanged',
			'blur .optional': 'optionalFieldChanged'
		},
		name: 'FormView',
		initialize: function() {
			console.log('FormView: init');
			this.template = template;
			this.services = JSON.parse(services).services;
			this.model = new FormModel();
			this.model.on('sync', this.success.bind(this));
		},
		getTemplateData: function() {
			var data = {
				services: ''
			};
			console.log('FormView: getTemplateData');
			
			this.services.forEach(function forEachService(service) {
				data.services += '<option value="' + service.id + '">' +
					service.name + '</option>';
			}, this);
			
			return data;
		},
		render: function() {
			console.log('FormView: render');
			
			this.$el.empty();
			this.$el.html(Mustache.render(this.template, this.getTemplateData()));
			
			this.$el.find('#services-list').select2({
				placeholder: 'Select a service',
				allowClear: true
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
			
			console.log('FormView: serviceSelected');
			
			if (serviceId) {
				this.model.set('serviceId', serviceId);
				
				this.$el.find('#personal-data').removeClass('hidden');
				this.$el.find('#toggle-optional').removeClass('hidden');
				this.$el.find('#submit-container').removeClass('hidden');
			}
			else {
				this.model.clear({
					silent: true
				});
				
				this.$el.find('#personal-data').addClass('hidden');
				this.$el.find('#toggle-optional').addClass('hidden');
				this.$el.find('#optional-information').addClass('hidden');
				this.$el.find('#submit-container').addClass('hidden');
			}
		},
		toggleOptional: function() {
			console.log('FormView: toogleOptional');
			this.$el.find('#optional-information').toggleClass('hidden');
		},
		submitForm: function(e) {
			console.log('FormView: submitForm');
			var optionalDay = this.$el.find('#optional-date').val(),
				optionalTime = this.$el.find('#optional-time').val(),
				optionalZipcodes = this.$el.find('#optional-zipcode').val();
				
			if ($(e.target).hasClass('disabled')) {
				return;
			}
				
			console.log('FormView: submitForm');	
				
			this.model.set('days', optionalDay);
			this.model.set('time', optionalTime);
			this.model.set('zipCodes', optionalZipcodes);
			
			console.log(this.model.toJSON());
			
			this.model.save();
		},
		requiredFieldChanged: function(e) {
			
			console.log('FormView: requiredFieldChanged');
			
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
			
			console.log('FormView: optionalFiledChanged');
			
			if (e.target.value.length === 0) {
				return;
			}
			
			this.model.set(e.target.name, e.target.value);
		},
		changeDay: function(e) {
			console.log('FormView: changeDay');
			var $element = $(e.target);
			
			if ($element.hasClass('btn-success')) {
				$element.addClass('btn-default').removeClass('btn-success');
			}
			else {
				$element.removeClass('btn-default').addClass('btn-success');
			}
		},
		success: function(model, response, options) {
			console.info(this.name + ': Success!', model, response, options);
//			TODO: Parse user ID and update hash
//			FAKE REDIRECT FOR TESTING
//			location.hash = '#result/' + response.userToken;
		}
	});
});