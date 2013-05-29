var MyView = Backbone.View.extend({

	initialize: function () {
		_.bindAll(this, "render");
		this.fetchSomeData();
	},

	fetchSomeData: function () {
		$.when(this.fetchFoo(), this.fetchBar())
			.always(function (fooData, barData) {
				this.render();
			});
	},

	fetchFoo: function () {
		var deferred = new $.Deferred();
		$.get('/foo', function(fooData){
			deferred.resolve(fooData);
		}, function(){
			deferred.reject([]);
		});
		return deferred.promise();
	},

	fetchBar: function () {
		var deferred = new $.Deferred();
		$.get('/bar', function(barData){
			deferred.resolve(barData);
		}, function(){
			deferred.reject([]);
		});
		return deferred.promise();
	},

	render:function () {
		$(this.el).html('');
		return this;
	}

});
