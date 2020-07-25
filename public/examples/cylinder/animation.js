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

axis.position.z = -10;

scene.add( cube1 );
scene.add( cube2 );
scene.add( cube3 );
scene.add( cylinder );
scene.add(line);
scene.add(axis);


var animate = function () {
    requestAnimationFrame( animate );
        
        var rotation = getOriginalRotationObject(cylinder)
        rotate(cylinder, rotation.x+ degreesToRadians(2), rotation.y, rotation.z);
        var position = getOriginalPositionObject(cylinder)
        addTranslation(cylinder, 0.05, 0, 0);
        

        if(getOriginalPositionObject(cylinder).x > 10){
            var position = getOriginalPositionObject(cylinder)
            translate(cylinder, -position.x, position.y, position.z);
        } 
        renderer.render( scene, camera );
    //}
};
animate();