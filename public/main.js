//NEEDED VARIABLES
const API_angleToGo = 55;
var API_socket = io();
var API_id;
var API_angleCamera;
var API_positionCamera = [0,0,0];
var API_angleCameraOr;
var API_positionCameraOr = [0,0,0];
var API_stopIm;
var API_objectTransform = [];
var API_firstTimeSync;
var API_firstTime = true;
var API_width ;
var API_height;
var API_noUsers;
var API_vectorCamera;

//SET BASIC INFO OF BROWSER AND THEN INIT WINDOW
API_socket.on('idSet', function(data) {
    if(API_firstTime != false){
        API_noUsers = API_id;
        API_id = data.id;
        API_angleCamera = data.angle;
        API_angleCameraOr = data.angle;
        API_positionCamera[0] = data.x;
        API_positionCamera[2] = data.z;
        API_positionCameraOr[0] = data.x;
        API_positionCameraOr[2] = data.z;
        API_setCamera(API_positionCamera[0], 0, API_positionCamera[2]);
        console.log(API_id + " " + API_angleCamera + " " + data.x);
        API_rotateCameraAngle(API_angleCamera);
        API_stopIm = false;
        API_firstTimeSync = true;
        API_firstTime =true;
    }
    API_initWindow();
});

//FUNCTION TO RESET VARIABLES WHEN WINDOW RESIZE
API_socket.on('idReset', function(data) {
    API_id = data.id;
    API_angleCamera = data.angle;
    API_angleCameraOr = data.angle;
    API_positionCamera[0] = data.x;
    API_positionCamera[2] = data.z;
    API_positionCameraOr[0] = data.x;
    API_positionCameraOr[2] = data.z;
    API_rotateVectorInit(API_angleCamera);
    API_setCamera(API_positionCamera[0], 0, API_positionCamera[2]);
    console.log(API_id + " " + API_angleCamera + " " + data.x);
    API_rotateCameraAngle(API_angleCamera);
    API_stopIm = false;
});

//FIRST FUNCTION CALLED BY THE SERVER TO SEND THE WINDOW INFORMATION
API_socket.on('getWindowSize', function() {
    API_width  = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
    API_height = window.innerHeight|| document.documentElement.clientHeight||  document.body.clientHeight;
    API_socket.emit('windowSize', {width: API_width, height: API_height});
});

//FUNCTION FOR EVERY NEW CONNECTION SO THAT EVERYTHING CAN BE SYNCHRONISED AGAIN
API_socket.on('newWindow', function(data) {
   API_noUsers = data;
   API_firstTimeSync = true;
});

//FUNCTION TO SEND THE CURRENT INFO FOR SYNCHRONISATION FROM MASTER
API_socket.on('whatTime', function() {
    if(API_id==1){
        API_socket.emit('currentTime', {trans: API_getTranslations()});
        API_stopIm = true
        API_doNothing();
    }
});

//FUNCTION TO CONTINUE ANIMATION WHEN SYNCHRONISING
API_socket.on('start', function(data) {
    API_objectTransform = [];
    API_stopIm = false;
});

//FUNCTION TO SYNCHRONISE OBJECTS
API_socket.on('synchronise', function(data) {
    API_stopIm = true;
    API_setTranslations(data.trans);
    API_socket.emit('confirmation', API_id);
    API_doNothing();
});

//INFINITE LOOP
function API_doNothing(){
    if(API_stopIm){
        requestAnimationFrame(API_doNothing);
    }
}

