package com.kyeah.android.math;

public class EdgeMatrix extends Matrix {
    
    private int lastCol;

    public EdgeMatrix() {
	super();
	lastCol = 0;
    }

    public EdgeMatrix( int c ) {
	super( c );
	lastCol = 0;
    }

    /*======== public void addSphere() ==========
      Inputs:   double cx
                double cy
                double r  
      Returns: 
      
      adds all the polygons required to make a 
      sphere with center (cx, cy) and radius r.
      
      should call generateSphere to create the
      necessary points

      ====================*/
    public void addSphere( double cx, double cy, double cz, double r ) { 

	double step = 0.05;
	EdgeMatrix points = new EdgeMatrix();
	
	int index;
	int numSteps = (int)(1 / step);
	int longStart, latStart, longtStop, latStop, lat, longt;
	longStart = 0;
	latStart = 0;
	longtStop = numSteps;
	latStop = numSteps;

	//generate the points on the sphere
	points.generateSphere( cx, cy, cz, r, step );

	for ( lat = latStart; lat < latStop; lat++ ) {
	    for ( longt = longStart; longt < longtStop; longt++ ) { 

		index = lat * numSteps + longt;  
		
		if ( lat != numSteps - 1 && longt != numSteps - 1 ) {
	
		    addPolygon( points.getX( index ),
				points.getY( index ),
				points.getZ( index ),
				points.getX( index + 1 ),
				points.getY( index + 1 ),
				points.getZ( index + 1 ),
				points.getX( index + numSteps ),
				points.getY( index + numSteps ),
				points.getZ( index + numSteps ));
		    addPolygon( points.getX( index + 1 ), //first vertex
				points.getY( index + 1 ),
				points.getZ( index + 1 ),
				points.getX( index + 1 + numSteps ),
				points.getY( index + 1 + numSteps ),
				points.getZ( index + 1 + numSteps ),
				points.getX( index + numSteps ),
				points.getY( index + numSteps ),
				points.getZ( index + numSteps ) );	
		} //end of non edge cases
      
		else if ( lat == numSteps - 1 ) {
		    if  ( longt != numSteps -1 ) {
			addPolygon( points.getX( index ),
				     points.getY( index ),
				     points.getZ( index ),
				     points.getX( index + 1 ),
				     points.getY( index + 1 ),
				     points.getZ( index + 1 ),
				     points.getX( index % numSteps ),
				     points.getY( index % numSteps ),
				     points.getZ( index % numSteps ) );
			addPolygon( points.getX( index + 1 ),
				     points.getY( index + 1 ),
				     points.getZ( index + 1 ),
				     points.getX((index+1) % numSteps),
				     points.getY((index+1) % numSteps),
				     points.getZ((index+1) % numSteps),
				     points.getX( index % numSteps ),
				     points.getY( index % numSteps ),
				     points.getZ( index % numSteps ) );
		    }
		    else {
			addPolygon( points.getX( index ),
				     points.getY( index ),
				     points.getZ( index ),
				     (points.getX( 0 )) - ( 2 * r ),
				     points.getY( 0 ),
				     points.getZ( 0 ),
				     points.getX( index % numSteps ),
				     points.getY( index % numSteps ),
				     points.getZ( index % numSteps ));	
		    }
		} //end latitude edge
		else {
		    addPolygon( points.getX( index ),
				 points.getY( index ),
				 points.getZ( index ),
				 (points.getX(index + 1 )) - ( 2 * r ),
				 points.getY( index + 1 ),
				 points.getZ( index + 1 ),
				 points.getX( index + numSteps ),
				 points.getY( index + numSteps ),
				 points.getZ( index + numSteps ) );	
		} //end longitude edge (south pole)
		
	    } //end for long
	}// end for lat
    }

