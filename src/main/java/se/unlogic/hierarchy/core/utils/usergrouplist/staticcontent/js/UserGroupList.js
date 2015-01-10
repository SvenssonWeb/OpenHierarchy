$(document).ready(function() {
	 
	 $(".usergroup-list").each(function(j){
		 var $list = $(this);
		 var prefix = getPrefix($list);
		 var suffix = getSuffix($list);
		 var $url = $list.find("input[name='connectorURL']").val();
		 
		 var $searchInput = $( "#" + prefix + "-search-" + suffix );
		 
		 $searchInput.autocomplete({
		 	source: function(request, response) {
		 		return searchUsersAndGroups(request, response, $url, $searchInput);
			},
			select: function( event, ui ) {
				
				addEntry(ui.item, $list, suffix);
				
				$(this).val("");
				
				return false;
			},
			focus: function(event, ui) {
		        event.preventDefault();
		    }			
		 });
		 
		 
		 var $entries = $list.find("li");
		 
		 $entries.each(function(j) {
				
			var $entry = $(this);
			
			initDeleteButton($entry, $list, prefix, suffix);
		}); 
		
		$(this).bind("change", function() {
			$(this).find("li").removeClass("lightbackground");
			$(this).find("li:odd").addClass("lightbackground");
		});
		
		$(this).trigger("change");
		
	 });
	 
	 $(".readonly-usergroup-list li:odd").addClass("lightbackground");
	 
});

function getPrefix($list){
	return $list.find("input[name='prefix']").val();
}

function getSuffix($list){
	return $list.find("input[name='suffix']").val();
}

function searchUsersAndGroups(request, response, $searchURL, $searchInput) {
	
	$searchInput.addClass("ui-autocomplete-loading");
	
	$.ajax({
		url : $searchURL,
		dataType : "json",
		contentType: "application/x-www-form-urlencoded;charset=UTF-8",
		data : {
			q : encodeURIComponent(request.term)
		},
		success : function(data) {
			
			if(data.hits != undefined && data.hits.length > 0) {
				
				response($.map(data.hits, function(item) {
					
					return {
						label : item.Name,
						value : item.ID,
						email : item.Email
					}
				}));
			}
			
			$searchInput.removeClass("ui-autocomplete-loading");
			
		},
		error : function() {
			
			$searchInput.removeClass("ui-autocomplete-loading");
		}
	});
}

function addEntry(item, $list, suffix){
	
	var prefix = getPrefix($list);
	
	if($("#" + prefix + "-" + suffix + "_" + item.value).length > 0) {
		return;
	}
	
	var $clone = $("#" + prefix + "-" + suffix + "-template").clone();
	
	var showEmail = $clone.hasClass("show-email-true");
	
	var label = showEmail ? item.label + ", " + item.email : item.label;
	
	$clone.find("span.text").text(label);
	$clone.find("input[name='" + prefix + "-" + suffix + "']").removeAttr("disabled").val(item.value);
	$clone.find("input[name='" + prefix + "-" + suffix + "name']").removeAttr("disabled").attr('name', prefix + "-" + suffix + "name" + item.value).val(label);
	$clone.attr("id", prefix + "-" + suffix + "_" + item.value);
	$clone.attr("class", prefix + "-" + suffix + "-list-entry");
	
	var $deleteButton = initDeleteButton($clone, $list, prefix, suffix);
	$deleteButton.attr("title", $deleteButton.attr("title") + " " + label);
	
	$list.find("li:last").before($clone);
	$clone.show();
	
	$list.trigger("change");
	
}

function initDeleteButton($entry, $list, prefix, suffix) {
	
	var $deleteButton = $entry.find("a.delete");
	
	$deleteButton.click(function(e) {
		e.preventDefault();
		$("#" + prefix + "-" + suffix + "_" + $entry.find("input[type='hidden']").val()).removeClass("disabled").show();
		$entry.remove();
		
		$list.trigger("change");
		
	});
	
	return $deleteButton;
}