//SIGNALS FOR USER CONTROL
API_socket.on('moveUp', function() {
    var biggestCamera = Math.max(camera.position.x, camera.position.y, camera.position.z);
    API_translateCamera(0, translationLinear(biggestCamera), 0)
});
API_socket.on('moveDown', function() {
    var biggestCamera = Math.max(camera.position.x, camera.position.y, camera.position.z);
    API_translateCamera(0, -translationLinear(biggestCamera), 0)
});
API_socket.on('moveLeft', function() {
    var biggestCamera = Math.max(camera.position.x, camera.position.y, camera.position.z);
    API_translateCamera(- translationLinear(biggestCamera), 0, 0)
});
API_socket.on('moveRight', function() {
    var biggestCamera = Math.max(camera.position.x, camera.position.y, camera.position.z);
    API_translateCamera(translationLinear(biggestCamera), 0, 0)
});
API_socket.on('moveForward', function() {
    var biggestCamera = Math.max(camera.position.x, camera.position.y, camera.position.z);
    API_translateCamera(0, 0, - translationLinear(biggestCamera))
});
API_socket.on('moveBackwards', function() {
    var biggestCamera = Math.max(camera.position.x, camera.position.y, camera.position.z);
    API_translateCamera(0, 0, translationLinear(biggestCamera))
});
function translationLinear(cameraParam){
    return cameraParam*0.2;
}
API_socket.on('rotateZPos', function() {
    API_rotateCameraZ(3);
});
API_socket.on('rotateZNeg', function() {
    API_rotateCameraZ(-3);
});
API_socket.on('rotateXPos', function() {
    API_rotateCameraX(3);
});
API_socket.on('rotateXNeg', function() {
    API_rotateCameraX(-3);
});
API_socket.on('rotateYPos', function() {
    API_rotateCameraY(3);
});
API_socket.on('rotateYNeg', function() {
    API_rotateCameraY(-3);
});
API_socket.on('resetCamera', function() {
    API_angleCamera = API_angleCameraOr;
    API_changeAngleCurrentToOriginalCamera(API_angleCamera);
});
API_socket.on('switchCamera', function() {
    API_callSwtich2Camera();
});

API_socket.on('moveKeySock', function(data) {
    if (data == 38) {
        API_rotateCameraX(-3);
    
    }else if (data == 40) {
    // down arrow
        API_rotateCameraX(3);
    }else if (data == 65) {
        // a key --> moving left
        API_translateCamera(-0.5, 0, 0)
    }else if (data == 68) {
        // d key --> moving right
        API_translateCamera(0.5, 0, 0)
    }else if (data == 87) {
        // w key --> moving forward
        API_translateCamera(0, 0, -0.5)
    }else if (data == 83) {
        // s key --> moving backwards
        API_translateCamera(0, 0, 0.5)
    }else if(data == 82){
        // r key --> reset
        API_angleCamera = API_angleCameraOr;
        API_changeAngleCurrentToOriginalCamera(API_angleCamera);
    }else if(data == 81){
        //q key
        API_rotateCameraZ(3);
    }else if(data == 69){
        //e key
        API_rotateCameraZ(-3);
    }else if(data == 37){
        //left arrow
        API_rotateCameraY(-3);
    }else if(data == 39){
        //right arrow
        API_rotateCameraY(3);
    }else if (data == 32) {
        // space bar
        API_translateCamera(0, 0.5, 0)
    }else if (data == 88) {
        // down arrow
        API_translateCamera(0, -0.5, 0)
    }else if (data == 72) {
        // h key
        API_showHelp();
    }else if (data == 67) {
        // c key
        API_callSwtich2Camera();
    }
});
document.onkeydown = function checkKey(e) {
    e = e.keyCode;
    API_socket.emit('moveKeySend', e);
}

