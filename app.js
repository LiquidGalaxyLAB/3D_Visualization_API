const express = require('express')
const app = express()
var http = require('http').Server(app);
var io = require('socket.io')(http);

app.use(express.static('public'))


app.get('/', function(req, res) {
   res.sendfile('index.html');
   res.redirect('http://localhost:3000/');
});
app.get('/test', function(req, res) {
   res.send({json: 'sample'});
});
var angleToGo;
const separation = 0.1
var noUsers = 0;
var activeUser = 1;
var angleNext = 0;
var lookingRight = 1; 
var startRight = true;
var receivedConfirmation = [];
io.on('connection', function(socket) {
   var id;
   console.log('New Connection');
   
   socket.emit('getWindowSize');

   socket.on('windowSize', function(data) {

      noUsers++;
      id = noUsers;

      socket.join('Window');
      var os = require('os');
      var ifaces = os.networkInterfaces();

      Object.keys(ifaces).forEach(function (ifname) {
         var alias = 0;

         ifaces[ifname].forEach(function (iface) {
            if ('IPv4' !== iface.family || iface.internal !== false) {
               // skip over internal (i.e. 127.0.0.1) and non-ipv4 addresses
               return;
            }

            if (alias >= 1) {
               // this single interface has multiple ipv4 addresses
               console.log(ifname + ':' + alias, iface.address);

            } else {
               // this interface has only one ipv4 adress
               console.log('hey', ifname, iface.address);

               angleToGo = (data.width - 500)/12.0357 + 54;
               var angleThisSocket = Math.floor((id/2))*angleToGo;
               if(id%2 == 0){
                  angleThisSocket = -angleThisSocket;
               }
               // console.log(lookingRight)
               console.log('Screen number ' + noUsers + ' connected with id ' + id);
               socket.emit('idSet', {id: id, active: id==activeUser, angle: angleThisSocket, 
                                    x: separation * (-(angleThisSocket)/angleToGo),
                                    z: 0, ip: iface.address});
            }
            ++alias;
         });
      });

      

      // if(noUsers%2 == 1){ angleNext+=angleToGo; }
      // lookingRight = -lookingRight;
      
      const interval = setInterval(function() {
         // console.log("synchronising")
         io.sockets.to('Window').emit('whatTime');
      }, 5000);
   })

   socket.on('windowResize', function(data) {

      angleToGo = (data.width - 500)/12.0357 + 54;
      var angleThisSocket = Math.floor((id/2))*angleToGo;
      if(id%2 == 0){
         angleThisSocket = -angleThisSocket;
      }
      // console.log(lookingRight)
      console.log(angleToGo + " " + (id%2) + " " + Math.floor((id/2)) + " " +angleThisSocket);
      socket.emit('idReset', {id: id, active: id==activeUser, angle: angleThisSocket, 
                           x: separation * (-(angleThisSocket)/angleToGo),
                           z: 0});

   })

   socket.on('currentTime', function(data) {
      // console.log("receive one " + receivedConfirmation)
      io.sockets.to('Window').emit('setCube', data);
   })

   socket.on('confirmation', function(data) {
      // console.log("confirmation " + receivedConfirmation + " " + data)
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

   socket.on('signalTablet', function() {
      console.log("It's a tablet")
      socket.join('Tablet');
   })

   socket.on('disconnect', (reason) => {
      console.log('Screen number ' + id + ' disconnected and there are ' + noUsers + ' users');
      // console.log(noUsers + " " + angleNext + " " + lookingRight);
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
      // io.sockets.emit('whatTime');
   });
});

http.listen(3000, function() {
   console.log('listening on localhost:3000');
});