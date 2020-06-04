if(rotateCamera == null){
    rotateCamera = false
}if(angleCamera == null){
    angleCamera = false
}
const width  = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
const height = window.innerHeight|| document.documentElement.clientHeight||  document.body.clientHeight;

var scene = new THREE.Scene();
var camera = new THREE.PerspectiveCamera( 75, window.innerWidth / window.innerHeight, 0.1, 1000 );

var camera_pivot = new THREE.Object3D()
var Y_AXIS = new THREE.Vector3( 0, 1, 0 );

scene.add( camera );
camera.position.z = 5;


var renderer = new THREE.WebGLRenderer();
renderer.setSize( window.innerWidth, window.innerHeight );
document.body.appendChild( renderer.domElement );

var geometry = new THREE.BoxGeometry();
var material = new THREE.MeshBasicMaterial( { color: 0x00ff00 } );
var cube = new THREE.Mesh( geometry, material );
scene.add( cube );

var position
var copyCamera= camera.clone();
position = cube.position.clone();
position.project(camera);
window.addEventListener( 'resize', onWindowResize, false );
cube.position.x = 0;
var angleBef = 0;
var animate = function () {
    requestAnimationFrame( animate );

    if(!stopIm){
        if(rotateCamera){
            camera.rotateOnAxis( Y_AXIS, degreesToRadians(-angleBef) );
            camera.rotateOnAxis( Y_AXIS, degreesToRadians(angleCamera) );
            rotateCamera = false;
            angleBef = angleCamera
        }
        cube.position.x += 0.05;
        position = cube.position.clone();
        position.project(camera);
        if(cube.position.x>8){
            cube.position.x = -cube.position.x;
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
function getCubePosition(){
    return cube.position;
}
function setCubePosition(position){
    cube.position.x = position.x;
    cube.position.y = position.y;
    cube.position.z = position.y;
}