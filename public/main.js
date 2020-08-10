const angleToGo = 55;
var socket = io();
var id;
var angleCamera;
var positionCamera = [0,0,0];
var angleCameraOr;
var positionCameraOr = [0,0,0];
var stopIm;
var objectTransform = [];
var firstTimeSync;
var firstTime = true;;
var width ;
var height;
var noUsers;

var vectorCamera;

socket.on('idSet', function(data) {
    if(firstTime != false){
        noUsers = id;
        // console.log(width)
        id = data.id;
        angleCamera = data.angle;
        // angleCamera = (window.width/window.height) *100
        // angleCamera = 0
        angleCameraOr = data.angle;
        positionCamera[0] = data.x;
        positionCamera[2] = data.z;
        positionCameraOr[0] = data.x;
        positionCameraOr[2] = data.z;
        // rotateVectorInit(angleCamera);
        // translateCamera(angleCamera/2, 0,0);
        setCamera(positionCamera[0], 0, positionCamera[2]);
        console.log(id + " " + angleCamera + " " + data.x);
        
        
        rotateCameraAngle(angleCamera);
        stopIm = false;
        firstTimeSync = true;
    
        firstTime =true;
    }

    console.log("init")
    initWindow();
});

socket.on('idReset', function(data) {
    // console.log(width)
    id = data.id;
    angleCamera = data.angle;
    angleCameraOr = data.angle;
    positionCamera[0] = data.x;
    positionCamera[2] = data.z;
    positionCameraOr[0] = data.x;
    positionCameraOr[2] = data.z;
    rotateVectorInit(angleCamera);
    setCamera(positionCamera[0], 0, positionCamera[2]);
    console.log(id + " " + angleCamera + " " + data.x);
    
    rotateCameraAngle(angleCamera);
    stopIm = false;
});

socket.on('getWindowSize', function() {
    width  = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
    height = window.innerHeight|| document.documentElement.clientHeight||  document.body.clientHeight;
    socket.emit('windowSize', {width: width, height: height});
});

socket.on('newWindow', function(data) {
   noUsers = data;
   firstTimeSync = true;
});

socket.on('whatTime', function() {
if(id==1){
    // console.log("sending position ")
    socket.emit('currentTime', {trans: getTranslations()});
    stopIm = true
    doNothing();
}
});


socket.on('start', function(data) {
    // console.log("Start ");
    objectTransform = [];
    stopIm = false;
});

socket.on('setCube', function(data) {
    // console.log("setting positions");
    stopIm = true;
    setTranslations(data.trans);
    socket.emit('confirmation', id);
    doNothing();
});

function doNothing(){
    if(stopIm){
    //console.log("waiting");
    requestAnimationFrame(doNothing);
    }
}

var angleTilted = 0;

socket.on('moveUp', function(data) {
    translateCamera(0, 0.5, 0)
});
socket.on('moveDown', function(data) {
    translateCamera(0, -0.5, 0)
});
socket.on('moveLeft', function(data) {
    translateCamera(-0.5, 0, 0)
});
socket.on('moveRight', function(data) {
    translateCamera(0.5, 0, 0)
});
socket.on('moveForward', function(data) {
    translateCamera(0, 0,-0.5)
});
socket.on('moveBackwards', function(data) {
    translateCamera(0, 0, 0.5)
});

socket.on('rotateZPos', function(data) {
    rotateCameraZ(3);
});
socket.on('rotateZNeg', function(data) {
    rotateCameraZ(-3);
});
socket.on('rotateXPos', function(data) {
    rotateCameraX(3);
});
socket.on('rotateXNeg', function(data) {
    rotateCameraX(-3);
});
socket.on('rotateYPos', function(data) {
    rotateCameraY(3);
});
socket.on('rotateYNeg', function(data) {
    rotateCameraY(-3);
});

socket.on('resetCamera', function(data) {
    angleCamera = angleCameraOr;
    changeAngleCurrentToOriginalCamera(angleCamera);
});