    /*======== public void addTorus() ==========
      Inputs:   double cx
                double cy
                double r1
		double r2
      Returns: 
      
      adds all the polygons required to make a 
      torus with center (cx, cy) and radii r1 and r2.
      
      should call generateTorus to create the
      necessary points.
      
      ====================*/
    public void addTorus( double cx, double cy, double cz, double r1, double r2 ) {


	double step = 0.025;
	EdgeMatrix points = new EdgeMatrix();
	
	int index;
	int numSteps = (int)(1 / step);
	int longStart, latStart, longtStop, latStop, lat, longt;
	longStart = 0;
	latStart = 0;
	longtStop = numSteps;
	latStop = numSteps;

	//generate the points on the sphere
	points.generateTorus( cx, cy, cz, r1, r2, step );


	for ( lat = latStart; lat < latStop; lat++ ) {
	    for ( longt = longStart; longt < longtStop; longt++ ) {

		index = lat * numSteps + longt;

		if ( lat != numSteps - 1 && longt != numSteps - 1 ) {
	
		    addPolygon( points.getX( index ),
				points.getY( index ),
				points.getZ( index ),
				points.getX( index + 1 ),
				points.getY( index + 1 ),
				points.getZ( index + 1 ),
			        points.getX( index + numSteps ),
				points.getY( index + numSteps ),
				points.getZ( index + numSteps ) );
		    addPolygon( points.getX( index + 1 ),
				 points.getY( index + 1 ),
				 points.getZ( index + 1 ),
				 points.getX( index + 1 + numSteps ),
				 points.getY( index + 1 + numSteps ),
				 points.getZ( index + 1 + numSteps ),
				 points.getX( index + numSteps ), 
				 points.getY( index + numSteps ),
				 points.getZ( index + numSteps ) );	
		    
		} //end of non edge cases

		else if ( lat == numSteps - 1 ) {
		    if  ( longt != numSteps -1 ) {
			addPolygon( points.getX( index ),
				     points.getY( index ),
				     points.getZ( index ),
				     points.getX( index + 1 ),
				     points.getY( index + 1 ),
				     points.getZ( index + 1 ),
				     points.getX( index % numSteps ),
				     points.getY( index % numSteps ),
				     points.getZ( index % numSteps ) );
			addPolygon( points.getX( index + 1 ),
				     points.getY( index + 1 ),
				     points.getZ( index + 1 ),
				     points.getX((index+1) % numSteps),
				     points.getY((index+1) % numSteps),
				     points.getZ((index+1) % numSteps),
				     points.getX( index % numSteps ),
				     points.getY( index % numSteps ),
				     points.getZ( index % numSteps ) );
		    }
		    else { 
			addPolygon( points.getX( index ),
				     points.getY( index ),
				     points.getZ( index ),
				     points.getX(index + 1 - numSteps),
				     points.getY(index + 1 - numSteps),
				     points.getZ(index + 1 - numSteps),
				     points.getX( index % numSteps ),
				     points.getY( index % numSteps ),
				     points.getZ( index % numSteps ) );
			addPolygon( points.getX(index + 1 - numSteps),
				     points.getY(index + 1 - numSteps),
				     points.getZ(index + 1 - numSteps),
				     points.getX( 0 ),
				     points.getY( 0 ),
				     points.getZ( 0 ),
				     points.getX( index % numSteps ),
				     points.getY( index % numSteps ),
				     points.getZ( index % numSteps ) );
		    }
		} //end latitude edge
		else {
		    addPolygon( points.getX( index ),
				 points.getY( index ),
				 points.getZ( index ),
				 points.getX( index + 1 - numSteps ),
				 points.getY( index + 1 - numSteps ),
				 points.getZ( index + 1 - numSteps ),
				 points.getX( index + numSteps ),
				 points.getY( index + numSteps ),
				 points.getZ( index + numSteps ) );	
		    addPolygon( points.getX( index + 1 - numSteps ),
				 points.getY( index + 1 - numSteps ),
				 points.getZ( index + 1 - numSteps ),
				 points.getX( index + 1 ),
				 points.getY( index + 1 ),
				 points.getZ( index + 1 ),
				 points.getX( index ), 
				 points.getY( index ),
				 points.getZ( index ) );	
		} //end longitude edge (south pole)
	    } //end for longt
	} //end for lat
    }

