define([
	'Backbone',
	'Router',
	'views/WelcomeView',
	'views/FormView',
	'views/ResultView'
], function DefineSite(
	Backbone,
	Router,
	WelcomeView,
	FormView,
	ResultView
) {
	function Site() {
		console.log('Site: init');
		this.currentPage = undefined;
		this.router = new Router();
		this.router.on('route:welcome', this.pageChange.bind(this, 'welcome'));
		this.router.on('route:search', this.pageChange.bind(this, 'search'));
		this.router.on('route:result', this.pageChange.bind(this, 'result'));
	}
	
	Site.prototype.pageChange = function(page, parameter) {
		
		console.log('Site: page change!', page, parameter);
		
		if (this.currentPage !== undefined) {
			console.warn('Site: killing %s', this.currentPage.name);
			this.currentPage.close();
			this.currentPage = undefined;
		}
		
		if (page === 'welcome') {
			this.currentPage = new WelcomeView();
		}
		else if(page === 'search') {
			this.currentPage = new FormView();
		}
		else if(page === 'result') {
			this.currentPage = new ResultView({
				response: parameter
			});
		}
		else {
			throw 'InvalidArgument: Page name not found!';
		}
		
		this.render();
	};
	
	Site.prototype.render = function() {
		console.log('Site: render');
		this.currentPage.render();
	};
	
	return Site;
});