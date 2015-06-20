define([
	'Backbone',
	'views/WelcomeView',
	'views/FormView',
	'views/ResultView'
], function DefineSite(
	Backbone,
	WelcomeView,
	FormView,
	ResultView
) {
	function Site() {
		Backbone.Events.on('page change', this.pageChange.bind(this));
	}
	
	Site.prototype.pageChange = function(page, parameter) {
		if(page === 'search') {
			this.currentPage = new FormView();
		}
		else if(page === 'result') {
			this.currentPage = new ResultView({
				resultID: parameter
			});
		}
		else {
			this.currentPage = new WelcomeView();
		}
		
		this.render();
	};
	
	Site.prototype.render = function() {
		if (this.currentPage) {
			this.currentPage.render();	
		}
	};
	
	return Site;
});