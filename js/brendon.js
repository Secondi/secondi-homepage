// JavaScript Document
// Author: Brendon John
// Description: This is basically the first set of js functions I have written from scratch

$(document).ready(function(){
	$("label").inFieldLabels();
	$('#submit').click(function () {
		
		var name = $('input[name=yourname]');
		var email1 = $('input[name=email]');
		var email2 = $('input[name=email2]');
		if(name.val()==''){
			name.addClass('highlight');
			return false;
		}else name.removeClass('highlight');
		
		if (email1.val()==''){
			email1.addClass('highlight');
			return false;
		}else email1.removeClass('highlight');
		
		if (email2.val()==''){
			email2.addClass('highlight');
			return false;
		}else email2.removeClass('highlight');
		
		if(email1.val()!= email2.val()){
			email1.addClass('highlight');
			email2.addClass('highlight');
			return false;
		}
		else{
			email1.removeClass('highlight');
			email2.removeClass('highlight');
		}
		
		var data = 'yourname='+name.val()+'&email='+email1.val();
		$.ajax({
			url:"submitemail.php",
			type:"GET",
			data:data,
			cache:false,
			success: function(html){
				if(html==1){
					
					setCookieFan(name.val());
					createThanks(name.val());
				}
				else{
					//Event for when this fails!
				}
			}
		});
		//break the clickEvent, prevent the page from reloading
		return false;
		
	});
	
	$('#resubmit').click(function () {
		document.cookie = 'fanName=; expires=Thu, 01-Jan-70 00:00:01 GMT;';
		$('#overlay').fadeOut('slow');
	});
	$("div.panel_button").click(function(){
		$("div#panel").animate({ height: "450px" },"fast");
		$("div.panel_button").toggle();  
	});
	$("div#hidebtn").click(function(){
			$("div#panel").animate({height: "0px"}, "fast");  
	});  
});



//checks to see if computer has already submitted
function checkFan(){
	var i,x,y,AllCookies = document.cookie.split(";");
	for(i=0;i<AllCookies.length;i++){
		var middleIndex = AllCookies[i].indexOf("=");
		x = AllCookies[i].substr(0,middleIndex);
		y = AllCookies[i].substr(middleIndex+1);
		
		if(x == "fanName"){
			createThanks(unescape(y));
		}
		
	}
}


function createThanks(message){
	$('#overlay').fadeIn('slow');
	document.getElementById('username').innerHTML= message;
	
}


//create a cookie with the users name
function setCookieFan(fanName){
	var exDate = new Date();
	exDate.setDate(exDate.getDate()+5);
	
	var cValue = escape(fanName)+"; expires="+exDate.toUTCString();
	
	document.cookie="fanName="+cValue;
}