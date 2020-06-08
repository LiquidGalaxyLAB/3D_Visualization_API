const express = require('express')
const app = express()
var http = require('http').Server(app);
var io = require('socket.io')(http);

app.use(express.static('public'))


app.get('/', function(req, res) {
   res.sendfile('index.html');
});
const angleToGo = 45;
var noUsers = 0;
var activeUser = 1;
var angleNext = 0;
var lookingRight = 1; 
var startRight = true;
var receivedConfirmation = [];
io.on('connection', function(socket) {
   noUsers++;
   var id = noUsers;
   console.log('Screen number ' + noUsers + ' connected');
   socket.emit('idSet', {id: noUsers, active: noUsers==activeUser, angle: angleNext*lookingRight, x: 0.1 * (-(angleNext*lookingRight)/angleToGo)});
   if(noUsers%2 == 1){ angleNext+=angleToGo; }
   lookingRight = -lookingRight;
  
   if(noUsers>2){
      io.sockets.emit('whatTime');
   }

   socket.on('currentTime', function(data) {
      receivedConfirmation.push(1);
      io.sockets.emit('setCube', data);
   })

   socket.on('confirmation', function(data) {
      if(receivedConfirmation.indexOf(data) == -1){
         receivedConfirmation.push(data);
         if(receivedConfirmation.length == noUsers){
            receivedConfirmation = []
            io.sockets.emit('start', data);
         }
      }
   })

   socket.on('moveKeySend', function(data) {
      io.sockets.emit('moveKeySock', data);
   })

   socket.on('rotateXServer', function(data) {
      io.sockets.emit('rotateX', data);
   })
   socket.on('rotateYServer', function(data) {
      io.sockets.emit('rotateY', data);
   })

   socket.on('updateIDReorganise', function(data) {
      console.log("reorganise " + id)
      if(id==2){id = 1;}
      else if(id%2 == 0){
         id = id-2;
      }else{
         if(data > id){
            id += 2;
         }
      }
      console.log("reorganise " + id)
   })

   socket.on('updateIDMove', function(data) {
      console.log("move " + id)
      if(data%2 == id%2 && id > data){id -=2;}
      console.log("move " + id)
   })

   socket.on('updateIDMirror', function() {
      console.log("mirror " + id)
      if(id > 1){
         if(id%2==1){
            id--;
         }else{
            id++;
         }
      }
      console.log("mirror " + id)
   })

   socket.on('disconnect', (reason) => {
      console.log('Screen number ' + id + ' disconnected');
      console.log(noUsers + " " + angleNext + " " + lookingRight);
      noUsers--;
      if(noUsers%2 ==0){ 
         angleNext-=angleToGo; 
         if(id%2 == 1){
            lookingRight = -lookingRight;
         }
      }else{
         lookingRight = -lookingRight;
      }
      console.log(noUsers + " " + angleNext + " " + lookingRight);

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
      io.sockets.emit('whatTime');
   });
});

http.listen(3000, function() {
   console.log('listening on localhost:3000');
});