socket.on('switchCamera', function(data) {
    callSwtich2Camera();
});

socket.on('moveKeySock', function(data) {
    // stopIm = true;
    // console.log(data)
    if (data == 38) {
    // up arrow
    rotateCameraX(-3);
    
    }else if (data == 40) {
    // down arrow
    rotateCameraX(3);
    }else if (data == 65) {
    // a key --> moving left
    translateCamera(-0.5, 0, 0)
    }else if (data == 68) {
    // d key --> moving right
    translateCamera(0.5, 0, 0)
    }else if (data == 87) {
    // w key --> moving forward
    translateCamera(0, 0, -0.5)
    }else if (data == 83) {
    // s key --> moving backwards
    translateCamera(0, 0, 0.5)
    }else if(data == 82){
    // r key --> reset
    angleCamera = angleCameraOr;
    changeAngleCurrentToOriginalCamera(angleCamera);
    }else if(data == 81){
    //q key
    rotateCameraZ(3);
    }else if(data == 69){
    //e key
    rotateCameraZ(-3);
    }else if(data == 37){
    //left arrow
    rotateCameraY(-3);
    }else if(data == 39){
    //right arrow
    rotateCameraY(3);
    }else if (data == 32) {
    // space bar
    translateCamera(0, 0.5, 0)
    }else if (data == 88) {
    // down arrow
    translateCamera(0, -0.5, 0)
    }else if (data == 72) {
        // h key
        showHelp();
    }else if (data == 67) {
        // c key
        callSwtich2Camera();
    }
    // socket.emit('confirmation', id);
});

socket.on('updateIDReorganiseSock', function(data) {
    // console.log("reorganise " + id)
    if(id==2){
    id = 1;
    angleCamera = 0;
    }
    else if(id==1){
    id = 3;
    if(data.startRight){
        angleCamera = angleToGo;
    }else{
        angleCamera = -angleToGo
    }
    }
    else if(id%2 == 0){
    id = id-2;
    var angleAbs = Math.abs(angleCamera); 
    var lookingAt = (angleCamera/angleAbs);
    angleAbs-=angleToGo;
    angleCamera = angleAbs*lookingAt;
    }else{
    if(data.noUser > id){
        id += 2;
        var angleAbs = Math.abs(angleCamera); 
        var lookingAt = (angleCamera/angleAbs);
        angleAbs+=angleToGo;
        angleCamera = angleAbs*lookingAt;
    }
    }
    rotateCameraAngle(angleCamera);
    // console.log("reorganise " + id)
    socket.emit('updateIDReorganise', data.id);
})

socket.on('updateIDMoveSock', function(data) {
    // console.log("move " + id)
    if(data%2 == id%2 && id > data){
    id -=2;
    var angleAbs = Math.abs(angleCamera); 
    var lookingAt = (angleCamera/angleAbs);
    angleAbs-=angleToGo;
    angleCamera = angleAbs*lookingAt;
    rotateCameraAngle(angleCamera);
    }
    // console.log("move " + id)
    socket.emit('updateIDMove', data);
})

socket.on('updateIDMirrorSock', function() {
    // console.log("mirror " + id)
    if(id > 1){
    if(id%2==1){
        id--;
    }else{
        id++;
    }
    }
    // console.log("mirror " + id)
    socket.emit('updateIDMirror');
})

socket.on('reload', function(data) {
    console.log("received reload " + data);
    if(id == data){
        console.log("Reload with " + id);
        socket.emit('disconnect');
        console.log(firstTime);

        var now = new Date().getTime();
        while(new Date().getTime() < now + (1000*id)){ /* do nothing */ } 
        location.reload();
    }
    if(id-1 == data){
        console.log("continue reload");
        socket.emit('serverReload')
    }
    
})

var ip;
document.onkeydown = function checkKey(e) {
    e = e.keyCode;
    // console.log("MOVING " + e);
    
    socket.emit('moveKeySend', e);
}

