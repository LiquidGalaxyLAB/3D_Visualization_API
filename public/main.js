console.log("hereeee")
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
    // cube.rotateZ(20*(Math.PI/180))
}
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
        // // cube.position.y += 0.01;

        // if(cube.position.x>6){
        //     cube.position.x = -cube.position.x;
        // }
        // translateX();
        
        renderer.render( scene, camera );
    }
};
animate();

var copyX
function translateX(){
    copyX = X_AXIS_camera.clone();
    copyX.setLength(0.05);

    // console.log(copyX);
    cube.position.x += copyX.x;
    cube.position.y += copyX.y;
    cube.position.z += copyX.z;

    copyX.setLength(6);
    if(cube.position.x > copyX.x){
        // console.log(copyX)
        cube.position.x -= copyX.x*2;
        cube.position.y -= copyX.y*2;
        cube.position.z -= copyX.z*2;
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

var newPos
var newPosCopy;
var children;
var i;
var rotationSoFarX = 0; var rotationSoFarY = 0; var rotationSoFarZ = 0; 
var historyRotations = []
function rotateCameraX(angle, curPosition, centerPosition){
    angle = degreesToRadians(angle)
    historyRotations.push({x: angle})
    rotationSoFarX+=angle;

    children = scene.children;
    // console.log(children[0].position);
    for(i = 0; i< children.length; i++){
        if((children[i] instanceof THREE.Camera) == false){
            newPos = new THREE.Vector3(children[i].position.x - centerPosition[0], children[i].position.y - centerPosition[1], children[i].position.z - centerPosition[2]);
            newPosCopy = newPos.clone();
            newPos.applyAxisAngle(X_AXIS_camera, angle);

            children[i].position.x += newPos.x - newPosCopy.x;
            children[i].position.y += newPos.y - newPosCopy.y;
            children[i].position.z += newPos.z - newPosCopy.z;

            children[i].rotateOnAxis( X_AXIS_camera, angle );
        }
    }
    // console.log(children[0].position);
    
    positionCamera[0] += newPos.x - newPosCopy.x;
    positionCamera[1] += newPos.y - newPosCopy.y;
    positionCamera[2] += newPos.z - newPosCopy.z;

    vectorCamera.applyAxisAngle(X_AXIS_camera, angle);
    Y_AXIS_camera.applyAxisAngle(X_AXIS_camera, angle);
    Z_AXIS_camera.applyAxisAngle(X_AXIS_camera, angle);
}
function rotateCameraY(angle, curPosition, centerPosition){
    angle = degreesToRadians(angle)
    rotationSoFarY+=angle;
    historyRotations.push({y: angle})

    children = scene.children;
    for(i = 0; i< children.length; i++){
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
function rotateCameraZ(angle, curPosition, centerPosition){
    angle = degreesToRadians(angle)
    rotationSoFarZ+=angle;
    historyRotations.push({z: angle})

    children = scene.children;
    // console.log(children[0].position);
    for(i = 0; i< children.length; i++){
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
    // console.log(children[0].position);

    positionCamera[0] += newPos.x - newPosCopy.x;
    positionCamera[1] += newPos.y - newPosCopy.y;
    positionCamera[2] += newPos.z - newPosCopy.z;

    vectorCamera.applyAxisAngle(Z_AXIS_camera, angle);
    X_AXIS_camera.applyAxisAngle(Z_AXIS_camera, angle);
    Y_AXIS_camera.applyAxisAngle(Z_AXIS_camera, angle);
    // console.log(X_AXIS_camera);
    // console.log(Y_AXIS_camera);
    // console.log(Z_AXIS_camera);
}

function rotateVectorInit(angle){
    vectorCamera = new THREE.Vector3( 0, 0, -1 );
    vectorCamera.applyAxisAngle(Y_AXIS, angle);
}

var angleTwoBefore; var copyX_axis_camera;  var copyY_axis_camera;  var copyZ_axis_camera; 
function changeAngleCurrentToOriginalCamera(angle, curPosition, centerPosition){
    // console.log(angle);
    // console.log(vectorCamera)
    vectorCamera = new THREE.Vector3( 0, 0, -1 );
    vectorCamera.applyAxisAngle(Y_AXIS, degreesToRadians(angle));

    newPos = new THREE.Vector3(curPosition[0] - centerPosition[0], curPosition[1] - centerPosition[1], curPosition[2] - centerPosition[2]);
    
    newPosCopy = newPos.clone();
    newPosCopy.projectOnPlane(Y_AXIS);
    newPosCopy.setLength(newPos.length());
    // newPos.lookAt(vectorCamera);

    children = scene.children;
    console.log(children[0]);
    // console.log(X_AXIS_camera)
    // console.log(X_AXIS)

    // console.log(X_AXIS_camera.angleTo(X_AXIS))
    console.log(historyRotations);
    for(i = 0; i< children.length; i++){
        if((children[i] instanceof THREE.Camera) == false){

            reverseRotation(children[i], centerPosition)
        }
    }
    console.log(children[0]);
    console.log(copyX_axis_camera);
    console.log(copyY_axis_camera);
    console.log(copyZ_axis_camera);
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