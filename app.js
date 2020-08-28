const express = require('express')
const app = express()
var http = require('http').Server(app);
var io = require('socket.io')(http);
const process = require('process');

app.use(express.static('public/'+process.argv[3]))
app.use(express.static('public'))

app.get('/', function(req, res) {
   res.sendFile(__dirname + '/public/'+process.argv[3]+'/index.html');
   res.redirect('http://localhost:'+process.argv[2]+'/');
});

var angleToGo;
const separation = 0.1
var noUsers = 0;
var activeUser = 1;
var startRight = true;
var receivedConfirmation = [];
var projectsReloaded = 0;
var noUsersReloaded = null;
var actuallyReloaded = 0;
io.on('connection', function(socket) {
   //New connection, an id is created for every new connection and now we wait to get window size
   var id;
   var isATablet = false;
   console.log('New Connection');
   socket.emit('getWindowSize');

   //First signal from socket to set all the initial variables
   socket.on('windowSize', function(data) {
      noUsers++;
      id = noUsers;
      socket.join('Window');

      var aspect = data.width/data.height;
      angleToGo=-18.7339*(aspect*aspect)+93.5448*aspect+0.0208;
      var angleThisSocket = Math.floor((id/2))*angleToGo;
      if(id%2 == 0){
         angleThisSocket = -angleThisSocket;
      }

      console.log('Screen number ' + noUsers + ' connected with id ' + id);

      socket.emit('idSet', {id: id, active: id==activeUser, angle: angleThisSocket, 
                           x: separation * (-(angleThisSocket)/angleToGo),
                           z: 0});
      io.sockets.to('Window').emit('newWindow', noUsers);
      
      //Synchronisation function
      const interval = setInterval(function() {
         io.sockets.to('Window').emit('whatTime');
      }, 5000);
   })

   //Same signal as initial signal but without synchronisation call and assignation of id
   socket.on('windowResize', function(data) {
      var aspect = data.width/data.height;
      angleToGo=-18.7339*(aspect*aspect)+93.5448*aspect+0.0208;

      var angleThisSocket = Math.floor((id/2))*angleToGo;
      if(id%2 == 0){
         angleThisSocket = -angleThisSocket;
      }
      console.log(angleToGo + " " + (id%2) + " " + Math.floor((id/2)) + " " +angleThisSocket);
      socket.emit('idReset', {id: id, active: id==activeUser, angle: angleThisSocket, 
                           x: separation * (-(angleThisSocket)/angleToGo),
                           z: 0});

   })

   //Functions for synchronisation
   socket.on('currentTime', function(data) {
      io.sockets.to('Window').emit('synchronise', data);
   })
   socket.on('confirmation', function(data) {
      if(receivedConfirmation.indexOf(data) == -1){
         receivedConfirmation.push(data);
         if(receivedConfirmation.length >= noUsers){
            receivedConfirmation = []
            io.sockets.to('Window').emit('start', data);
         }
      }
   })

   socket.on('moveKeySend', function(data) {
      io.sockets.to('Window').emit('moveKeySock', data);
   })

   //USER FUNCTIONS
   socket.on('serverMoveUp', function() {
      io.sockets.to('Window').emit('moveUp');
   })
   socket.on('serverMoveDown', function() {
      io.sockets.to('Window').emit('moveDown');
   })
   socket.on('serverMoveLeft', function() {
      io.sockets.to('Window').emit('moveLeft');
   })
   socket.on('serverMoveRight', function() {
      io.sockets.to('Window').emit('moveRight');
   })
   socket.on('serverMoveBackwards', function() {
      io.sockets.to('Window').emit('moveBackwards');
   })
   socket.on('serverMoveForward', function() {
      io.sockets.to('Window').emit('moveForward');
   })
   socket.on('serverRotateZPos', function() {
      io.sockets.to('Window').emit('rotateZPos');
   })
   socket.on('serverRotateZNeg', function() {
      io.sockets.to('Window').emit('rotateZNeg');
   })
   socket.on('serverRotateYPos', function() {
      io.sockets.to('Window').emit('rotateYPos');
   })
   socket.on('serverRotateYNeg', function() {
      io.sockets.to('Window').emit('rotateYNeg');
   })
   socket.on('serverRotateXPos', function() {
      io.sockets.to('Window').emit('rotateXPos');
   })
   socket.on('serverRotateXNeg', function() {
      io.sockets.to('Window').emit('rotateXNeg');
   })
   socket.on('serverResetCamera', function() {
      io.sockets.to('Window').emit('resetCamera');
   })

   socket.on('serverSwitchCamera', function() {
      io.sockets.to('Window').emit('switchCamera');
   })

   //REORGANISATION OF IDS WHEN A SOCKET DISCONNECTS
   socket.on('updateIDReorganise', function(data) {
      if(id==2){id = 1;}
      else if(id%2 == 0){
         id = id-2;
      }else{
         if(data > id){
            id += 2;
         }
      }
   })
   socket.on('updateIDMove', function(data) {
      if(data%2 == id%2 && id > data){id -=2;}
   })
   socket.on('updateIDMirror', function() {
      if(id > 1){
         if(id%2==1){
            id--;
         }else{
            id++;
         }
      }
   })

   //FUNCTION THAT SETS SOCKET AS A TABLET
   socket.on('signalTablet', function() {
      isATablet = true;
      console.log("It's a tablet")
      socket.join('Tablet');
   })

   //Functions for managin browsers already connected to a port from before the server wash launched, to set the new image to the
   // correct browser
   socket.on('newProjectServer', function(idSocket) {
      console.log("reloading " + id + "  and " + idSocket);
      noUsersReloaded = idSocket
      projectsReloaded++;
      if(projectsReloaded == idSocket){
         angleToGo = null;
         noUsers = 0;
         activeUser = 1;
         startRight = true;
         receivedConfirmation = [];
         projectsReloaded = 0;
         sendReload();
      }
   })
   socket.on('serverReload', function() {
      console.log("reloading " + id);
      sendReload();
   })
   function sendReload(){
      console.log("send reloading " + id + " to " + (projectsReloaded+1));
      projectsReloaded++;
      io.sockets.to('Window').emit('reload', projectsReloaded);
   }

   //Function for disconnection of a socket
   socket.on('disconnect', (reason) => {
      console.log('Disconnection')
      if(projectsReloaded != 0){
         actuallyReloaded++;
      }
      if(!isATablet && projectsReloaded == 0){
         noUsers--;
         console.log('Screen number ' + id + ' disconnected and there are ' + noUsers + ' users');
   
         if(noUsers %2 == 1 && id%2 ==1){
            io.sockets.emit('updateIDReorganise', id);
            io.sockets.emit('updateIDReorganiseSock', {noUser:id, startRight:startRight});
         }else{
            io.sockets.emit('updateIDMove', id);
            io.sockets.emit('updateIDMoveSock', id);
   
            if(id%2==0 && noUsers%2==0){
               io.sockets.emit('updateIDMirror');
               io.sockets.emit('updateIDMirrorSock');
               startRight = !startRight
            }
         }
      }
      if(actuallyReloaded == noUsersReloaded){
            projectsReloaded=0;
            noUsersReloaded=null;
            actuallyReloaded=0;
      }
   });
});

http.listen(process.argv[2], function() {
   console.log('listening on localhost:'+process.argv[2]);
});