var mouseDown;
var mouseXBef;
var mouseYBef;
var howMuchMoved;
document.onmousedown = function checkMouse(e) {

    if(e.which != 3){
    // console.log("mouse " + e.clientX);
    mouseDown = true;
    mouseXBef = e.clientX;
    mouseYBef = e.clientY;
    }
    
}
document.onmouseup = function checkMouse() {
    // console.log("mouse up");
    mouseDown = false
}

document.onmousemove = function checkMouse(e) {
    if(mouseDown){
    var howMuch = 50;
    var a = 3;
    // console.log("mouse dragging " + e.clientX);
    if(Math.abs(e.clientX - mouseXBef) >=howMuch){
        // console.log("moving X");
        if(mouseXBef>e.clientX){
            a = -a
        }
        // socket.emit('rotateYServer', a);
        // rotateCameraY(a);
        mouseXBef = e.clientX;
    }
    if(Math.abs(e.clientY - mouseYBef) >=howMuch){
        // console.log("moving Y");
        if(mouseYBef>e.clientY){
            a = -a
        }
        // socket.emit('rotateXServer', a);
        // rotateCameraX(a);
        mouseYBef = e.clientY;
    }
    }
}


//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


var scene = new THREE.Scene();
var constant = 5;
var cameraOrthographic = new THREE.OrthographicCamera(  window.innerWidth / (- window.innerWidth/constant),  window.innerWidth / (window.innerWidth/constant), window.innerHeight / (window.innerWidth/constant), window.innerHeight / - (window.innerWidth/constant), -30, 30 );
var aspectRatio = window.innerWidth / window.innerHeight;
var cameraPerspective = new THREE.PerspectiveCamera(75, aspectRatio, 0.1, 1000 );
var camera = cameraPerspective;
scene.add( camera );

var isOrtographic = false;

vectorCamera = new THREE.Vector3( 0, 0, -1 );

var Y_AXIS = new THREE.Vector3( 0, 1, 0 );
var X_AXIS = new THREE.Vector3( 1, 0, 0 );
var Z_AXIS = new THREE.Vector3( 0, 0, -1 );

var Y_AXIS_camera = new THREE.Vector3( 0, 1, 0 );
var X_AXIS_camera = new THREE.Vector3( 1, 0, 0 );
var Z_AXIS_camera = new THREE.Vector3( 0, 0, -1 );
var renderer = new THREE.WebGLRenderer();
renderer.setSize( window.innerWidth, window.innerHeight );
document.body.appendChild( renderer.domElement );

function initWindow(){
    console.log(firstTime);
    if(!firstTime){
        // if(id==1){
            console.log("StartReload");
            socket.emit('newProjectServer', noUsers)
        // }
    }
    firstTime = false;
    window.addEventListener( 'resize', onWindowResize, false );
}

function callSwtich2Camera() {
    var cameraPosition = camera.position.clone();
    var cameraMatrix =  camera.matrix.clone();
    if (isOrtographic == true) {
        console.log("Swtiching to Perspective");
        isOrtographic = false;
        console.log("cameraPosition", cameraPosition);
        camera = cameraPerspective;
        camera.position.copy(cameraPosition);
        camera.matrix.copy(cameraMatrix);
        console.log("activeCamera", camera);
        setCamera(positionCameraOr[0], 0, positionCameraOr[2]);
        rotateCameraAngle(angleCamera)
    } else {
        console.log("Swtiching to Ortographic");
        isOrtographic = true;
        console.log("cameraPosition", cameraPosition);
        camera = cameraOrthographic;
        camera.position.copy(cameraPosition);
        camera.matrix.copy(cameraMatrix);
        console.log("activeCamera", camera);
        setCamera(positionCameraOr[0]*100, 0, positionCameraOr[2]);
        
    }
    camera.updateProjectionMatrix();
    renderer.render( scene, camera );
}

var historyTransformation = [];
var originalPositionObject = {};
var originalRotationObject = {};

