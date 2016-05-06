// last updated by david 25/06/2014

// retina image replacement - for performance run speed test before dom is ready.
$.hisrc.speedTest({ speedTestUri: '/assets/img/hisrc-50K.jpg' });

$(document).ready(function(){
	
	// remove default value from any input with class .default-value on focus
	$('.default-value').each(function() {
		var default_value = this.value;
		$(this).focus(function() {
			if(this.value == default_value) {
				this.value = '';
			}
		});
		$(this).blur(function() {
			if(this.value == '') {
				this.value = default_value;
			}
		});
	});	
	
	// validate form with class .validate on onfocusout and submit
	$('form.validate').validate({
		onkeyup: false,
		rules: {
			f_password_1: {
		  		required: true,
		  		minlength: 8
				}, 
			f_password_2: {
		  		required: true,
		  		minlength: 8,
		  		equalTo: "#f_password_1"
				}
		}
	});
	
	jQuery.validator.addMethod(
		"phone",
		function(phone_number, element) {
			return this.optional(element) || /^\d{8,}$/.test(phone_number.replace(/\s/g, ''));
		},
		"Please enter a valid phone number"
	);
	
	// add tooltip to anything with class .tip
	$('.tip').tooltip();
	
	// show/hide for faqs etc
	$('.collapse').on('show', function () {
		$(this).prev().find('i').removeClass('icon-plus').addClass('icon-minus');
	}).on('hide', function(){
		$(this).prev().find('i').removeClass('icon-minus').addClass('icon-plus');
	});
	
	// retina image replacement
	$("img.hisrc").hisrc({
		srcIsLowResolution: false, 
		speedTestUri: '/assets/img/hisrc-50K.jpg'
	});
	
});
