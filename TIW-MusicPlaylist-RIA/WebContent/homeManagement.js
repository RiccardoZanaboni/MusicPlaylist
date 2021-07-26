{ // avoid variables ending up in the global scope

	  // page components
	  let playlistList,playlistDetails,playlistForm,songForm,songDetails,reorderButton,saveButton,addSongForm
	    pageOrchestrator = new PageOrchestrator(); // main controller
	  reorderButton = document.getElementById("id_reorderbutton");
	  saveButton = document.getElementById("id_savebutton"); 
	  orderList = document.getElementById("songscontainerorder");
      songList = document.getElementById("songscontainer");
	  eventListenerPresence = false;	
	  window.addEventListener("load", () => {
	    if (sessionStorage.getItem("username") == null) {
	      window.location.href = "index.html";
	    } else {
	      pageOrchestrator.start(); // initialize the components
	      pageOrchestrator.refresh();
		}
		  saveButton.querySelector("input[type='button'].submit").addEventListener("click", (e) => {
			var orderArray = Array.from(document.getElementById("id_songsorderbody").querySelectorAll('tbody > tr > td'));
			var arrayId = [];
			for (let i = 0; i <orderArray.length; i++) {
   			arrayId.push(orderArray[i].getAttribute("songid"));
			}
			makeCall("POST", "AddOrder?playlistId="+this.saveButton.querySelector("input[type = 'hidden']").value+"&arrayId="+arrayId, null,
	            function(req) {
	              if (req.readyState == XMLHttpRequest.DONE) {
	                var message = req.responseText; 
	                if (req.status == 200) {
	                  playlistDetails.show(this.saveButton.querySelector("input[type = 'hidden']").value);
	                } else if (req.status == 403) {
                      window.location.href = req.getResponseHeader("Location");
                      window.sessionStorage.removeItem('username');
                  }
                  else {
	                  document.getElementById("id_alert").textContent = message;
	                }
	              }
	            }
	          );
	  	  },false);
	  }, false);

	function PlaylistList(_alert, _playlistcontainer, _playlistcontainerbody) {
	    this.alert = _alert;
	    this.playlistcontainer = _playlistcontainer;
	    this.playlistcontainerbody = _playlistcontainerbody;

	    this.show = function(next) {
	      var self = this;
	      makeCall("GET", "GetPlaylistData", null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var playlistToShow = JSON.parse(req.responseText);
	              if (playlistToShow.length == 0) {
					document.getElementById("buttons").style.display="none";
					document.getElementById("addsongtoplaylistcontainer").style.display="none";
	                self.alert.textContent = "No playlist yet!";
	                return;
	              }
	              self.update(playlistToShow); // self visible by closure
			      if (next) next(); // show the default element of the list if present
	            
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

		this.autoclick = function(playlistId) {
	      var e = new Event("click");
	      var selector = "a[playlistid='" + playlistId + "']";
	      var anchorToClick =  // the first playlist or the playlist with id = playlistId
	        (playlistId) ? document.querySelector(selector) : this.playlistcontainerbody.querySelectorAll("a")[0];
	      if (anchorToClick) anchorToClick.dispatchEvent(e);
	    }
	  }

	function PlaylistDetails(_alert, _songscontainer, _songscontainerbody,  _songsorderdiv,  _songsorderbody, _playlistForm, _songForm,_playerContainer, _addSongForm ){
		this.alert = _alert;
	    this.songscontainer = _songscontainer;
	    this.songscontainerbody = _songscontainerbody;
		this.songsorderdiv = _songsorderdiv;
	    this.songsorderbody = _songsorderbody;
		this.playlistForm = _playlistForm;
		this.songForm = _songForm;
		this.addSongForm = _addSongForm;
		this.playerContainer = _playerContainer;
		this.reorderButton = document.getElementById("id_reorderbutton");
		this.saveButton = document.getElementById("id_savebutton");
		this.leftbutton = document.getElementById("id_leftbutton");
	    this.rightbutton = document.getElementById("id_rightbutton");
		
		this.show = function(playlistid,next){
			next=function() {
	        playlistDetails.autoclick();
	      };
			var self = this;
			var startingsongid = 0;
	      	makeCall("GET", "GetSongs?playlistid=" + playlistid, null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var songsToShow = JSON.parse(req.responseText);
				  addSongForm.show(playlistid);
				  self.addSongForm.playlistid.value = playlistid;
				  if (songsToShow.length == 0) {
					reorderButton.style.display = "none";
					saveButton.style.display = "none";
					leftbutton.style.visibility = "hidden";
					rightbutton.style.visibility = "hidden";
					self.songsorderdiv.style.display = "none";
					self.songscontainer.style.display = "none";
					self.playerContainer.style.display = "none";	
	               	self.alert.textContent = "No songs in the playlist yet!";
	                return;
	              }
				  var songsDivided = self.songsDivision(songsToShow, startingsongid);
	              self.update(songsDivided, startingsongid);
				  self.updateOrder(songsToShow);
				  self.alert.textContent="";
				  self.playlistForm.playlistid.value = playlistid;
				  self.songForm.playlistid.value = playlistid;
				  self.saveButton.playlistid.value = playlistid;
				  if (next) next();
				} else if (req.status == 403) {
                  window.location.href = req.getResponseHeader("Location");
                  window.sessionStorage.removeItem('username');
                } else {
	            self.alert.textContent = message;
	          }}
	        }
	      );
	    };
		this.updateOrder = function(arraySongs) {
	      var  row;
	      this.songsorderbody.innerHTML = ""; // empty the table body
	      // build updated list
	      var self = this;
	      arraySongs.forEach(function(song) { // self visible here, not this
	        row = document.createElement("tr");
			cell = document.createElement("td");
	        cell.textContent = song.title;
			cell.setAttribute('songid', song.id);
	        row.appendChild(cell);
			row.className="draggable";
			self.songsorderbody.appendChild(row);
	      });
		  this.songsorderdiv.style.display = "none";
		}
		
		this.update = function(arraySongs, index) {	
	      let row= document.createElement("tr"), linkcell, anchor;
	      this.songscontainerbody.innerHTML = ""; // empty the table body
	      // build updated list
	      var self = this;
	      arraySongs[index].forEach(function(song) { // self visible here, not this
			linkcell = document.createElement("td");
			imgDiv=document.createElement("div");
			img = document.createElement("img");
			imgDiv.appendChild(img);
			img.setAttribute('src',"data:image/png;base64,"+song.image);
	        anchor = document.createElement("a");
	        linkcell.appendChild(anchor);
	        linkcell.appendChild(imgDiv);
	        linkText = document.createTextNode(song.title);
		    
	        anchor.appendChild(linkText);
	        //anchor.songid = song.id; // make list item clickable
	        anchor.setAttribute('songid', song.id); // set a custom HTML attribute
            anchor.addEventListener("click", (e) => {  
	          // dependency via module parameter
	          songDetails.show(e.target.getAttribute("songid")); // the list must know the details container
	        }, false);
	        anchor.href = "#";
	        row.appendChild(linkcell);
	      });
	      self.saveButton.style.display = "none";	
		  self.songscontainerbody.appendChild(row);
		  this.songscontainer.style.display = "block";	
		  self.reorderButton.style.display = "block";
		  if(arraySongs[index+1] != undefined){
			newr = rightbutton.cloneNode(true);
			rightbutton.replaceWith(newr);
			rightbutton=newr;
			rightbutton.style.visibility = "visible";
			rightbutton.style.display = "block";
			rightbutton.querySelector("input[type='button'].submit").addEventListener("click", (e) => {
			  this.update(arraySongs, index+1);
		    }, false);
		  }else{
			rightbutton.style.visibility = "hidden";
			rightbutton.style.display = "block";
		  }
		  if(arraySongs[index-1] != undefined){
			newl = leftbutton.cloneNode(true);
			leftbutton.replaceWith(newl);
			leftbutton=newl;
			leftbutton.style.visibility = "visible";
			leftbutton.style.display = "block";
			leftbutton.querySelector("input[type='button'].submit").addEventListener("click", (e) => {
			  this.update(arraySongs, index-1);
		    }, false);
		  }else{
			leftbutton.style.visibility = "hidden";
			leftbutton.style.display = "block";
		  }
		}
		
		this.songsDivision = function(arraySongs, startingindex){
			var endindex=0;
			var arraySize = arraySongs.length;
			var iterations = Math.ceil(arraySize/5);
			var temporaryArray = [], arraySongsDivided = [];
			for (let i = 0; i < iterations; i++){
				if((arraySize - startingindex) >= 5){
					endindex += 5;
				}else{
					endindex += arraySize - startingindex;
				}
				for(let k = startingindex; k < endindex; k++){
					temporaryArray.push(arraySongs[k]);
				}
				arraySongsDivided.push(temporaryArray);
				startingindex = endindex;
				temporaryArray = [];
			}
			return arraySongsDivided;
		}
		this.autoclick = function() {
		  var e = new Event("click");	
	      var anchorToClick =   this.songscontainerbody.querySelectorAll("a")[0];
	      if (anchorToClick) anchorToClick.dispatchEvent(e);
	    }
		
	}
	function SongDetails(_alert, _playercontainer) {
	    this.alert = _alert;
	    this.playercontainer = _playercontainer;

	    this.show = function(songid) {
	      var self = this;
	      makeCall("GET", "GetSongDetails?songId="+songid, null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var songDetailsToShow = JSON.parse(req.responseText);
	              if (songDetailsToShow == null) {
	                self.alert.textContent = "Song is null";
	                return;
	              }
	              self.update(songDetailsToShow); // self visible by closure
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
		this.update = function(songDetailsToShow) {
	      this.playercontainer.innerHTML = ""; // empty the div body
	      // build updated song
	        title = document.createElement("h1");
	   		singer = document.createElement("span");
	        genre = document.createElement("span");
			release_date = document.createElement("span");
			title.className = "title", singer.className = "details",genre.className = "details",release_date.className = "details";
			singer.id = "singer",genre.id = "genre",release_date.id = "release_date";
			img = document.createElement("img");
			img.setAttribute('src',"data:image/png;base64,"+songDetailsToShow.image);
			img.id = "playerImg";
			audio = document.createElement("AUDIO");
			audio.setAttribute('src',"data:audio/mpeg;base64,"+songDetailsToShow.file);
			audio.setAttribute("controls", "controls");
	        title.textContent = songDetailsToShow.title;
			singer.textContent = "Singer : " + songDetailsToShow.singer;
			genre.textContent = "Genre : " + songDetailsToShow.musical_genre;
			release_date.textContent = "Release Date : " + songDetailsToShow.release_date;
			playercontainer.innerHTML+= title.outerHTML + singer.outerHTML + genre.outerHTML + release_date.outerHTML + img.outerHTML + audio.outerHTML;
			
	      this.playercontainer.style.display = "block";

	    }

	  }

	function AddSongForm(addsongid, alert){
		this.addSongForm = addsongid;
		this.alert = alert;
		var songselect = document.getElementById("songtoadd");
		
		this.show = function(playlistid) {
	      var self = this;
		  this.addSongForm.style.display="block";
	      makeCall("GET", "GetSongsUser?playlistid="+playlistid, null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var songsOfUser = JSON.parse(req.responseText);
	              if (songsOfUser.length == 0) {
	                self.alert.textContent = "No songs to add!";
					songselect.innerHTML = "";
	                return;
	              }
				self.alert.textContent = "";
	            self.update(songsOfUser);
	          } else if (req.status == 403) {
                  window.location.href = req.getResponseHeader("Location");
                  window.sessionStorage.removeItem('username');
              } else {
	            self.alert.textContent = message;
	          }}
	        }
	      );
	    };

		this.update = function(songsOfUser) {	
	   	  songselect.innerHTML = "";
	      songsOfUser.forEach(function(song) { // self visible here, not this
			optelement = document.createElement("option");
			optelement.text = song.title;
			optelement.value = song.id;
			//optelement.setAttribute("songid", song.id);
	        songselect.appendChild(optelement);
	      });
		}
		
		this.registerSongToAdd = function(orchestrator) {
	      // Manage submit button
	       this.addSongForm.querySelector("input[type='button'].submit").addEventListener('click', (e) => {
	        valid = true;
	        if (valid) {
	          var self = this;
			  playlistToReport = this.addSongForm.querySelector("input[type = 'hidden']").value;
	          makeCall("POST", 'AddSong?playlistid='+playlistToReport+'&songid='+e.target.parentNode.childNodes[1].value, null,
	            function(req) {
	              if (req.readyState == XMLHttpRequest.DONE) {
	                var message = req.responseText; // error message or mission id
	                if (req.status == 200) {
	                  orchestrator.refresh(playlistToReport); // id of the new mission passed
	                } else if (req.status == 403) {
                      window.location.href = req.getResponseHeader("Location");
                      window.sessionStorage.removeItem('username');
                    } else {
	                  self.alert.textContent = message;
	                }
	              }
	            }
	          );
	        }
	      });
		}
	}

		
	function PlaylistForm(playlistId, alert) {
	    this.playlistForm = playlistId;
	    this.alert = alert;

	    this.registerPlaylist = function(orchestrator) {
	      // Manage submit button
	      this.playlistForm.querySelector("input[type='button'].submit").addEventListener('click', (e) => {
	        var playlist_title = e.target.closest("form"),valid = true;
	        if (!playlist_title.checkValidity()) {
	            playlist_title.reportValidity();
	            valid = false;
	          }
	        if (valid) {
	          var self = this;
			  playlistToReport = this.playlistForm.querySelector("input[type = 'hidden']").value;
	          makeCall("POST", 'CreatePlaylist', e.target.closest("form"),
	            function(req) {
	              if (req.readyState == XMLHttpRequest.DONE) {
	                var message = req.responseText; // error message or mission id
	                if (req.status == 200) {
	                  orchestrator.refresh(playlistToReport); // id of the new mission passed
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
	      var title = document.querySelectorAll("#" + this.playlistForm.id + " form");
	      title.hidden = true;
	    }
	}

	function SongForm(songId, alert) {
	    this.songForm = songId;
	    this.alert = alert;

	    this.registerSong = function(orchestrator) {
	      // Manage submit button
	      this.songForm.querySelector("input[type='button'].submit").addEventListener('click', (e) => {
			var form = e.target.closest("form");
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
			  playlistToReport = form.querySelector("input[type = 'hidden']").value;
	          makeCall("POST", 'CreateSong', e.target.closest("form"),
	            function(req) {
	              if (req.readyState == XMLHttpRequest.DONE) {
	                var message = req.responseText; // error message or mission id
	                if (req.status == 200) {
	                  orchestrator.refresh(playlistToReport); // id of the new mission passed
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
	      var input = document.querySelectorAll("#" + this.songForm.id + " form");
	      input.hidden = true;
	    }
	}
	  function PageOrchestrator() {
		var alert_playlist = document.getElementById("id_alert_playlist");
		var alert_songs = document.getElementById("id_alert_songs");
		var alert_player = document.getElementById("id_alert_player");
		var alert_playlistform = document.getElementById("id_alert_playlistform");
		var alert_songform = document.getElementById("id_alert_songform");
		var alert_addform = document.getElementById("id_alert_addform");

	    this.start = function() {
	      playlistList = new PlaylistList(
	        alert_playlist,
	        document.getElementById("id_playlistcontainer"),
	        document.getElementById("id_playlistcontainerbody"));
		  
		  playlistDetails = new PlaylistDetails(
			alert_songs,
			document.getElementById("songscontainer"),
			document.getElementById("id_songscontainerbody"),
			document.getElementById("songscontainerorder"),
			document.getElementById("id_songsorderbody"),
			document.getElementById("id_createplaylistform"),
			document.getElementById("id_createsongform"),
			document.getElementById("playercontainer"),
			document.getElementById("id_addsongtoplaylist"));
			

		  songDetails = new SongDetails(
	        alert_player,
	        document.getElementById("playercontainer"));

		  playlistForm = new PlaylistForm(document.getElementById("id_createplaylistform"), alert_playlistform);
	      playlistForm.registerPlaylist(this);  // the orchestrator passes itself --this-- so that the playlistForm can call its refresh function after creating a mission
	    };
		  songForm = new SongForm(document.getElementById("id_createsongform"), alert_songform);
		  songForm.registerSong(this);

		  addSongForm = new AddSongForm(document.getElementById("id_addsongtoplaylist"),alert_addform);
		  addSongForm.registerSongToAdd(this);

		this.refresh = function(currentPlaylist) {
	      alert_playlist.textContent = "";        // not null after creation of status change
	      alert_songs.textContent = "";
		  alert_player.textContent = "";
    	  alert_playlistform.textContent = "";
		  alert_songform.textContent = "";
		  alert_addform.textContent = "";

		  playlistList.show(function() {
	        playlistList.autoclick(currentPlaylist); 
	      }); // closure preserves visibility of this);

		  //TODO playlistDetails.reset();
		  playlistForm.reset();
		  songForm.reset(); //CONTROLLARE SE ANCHE SENZA I RESET I VALORI VENGO AZZERATI
	  	};
	  }	
}