var angleBef = 0;

function rotateCameraAngle(angleNow){
    camera.rotateOnAxis( Y_AXIS, degreesToRadians(-angleBef) );
    camera.rotateOnAxis( Y_AXIS, degreesToRadians(angleNow) );
    angleBef = angleNow
}

var toTranslation
function translate(object, translateX, translateY, translateZ){
    originalPositionObject[object.id] = {x: translateX, y: translateY, z: translateZ};
    
    toTranslation = executeTranslation([translateX, translateY, translateZ]); 
    object.position.x = toTranslation[0];
    object.position.y = toTranslation[1];
    object.position.z = toTranslation[2];

    if(id == 1 && objectTransform.indexOf(object.id) == -1){
        objectTransform.push(object.id);
    }
}

function addTranslation(object, translateX, translateY, translateZ){
    var position = getOriginalPositionObject(object)
    translate(object, position.x+translateX, position.y+translateY, position.z+translateZ);
}


function rotate(object, angleX, angleY, angleZ){
    originalRotationObject[object.id] = {x: angleX, y: angleY, z: angleZ};

    executeRotation(object, angleX, angleY, angleZ);
    if(id == 1 && objectTransform.indexOf(object.id) == -1){
        objectTransform.push(object.id);
    }
}

function addRotation(object, angleX, angleY, angleZ){
    var rotation = getOriginalRotationObject(object)
    rotate(object, rotation.x+ angleX, rotation.y + angleY, rotation.z + angleZ);
}

function scale(object, scaleX, scaleY, scaleZ){
    object.scale.x = scaleX;
    object.scale.y = scaleY;
    object.scale.z = scaleZ;
        
    if(id == 1 && objectTransform.indexOf(object.id) == -1){
        objectTransform.push(object.id);
    }
}

function setColor(object, color, colorWrite, vertexColor){
    object.material.color.setHex( color );
    object.material.colorWrite = colorWrite;
    object.material.vertexColor = vertexColor;

    if(id == 1 && objectTransform.indexOf(object.id) == -1){
        objectTransform.push(object.id);
    }
}

