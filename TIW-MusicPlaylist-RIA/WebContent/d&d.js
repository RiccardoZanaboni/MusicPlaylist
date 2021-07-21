(function(){
	
	window.addEventListener("load", () => {
		reorderButton = document.getElementById("id_reorderbutton");
		saveButton = document.getElementById("id_savebutton");
		orderList = document.getElementById("songscontainerorder");
		list = document.getElementById("songscontainer");
		saveButton.style.display = "none";
		this.leftbutton = document.getElementById("id_leftbutton");
	    this.rightbutton = document.getElementById("id_rightbutton");
		reorderButton.querySelector("input[type='button'].submit").addEventListener("click", (e) => {
			list.style.display = "none";
			orderList.style.display = "block";
			reorderButton.style.display = "none";
			saveButton.style.display = "inline-block";
			leftbutton.style.display = "none";
			rightbutton.style.display = "none";
			var elements = document.getElementsByClassName("draggable")
			for (let i = elements.length - 1; i >= 0; i--){
				elements[i].draggable=true;
				elements[i].addEventListener("dragstart",dragStart);
				elements[i].addEventListener("dragover",dragOver);
				elements[i].addEventListener("dragleave",dragLeave);
				elements[i].addEventListener("drop",drop);
			}
		},false);
	},false);
	
	let startElement;
	
	function dragStart(event) {
		startElement = event.target.closest("tr");
	}
	
	function unselectRows(rowsArray) {
		for (var i = 0; i< rowsArray.length; i++){
			rowsArray[i].className = "notselected";
		}
	}
	
	function dragOver(event){
		event.preventDefault();
		var dest = event.target.closest("tr");
		dest.className= "selected";
	}
	
	function dragLeave(event){
		var dest = event.target.closest("tr");
		dest.className= "notselected";
	}
	
	function drop(event) {
		var dest = event.target.closest("tr");
		var table = dest.closest('table');
		var rowsArray = Array.from(table.querySelectorAll('tbody > tr'));
		var indexDest = rowsArray.indexOf(dest);
		
		if (rowsArray.indexOf(startElement) < indexDest)
			startElement.parentElement.insertBefore(startElement , rowsArray[indexDest + 1]);
		else
			startElement.parentElement.insertBefore(startElement , rowsArray[indexDest]);			
		unselectRows(rowsArray);
	}

		
})();