    /*======== public void addBox() ==========
      Inputs:   double x
                double y
                double z
                double width
                double height
                double depth
      Returns: 
      
      adds all the polygons necessary to make the
      rectangular prism with upper-left-front corner
      (x, y, z) and dimensions width, depth and height

      ====================*/
    public void addBox( double x, double y, double z, 
			double width, double height, double depth ) {
	
	double x1, y1, z1;
	
	x1 = x + width;
	y1 = y + height;
	z1 = z - depth;
		
	//front
	addPolygon( x, y, z,
		    x, y1, z,
		    x1, y1, z);
	addPolygon( x1, y1, z, 
		    x1, y, z,
		    x, y, z);
	//back
	addPolygon( x1, y, z1,
		    x1, y1, z1,
		    x, y1, z1);
	addPolygon( x, y1, z1,
		    x, y, z1,
		    x1, y, z1);
	
	//top
	addPolygon( x, y, z1,
		    x, y, z,
		    x1, y, z);
	addPolygon( x1, y, z, 
		    x1, y, z1,
		    x, y, z1);	

	//bottom
	addPolygon( x, y1, z,
		    x, y1, z1,
		    x1, y1, z1);
	addPolygon( x1, y1, z1, 
		    x1, y1, z,
		    x, y1, z);
	
	
	//left
	addPolygon( x, y, z1,
		    x, y1, z1,
		    x, y1, z);
	addPolygon( x, y1, z, 
		    x, y, z,
		    x, y, z1);
	//right
	addPolygon( x1, y, z,
		    x1, y1, z,
		    x1, y1, z1);
	addPolygon( x1, y1, z1, 
		     x1, y, z1,
		     x1, y, z);

    }

    /*======== public void addPolygon() ==========
      Inputs:  int x0
               int y0
	       int z0
	       int x1
	       int y1
	       int z1 
	       int x2
	       int y2
	       int z2 
      Returns: 
      adds the points (x0, y0, z0), (x1, y1, z1)
      and (x2, y2, z2 ) to the calling object
      ====================*/
    public void addPolygon(double x0, double y0, double z0, 
			   double x1, double y1, double z1,
			   double x2, double y2, double z2) {

	addPoint(x0, y0, z0);
	addPoint(x1, y1, z1);
	addPoint(x2, y2, z2);
    }

    
    /*======== public void addSphereMesh() ==========
      Inputs:   double cx
      double cy
      double r
      double step  
      Returns: 
      
      adds all the edges required to make a wire frame mesh
      for a sphere with center (cx, cy) and radius r.
      
      should call generateSphere to create the
      necessary points
      
      ====================*/
    public void addSphereMesh( double cx, double cy, double r ) {

	double step = 0.025;
	EdgeMatrix points = new EdgeMatrix();
	
	double x, y, z;
	int index;
	int numSteps = (int)(1 / step);
	int longStart, latStart, longStop, latStop;
	longStart = 0;
	latStart = 0;
	longStop = numSteps;
	latStop = numSteps;

	points.generateSphere( cx, cy, 0, r, step );

	//longitude lines
	for ( int lat = latStart; lat < latStop; lat++ ) 
	    for ( int longt = longStart; longt < longStop; longt++ ) {

		index = lat * numSteps + longt;
		
		if ( longt < numSteps - 1 ) {
		    addEdge( points.getX( index ),
			     points.getY( index ),
			     points.getZ( index ),
			     points.getX( index + 1 ),
			     points.getY( index + 1 ),
			     points.getZ( index + 1 ) );
		}
		else {
		    if ( lat == numSteps - 1 ) {
			x = points.getX( 0 ) - ( 2 * r );
			y = points.getY( 0 );
			z = points.getZ( 0 );
		    }
		    else {
			x = points.getX( index + 1) - ( 2 * r );
			y = points.getY( index + 1 );
			z = points.getZ( index + 1 );
		    }
		    addEdge( points.getX( index ),
			     points.getY( index ),
			     points.getZ( index ),
			     x, y, z );
		}
	    } //end longitude	

	//latitude lines
	for ( int longt = longStart; longt < longStop; longt++ )
	    for ( int lat = latStart; lat < latStop; lat++ )  {

		index = lat * numSteps + longt;

		if ( lat == numSteps - 1 ){
		    addEdge( points.getX( index ),
			     points.getY( index ),
			     points.getZ( index ),
			     points.getX( index % numSteps ),
			     points.getY( index % numSteps ),
			     points.getZ( index % numSteps ) ); 
		}
		else
		    addEdge( points.getX( index ),
			     points.getY( index ),
			     points.getZ( index ),
			     points.getX( index + numSteps ),
			     points.getY( index + numSteps ),
			     points.getZ( index + numSteps ) );
	    } //end latitude

	/*
	//points only
	for ( int lat = latStart; lat < latStop; lat++ ) 
	for ( int longt = longStart; longt < longStop; longt++ ) {

	index = lat * numSteps + longt;
	addEdge( points.getX( index ),
	points.getY( index ),
	points.getZ( index ),
	points.getX( index ),
	points.getY( index ),
	points.getZ( index ) );
	} //end poitns only
	*/
    }
    