function getTranslations(){
    var toSend = [];
    if(firstTimeSync){
        console.log("synchronising first");
        var children = scene.children;

        
        for(i = 0; i< children.length; i++){
            console.log(children[i].material);
            if((children[i] instanceof THREE.Camera) == false){
                if((children[i] instanceof THREE.Points) == false){
                    if(children[i].color != undefined){
                        toSend.push({id: children[i].id, pos: getOriginalPositionObject(children[i]), rot: getOriginalRotationObject(children[i]), 
                            sca: children[i].scale, colHex: children[i].color.getHex()});
                    }else if(children[i].material.color != undefined){
                        toSend.push({id: children[i].id, pos: getOriginalPositionObject(children[i]), rot: getOriginalRotationObject(children[i]), 
                            sca: children[i].scale, colHex: children[i].material.color.getHex()});
                    }else{
                        toSend.push({id: children[i].id, pos: getOriginalPositionObject(children[i]), rot: getOriginalRotationObject(children[i]), 
                            sca: children[i].scale});
                    }
                    
                }else{
                    if(children[i].color != undefined){
                        toSend.push({id: children[i].id, pos: getOriginalPositionObject(children[i]), rot: getOriginalRotationObject(children[i]), 
                            sca: children[i].scale, geo: children[i].geometry.attributes.position.array, colHex: children[i].color.getHex()});    
                    }else if(children[i].material.color != undefined){
                        toSend.push({id: children[i].id, pos: getOriginalPositionObject(children[i]), rot: getOriginalRotationObject(children[i]), 
                            sca: children[i].scale, geo: children[i].geometry.attributes.position.array, colHex: children[i].material.color.getHex()});    
                    }else{
                        toSend.push({id: children[i].id, pos: getOriginalPositionObject(children[i]), rot: getOriginalRotationObject(children[i]), 
                            sca: children[i].scale, geo: children[i].geometry.attributes.position.array});    
                    }
                }
            }
        }
        firstTimeSync=false
    }else{
        for(const id of  objectTransform){
            if((scene.getObjectById(id) instanceof THREE.Points) == false){
                if(scene.getObjectById(id).color != undefined){
                    toSend.push({id: id, pos: getOriginalPositionObject(scene.getObjectById(id)), 
                        rot: getOriginalRotationObject(scene.getObjectById(id)), sca: scene.getObjectById(id).scale,  
                        colHex: scene.getObjectById(id).color.getHex()});  
                }else if(scene.getObjectById(id).material.color != undefined){
                    toSend.push({id: id, pos: getOriginalPositionObject(scene.getObjectById(id)), 
                        rot: getOriginalRotationObject(scene.getObjectById(id)), sca: scene.getObjectById(id).scale,  
                        colHex: scene.getObjectById(id).material.color.getHex()});    
                }else{
                    toSend.push({id: id, pos: getOriginalPositionObject(scene.getObjectById(id)), 
                        rot: getOriginalRotationObject(scene.getObjectById(id)), sca: scene.getObjectById(id).scale});
                }
            }else{
                if(scene.getObjectById(id).color != undefined){
                    toSend.push({id: id, pos: getOriginalPositionObject(scene.getObjectById(id)), 
                        rot: getOriginalRotationObject(scene.getObjectById(id)), sca: scene.getObjectById(id).scale, 
                        geo: scene.getObjectById(id).geometry.attributes.position.array, 
                        colHex: scene.getObjectById(id).color.getHex()});
                }else if(scene.getObjectById(id).material.color != undefined){
                    toSend.push({id: id, pos: getOriginalPositionObject(scene.getObjectById(id)), 
                        rot: getOriginalRotationObject(scene.getObjectById(id)), sca: scene.getObjectById(id).scale, 
                        geo: scene.getObjectById(id).geometry.attributes.position.array, 
                        colHex: scene.getObjectById(id).material.color.getHex()});   
                }else{
                    toSend.push({id: id, pos: getOriginalPositionObject(scene.getObjectById(id)), 
                        rot: getOriginalRotationObject(scene.getObjectById(id)), sca: scene.getObjectById(id).scale, 
                        geo: scene.getObjectById(id).geometry.attributes.position.array});
                }
            }
        }
    }
    
    return toSend;
}

function setTranslations(array){
    for(const object of array){
        // console.log(object)
        var obj = scene.getObjectById(object.id);
        // console.log(obj)
        originalPositionObject[object.id] = object.pos
        toTranslation = executeTranslation([object.pos.x, object.pos.y, object.pos.z]); 
        obj.position.x = toTranslation[0];
        obj.position.y = toTranslation[1];
        obj.position.z = toTranslation[2];

        originalRotationObject[object.id] = object.rot;

        executeRotation(obj, object.rot.x, object.rot.y, object.rot.z);

        obj.scale.x = object.sca.x;
        obj.scale.y = object.sca.y;
        obj.scale.z = object.sca.z;

        if(obj.color != undefined){
            obj.color.setHex(object.colHex)
        }else if(obj.material.color != undefined){
            obj.material.color.setHex(object.colHex)
        }
        if((obj instanceof THREE.Points) == true){
            var positions = obj.geometry.attributes.position.array;
            for(var i = 0; i<positions.length; i++){
                positions[i] = object.geo[i]; 
            }
            // obj.geometry.attributes.position.array = object.geo;
            obj.geometry.attributes.position.needsUpdate = true;
        }
    }
}

