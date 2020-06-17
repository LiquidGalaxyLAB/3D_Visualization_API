var vectorCamera;
var firstTime = true;
const width  = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
const height = window.innerHeight|| document.documentElement.clientHeight||  document.body.clientHeight;

var scene = new THREE.Scene();
var camera = new THREE.PerspectiveCamera( 75, window.innerWidth / window.innerHeight, 0.1, 1000 );
var camera_pivot = new THREE.Object3D()
var Y_AXIS = new THREE.Vector3( 0, 1, 0 );
var X_AXIS = new THREE.Vector3( 1, 0, 0 );
var Z_AXIS = new THREE.Vector3( 0, 0, 1 );

var Y_AXIS_camera = new THREE.Vector3( 0, 1, 0 );
var X_AXIS_camera = new THREE.Vector3( 1, 0, 0 );
var Z_AXIS_camera = new THREE.Vector3( 0, 0, 1 );
var renderer = new THREE.WebGLRenderer();
renderer.setSize( window.innerWidth, window.innerHeight );
document.body.appendChild( renderer.domElement );

var geometry = new THREE.BoxGeometry();
var material = new THREE.MeshBasicMaterial( { color: 0x00ff00 } );
var cube = new THREE.Mesh( geometry, material );


function init(){
    console.log(firstTime);
    if(!firstTime){
        console.log("herr")
        location.reload();
    }
    firstTime = false;  
     scene.add( camera );
     scene.add( cube );
     console.log(scene.getObjectById(cube.id));

    if(rotateCamera == null){
        rotateCamera = false
    }if(angleCamera == null){
        angleCamera = false
    }
    
    window.addEventListener( 'resize', onWindowResize, false );
    cube.position.x = 0;
    cube.position.z = -5;
    cube.position.y = 0;
    // cube.rotateZ(20*(Math.PI/180))
    howLong = 0;
}
var angleBef = 0;
var animate = function () {
    requestAnimationFrame( animate );

    if(!stopIm){
        if(rotateCamera){
            camera.rotateOnAxis( Y_AXIS, degreesToRadians(-angleBef) );
            camera.rotateOnAxis( Y_AXIS, degreesToRadians(angleCamera) );
            X_AXIS_camera.applyAxisAngle(Y_AXIS, degreesToRadians(angleCamera));
            rotateCamera = false;
            angleBef = angleCamera
        }
        // cube.position.x += 0.05;

        translate(cube, cube.position.x+0.05, cube.position.y, cube.position.z);

        if(cube.position.x>6){
            // cube.position.x = -cube.position.x;
            translate(cube, -cube.position.x, cube.position.y, cube.position.z);
        }
        
        renderer.render( scene, camera );
    }
};
animate();

function translate(object, translateX, translateY, translateZ){
    object.position.x = translateX;
    object.position.y = translateY;
    object.position.z = translateZ;
    if(id == 1 && objectTransform.indexOf(object.id) == -1){
        objectTransform.push(object.id);
        // console.log(objectTransform)
    }
}

function getTranslations(){
    var toSend = [];
    for(const id of  objectTransform){
        // console.log(id);
        // console.log(scene.getObjectById(id));
        toSend.push({id: id, pos: scene.getObjectById(id).position});
    }
    console.log(toSend)
    return toSend;
}

function setTranslations(array){
    // console.log("setting translation")
    for(const object of  array){
        scene.getObjectById(object.id).position = object.pos;
    }
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
    return cube.position;
}
function setCubePosition(position){
    cube.position.x = position.x;
    cube.position.y = position.y;
    cube.position.z = position.z;
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
function rotateCameraX(angle){
    camera.rotateOnAxis( X_AXIS, degreesToRadians(angle) );
    vectorCamera.applyAxisAngle(X_AXIS, angle);
}
function rotateCameraY(angle){
    camera.rotateOnAxis( Y_AXIS, degreesToRadians(angle) );
    vectorCamera.applyAxisAngle(Y_AXIS, angle);
}
function rotateCameraZ(angle){
    camera.rotateOnAxis( Z_AXIS, degreesToRadians(angle) );
    vectorCamera.applyAxisAngle(Z_AXIS, angle);
}
function rotateVectorInit(angle){
    vectorCamera = new THREE.Vector3( 0, 0, -1 );
    vectorCamera.applyAxisAngle(Y_AXIS, angle);
}
function changeAngleCurrentToOriginalCamera(angle){
    console.log(angle);
    vectorCamera = new THREE.Vector3( 0, 0, -1 );
    vectorCamera.applyAxisAngle(Y_AXIS, degreesToRadians(angle));
    camera.lookAt(vectorCamera);
}