    /*======== public void generateSphere() ==========
      Inputs:   double cx
      double cy
      double r
      double step  
      Returns: 

      Generates all the points along the surface of a 
      sphere with center (cx, cy) and radius r
      
      Adds these points to the matrix parameter

      ====================*/
    public void generateSphere( double cx, double cy, double cz, double r, double step ) {
	
	double x, y, z;

	for ( double rotation = 0; rotation <= 1; rotation+= step )
	    for ( double circle = 0; circle <= 1; circle+= step ) {

		x = r * Math.cos( Math.PI * circle ) + cx;
		y = r * Math.sin( Math.PI * circle ) * 
		    Math.cos( 2 * Math.PI * rotation ) + cy;
		z = r * Math.sin( Math.PI * circle ) * 
		    Math.sin( 2 * Math.PI * rotation ) + cz;
		
		addPoint(x, y, z);
	    }
    }

    /*======== public void addTorusMesh() ==========
      Inputs:   double cx
      double cy
      double r1
      double r2
      double step  
      Returns: 
      
      adds all the edges required to make a wire frame mesh
      for a torus with center (cx, cy) and radii r1 and r2.

      should call generateTorus to create the
      necessary points

      ====================*/
    public void addTorusMesh( double cx, double cy, double r1, double r2 ) {
	double step = 0.05;
	EdgeMatrix points = new EdgeMatrix();
	
	int index;
	int numSteps = (int)(1 / step);
	int longStart, latStart, longStop, latStop;
	longStart = 0;
	latStart = 0;
	longStop = numSteps;
	latStop = numSteps;

	points.generateTorus( cx, cy, 0, r1, r2, step );
	
	//longitude lines
	
	for ( int lat = latStart; lat < latStop; lat++ ) 
	    for ( int longt = longStart; longt < longStop; longt++ ) {

		index = lat * numSteps + longt;
		
		if ( longt < numSteps - 1 ) {
		    addEdge( points.getX( index ),
			     points.getY( index ),
			     points.getZ( index ),
			     points.getX( index + 1 ),
			     points.getY( index + 1 ),
			     points.getZ( index + 1 ) );
		}
		
		else {
		    addEdge( points.getX( index ),
			     points.getY( index ),
			     points.getZ( index ),
			     points.getX( index + 1 - numSteps ),
			     points.getY( index + 1 - numSteps ),
			     points.getZ( index + 1 - numSteps ) );
		}
	    } //end longitude
	
	
	//latitude lines
	for ( int longt = longStart; longt < longStop; longt++ )
	    for ( int lat = latStart; lat < latStop; lat++ )  {

		index = lat * numSteps + longt;

		if ( lat == numSteps - 1 ){
		    addEdge( points.getX( index ),
			     points.getY( index ),
			     points.getZ( index ),
			     points.getX( index % numSteps ),
			     points.getY( index % numSteps ),
			     points.getZ( index % numSteps ) ); 
		}
		else
		    addEdge( points.getX( index ),
			     points.getY( index ),
			     points.getZ( index ),
			     points.getX( index + numSteps ),
			     points.getY( index + numSteps ),
			     points.getZ( index + numSteps ) );
	    } //end latitude
	
	/*
	//points only
	for ( int longt = longStart; longt < longStop; longt++ )
	for ( int lat = latStart; lat < latStop; lat++ )  {

	index = lat * numSteps + longt;
	addEdge( points.getX( index ),
	points.getY( index ),
	points.getZ( index ),
	points.getX( index ),
	points.getY( index ),
	points.getZ( index ) ); 
		
	}
	*/
    }