var tempVector;
var angleToReverse;
var k;
var newPos = new THREE.Vector3();
function executeTranslation(positionToTranslate){
    // console.log(object);
    copyX_axis = X_AXIS.clone();
    copyY_axis = Y_AXIS.clone();
    copyZ_axis = Z_AXIS.clone();
    for(k = 0; k <  historyTransformation.length; k++){
        if(historyTransformation[k].trans != null){
            if(historyTransformation[k].trans.x != null){
                positionToTranslate[0] += historyTransformation[k].trans.x;
            } if(historyTransformation[k].trans.y != null){
                // console.log("herereeere")

                positionToTranslate[1] += historyTransformation[k].trans.y;
            } if(historyTransformation[k].trans.z != null){
                positionToTranslate[2] += historyTransformation[k].trans.z;
            }
        }else if(historyTransformation[k].rot != null){
            if(historyTransformation[k].rot.x != null){
                tempVector = copyX_axis.clone();
                angleToReverse = historyTransformation[k].rot.x;
            }else if(historyTransformation[k].rot.y != null){
                tempVector = copyY_axis.clone();
                angleToReverse = historyTransformation[k].rot.y
            }else if(historyTransformation[k].rot.z != null){
                tempVector = copyZ_axis.clone();
                angleToReverse = historyTransformation[k].rot.z
            }
    
            newPos.set(positionToTranslate[0] , positionToTranslate[1] , positionToTranslate[2] );
            newPos.applyAxisAngle(tempVector, angleToReverse);
    
            positionToTranslate[0] = newPos.x;
            positionToTranslate[1] = newPos.y;
            positionToTranslate[2] = newPos.z;
        }
        
    }
    return positionToTranslate;
}

function executeRotation(object, angleX, angleY, angleZ){
    object.rotation.x = angleX;
    object.rotation.y = angleY;
    object.rotation.z = angleZ;
    for(k = 0; k <  historyTransformation.length; k++){
        if(historyTransformation[k].rot != null){
            if(historyTransformation[k].rot.x != null){
                object.rotateOnWorldAxis(X_AXIS, historyTransformation[k].rot.x)
            }else if(historyTransformation[k].rot.y != null){
                object.rotateOnWorldAxis(Y_AXIS, historyTransformation[k].rot.y)
            }else if(historyTransformation[k].rot.z != null){
                object.rotateOnWorldAxis(Z_AXIS, historyTransformation[k].rot.z);
            }
        }
    }
}


function onWindowResize() {
    width  = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
    height = window.innerHeight|| document.documentElement.clientHeight||  document.body.clientHeight;
    var aspectRatio = width / height;
    console.log(width)
    console.log(height)
    console.log(aspectRatio)
    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();
    renderer.setSize( window.innerWidth, window.innerHeight );
    socket.emit('windowResize', {width: width, height: height});
}

function degreesToRadians(degrees){
  var pi = Math.PI;
  return degrees * (pi/180);
}
function radiansToDegrees(radians){
    var pi = Math.PI;
    return radians * (180/pi);
}


function setCamera(posx, posy, posz) {
    camera.position.x = posx
    camera.position.y = posy
    camera.position.z = posz
}
function moveCamera(posx, posy, posz) {
    camera.position.x += posx
    camera.position.y += posy
    camera.position.z += posz
}

function getOriginalPositionObject(object){
    if(originalPositionObject[object.id] != null){
        return originalPositionObject[object.id];
    }else{
        return object.position;
    }
}

function getOriginalRotationObject(object){
    if(originalRotationObject[object.id] != null && originalRotationObject[object.id] != undefined){
        return originalRotationObject[object.id];
    }else{
        // console.log("no rotation")
        return {x: object.rotation.x, y: object.rotation.y, z: object.rotation.z};
    }
}

