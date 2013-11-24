$(document).ready(function() {  
    $("div.panel_button").click(function(){
		$("div#panel").animate({ height: "450px" },"fast");
		$("div.panel_button").toggle();  
	});
	$("div#hidebtn").click(function(){
			$("div#panel").animate({height: "0px"}, "fast");  
	});  
});  