    /*======== public void generateTorus() ==========
      Inputs:   double cx
      double cy
      double r1
      double r2
      double step  
      Returns: 

      Generates all the points along the surface of a 
      tarus with center (cx, cy) and radii r1 and r2

      Adds these points to the matrix parameter

      ====================*/
    public void generateTorus( double cx, double cy, double cz, double r1, double r2, double step ) {
	double x, y, z;

	for ( double rotation = 0; rotation <= 1; rotation+= step )
	    for ( double circle = 0; circle <= 1; circle+= step ) {

		//y rotation
		x = Math.cos( 2 * Math.PI * rotation ) *
		    ( r1 * Math.cos( 2 * Math.PI * circle ) + r2 ) + cx;
		y = r1 * Math.sin( 2 * Math.PI * circle ) + cy;
		z = Math.sin( 2 * Math.PI * rotation ) *
		    ( r1 * Math.cos( 2 * Math.PI * circle ) + r2 ) + cz;
		/*
		// x rotation
		x = r1 * Math.cos( 2 * Math.PI * circle ) + cx;
		y = Math.cos( 2 * Math.PI * rotation ) * 
		( r1 * Math.sin( 2 * Math.PI * circle ) + r2 ) + cy;
		z = Math.sin( 2 * Math.PI * rotation ) *
		( r1 * Math.sin( 2 * Math.PI * circle ) + r2 );
		*/

		addPoint(x, y, z);
	    }
	
    }

    /*======== public void addBoxMesh() ==========
      Inputs:   double x
      double y
      double z
      double width
      double depth
      double height  
      Returns: 

      adds all the edges required to make a wire frame mesh
      for a rectagular prism whose upper-left corner is
      (x, y, z) with width, height and depth dimensions.

      ====================*/
    public void addBoxMesh( double x, double y, double z, double width, double height, double depth ) {

	double x2, y2, z2;
		
	x2 = x + width;
	y2 = y + height;
	z2 = z - depth;
	
	//front
	addEdge( x, y, z,
		 x2, y, z);
	addEdge( x2, y, z,
		 x2, y2, z);
	addEdge( x2, y2, z,
		 x, y2, z);
	addEdge( x, y2, z,
		 x, y, z);

	//back
	addEdge( x, y, z2,
		 x2, y, z2);
	addEdge( x2, y, z2,
		 x2, y2, z2);
	addEdge( x2, y2, z2,
		 x, y2, z2);
	addEdge( x, y2, z2,
		 x, y, z2);
	
	//top
	addEdge( x, y, z,
		 x, y, z2);
	addEdge( x2, y, z,
		 x2, y, z2);
	
	//bottom
	addEdge( x2, y2, z,
		 x2, y2, z2);
	addEdge( x, y2, z,
		 x, y2, z2);
	    
    }

    /*======== public static double distance() ==========
      Inputs:  double x0
      double y0
      double x1
      double y1 
      Returns: The distance between (x0, y0) and (x1, y1)

      ====================*/
    public static double distance(double x0, double y0, 
				  double x1, double y1) {
	return Math.sqrt( (x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0) );
    }
	   

