var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);

app.get('/', function(req, res) {
   res.sendfile('index.html');
});

users = [];
noUsers = 0;
activeUser = 1;
io.on('connection', function(socket) {
   noUsers++;
   var id = noUsers;
   console.log('Screen number ' + noUsers + ' connected');
   socket.emit('idSet', {id: noUsers, active: noUsers==activeUser});

   socket.on('moveObject', function(data) {
     activeUser++;
     if(activeUser>noUsers) activeUser = 1;
    //Send message to everyone
    io.sockets.emit('nextScreen', activeUser);
 })
 socket.on('disconnect', (reason) => {
    console.log('Screen number ' + noUsers + ' disconnected');
    noUsers--;
    if(activeUser>noUsers) activeUser = 1;
    io.sockets.emit('oneLessScreen', {noUser: id, active: activeUser});
  });
});

http.listen(3000, function() {
   console.log('listening on localhost:3000');
});