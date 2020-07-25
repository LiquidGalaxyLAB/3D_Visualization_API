// var material;

// // camera.position.z = 1000;

// scene.fog = new THREE.FogExp2( 0x000000, 0.001 );

// var geometry = new THREE.BufferGeometry();
// var vertices = [];

// var sprite = new THREE.TextureLoader().load( 'disc.png' );

// for ( var i = 0; i < 10; i ++ ) {

//     var x = 10 * Math.random() -5;
//     var y = 10 * Math.random() -5;
//     var z = 10 * Math.random() -5;

//     vertices.push( x, y, z );

// }

// geometry.setAttribute( 'position', new THREE.Float32BufferAttribute( vertices, 3 ) );

// material = new THREE.PointsMaterial( { size: 1, sizeAttenuation: false, map: sprite, alphaTest: 0.5, transparent: false } );
// material.color.setHSL( 1.0, 0.3, 0.7 );

// var particles = new THREE.Points( geometry, material );
// // var particles = new THREE.Points( geometry );
// scene.add( particles );

var vertices = [];

var sprite = new THREE.TextureLoader().load( 'disc.png' );
for ( var i = 0; i < 5000; i ++ ) {

	var x = 20 * Math.random() -10;
	var y = 20 * Math.random() -10;
	var z =20 * Math.random() -10;

	vertices.push( x, y, z );

}

var geometry = new THREE.BufferGeometry();
geometry.setAttribute( 'position', new THREE.Float32BufferAttribute( vertices, 3 ).setUsage( THREE.DynamicDrawUsage ) );
geometry.dynamic = true;
var material = new THREE.PointsMaterial( { color: 0x888888, sizeAttenuation: false, size: 30, map: sprite, alphaTest: 0.5, transparent: false  } );
material.color.setHSL( 1.0, 0.3, 0.7 );

var points = new THREE.Points( geometry, material );

scene.add( points );

animate();

function animate() {

    requestAnimationFrame( animate );

    render();

}

function render() {

    var time = Date.now() * 0.00005;

    var h = ( 360 * ( 1.0 + time ) % 360 ) / 360;
    material.color.setHSL( h, 0.5, 0.5 );

    renderer.render( scene, camera );

}