//reorganisation for socket disconnections
API_socket.on('updateIDReorganiseSock', function(data) {
    if(API_id==2){
        API_id = 1;
        API_angleCamera = 0;
    }else if(API_id==1){
        API_id = 3;
        if(data.startRight){
            API_angleCamera = API_angleToGo;
        }else{
            API_angleCamera = -API_angleToGo
        }
    }
    else if(API_id%2 == 0){
        API_id = API_id-2;
        var angleAbs = Math.abs(API_angleCamera); 
        var lookingAt = (API_angleCamera/angleAbs);
        angleAbs-=API_angleToGo;
        API_angleCamera = angleAbs*lookingAt;
    }else{
        if(data.noUser > API_id){
            API_id += 2;
            var angleAbs = Math.abs(API_angleCamera); 
            var lookingAt = (API_angleCamera/angleAbs);
            angleAbs+=API_angleToGo;
            API_angleCamera = angleAbs*lookingAt;
        }
    }
    API_rotateCameraAngle(API_angleCamera);
    API_socket.emit('updateIDReorganise', data.id);
})
API_socket.on('updateIDMoveSock', function(data) {
    if(data%2 == API_id%2 && API_id > data){
        API_id -=2;
        var angleAbs = Math.abs(API_angleCamera); 
        var lookingAt = (API_angleCamera/angleAbs);
        angleAbs-=API_angleToGo;
        API_angleCamera = angleAbs*lookingAt;
        API_rotateCameraAngle(API_angleCamera);
    }
    API_socket.emit('updateIDMove', data);
})

API_socket.on('updateIDMirrorSock', function() {
    if(API_id > 1){
        if(API_id%2==1){
            API_id--;
        }else{
            API_id++;
        }
    }
    API_socket.emit('updateIDMirror');
})

//FUNCTION USED when a new server disconnects and the connects again
API_socket.on('reload', function(data) {
    if(API_id == data){
        API_socket.emit('disconnect');
        var now = new Date().getTime();
        while(new Date().getTime() < now + (1000*API_id)){ /* do nothing */ } 
        location.reload();
    }
    if(API_id-1 == data){
        API_socket.emit('serverReload')
    }
    
})

//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
//ACTUAL ANIMATION CODE---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

//Funcitons for the actual scene in browser
var scene = new THREE.Scene();
var API_constant = 5;
var API_cameraOrthographic = new THREE.OrthographicCamera(  window.innerWidth / (- window.innerWidth/API_constant),  window.innerWidth / (window.innerWidth/API_constant), window.innerHeight / (window.innerWidth/API_constant), window.innerHeight / - (window.innerWidth/API_constant), -30, 30 );
var aspectRatio = window.innerWidth / window.innerHeight;
var API_cameraPerspective = new THREE.PerspectiveCamera(75, aspectRatio, 0.1, 1000 );
var camera = API_cameraPerspective;
scene.add( camera );

var API_isOrtographic = false;
API_vectorCamera = new THREE.Vector3( 0, 0, -1 );
var API_Y_AXIS = new THREE.Vector3( 0, 1, 0 );
var API_X_AXIS = new THREE.Vector3( 1, 0, 0 );
var API_Z_AXIS = new THREE.Vector3( 0, 0, -1 );
var API_Y_AXIS_camera = new THREE.Vector3( 0, 1, 0 );
var API_X_AXIS_camera = new THREE.Vector3( 1, 0, 0 );
var API_Z_AXIS_camera = new THREE.Vector3( 0, 0, -1 );
var renderer = new THREE.WebGLRenderer();
renderer.setSize( window.innerWidth, window.innerHeight );
document.body.appendChild( renderer.domElement );

var API_historyTransformation = [];
var API_originalPositionObject = {};
var API_originalRotationObject = {};
var API_angleBef = 0;

//FUNCTION TO INIT THE ANIMATION (main for reload)
function API_initWindow(){
    if(!API_firstTime){
        API_socket.emit('newProjectServer', API_noUsers)
    }
    API_firstTime = false;
    window.addEventListener( 'resize', onWindowResize, false );
}

//Switch between orthogonal and perspective camera
function API_callSwtich2Camera() {
    var cameraPosition = camera.position.clone();
    var cameraMatrix =  camera.matrix.clone();
    if (API_isOrtographic == true) {
        API_isOrtographic = false;
        camera = API_cameraPerspective;
        camera.position.copy(cameraPosition);
        camera.matrix.copy(cameraMatrix);
        API_setCamera(API_positionCameraOr[0], 0, API_positionCameraOr[2]);
        API_rotateCameraAngle(API_angleCamera)
    } else {
        API_isOrtographic = true;
        camera = API_cameraOrthographic;
        camera.position.copy(cameraPosition);
        camera.matrix.copy(cameraMatrix);
        API_setCamera(API_positionCameraOr[0]*100, 0, API_positionCameraOr[2]);
        
    }
    camera.updateProjectionMatrix();
    renderer.render( scene, camera );
}

