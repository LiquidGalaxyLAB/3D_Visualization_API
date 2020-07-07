var vectorCamera;
var firstTime = true;

var scene = new THREE.Scene();
var camera = new THREE.PerspectiveCamera( 75, window.innerWidth / window.innerHeight, 0.1, 1000 );
var Y_AXIS = new THREE.Vector3( 0, 1, 0 );
var X_AXIS = new THREE.Vector3( 1, 0, 0 );
var Z_AXIS = new THREE.Vector3( 0, 0, -1 );

var Y_AXIS_camera = new THREE.Vector3( 0, 1, 0 );
var X_AXIS_camera = new THREE.Vector3( 1, 0, 0 );
var Z_AXIS_camera = new THREE.Vector3( 0, 0, -1 );
var renderer = new THREE.WebGLRenderer();
renderer.setSize( window.innerWidth, window.innerHeight );
document.body.appendChild( renderer.domElement );

var lights = [];
lights[ 0 ] = new THREE.PointLight( 0xffffff, 1, 0 );
lights[ 1 ] = new THREE.PointLight( 0xffffff, 1, 0 );
lights[ 2 ] = new THREE.PointLight( 0xffffff, 1, 0 );

lights[ 0 ].position.set( 0, 10, 0 );
lights[ 1 ].position.set( 10, 20, 10 );
lights[ 2 ].position.set( - 10, - 20, - 10 );

scene.add( lights[ 0 ] );
scene.add( lights[ 1 ] );
scene.add( lights[ 2 ] );

var geometry = new THREE.BoxGeometry();
var material = new THREE.MeshPhongMaterial( { color: 0x156289, emissive: 0xff2200, side: THREE.DoubleSide, flatShading: true } );
var cube1 = new THREE.Mesh( geometry, material );
var cube2 = new THREE.Mesh( geometry, material );
var cube3 = new THREE.Mesh( geometry, material );

var materialLine = new THREE.LineBasicMaterial({
    color: 0x0000ff
});

var points = [];
var length = 20; var step = 1; var cornerZ = length/2; var cornerX = -(length/2); howManyCells = 20/0.2; var heightGrid = -2;
for(var i=0; i< howManyCells; i = i+2){
    points.push( new THREE.Vector3( cornerX + (0.2*i), heightGrid, cornerZ ) );
    points.push( new THREE.Vector3( cornerX + (0.2*i), heightGrid, cornerZ - length ) );
    points.push( new THREE.Vector3( cornerX + (0.2*(i+1)), heightGrid, cornerZ - length ) );
    points.push( new THREE.Vector3( cornerX + (0.2*(i+1)), heightGrid, cornerZ ) );
}
points.push( new THREE.Vector3( cornerX + (0.2*howManyCells), heightGrid, cornerZ ) );
for(var i=0; i< howManyCells; i = i+2){
    points.push( new THREE.Vector3( cornerX +length, heightGrid, cornerZ - length + (0.2*i) ) );
    points.push( new THREE.Vector3( cornerX , heightGrid, cornerZ - length + (0.2*i)) );
    points.push( new THREE.Vector3( cornerX, heightGrid, cornerZ - length + (0.2*(i+1))) );
    points.push( new THREE.Vector3( cornerX + length, heightGrid, cornerZ - length + (0.2*(i+1))) );
}


var geometryLine = new THREE.BufferGeometry().setFromPoints( points );
var line = new THREE.Line( geometryLine, materialLine );

var materialAxis = new THREE.LineBasicMaterial({
    color: 0x00ffff
});
var geometryAxis = new THREE.BufferGeometry().setFromPoints( [new THREE.Vector3(-10,-1.8, 0 ), new THREE.Vector3(10,-1.8, 0 )] );
var axis = new THREE.Line( geometryAxis, materialAxis );

var geometryCyl = new THREE.CylinderGeometry( 1, 1, 4, 32 );
var materialCyl = new THREE.MeshBasicMaterial( {color: 0xffff00} );
var cylinder = new THREE.Mesh( geometryCyl, materialCyl );

cube1.position.x = 0;
cube1.position.z = -5;
cube1.position.y = 0;
cube2.position.x = 6;
cube2.position.z = -5;
cube2.position.y = 0;
cube3.position.x = -6;
cube3.position.z = -5;
cube3.position.y = 0;
cylinder.position.x = 0;
cylinder.position.z = -10;
cylinder.position.y = 0;

axis.position.z = -10;

function init(){
    if(!firstTime){
        socket.emit('disconnect');
        location.reload();
    }
    firstTime = false;  
     scene.add( camera );
     scene.add( cube1 );
     scene.add( cube2 );
     scene.add( cube3 );
     scene.add( cylinder );
     scene.add(line);
     scene.add(axis);

    if(rotateCamera == null){
        rotateCamera = false
    }if(angleCamera == null){
        angleCamera = false
    }
    
    window.addEventListener( 'resize', onWindowResize, false );
    
    // cube.rotateZ(20*(Math.PI/180))
    howLong = 0;
    // console.log(camera.getFilmWidth());
}

var historyTransformation = [];
var originalPositionObject = {};
var originalRotationObject = {};

var angleBef = 0;
var animate = function () {
    requestAnimationFrame( animate );

    if(!stopIm){
        if(rotateCamera){
            camera.rotateOnAxis( Y_AXIS, degreesToRadians(-angleBef) );
            camera.rotateOnAxis( Y_AXIS, degreesToRadians(angleCamera) );
            // X_AXIS_camera.applyAxisAngle(Y_AXIS, degreesToRadians(angleCamera));
            rotateCamera = false;
            angleBef = angleCamera
        }
        
        var rotation = getOriginalRotationObject(cylinder)
        rotate(cylinder, rotation.x+ degreesToRadians(2), rotation.y, rotation.z);
        var position = getOriginalPositionObject(cylinder)
        addTranslation(cylinder, 0.05, 0, 0);
        

        if(getOriginalPositionObject(cylinder).x > 10){
            var position = getOriginalPositionObject(cylinder)
            translate(cylinder, -position.x, position.y, position.z);
        } 
        renderer.render( scene, camera );
    }
};
animate();

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
    var position = getOriginalPositionObject(cylinder)
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

function getTranslations(){
    var toSend = [];
    for(const id of  objectTransform){
        toSend.push({id: id, pos: getOriginalPositionObject(scene.getObjectById(id)), rot: getOriginalRotationObject(scene.getObjectById(id)), sca: scene.getObjectById(id).scale});
    }
    return toSend;
}

function setTranslations(array){
    for(const object of array){
        var obj = scene.getObjectById(object.id);

        originalPositionObject[object.id] = object.pos
        toTranslation = executeTranslation([object.pos.x, object.pos.y, object.pos.z]); 
        obj.position.x = toTranslation[0];
        obj.position.y = toTranslation[1];
        obj.position.z = toTranslation[2];

        originalRotationObject[object.id] = object.rot
        executeRotation(obj, object.rot._x, object.rot._y, object.rot._z);

        obj.scale.x = object.sca.x;
        obj.scale.y = object.sca.y;
        obj.scale.z = object.sca.z;
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

function getCubePosition(){
    return cube1.position;
}
function setCubePosition(position){
    cube1.position.x = position.x;
    cube1.position.y = position.y;
    cube1.position.z = position.z;
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
    if(originalRotationObject[object.id] != null){
        return originalRotationObject[object.id];
    }else{
        return object.rotation;
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