    /*======== public void addCircle() ==========
      Inputs:  int cx
      int cy
      double r
      Returns: 
      
      Generates the edges required to make a circle and 
      adds them to the EdgeMatrix.

      The circle is centered at (cx, cy) with radius r

      ====================*/
    public void addCircle(double cx, double cy, double r) {
	
	double x0, y0, x, y, step;
	
	step = 0.01;

	x0 = r + cx;
	y0 = cy;

	for( double t= step; t <= 1; t+= step) {
	    
	    x = r * Math.cos( 2 * Math.PI * t) + cx;
	    y = r * Math.sin( 2 * Math.PI * t) + cy;

	    addEdge(x0, y0, 0, x, y, 0);
	    x0 = x;
	    y0 = y;
	}

	addEdge(x0, y0, 0, r + cx, cy, 0);
    }


    /*======== public void addCurve() ==========
      Inputs:   int x0
      int y0
      int x1
      int y1
      int x2
      int y2
      int x3
      int y3 
      Returns: 
      
      Generates the edges required to create a curve
      and adds them to the edge matrix

      ====================*/
    /*
    public void addCurve( double x0, double y0, 
			  double x1, double y1, 
			  double x2, double y2, 
			  double x3, double y3, int type ) {
	
	EdgeMatrix xcoefs = new EdgeMatrix(1);
	EdgeMatrix ycoefs = new EdgeMatrix(1);

	//a lower step value makes a more precise curve
	double step = 0.01;

	double x, y, z, ax, ay, bx, by, cx, cy, dx, dy;
	
	if ( type == Parser.HERMITE_MODE ) {
	    xcoefs.generateHermiteCoefs(x0, x1, x2, x3);
	    ycoefs.generateHermiteCoefs(y0, y1, y2, y3);
	}
	else {
	    xcoefs.generateBezierCoefs(x0, x1, x2, x3);
	    ycoefs.generateBezierCoefs(y0, y1, y2, y3);
	}

	ax = xcoefs.getX(0);
	bx = xcoefs.getY(0);
	cx = xcoefs.getZ(0);
	dx = xcoefs.getD(0);

	ay = ycoefs.getX(0);
	by = ycoefs.getY(0);
	cy = ycoefs.getZ(0);
	dy = ycoefs.getD(0);

	double startx = x0;
	double starty = y0;

	for (double t = step; t <= 1; t+= step ) {
	    
	    x = ax * t * t * t + bx * t * t + cx * t + dx;
	    y = ay * t * t * t + by * t * t + cy * t + dy;

	    addEdge( startx, starty, 0, x, y, 0 );
	    startx = x;
	    starty = y;
	}
    }
    */	    
    /*======== public void addPoint() ==========
      Inputs:  int x
      int y
      int z 
      Returns: 
      adds (x, y, z) to the calling object
      if lastcol is the maxmium value for this current matrix, 
      call grow
      ====================*/
    public void addPoint(double x, double y, double z) {

	if ( lastCol == m[0].length ) 
	    grow();
	
	m[0][lastCol] = x;
	m[1][lastCol] = y;
	m[2][lastCol] = z;
	m[3][lastCol] = 1;
	lastCol++;
    }

    /*======== public void addEdge() ==========
      Inputs:  int x0
      int y0
      int z0
      int x1
      int y1
      int z1 
      Returns: 
      adds the line connecting (x0, y0, z0) and (x1, y1, z1)
      to the calling object
      should use addPoint
      ====================*/
    public void addEdge(double x0, double y0, double z0, 
			double x1, double y1, double z1) {

	addPoint(x0, y0, z0);
	addPoint(x1, y1, z1);
    }



    /*======== accessors ==========
      ====================*/
    public int getLastCol() {
	return lastCol;
    }
    public double getX(int c) {
	return m[0][c];
    }
    public double getY(int c) {
	return m[1][c];
    }
    public double getZ(int c) {
	return m[2][c];
    }
    public double getD(int c) {
	return m[3][c];
    }

    public void clear() {
	super.clear();
	lastCol = 0;
    }
   
    public EdgeMatrix copy() {
	
	EdgeMatrix n = new EdgeMatrix( m[0].length );
	for (int r=0; r<m.length; r++)
	    for (int c=0; c<m[r].length; c++)
		n.m[r][c] = m[r][c];
	n.lastCol = lastCol;
	return n;
    }
    
}
