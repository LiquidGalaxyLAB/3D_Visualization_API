const express = require('express')
const app = express()
var http = require('http').Server(app);
var io = require('socket.io')(http);

app.use(express.static('public'))


app.get('/', function(req, res) {
   res.sendfile('index.html');
});

var noUsers = 0;
var activeUser = 1;
var angleNext = 0;
var lookingRight = 1; 
io.on('connection', function(socket) {
   noUsers++;
   var id = noUsers;
   console.log('Screen number ' + noUsers + ' connected');
   socket.emit('idSet', {id: noUsers, active: noUsers==activeUser, angle: angleNext*lookingRight});
   if(noUsers%2 ==1){ angleNext+=45; }
   lookingRight = -lookingRight;
  
   if(noUsers>2){
      io.sockets.emit('whatTime');
   }

   socket.on('currentTime', function(data) {
      io.sockets.emit('setCube', data);
   })

   socket.on('updateID', function(data) {
      if(data > noUsers){id--;}
   })

   socket.on('disconnect', (reason) => {
      console.log('Screen number ' + id + ' disconnected');
      noUsers--;
      if(noUsers%2 ==0){ angleNext-=45; }
      io.sockets.emit('oneLessScreen', {noUser: id, noUsers: noUsers});
      io.sockets.emit('updateID', id);
      io.sockets.emit('whatTime');
   });
});

http.listen(3000, function() {
   console.log('listening on localhost:3000');
});