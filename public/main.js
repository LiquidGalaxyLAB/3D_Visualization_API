var vectorCamera;
var firstTime = true;
const width  = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
const height = window.innerHeight|| document.documentElement.clientHeight||  document.body.clientHeight;

var scene = new THREE.Scene();
var camera = new THREE.PerspectiveCamera( 75, window.innerWidth / window.innerHeight, 0.1, 1000 );
var Y_AXIS = new THREE.Vector3( 0, 1, 0 );
var X_AXIS = new THREE.Vector3( 1, 0, 0 );
var Z_AXIS = new THREE.Vector3( 0, 0, 1 );

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

var geometryCyl = new THREE.CylinderGeometry( 1, 1, 4, 32 );
var materialCyl = new THREE.MeshBasicMaterial( {color: 0xffff00} );
var cylinder = new THREE.Mesh( geometryCyl, materialCyl );

function init(){
    console.log(firstTime);
    if(!firstTime){
        console.log("herr")
        location.reload();
    }
    firstTime = false;  
     scene.add( camera );
     scene.add( cube1 );
     scene.add( cube2 );
     scene.add( cube3 );
     scene.add( cylinder );
     scene.add(line)

    if(rotateCamera == null){
        rotateCamera = false
    }if(angleCamera == null){
        angleCamera = false
    }
    
    window.addEventListener( 'resize', onWindowResize, false );
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
    // cube.rotateZ(20*(Math.PI/180))
    howLong = 0;
}

var historyRotations = []
var originalPositionObject = {};
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
        // cube.position.x += 0.05;

        // translate(cube, cube.position.x+0.02, cube.position.y, cube.position.z);
        // scale(cube, cube.scale.x, cube.scale.y+0.02, cube.scale.z);
        // // console.log(stopIm)
        rotate(cylinder, cylinder.rotation.x+ degreesToRadians(2), cylinder.rotation.y, cylinder.rotation.z);
        var position = getOriginalPositionObject(cylinder)
        translate(cylinder, position.x+0.05, position.y, position.z);
        // addTranslation(cylinder, 0.05, 0, 0);
        

        if(getOriginalPositionObject(cylinder).x > 10){
            var position = getOriginalPositionObject(cylinder)
            translate(cylinder, -position.x, position.y, position.z);
            // addTranslation(cylinder, -10, 0, 0);
        }

        // if(cube.position.x>6){
        //     // cube.position.x = -cube.position.x;
        //     translate(cube, -cube.position.x, cube.position.y, cube.position.z);
        // }if(cube.scale.y>6){
        //     // cube.position.x = -cube.position.x;
        //     scale(cube, cube.scale.x, -cube.scale.y, cube.scale.z);
        // }
        
        renderer.render( scene, camera );
    }
};
animate();

var toTranslation
function translate(object, translateX, translateY, translateZ){

    copyX = X_AXIS_camera.clone();
    copyX.setLength(translateX - originalPositionObject[object.id]);
    copyY = Y_AXIS_camera.clone();
    copyY.setLength(translateY - originalPositionObject[object.id]);
    copyZ = Z_AXIS_camera.clone();
    copyZ.negate();
    copyZ.setLength(translateZ - originalPositionObject[object.id]);

    // object.position.x = translateX;
    // object.position.y = translateY;
    // object.position.z = translateZ;
    // if(translateY == null){
    //     console.log(translateY)
    // }
    // console.log(translateY)
    originalPositionObject[object.id] = {x: translateX, y: translateY, z: translateZ}
    // console.log(originalPositionObject)
    // // console.log([translateX, translateY, translateZ])
    // toTranslation = executeRotation([translateX, translateY, translateZ]); 
    // // console.log(toTranslation)
    // object.position.x = toTranslation[0];
    // object.position.y = toTranslation[1];
    // object.position.z = toTranslation[2];


    // console.log([copyX, copyY, copyZ]);
    object.position.x += copyX.x + copyY.x + copyZ.x;
    object.position.y += copyX.y + copyY.y + copyZ.y;
    object.position.z += copyX.z + copyY.z + copyZ.z;

    if(id == 1 && objectTransform.indexOf(object.id) == -1){
        objectTransform.push(object.id);
        // console.log(objectTransform)
    }
}