function translateCamera(positionX, positionY, positionZ){
    // console.log(historyTransformation);

    if(historyTransformation.length && historyTransformation[historyTransformation.length-1].trans != null){
        if(positionX!=0){
            if(historyTransformation[historyTransformation.length-1].trans.x != null){
                historyTransformation[historyTransformation.length-1].trans.x -= positionX;
            }else{
                historyTransformation[historyTransformation.length-1].trans.x = -positionX;
            }
        }
        if(positionY!=0){
            if(historyTransformation[historyTransformation.length-1].trans.y != null){
                historyTransformation[historyTransformation.length-1].trans.y -= positionY;
            }else{
                historyTransformation[historyTransformation.length-1].trans.y = -positionY;
            }
        }
        if(positionZ!=0){
            if(historyTransformation[historyTransformation.length-1].trans.z != null){
                historyTransformation[historyTransformation.length-1].trans.z -= positionZ;
            }else{
                historyTransformation[historyTransformation.length-1].trans.z = -positionZ;
            }
        }
    }else{
        if(positionX!=0){
            console.log("x")
            historyTransformation.push({trans: {x: -positionX}})
        }
        if(positionY!=0){
            console.log("y")
            historyTransformation.push({trans: {y: -positionY}})
        }
        if(positionZ!=0){
            console.log("z")
            historyTransformation.push({trans: {z: -positionZ}})
        }
    }
    // console.log(historyTransformation);

    children = scene.children;

    for(i = 0; i< children.length; i++){
        if((children[i] instanceof THREE.Camera) == false){

            if(originalPositionObject[children[i].id] == null){
                originalPositionObject[children[i].id] = {x: children[i].position.x, y: children[i].position.y, z: children[i].position.z};
            }
            
            children[i].position.x -= positionX;
            children[i].position.y -= positionY;
            children[i].position.z -= positionZ;
        }
    }


}

var newPos
var children;
var i;
function rotateCameraX(angle){
    angle = degreesToRadians(angle);
    if(historyTransformation.length && historyTransformation[historyTransformation.length-1].rot != null && 
        historyTransformation[historyTransformation.length-1].rot.x != null){
            historyTransformation[historyTransformation.length-1].rot.x +=angle
        if(historyTransformation[historyTransformation.length-1].rot.x == 0){
            historyTransformation.length = historyTransformation.length - 1;
        }
    }else{
        historyTransformation.push({rot: {x: angle}})
    }

    children = scene.children;
    // console.log(children);
    for(i = 0; i< children.length; i++){
        if((children[i] instanceof THREE.Camera) == false){
            if(originalPositionObject[children[i].id] == null){
                originalPositionObject[children[i].id] = {x: children[i].position.x, y: children[i].position.y, z: children[i].position.z};
            }
            if(originalRotationObject[children[i].id] == null){
                originalRotationObject[children[i].id] = {x: children[i].rotation.x, y: children[i].rotation.y, z: children[i].rotation.z};
            }
            newPos = new THREE.Vector3(children[i].position.x, children[i].position.y, children[i].position.z);

            newPos.applyAxisAngle(X_AXIS, angle);
            children[i].position.x = newPos.x;
            children[i].position.y = newPos.y;
            children[i].position.z = newPos.z;

            children[i].rotateOnWorldAxis( X_AXIS, angle );
        }
    }

    vectorCamera.applyAxisAngle(X_AXIS, angle);
    Y_AXIS_camera.applyAxisAngle(X_AXIS, angle);
    Z_AXIS_camera.applyAxisAngle(X_AXIS , angle);
}

function rotateCameraY(angle){
    angle = degreesToRadians(angle);
    if(historyTransformation.length && historyTransformation[historyTransformation.length-1].rot != null && 
        historyTransformation[historyTransformation.length-1].rot.y != null){
            historyTransformation[historyTransformation.length-1].rot.y +=angle
        if(historyTransformation[historyTransformation.length-1].rot.y == 0){
            historyTransformation.length = historyTransformation.length - 1;
        }
    }else{
        historyTransformation.push({rot: {y: angle}})
    }

    children = scene.children;
    for(i = 0; i< children.length; i++){

        if((children[i] instanceof THREE.Camera) == false){
            if(originalPositionObject[children[i].id] == null){
                originalPositionObject[children[i].id] = {x: children[i].position.x, y: children[i].position.y, z: children[i].position.z};
            }
            if(originalRotationObject[children[i].id] == null){
                originalRotationObject[children[i].id] = {x: children[i].rotation.x, y: children[i].rotation.y, z: children[i].rotation.z};
            }

            newPos = new THREE.Vector3(children[i].position.x, children[i].position.y, children[i].position.z);
            newPos.applyAxisAngle(Y_AXIS, angle);
            children[i].position.x = newPos.x;
            children[i].position.y = newPos.y;
            children[i].position.z = newPos.z;

            children[i].rotateOnWorldAxis( Y_AXIS, angle );

        }
    }

    vectorCamera.applyAxisAngle(Y_AXIS, angle);
    X_AXIS_camera.applyAxisAngle(Y_AXIS, angle);
    Z_AXIS_camera.applyAxisAngle(Y_AXIS, angle);
}

