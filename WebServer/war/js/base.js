/**
 * @fileoverview
 * Provides methods for the Task Manager UI and interaction with the API.
 */

/** namespace for the projects. */
var taskManager = taskManager || {};

/**
 * Client ID of the application (from the APIs Console).
 * @type {string}
 */
taskManager.CLIENT_ID = 
	'390025593484.apps.googleusercontent.com';

/**
 * Scopes used by the application.
 * @type {string}
 */
taskManager.SCOPES = 
	'https://www.googleapis.com/auth/userinfo.email';

/**
 * Whether or not the user is signed in.
 * @type {boolean}
 */
taskManager.signedIn = false;

/**
 * Loads the application UI after the user has completed auth.
 */
taskManager.userAuthed = function() {
  var request = gapi.client.oauth2.userinfo.get().execute(function(resp) {
    if (!resp.code) {
      taskManager.signedIn = true;
      //document.getElementById('signinButton').innerHTML = 'Sign out';
      //document.getElementById('authedGreeting').disabled = false;
    }
  });
};

/**
 * Handles the auth flow, with the given value for immediate mode.
 * @param {boolean} mode Whether or not to use immediate mode.
 * @param {Function} callback Callback to call on completion.
 */
taskManager.signin = function(mode, callback) {
  gapi.auth.authorize({client_id: taskManager.CLIENT_ID,
      scope: taskManager.SCOPES, immediate: mode},
      callback);
};
/**
 * Presents the user with the authorization popup.
 */
taskManager.auth = function() {
  if (!taskManager.signedIn) {
    taskManager.signin(false, taskManager.userAuthed);
  } else {
    taskManager.signedIn = false;
    //document.getElementById('signinButton').innerHTML = 'Sign in';
    //document.getElementById('authedGreeting').disabled = true;
  }
};


/**
 * Lists the tasks that matches the request.
 * @param {object} data. JS Object with the search filters 
 */
taskManager.listTasks = function(data) {
  gapi.client.observationendpoint.listObservation(data).execute(
      function(resp) {
        if (!resp.code) {
          $('.table tbody').empty();
          resp.items = resp.items || [];
          for (var i = 0; i < resp.items.length; i++) {
        	  var taskRow = 
        		  '<tr data-id="' + resp.items[i].objectId + '">' +
        		  '<td>' + resp.items[i].objectId + '</td>' +
        		  '<td>' + resp.items[i].captureDate + '</td>' +
        		  '<td>' + resp.items[i].occupancy + '</td>' +
        		  '<td class="actions">'+
        		  	'<button type="button" class="remove btn btn-default"><span class="glyphicon glyphicon-trash"></span></button>' +
        		  	'<button type="button" class="edit btn btn-default"><span class="glyphicon glyphicon-edit"></span></button>'+
        		  '</td>' +
        		  '</tr>'
        	  $('.table tbody').append(taskRow);
          }
        }
      });
};

/**
 * Call to the API to add a Task
 * @param {object} task. JS Object representing the task
 */
taskManager.addTask = function(observation) {
  gapi.client.observationendpoint.insertObservation(observation).execute(function(resp) {
      if (!resp.code) {
    	  taskManager.listTasks();
      }
    });
};

/**
 * Call to the API to delete a Task
 * @param {string} id. ObjectId of the task.
 */
taskManager.deleteTask = function(objectId) {
	gapi.client.observationendpoint.removeObservation({'id': objectId}).execute(function(resp) {
		if(!resp.code) {
			taskManager.listTasks();
		}
	});
}

/**
 * Greets the current user via the API.
 */
taskManager.authedGreeting = function(id) {
  gapi.client.tasks.greetings.authed().execute(
      function(resp) {
        taskManager.print(resp);
      });
};
/**
 * Enables the button callbacks in the UI.
 */
taskManager.enableButtons = function() {
	
	$('#showAddBlock').on('click', function(){
		$('.new-task-form').addClass('editable');
		$('#showAddBlock').hide();
	});
	
	$('#cancelAdd, #addTask').on('click', function(){
		$('.new-task-form').removeClass('editable');
		$('#showAddBlock').show();
	});
	
	$('#cancelUpdate, #updateTask').on('click', function(){
		$('.edit-task-form').removeClass('editable');
	});
	
	$('#addTask').on('click', function(){
		 var data = {}; 
		 var formTags= $('#addForm').serializeArray();
		 $.each(formTags, function() {
			 data[this.name] = this.value; 
		 }); 
		taskManager.addTask(data);
	});
	

};
/**
 * Initializes the application.
 * @param {string} apiRoot Root of the API's path.
 */
taskManager.init = function(apiRoot) {
  // Loads the OAuth and helloworld APIs asynchronously, and triggers login
  // when they have completed.
  var apisToLoad;
  var callback = function() {
    if (--apisToLoad == 0) {
      taskManager.enableButtons();
      taskManager.signin(true,
          taskManager.userAuthed);
      taskManager.listTasks();
    }
  }

  apisToLoad = 1; // must match number of calls to gapi.client.load()
  gapi.client.load('occupancyPredictionAPI', 'v1', callback, apiRoot);
  //gapi.client.load('oauth2', 'v2', callback);
};