var copyX; var copyY; var copyZ; 
function addTranslation(object, translateX, translateY, translateZ){
    // console.log(originalPositionObject[object.id]);
    if(originalPositionObject[object.id] == null){
        object.position.x += translateX;
        object.position.y += translateY;
        object.position.z += translateZ;
        originalPositionObject[object.id] = object.position;

    }else{
        originalPositionObject[object.id].x += translateX;
        originalPositionObject[object.id].y += translateY;
        originalPositionObject[object.id].z += translateZ;

        copyX = X_AXIS_camera.clone();
        copyX.setLength(translateX);
        copyY = Y_AXIS_camera.clone();
        copyY.setLength(translateY);
        copyZ = Z_AXIS_camera.clone();
        copyZ.negate();
        copyZ.setLength(translateZ);

        // console.log([copyX, copyY, copyZ]);
        object.position.x += copyX.x + copyY.x + copyZ.x;
        object.position.y += copyX.y + copyY.y + copyZ.y;
        object.position.z += copyX.z + copyY.z + copyZ.z;
    }
    if(id == 1 && objectTransform.indexOf(object.id) == -1){
        objectTransform.push(object.id);
        // console.log(objectTransform)
    }
    
}


function rotate(object, angleX, angleY, angleZ){
    // console.log(object.rotation)
    object.rotation.x = angleX;
    object.rotation.y = angleY;
    object.rotation.z = angleZ;
        
    if(id == 1 && objectTransform.indexOf(object.id) == -1){
        objectTransform.push(object.id);
        // console.log(object.rotation)
        // console.log(scene.getObjectById(object.id).rotation)
    }
}

function scale(object, scaleX, scaleY, scaleZ){
    // console.log(object.rotation)
    object.scale.x = scaleX;
    object.scale.y = scaleY;
    object.scale.z = scaleZ;
        
    if(id == 1 && objectTransform.indexOf(object.id) == -1){
        objectTransform.push(object.id);
        // console.log(object.rotation)
        // console.log(scene.getObjectById(object.id).rotation)
    }
}

function getTranslations(){
    var toSend = [];
    for(const id of  objectTransform){
        toSend.push({id: id, pos: scene.getObjectById(id).position, rot: scene.getObjectById(id).rotation, sca: scene.getObjectById(id).scale});
    }
    return toSend;
}

function setTranslations(array){
    for(const object of array){
        var obj = scene.getObjectById(object.id);

        originalPositionObject[object.id] = object.pos
        // if(object.pos.y ==null){
        //     console.log(object.pos)
        // }
        toTranslation = executeRotation([object.pos.x, object.pos.y, object.pos.z]); 
        // if(toTranslation[1] ==null){
        //     console.log(toTranslation)
        // }
        obj.position.x = toTranslation[0];
        obj.position.y = toTranslation[1];
        obj.position.z = toTranslation[2];

        obj.rotation.x = object.rot._x;
        obj.rotation.y = object.rot._y;
        obj.rotation.z = object.rot._z;

        obj.scale.x = object.sca.x;
        obj.scale.y = object.sca.y;
        obj.scale.z = object.sca.z;
    }
}

var tempVector;
var angleToReverse;
var k;
var newPos = new THREE.Vector3();
var checkinggggg;
function executeRotation(positionToTranslate){
    // console.log(object);
    copyX_axis = X_AXIS.clone();
    copyY_axis = Y_AXIS.clone();
    copyZ_axis = Z_AXIS.clone();
    copyZ_axis.negate();
    // console.log(positionToTranslate)
    for(k = 0; k <  historyRotations.length; k++){
        // console.log(historyRotations[j].x);
        if(historyRotations[k].x != null){
            tempVector = copyX_axis.clone();
            angleToReverse = historyRotations[k].x;

            copyY_axis.applyAxisAngle(copyX_axis, angleToReverse);
            copyZ_axis.applyAxisAngle(copyX_axis, angleToReverse);
        }else if(historyRotations[k].y != null){
            tempVector = copyY_axis.clone();
            angleToReverse = historyRotations[k].y

            copyX_axis.applyAxisAngle(copyY_axis, angleToReverse);
            copyZ_axis.applyAxisAngle(copyY_axis, angleToReverse);
        }else if(historyRotations[k].z != null){
            tempVector = copyZ_axis.clone();
            angleToReverse = historyRotations[k].z

            copyX_axis.applyAxisAngle(copyZ_axis, angleToReverse);
            copyY_axis.applyAxisAngle(copyZ_axis, angleToReverse);
        }

        newPos.set(positionToTranslate[0] - center_camera_id1[0], positionToTranslate[1] - center_camera_id1[1], positionToTranslate[2] - center_camera_id1[2]);
        newPosCopy = newPos.clone();
        newPosCopy.applyAxisAngle(tempVector, angleToReverse);

        positionToTranslate[0] += newPosCopy.x - newPos.x;
        positionToTranslate[1] += newPosCopy.y - newPos.y;
        positionToTranslate[2] += newPosCopy.z - newPos.z;
        // console.log(k)
        
    }
    checkinggggg = positionToTranslate;
    return positionToTranslate;
}


