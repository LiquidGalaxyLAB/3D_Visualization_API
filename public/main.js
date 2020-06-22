console.log("hereeee")
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
var cube = new THREE.Mesh( geometry, material );

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


function init(){
    console.log(firstTime);
    if(!firstTime){
        console.log("herr")
        location.reload();
    }
    firstTime = false;  
     scene.add( camera );
     scene.add( cube );
     scene.add(line)

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
        cube.position.x += 0.05;
        // cube.position.y += 0.01;

        if(cube.position.x>6){
            cube.position.x = -cube.position.x;
            cube.position.y = -cube.position.y;
        }
        
        renderer.render( scene, camera );
    }
};
animate();

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