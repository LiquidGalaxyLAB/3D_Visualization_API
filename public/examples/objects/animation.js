

// 
    // camera.position.z = 1800;
    scene.background = new THREE.Color( 0xffffff );

    var light = new THREE.DirectionalLight( 0xffffff );
    light.position.set( 0, 0, 1 );
    scene.add( light );

    // shadow

    var canvas = document.createElement( 'canvas' );
    canvas.width = 128;
    canvas.height = 128;

    var context = canvas.getContext( '2d' );
    var gradient = context.createRadialGradient( canvas.width / 2, canvas.height / 2, 0, canvas.width / 2, canvas.height / 2, canvas.width / 2 );
    gradient.addColorStop( 0.1, 'rgba(210,210,210,1)' );
    gradient.addColorStop( 1, 'rgba(255,255,255,1)' );

    context.fillStyle = gradient;
    context.fillRect( 0, 0, canvas.width, canvas.height );

    var shadowTexture = new THREE.CanvasTexture( canvas );

    var shadowMaterial = new THREE.MeshBasicMaterial( { map: shadowTexture } );
    var shadowGeo = new THREE.PlaneBufferGeometry( 3, 3, 1, 1 );

    var shadowMesh;

    shadowMesh = new THREE.Mesh( shadowGeo, shadowMaterial );
    shadowMesh.position.y = - 2.5;
    shadowMesh.rotation.x = - Math.PI / 2;
    scene.add( shadowMesh );

    shadowMesh = new THREE.Mesh( shadowGeo, shadowMaterial );
    shadowMesh.position.y = - 2.50;
    shadowMesh.position.x = - 4.00;
    shadowMesh.rotation.x = - Math.PI / 2;
    scene.add( shadowMesh );

    shadowMesh = new THREE.Mesh( shadowGeo, shadowMaterial );
    shadowMesh.position.y = - 2.50;
    shadowMesh.position.x = 4.00;
    shadowMesh.rotation.x = - Math.PI / 2;
    scene.add( shadowMesh );

    var radius = 2.00;

    var geometry1 = new THREE.IcosahedronBufferGeometry( radius, 1 );

    var count = geometry1.attributes.position.count;
    geometry1.setAttribute( 'color', new THREE.BufferAttribute( new Float32Array( count * 3 ), 3 ) );

    var geometry2 = geometry1.clone();
    var geometry3 = geometry1.clone();

    var color = new THREE.Color();
    var positions1 = geometry1.attributes.position;
    var positions2 = geometry2.attributes.position;
    var positions3 = geometry3.attributes.position;
    var colors1 = geometry1.attributes.color;
    var colors2 = geometry2.attributes.color;
    var colors3 = geometry3.attributes.color;

    for ( var i = 0; i < count; i ++ ) {

        color.setHSL( ( positions1.getY( i ) / radius + 1 ) / 2, 1.0, 0.5 );
        colors1.setXYZ( i, color.r, color.g, color.b );

        color.setHSL( 0, ( positions2.getY( i ) / radius + 1 ) / 2, 0.5 );
        colors2.setXYZ( i, color.r, color.g, color.b );

        color.setRGB( 1, 0.8 - ( positions3.getY( i ) / radius + 1 ) / 2, 0 );
        colors3.setXYZ( i, color.r, color.g, color.b );

    }

    var material = new THREE.MeshPhongMaterial( {
        color: 0xffffff,
        flatShading: true,
        vertexColors: true,
        shininess: 0
    } );

    var wireframeMaterial = new THREE.MeshBasicMaterial( { color: 0x000000, wireframe: true, transparent: true } );

    var mesh = new THREE.Mesh( geometry1, material );
    var wireframe = new THREE.Mesh( geometry1, wireframeMaterial );
    mesh.add( wireframe );
    mesh.position.x = - 4.00;
    mesh.rotation.x = - 1.87;
    scene.add( mesh );

    var mesh = new THREE.Mesh( geometry2, material );
    var wireframe = new THREE.Mesh( geometry2, wireframeMaterial );
    mesh.add( wireframe );
    mesh.position.x = 4.00;
    scene.add( mesh );

    var mesh = new THREE.Mesh( geometry3, material );
    var wireframe = new THREE.Mesh( geometry3, wireframeMaterial );
    mesh.add( wireframe );
    scene.add( mesh );

    
//
animate();


function animate() {

    requestAnimationFrame( animate );

    render();

}

function render() {

    camera.position.z = 10;

    renderer.render( scene, camera );

}