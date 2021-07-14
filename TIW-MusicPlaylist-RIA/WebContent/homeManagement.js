{ // avoid variables ending up in the global scope

	  // page components
	  let playlistList,playlistDetails,playlistForm,songForm
	    pageOrchestrator = new PageOrchestrator(); // main controller

	  window.addEventListener("load", () => {
	    if (sessionStorage.getItem("username") == null) {
	      window.location.href = "index.html";
	    } else {
	      pageOrchestrator.start(); // initialize the components
	      pageOrchestrator.refresh();
	    }
	  }, false);

	function PlaylistList(_alert, _playlistcontainer, _playlistcontainerbody) {
	    this.alert = _alert;
	    this.playlistcontainer = _playlistcontainer;
	    this.playlistcontainerbody = _playlistcontainerbody;

	    this.show = function() {
	      var self = this;
	      makeCall("GET", "GetPlaylistData", null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var playlistToShow = JSON.parse(req.responseText);
	              if (playlistToShow.length == 0) {
	                self.alert.textContent = "No playlist yet!";
	                return;
	              }
	              self.update(playlistToShow); // self visible by closure
	            
	          } else if (req.status == 403) {
                  window.location.href = req.getResponseHeader("Location");
                  window.sessionStorage.removeItem('username');
                  }
                  else {
	            self.alert.textContent = message;
	          }}
	        }
	      );
	    };
		this.update = function(arrayPlaylists) {
	      var  row, datecell, linkcell, anchor;
	      this.playlistcontainerbody.innerHTML = ""; // empty the table body
	      // build updated list
	      var self = this;
	      arrayPlaylists.forEach(function(playlist) { // self visible here, not this
	        row = document.createElement("tr");
	        linkcell = document.createElement("td");
	        anchor = document.createElement("a");
	        linkcell.appendChild(anchor);
	        linkText = document.createTextNode(playlist.title);
	        anchor.appendChild(linkText);
	        //anchor.playlistid = playlist.id; // make list item clickable
	        anchor.setAttribute('playlistid', playlist.id); // set a custom HTML attribute
	        anchor.addEventListener("click", (e) => {  
	          // dependency via module parameter
	          playlistDetails.show(e.target.getAttribute("playlistid")); // the list must know the details container
	        }, false);
	        anchor.href = "#";
	        row.appendChild(linkcell);
			datecell = document.createElement("td");
	        datecell.textContent = playlist.creation_date;
	        row.appendChild(datecell);
	        self.playlistcontainerbody.appendChild(row);
	      });
	      this.playlistcontainer.style.visibility = "visible";

	    }
	  }

	function PlaylistDetails(_alert, _songscontainer, _songscontainerbody){
		this.alert = _alert;
	    this.songscontainerr = _songscontainer;
	    this.songscontainerbody = _songscontainerbody;

		
		this.show = function(playlistid){
			 var self = this;
	      makeCall("GET", "GetSongs?playlistid=" + playlistid, null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var songsToShow = JSON.parse(req.responseText);
				  if (playlistToShow.length == 0) {
	               	self.alert.textContent = "No songs in the playlist yet!";
	                return;
	              }
	              self.update(songsToShow);
				} else if (req.status == 403) {
                  window.location.href = req.getResponseHeader("Location");
                  window.sessionStorage.removeItem('username');
                  }
                  else {
	            self.alert.textContent = message;
	          }}
	        }
	      );
	    };
		this.update = function(arraySongs) {
	      var  row, datecell, linkcell, anchor;
	      this.songscontainerbody.innerHTML = ""; // empty the table body
	      // build updated list
	      var self = this;
		  row = document.createElement("tr");
		  self.playlistcontainerbody.appendChild(row);
	      arraysongs.forEach(function(song) { // self visible here, not this
	        linkcell = document.createElement("td");
	        anchor = document.createElement("a");
	        linkcell.appendChild(anchor);
	        linkText = document.createTextNode(song.title);
	        anchor.appendChild(linkText);
	        //anchor.songid = song.id; // make list item clickable
	        anchor.setAttribute('songid', song.id); // set a custom HTML attribute
	        //anchor.addEventListener("click", (e) => {  
	          // dependency via module parameter
	          //playlistDetails.show(e.target.getAttribute("playlistid")); // the list must know the details container
	        //}, false);
	        anchor.href = "#";
	        row.appendChild(linkcell);
	      });
		  this.playlistcontainer.style.visibility = "visible";
		}
	}
		
	function PlaylistForm(playlistId, alert) {
	    this.playlistForm = playlistId;
	    this.alert = alert;

	    this.registerPlaylist = function(orchestrator) {
	      // Manage submit button
	      this.playlistForm.querySelector("input[type='button'].submit").addEventListener('click', (e) => {
	        var playlist_title = e.target.closest("input"),valid = true;
	        if (!playlist_title.checkValidity()) {
	            playlist_title.reportValidity();
	            valid = false;
	          }

	        if (valid) {
	          var self = this;
	          makeCall("POST", 'CreatePlaylist', e.target.closest("form"),
	            function(req) {
	              if (req.readyState == XMLHttpRequest.DONE) {
	                var message = req.responseText; // error message or mission id
	                if (req.status == 200) {
	                  orchestrator.refresh(); // id of the new mission passed
	                } else if (req.status == 403) {
                      window.location.href = req.getResponseHeader("Location");
                      window.sessionStorage.removeItem('username');
                  }
                  else {
	                  self.alert.textContent = message;
	                  self.reset();
	                }
	              }
	            }
	          );
	        }
	      });
		}
		this.reset = function() {
	      var title = document.querySelectorAll("#" + this.playlistForm.id + " title");
	      title.hidden = true;
	    }
	}

	function SongForm(songId, alert) {
	    this.songForm = songId;
	    this.alert = alert;

	    this.registerSong = function(orchestrator) {
	      // Manage submit button
	      this.songForm.querySelector("input[type='button'].submit").addEventListener('click', (e) => {
	        var song_fieldset = e.target.closest("fieldset"),valid = true; 
	        for (i = 0; i < song_fieldset.elements.length; i++) {
	          if (!song_fieldset.elements[i].checkValidity()) {
	            song_fieldset.elements[i].reportValidity();
	            valid = false;
	            break;
	          }
	        }

	        if (valid) {
	          var self = this;
	          makeCall("POST", 'CreateSong', e.target.closest("form"),
	            function(req) {
	              if (req.readyState == XMLHttpRequest.DONE) {
	                var message = req.responseText; // error message or mission id
	                if (req.status == 200) {
	                  orchestrator.refresh(); // id of the new mission passed
	                } else if (req.status == 403) {
                      window.location.href = req.getResponseHeader("Location");
                      window.sessionStorage.removeItem('username');
                  }
                  else {
	                  self.alert.textContent = message;
	                  self.reset();
	                }
	              }
	            }
	          );
	        }
	      });
		}
		this.reset = function() {
	      var input = document.querySelectorAll("#" + this.songForm.id + " input");
	      input.hidden = true;
	    }
	}

	  function PageOrchestrator() {
	    var alertContainer = document.getElementById("id_alert");
	    
	    this.start = function() {
	      playlistList = new PlaylistList(
	        alertContainer,
	        document.getElementById("id_playlistcontainer"),
	        document.getElementById("id_playlistcontainerbody"));
		  
		  playlistDetails = new PlaylistDetails(
			alertcontainer,
			document.getElementById("id_songscontainer"),
			document.getElementById("id_songscontainerbody"));

		  playlistForm = new PlaylistForm(document.getElementById("id_createplaylistform"), alertContainer);
	      playlistForm.registerPlaylist(this);  // the orchestrator passes itself --this-- so that the playlistForm can call its refresh function after creating a mission
	    };
		  songForm = new SongForm(document.getElementById("id_createsongform"), alertContainer);
		  songForm.registerSong(this);

		this.refresh = function() {
	      alertContainer.textContent = "";        // not null after creation of status change
	      playlistList.show();
		  playlistDetails.show();
		  playlistForm.reset();
		  songForm.reset(); //CONTROLLARE SE ANCHE SENZA I RESET I VALORI VENGO AZZERATI
	  	};
	  }	
}