//Called for the different side sockets
function API_rotateCameraAngle(angleNow){
    camera.rotateOnAxis( API_Y_AXIS, degreesToRadians(-API_angleBef) );
    camera.rotateOnAxis( API_Y_AXIS, degreesToRadians(angleNow) );
    API_angleBef = angleNow
}

//API MAIN FUNTIONS
var toTranslation
function translate(object, translateX, translateY, translateZ){
    API_originalPositionObject[object.id] = {x: translateX, y: translateY, z: translateZ};
    
    toTranslation = API_executeTranslation([translateX, translateY, translateZ]); 
    object.position.x = toTranslation[0];
    object.position.y = toTranslation[1];
    object.position.z = toTranslation[2];

    if(API_id == 1 && API_objectTransform.indexOf(object.id) == -1){
        API_objectTransform.push(object.id);
    }
}
function addTranslation(object, translateX, translateY, translateZ){
    var position = getOriginalPositionObject(object)
    translate(object, position.x+translateX, position.y+translateY, position.z+translateZ);
}

function rotate(object, angleX, angleY, angleZ){
    API_originalRotationObject[object.id] = {x: angleX, y: angleY, z: angleZ};

    API_executeRotation(object, angleX, angleY, angleZ);
    if(API_id == 1 && API_objectTransform.indexOf(object.id) == -1){
        API_objectTransform.push(object.id);
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
        
    if(API_id == 1 && API_objectTransform.indexOf(object.id) == -1){
        API_objectTransform.push(object.id);
    }
}

function setColor(object, color, colorWrite, vertexColor){
    object.material.color.setHex( color );
    object.material.colorWrite = colorWrite;
    object.material.vertexColor = vertexColor;

    if(API_id == 1 && API_objectTransform.indexOf(object.id) == -1){
        API_objectTransform.push(object.id);
    }
}

//CALLED FOR SYNCHRONISATION, LIST OF INFO OF OBJECTS TO SEND
function API_getTranslations(){
    var toSend = [];
    if(API_firstTimeSync){
        var API_children = scene.children;
        for(API_i = 0; API_i< API_children.length; API_i++){
            if((API_children[API_i] instanceof THREE.Camera) == false){
                if((API_children[API_i] instanceof THREE.Points) == false){
                    if(API_children[API_i].color != undefined){
                        toSend.push({id: API_children[API_i].id, pos: getOriginalPositionObject(API_children[API_i]), 
                            rot: getOriginalRotationObject(API_children[API_i]), 
                            sca: API_children[API_i].scale, colHex: API_children[API_i].color.getHex()});
                    }else if(API_children[API_i].material.color != undefined){
                        toSend.push({id: API_children[API_i].id, pos: getOriginalPositionObject(API_children[API_i]), 
                            rot: getOriginalRotationObject(API_children[API_i]), 
                            sca: API_children[API_i].scale, colHex: API_children[API_i].material.color.getHex()});
                    }else{
                        toSend.push({id: API_children[API_i].id, pos: getOriginalPositionObject(API_children[API_i]), 
                            rot: getOriginalRotationObject(API_children[API_i]), 
                            sca: API_children[API_i].scale});
                    }
                }else{
                    if(API_children[API_i].color != undefined){
                        toSend.push({id: API_children[API_i].id, pos: getOriginalPositionObject(API_children[API_i]), 
                            rot: getOriginalRotationObject(API_children[API_i]), 
                            sca: API_children[API_i].scale, geo: API_children[API_i].geometry.attributes.position.array, 
                            colHex: API_children[API_i].color.getHex()});    
                    }else if(API_children[API_i].material.color != undefined){
                        toSend.push({id: API_children[API_i].id, pos: getOriginalPositionObject(API_children[API_i]), 
                            rot: getOriginalRotationObject(API_children[API_i]), 
                            sca: API_children[API_i].scale, geo: API_children[API_i].geometry.attributes.position.array, 
                            colHex: API_children[API_i].material.color.getHex()});    
                    }else{
                        toSend.push({id: API_children[API_i].id, pos: getOriginalPositionObject(API_children[API_i]), 
                            rot: getOriginalRotationObject(API_children[API_i]), 
                            sca: API_children[API_i].scale, geo: API_children[API_i].geometry.attributes.position.array});    
                    }
                }
            }
        }
        API_firstTimeSync=false
    }else{
        for(const id of  API_objectTransform){
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

//SET OBJECTS WHEN SYNCHRONISING TO MASTER SOCKET
function API_setTranslations(array){
    for(const object of array){
        var obj = scene.getObjectById(object.id);
        API_originalPositionObject[object.id] = object.pos
        toTranslation = API_executeTranslation([object.pos.x, object.pos.y, object.pos.z]); 
        obj.position.x = toTranslation[0];
        obj.position.y = toTranslation[1];
        obj.position.z = toTranslation[2];

        API_originalRotationObject[object.id] = object.rot;

        API_executeRotation(obj, object.rot.x, object.rot.y, object.rot.z);

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
            obj.geometry.attributes.position.needsUpdate = true;
        }
    }
}

//FUNCTION USED FOR SYNCHORNISATION, this sets the objects to the right info
// needed to maintain the order of transtaltion
var API_tempVector;
var API_angleToReverse;
var API_k;
var API_newPos = new THREE.Vector3();
function API_executeTranslation(positionToTranslate){
    copyX_axis = API_X_AXIS.clone();
    copyY_axis = API_Y_AXIS.clone();
    copyZ_axis = API_Z_AXIS.clone();
    for(API_k = 0; API_k <  API_historyTransformation.length; API_k++){
        if(API_historyTransformation[API_k].trans != null){
            if(API_historyTransformation[API_k].trans.x != null){
                positionToTranslate[0] += API_historyTransformation[API_k].trans.x;
            } if(API_historyTransformation[API_k].trans.y != null){
                positionToTranslate[1] += API_historyTransformation[API_k].trans.y;
            } if(API_historyTransformation[API_k].trans.z != null){
                positionToTranslate[2] += API_historyTransformation[API_k].trans.z;
            }
        }else if(API_historyTransformation[API_k].rot != null){
            if(API_historyTransformation[API_k].rot.x != null){
                API_tempVector = copyX_axis.clone();
                API_angleToReverse = API_historyTransformation[API_k].rot.x;
            }else if(API_historyTransformation[API_k].rot.y != null){
                API_tempVector = copyY_axis.clone();
                API_angleToReverse = API_historyTransformation[API_k].rot.y
            }else if(API_historyTransformation[API_k].rot.z != null){
                API_tempVector = copyZ_axis.clone();
                API_angleToReverse = API_historyTransformation[API_k].rot.z
            }
            API_newPos.set(positionToTranslate[0] , positionToTranslate[1] , positionToTranslate[2] );
            API_newPos.applyAxisAngle(API_tempVector, API_angleToReverse);
    
            positionToTranslate[0] = API_newPos.x;
            positionToTranslate[1] = API_newPos.y;
            positionToTranslate[2] = API_newPos.z;
        }
    }
    return positionToTranslate;
}

//Function to redo all rotations  in history of user controls
function API_executeRotation(object, angleX, angleY, angleZ){
    object.rotation.x = angleX;
    object.rotation.y = angleY;
    object.rotation.z = angleZ;
    for(API_k = 0; API_k <  API_historyTransformation.length; API_k++){
        if(API_historyTransformation[API_k].rot != null){
            if(API_historyTransformation[API_k].rot.x != null){
                object.rotateOnWorldAxis(API_X_AXIS, API_historyTransformation[API_k].rot.x)
            }else if(API_historyTransformation[API_k].rot.y != null){
                object.rotateOnWorldAxis(API_Y_AXIS, API_historyTransformation[API_k].rot.y)
            }else if(API_historyTransformation[API_k].rot.z != null){
                object.rotateOnWorldAxis(API_Z_AXIS, API_historyTransformation[API_k].rot.z);
            }
        }
    }
}

function onWindowResize() {
    API_width  = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
    API_height = window.innerHeight|| document.documentElement.clientHeight||  document.body.clientHeight;
    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();
    renderer.setSize( window.innerWidth, window.innerHeight );
    API_socAPI_ket.emit('windowResize', {width: API_width, height: API_height});
}

function degreesToRadians(degrees){
  var pi = Math.PI;
  return degrees * (pi/180);
}
function radiansToDegrees(radians){
    var pi = Math.PI;
    return radians * (180/pi);
}

//Functions for camera variables
function API_setCamera(posx, posy, posz) {
    camera.position.x = posx
    camera.position.y = posy
    camera.position.z = posz
}

//Function to get the actual parameters of position and rotation from  the master (in the other sockets
//  these would be different with user controls)
function getOriginalPositionObject(object){
    if(API_originalPositionObject[object.id] != null){
        return API_originalPositionObject[object.id];
    }else{
        return object.position;
    }
}
function getOriginalRotationObject(object){
    if(API_originalRotationObject[object.id] != null && API_originalRotationObject[object.id] != undefined){
        return API_originalRotationObject[object.id];
    }else{
        return {x: object.rotation.x, y: object.rotation.y, z: object.rotation.z};
    }
}

//Function  called when translating caemra with user controls
function API_translateCamera(positionX, positionY, positionZ){
    if(API_historyTransformation.length && API_historyTransformation[API_historyTransformation.length-1].trans != null){
        if(positionX!=0){
            if(API_historyTransformation[API_historyTransformation.length-1].trans.x != null){
                API_historyTransformation[API_historyTransformation.length-1].trans.x -= positionX;
            }else{
                API_historyTransformation[API_historyTransformation.length-1].trans.x = -positionX;
            }
        }
        if(positionY!=0){
            if(API_historyTransformation[API_historyTransformation.length-1].trans.y != null){
                API_historyTransformation[API_historyTransformation.length-1].trans.y -= positionY;
            }else{
                API_historyTransformation[API_historyTransformation.length-1].trans.y = -positionY;
            }
        }
        if(positionZ!=0){
            if(API_historyTransformation[API_historyTransformation.length-1].trans.z != null){
                API_historyTransformation[API_historyTransformation.length-1].trans.z -= positionZ;
            }else{
                API_historyTransformation[API_historyTransformation.length-1].trans.z = -positionZ;
            }
        }
    }else{
        if(positionX!=0){
            API_historyTransformation.push({trans: {x: -positionX}})
        }
        if(positionY!=0){
            API_historyTransformation.push({trans: {y: -positionY}})
        }
        if(positionZ!=0){
            API_historyTransformation.push({trans: {z: -positionZ}})
        }
    }
    API_children = scene.children;

    for(API_i = 0; API_i< API_children.length; API_i++){
        if((API_children[API_i] instanceof THREE.Camera) == false){
            if(API_originalPositionObject[API_children[API_i].id] == null){
                API_originalPositionObject[API_children[API_i].id] = {x: API_children[API_i].position.x, y: API_children[API_i].position.y, z: API_children[API_i].position.z};
            }
            API_children[API_i].position.x -= positionX;
            API_children[API_i].position.y -= positionY;
            API_children[API_i].position.z -= positionZ;
        }
    }
}

//Function  called when rotating the camera aroundaxis X with user controls
var API_children;
var API_i;
function API_rotateCameraX(angle){
    angle = degreesToRadians(angle);
    if(API_historyTransformation.length && API_historyTransformation[API_historyTransformation.length-1].rot != null && 
        API_historyTransformation[API_historyTransformation.length-1].rot.x != null){
            API_historyTransformation[API_historyTransformation.length-1].rot.x +=angle
        if(API_historyTransformation[API_historyTransformation.length-1].rot.x == 0){
            API_historyTransformation.length = API_historyTransformation.length - 1;
        }
    }else{
        API_historyTransformation.push({rot: {x: angle}})
    }

    API_children = scene.children;
    for(API_i = 0; API_i< API_children.length; API_i++){
        if((API_children[API_i] instanceof THREE.Camera) == false){
            if(API_originalPositionObject[API_children[API_i].id] == null){
                API_originalPositionObject[API_children[API_i].id] = {x: API_children[API_i].position.x, y: API_children[API_i].position.y, z: API_children[API_i].position.z};
            }
            if(API_originalRotationObject[API_children[API_i].id] == null){
                API_originalRotationObject[API_children[API_i].id] = {x: API_children[API_i].rotation.x, y: API_children[API_i].rotation.y, z: API_children[API_i].rotation.z};
            }
            API_newPos = new THREE.Vector3(API_children[API_i].position.x, API_children[API_i].position.y, API_children[API_i].position.z);

            API_newPos.applyAxisAngle(API_X_AXIS, angle);
            API_children[API_i].position.x = API_newPos.x;
            API_children[API_i].position.y = API_newPos.y;
            API_children[API_i].position.z = API_newPos.z;
            API_children[API_i].rotateOnWorldAxis( API_X_AXIS, angle );
        }
    }
    API_vectorCamera.applyAxisAngle(API_X_AXIS, angle);
    API_Y_AXIS_camera.applyAxisAngle(API_X_AXIS, angle);
    API_Z_AXIS_camera.applyAxisAngle(API_X_AXIS , angle);
}

//Function  called when rotating the camera aroundaxis Y with user controls
function API_rotateCameraY(angle){
    angle = degreesToRadians(angle);
    if(API_historyTransformation.length && API_historyTransformation[API_historyTransformation.length-1].rot != null && 
        API_historyTransformation[API_historyTransformation.length-1].rot.y != null){
            API_historyTransformation[API_historyTransformation.length-1].rot.y +=angle
        if(API_historyTransformation[API_historyTransformation.length-1].rot.y == 0){
            API_historyTransformation.length = API_historyTransformation.length - 1;
        }
    }else{
        API_historyTransformation.push({rot: {y: angle}})
    }

    API_children = scene.children;
    for(API_i = 0; API_i< API_children.length; API_i++){
        if((API_children[API_i] instanceof THREE.Camera) == false){
            if(API_originalPositionObject[API_children[API_i].id] == null){
                API_originalPositionObject[API_children[API_i].id] = {x: API_children[API_i].position.x, y: API_children[API_i].position.y, z: API_children[API_i].position.z};
            }
            if(API_originalRotationObject[API_children[API_i].id] == null){
                API_originalRotationObject[API_children[API_i].id] = {x: API_children[API_i].rotation.x, y: API_children[API_i].rotation.y, z: API_children[API_i].rotation.z};
            }
            API_newPos = new THREE.Vector3(API_children[API_i].position.x, API_children[API_i].position.y, API_children[API_i].position.z);

            API_newPos.applyAxisAngle(API_Y_AXIS, angle);
            API_children[API_i].position.x = API_newPos.x;
            API_children[API_i].position.y = API_newPos.y;
            API_children[API_i].position.z = API_newPos.z;
            API_children[API_i].rotateOnWorldAxis( API_Y_AXIS, angle );
        }
    }
    API_vectorCamera.applyAxisAngle(API_Y_AXIS, angle);
    API_X_AXIS_camera.applyAxisAngle(API_Y_AXIS, angle);
    API_Z_AXIS_camera.applyAxisAngle(API_Y_AXIS , angle);
}

//Function  called when rotating the camera around axis Z with user controls
function API_rotateCameraZ(angle){
    angle = degreesToRadians(angle);
    if(API_historyTransformation.length && API_historyTransformation[API_historyTransformation.length-1].rot != null && 
        API_historyTransformation[API_historyTransformation.length-1].rot.z != null){
            API_historyTransformation[API_historyTransformation.length-1].rot.z +=angle
        if(API_historyTransformation[API_historyTransformation.length-1].rot.z == 0){
            API_historyTransformation.length = API_historyTransformation.length - 1;
        }
    }else{
        API_historyTransformation.push({rot: {z: angle}})
    }

    API_children = scene.children;
    for(API_i = 0; API_i< API_children.length; API_i++){
        if((API_children[API_i] instanceof THREE.Camera) == false){
            if(API_originalPositionObject[API_children[API_i].id] == null){
                API_originalPositionObject[API_children[API_i].id] = {x: API_children[API_i].position.x, y: API_children[API_i].position.y, z: API_children[API_i].position.z};
            }
            if(API_originalRotationObject[API_children[API_i].id] == null){
                API_originalRotationObject[API_children[API_i].id] = {x: API_children[API_i].rotation.x, y: API_children[API_i].rotation.y, z: API_children[API_i].rotation.z};
            }
            API_newPos = new THREE.Vector3(API_children[API_i].position.x, API_children[API_i].position.y, API_children[API_i].position.z);

            API_newPos.applyAxisAngle(API_Z_AXIS, angle);
            API_children[API_i].position.x = API_newPos.x;
            API_children[API_i].position.y = API_newPos.y;
            API_children[API_i].position.z = API_newPos.z;
            API_children[API_i].rotateOnWorldAxis( API_Z_AXIS, angle );
        }
    }
    API_vectorCamera.applyAxisAngle(API_Z_AXIS, angle);
    API_X_AXIS_camera.applyAxisAngle(API_Z_AXIS, angle);
    API_Y_AXIS_camera.applyAxisAngle(API_Z_AXIS , angle);
}

//Function called in the initial settings
function API_rotateVectorInit(angle){
    API_vectorCamera = new THREE.Vector3( 0, 0, -1 );
    API_vectorCamera.applyAxisAngle(API_Y_AXIS, angle);
}

//Reset function
function API_changeAngleCurrentToOriginalCamera(angle){
    API_vectorCamera = new THREE.Vector3( 0, 0, -1 );
    API_vectorCamera.applyAxisAngle(API_Y_AXIS, degreesToRadians(angle));

    API_children = scene.children;
    for(API_i = 0; API_i< API_children.length; API_i++){
        if((API_children[API_i] instanceof THREE.Camera) == false){
            API_children[API_i].rotation.x = getOriginalRotationObject(API_children[API_i]).x;
            API_children[API_i].rotation.y = getOriginalRotationObject(API_children[API_i]).y;
            API_children[API_i].rotation.z = getOriginalRotationObject(API_children[API_i]).z;
            API_children[API_i].position.x = getOriginalPositionObject(API_children[API_i]).x;
            API_children[API_i].position.y = getOriginalPositionObject(API_children[API_i]).y;
            API_children[API_i].position.z = getOriginalPositionObject(API_children[API_i]).z;
        }
    }
    API_historyTransformation = [];
    API_Y_AXIS_camera = new THREE.Vector3( 0, 1, 0 );
    API_X_AXIS_camera = new THREE.Vector3( 1, 0, 0 );
    API_Z_AXIS_camera = new THREE.Vector3( 0, 0, -1 );
}

function API_showHelp() {
    if (API_id == 1) {
        var help = document.getElementById('help-message');
        help.style.display = help.style.display == 'block' ? 'none' : 'block';  
    }
}