function rotateCameraZ(angle){
    angle = degreesToRadians(angle);
    if(historyTransformation.length && historyTransformation[historyTransformation.length-1].rot != null && 
        historyTransformation[historyTransformation.length-1].rot.z != null){
            historyTransformation[historyTransformation.length-1].rot.z +=angle
        if(historyTransformation[historyTransformation.length-1].rot.z == 0){
            historyTransformation.length = historyTransformation.length - 1;
        }
    }else{
        historyTransformation.push({rot: {z: angle}})
    }

    children = scene.children;
    // console.log(children[0].position);
    for(i = 0; i< children.length; i++){
        if((children[i] instanceof THREE.Camera) == false){
            if(originalPositionObject[children[i].id] == null){
                originalPositionObject[children[i].id] = {x: children[i].position.x, y: children[i].position.y, z: children[i].position.z};
            }
            if(originalRotationObject[children[i].id] == null){
                originalRotationObject[children[i].id] = {x: children[i].rotation.x, y: children[i].rotation.y, z: children[i].rotation.z};
            }
            newPos = new THREE.Vector3(children[i].position.x, children[i].position.y, children[i].position.z);
            newPos.applyAxisAngle(Z_AXIS, angle);

            children[i].position.x = newPos.x;
            children[i].position.y = newPos.y;
            children[i].position.z = newPos.z;

            children[i].rotateOnWorldAxis ( Z_AXIS, angle );
        }
    }

    vectorCamera.applyAxisAngle(Z_AXIS, angle);
    X_AXIS_camera.applyAxisAngle(Z_AXIS, angle);
    Y_AXIS_camera.applyAxisAngle(Z_AXIS, angle);
}

function rotateVectorInit(angle){
    vectorCamera = new THREE.Vector3( 0, 0, -1 );
    vectorCamera.applyAxisAngle(Y_AXIS, angle);
}

var angleTwoBefore; var copyX_axis_camera;  var copyY_axis_camera;  var copyZ_axis_camera; 
function changeAngleCurrentToOriginalCamera(angle, centerPosition){
    vectorCamera = new THREE.Vector3( 0, 0, -1 );
    vectorCamera.applyAxisAngle(Y_AXIS, degreesToRadians(angle));

    children = scene.children;
    for(i = 0; i< children.length; i++){
        if((children[i] instanceof THREE.Camera) == false){
            children[i].rotation.x = getOriginalRotationObject(children[i]).x;
            children[i].rotation.y = getOriginalRotationObject(children[i]).y;
            children[i].rotation.z = getOriginalRotationObject(children[i]).z;
            children[i].position.x = getOriginalPositionObject(children[i]).x;
            children[i].position.y = getOriginalPositionObject(children[i]).y;
            children[i].position.z = getOriginalPositionObject(children[i]).z;
        }
    }
    historyTransformation = [];
    Y_AXIS_camera = new THREE.Vector3( 0, 1, 0 );
    X_AXIS_camera = new THREE.Vector3( 1, 0, 0 );
    Z_AXIS_camera = new THREE.Vector3( 0, 0, -1 );
}

function showHelp() {
    if (id == 1) {
        var help = document.getElementById('help-message');
        help.style.display = help.style.display == 'block' ? 'none' : 'block';  
    }
}