function onWindowResize() {
    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();
    renderer.setSize( window.innerWidth, window.innerHeight );
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

var newPos
var newPosCopy;
var children;
var i;
function rotateCameraX(angle, centerPosition){
    angle = degreesToRadians(angle);
    var setOriginal = false;
    if(historyRotations.length){
        if(historyRotations[historyRotations.length-1].x != null){
            historyRotations[historyRotations.length-1].x +=angle
            if(historyRotations[historyRotations.length-1].x == 0){
                historyRotations.length = historyRotations.length - 1;
            }
        }else{
            historyRotations.push({x: angle})
        }
    }else{
        setOriginal = true;
        historyRotations.push({x: angle})
    }

    children = scene.children;
    // console.log(children);
    for(i = 0; i< children.length; i++){

        if(setOriginal && originalPositionObject[children[i].id] == null){
            originalPositionObject[children[i].id] = children[i].position
        }

        if((children[i] instanceof THREE.Camera) == false){
            newPos = new THREE.Vector3(children[i].position.x - centerPosition[0], children[i].position.y - centerPosition[1], children[i].position.z - centerPosition[2]);
            newPosCopy = newPos.clone();
            newPos.applyAxisAngle(X_AXIS_camera, angle);

            children[i].position.x += newPos.x - newPosCopy.x;
            children[i].position.y += newPos.y - newPosCopy.y;
            children[i].position.z += newPos.z - newPosCopy.z;

            children[i].rotateOnAxis( X_AXIS_camera, angle );
        }
        // console.log(originalPositionObject[21])

    }
    // console.log(originalPositionObject);
    // console.log(children[0].position);
    
    positionCamera[0] += newPos.x - newPosCopy.x;
    positionCamera[1] += newPos.y - newPosCopy.y;
    positionCamera[2] += newPos.z - newPosCopy.z;

    vectorCamera.applyAxisAngle(X_AXIS_camera, angle);
    Y_AXIS_camera.applyAxisAngle(X_AXIS_camera, angle);
    Z_AXIS_camera.applyAxisAngle(X_AXIS_camera, angle);
}
function rotateCameraY(angle, centerPosition){
    angle = degreesToRadians(angle)
    var setOriginal = false;
    if(historyRotations.length){
        if(historyRotations[historyRotations.length-1].y != null){
            historyRotations[historyRotations.length-1].y +=angle
            if(historyRotations[historyRotations.length-1].y == 0){
                historyRotations.length = historyRotations.length - 1;
            }
        }else{
            historyRotations.push({y: angle})
        }
    }else{
        setOriginal = true;
        historyRotations.push({y: angle})
    }

    children = scene.children;
    for(i = 0; i< children.length; i++){
        if(setOriginal){
            originalPositionObject[children[i].id] = children[i].position
        }

        if((children[i] instanceof THREE.Camera) == false){
            newPos = new THREE.Vector3(children[i].position.x - centerPosition[0], children[i].position.y - centerPosition[1], children[i].position.z - centerPosition[2]);
            newPosCopy = newPos.clone();
            newPos.applyAxisAngle(Y_AXIS_camera, angle);

            children[i].position.x += newPos.x - newPosCopy.x;
            children[i].position.y += newPos.y - newPosCopy.y;
            children[i].position.z += newPos.z - newPosCopy.z;

            children[i].rotateOnAxis( Y_AXIS_camera, angle );
        }
    }

    positionCamera[0] += newPos.x - newPosCopy.x;
    positionCamera[1] += newPos.y - newPosCopy.y;
    positionCamera[2] += newPos.z - newPosCopy.z;

    vectorCamera.applyAxisAngle(Y_AXIS_camera, angle);
    X_AXIS_camera.applyAxisAngle(Y_AXIS_camera, angle);
    Z_AXIS_camera.applyAxisAngle(Y_AXIS_camera, angle);
}
function rotateCameraZ(angle, centerPosition){
    angle = degreesToRadians(angle);2
    var setOriginal = false;
    if(historyRotations.length){
        if(historyRotations[historyRotations.length-1].z != null){
            historyRotations[historyRotations.length-1].z +=angle
            if(historyRotations[historyRotations.length-1].z == 0){
                historyRotations.length = historyRotations.length - 1;
            }
        }else{
            historyRotations.push({z: angle})
        }
    }else{
        setOriginal = true;
        historyRotations.push({z: angle})
    }
    children = scene.children;
    // console.log(children[0].position);
    for(i = 0; i< children.length; i++){
        if(setOriginal){
            originalPositionObject[children[i].id] = children[i].position
        }
        if((children[i] instanceof THREE.Camera) == false){
            newPos = new THREE.Vector3(children[i].position.x - centerPosition[0], children[i].position.y - centerPosition[1], children[i].position.z - centerPosition[2]);
            newPosCopy = newPos.clone();
            newPos.applyAxisAngle(Z_AXIS_camera, angle);

            children[i].position.x += newPos.x - newPosCopy.x;
            children[i].position.y += newPos.y - newPosCopy.y;
            children[i].position.z += newPos.z - newPosCopy.z;

            children[i].rotateOnAxis( Z_AXIS_camera, angle );
        }
    }

    positionCamera[0] += newPos.x - newPosCopy.x;
    positionCamera[1] += newPos.y - newPosCopy.y;
    positionCamera[2] += newPos.z - newPosCopy.z;

    vectorCamera.applyAxisAngle(Z_AXIS_camera, angle);
    X_AXIS_camera.applyAxisAngle(Z_AXIS_camera, angle);
    Y_AXIS_camera.applyAxisAngle(Z_AXIS_camera, angle);
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
    // console.log(children[0]);
    
    // console.log(historyRotations);
    for(i = 0; i< children.length; i++){
        if((children[i] instanceof THREE.Camera) == false){

            reverseRotation(children[i], centerPosition)
        }
    }
    // console.log(children[0]);
    // console.log(copyX_axis_camera);
    // console.log(copyY_axis_camera);
    // console.log(copyZ_axis_camera);
    rotationSoFarX = 0;
    rotationSoFarY = 0;
    rotationSoFarZ = 0;
    historyRotations = [];
    Y_AXIS_camera = new THREE.Vector3( 0, 1, 0 );
    X_AXIS_camera = new THREE.Vector3( 1, 0, 0 );
    Z_AXIS_camera = new THREE.Vector3( 0, 0, -1 );
}

var tempVector;
var angleToReverse;
var j
function reverseRotation(object, centerPosition){
    // console.log(object);
    copyX_axis_camera = X_AXIS_camera.clone();
    copyY_axis_camera = Y_AXIS_camera.clone();
    copyZ_axis_camera = Z_AXIS_camera.clone();

    for(j = historyRotations.length-1; j>= 0; j = j-1){
        // console.log(historyRotations[j].x);
        if(historyRotations[j].x != null){
            tempVector = copyX_axis_camera.clone();
            angleToReverse = -historyRotations[j].x;

            copyY_axis_camera.applyAxisAngle(copyX_axis_camera, angleToReverse);
            copyZ_axis_camera.applyAxisAngle(copyX_axis_camera, angleToReverse);
        }else if(historyRotations[j].y != null){
            tempVector = copyY_axis_camera.clone();
            angleToReverse = -historyRotations[j].y

            copyX_axis_camera.applyAxisAngle(copyY_axis_camera, angleToReverse);
            copyZ_axis_camera.applyAxisAngle(copyY_axis_camera, angleToReverse);
        }else if(historyRotations[j].z != null){
            tempVector = copyZ_axis_camera.clone();
            angleToReverse = -historyRotations[j].z

            copyX_axis_camera.applyAxisAngle(copyZ_axis_camera, angleToReverse);
            copyY_axis_camera.applyAxisAngle(copyZ_axis_camera, angleToReverse);
        }

        object.rotateOnAxis( tempVector , angleToReverse);

        newPos = new THREE.Vector3(object.position.x - centerPosition[0], object.position.y - centerPosition[1], object.position.z - centerPosition[2]);
        newPosCopy = newPos.clone();
        newPosCopy.applyAxisAngle(tempVector, angleToReverse);

        object.position.x -= newPos.x - newPosCopy.x;
        object.position.y -= newPos.y - newPosCopy.y;
        object.position.z -= newPos.z - newPosCopy.z;
        
    }
}