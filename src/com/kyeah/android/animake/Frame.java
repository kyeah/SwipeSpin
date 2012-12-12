package com.kyeah.android.animake;

import java.util.ArrayList;

import com.kyeah.android.light.Light;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class Frame extends SurfaceView implements OnTouchListener,
		SurfaceHolder.Callback {

	final int PREVIEW = 0;
	final int BG = 1;
	final int CURR = 2;
	
	private Bitmap[] canvasBitmap;
	private Canvas[] bitCanvas;
	private Matrix identityMatrix;
	
	protected int maxx, maxy;
	protected double[][] zBuffer;
	protected double[] view = { 0, 0, -1 }; // TODO: create setView(x y z)
											// instead of hardcoding
	protected ArrayList<Light> lights;
	protected double ambientR, ambientG, ambientB;

	protected ArrayList<Layer> layers;
	protected Layer currentLayer;
	protected PreviewLayer previewLayer;
	private int currentColor;

	private DrawThread drawThread;
	private int tool;
	protected int interp;
	
	private boolean _touched;
	/*
	SurfaceHolder holder = getHolder();
	
	// Need handler for callbacks to the UI thread
    final Handler mHandler = new Handler();

    // Create runnable for posting
    final Runnable mUpdateResults = new Runnable() {
        public void run() {
        	Canvas c;
            	c = null;
                try {
                    c = holder.lockCanvas(null);
                    	//c.drawColor(0, PorterDuff.Mode.CLEAR);
                    	//c.drawColor(Color.BLACK);
                    	
                		c.drawBitmap(canvasBitmap, identityMatrix, null);
                		
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) 
                        getHolder().unlockCanvasAndPost(c);
                    }
        }
    };
	*/
	
	/**
	 * ========== public Frame() ==========
	 * 
	 * @param: Context
	 * @returns:
	 * 
	 *           Sets up the screen for each frame in the animation.
	 * 
	 *           =========================
	 */
	public Frame(Context context) {
		super(context);
		
		// Screen Setup
		DisplayMetrics metrics = new DisplayMetrics();
		((Animake)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		maxy = metrics.heightPixels;
		maxx = metrics.widthPixels;
		
		zBuffer = new double[maxx][maxy];
		clearBuffer();

		// Default light & layer setup
		lights = new ArrayList<Light>();
		addAmbient(1, 1, 1);
		addLight("lighty", 200, 200, 200, 0.5, 0.5, 0.5);
		
		layers = new ArrayList<Layer>();
		previewLayer = new PreviewLayer(this);
		currentColor = Color.WHITE;

		// Thread setup
		getHolder().addCallback(this);
		drawThread = new DrawThread(getHolder(), this);
		
		// Touch function setup
		this.setOnTouchListener(this);
		_touched = false;
		
		// Set the default tools
		this.setTool(R.id.menu_sphere);
		this.setInterp(R.id.shade_wireframe);

		/////////////////////
		canvasBitmap = new Bitmap[3];
		bitCanvas = new Canvas[3];
		
		for(int i=0; i<3; i++) {
			canvasBitmap[i] = Bitmap.createBitmap(maxx, maxy, Bitmap.Config.ARGB_8888);
			bitCanvas[i] = new Canvas(canvasBitmap[i]);
		//	bitCanvas[i].setBitmap(canvasBitmap[i]);
		}
		
		identityMatrix = new Matrix();
		
	}

	/**
	 * ========== public clearBuffer() ==========
	 * 
	 * @param:
	 * @returns:
	 * 
	 *           Clears the Z-Buffer.
	 * 
	 *           =============================
	 */
	public void clearBuffer() {
		for (int i = 0; i < maxx; i++)
			for (int j = 0; j < maxy; j++)
				zBuffer[i][j] = Double.NEGATIVE_INFINITY;
	}

	/**
	 * ========== public addLayer() ==========
	 * 
	 * @param:
	 * @returns: true if new layer was added successfully.
	 * 
	 *           Adds a new Layer to the ArrayList.
	 * 
	 *           ==============================
	 */
	public boolean addLayer() {

		layers.add(new Layer(this));
		return true;
	}

	/**
	 * ========== public boolean setLayer() ==========
	 * 
	 * @param: int position
	 * @returns: true if the a valid position was passed; false otherwise.
	 * 
	 *           Sets the layer indicated by position as the layer to be drawn
	 *           on.
	 * 
	 *           ======================================
	 */
	public boolean setLayer(int position) {

		if (position >= layers.size())
			return false;
		else {
			
			currentLayer = layers.get(position);
			currentLayer.setAlpha(255);
			
			bitCanvas[BG].drawColor(0, PorterDuff.Mode.CLEAR);
			
			for(int i=0; i<layers.size(); i++)
				if (i != position ) {
					layers.get(i).setAlpha(155);
					layers.get(i).drawPolygons(bitCanvas[BG]);
				}
			
		}
		return true;
	}

	public void addAmbient(double r, double g, double b) {
		ambientR = r;
		ambientG = g;
		ambientB = b;
	}

	public void addLight(String name, double x, double y, double z, double r,
			double g, double b) {
		lights.add(new Light(name, x, y, z, r, g, b));

	}

	public void setTool(int id) {
		tool = id;
	}

	public void setInterp(int id) {
		interp = id;
	}

	/**
	 * ========== public onTouch() ==========
	 * 
	 * @param: View v, MotionEvent event
	 * @returns true if event was recognized; false otherwise.
	 * 
	 *          Handles touch events.
	 * 
	 *          ==============================
	 */
	public boolean onTouch(View v, MotionEvent event) {
		double x, y;
		x = event.getX();
		y = event.getY();

		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:
			_touched = true;
			previewLayer.onDown(x, y, tool, currentLayer.getOrigin(),
					currentLayer.getEdgeMat());
			break;
		case MotionEvent.ACTION_MOVE:
			previewLayer.onMove(x, y, tool);
			break;
			
		case MotionEvent.ACTION_UP:
			
			_touched = false;
			
			switch (tool) {
			case R.id.menu_box:
			case R.id.menu_sphere:
				/*
				 * double width, height, depth;
				 * 
				 * width = Math.abs( x - previewLayer.getX(0) ); height =
				 * Math.abs( y - previewLayer.getY(0) ); depth = height; //in
				 * the future, rotate preview and use mouse move/click as in
				 * TORUS to add depth
				 * 
				 * currentLayer.addBox(previewLayer.getX(0),
				 * previewLayer.getY(0), currentLayer.getZ(), width, height,
				 * depth, currentColor);
				 */
				currentLayer.append(previewLayer.getEdgeMat(), currentColor);
				bitCanvas[PREVIEW].drawColor(0, PorterDuff.Mode.CLEAR);
				break;
			/*
			 * double r = EdgeMatrix.distance(previewLayer.getX(0),
			 * previewLayer.getY(0), x, y);
			 * currentLayer.addSphere(previewLayer.getX(0),
			 * previewLayer.getY(0), currentLayer.getZ(), r, currentColor);
			 */
			case R.id.menu_torus:

				break;
			case R.id.menu_move_x:
			case R.id.menu_move_y:
			case R.id.menu_move_z:
			case R.id.menu_move_all:
			case R.id.menu_scale_x:
			case R.id.menu_scale_y:
			case R.id.menu_scale_z:
			case R.id.menu_scale_all:
			case R.id.menu_rotate_x:
			case R.id.menu_rotate_y:
			case R.id.menu_rotate_z:
			case R.id.menu_rotate_all:
				currentLayer.convert(previewLayer.getEdgeMat(),
						previewLayer.getOrigin());
				bitCanvas[PREVIEW].drawColor(0, PorterDuff.Mode.CLEAR);
				break;
			}

			previewLayer.clear();
			// currentLayer.addSphere((double)event.getX(),
			// (double)event.getY(), 0.0, 75.0, currentColor);
			break;
		default:
			return false;
		}

		return true;
	}

	/**
	 * ========== public void onDraw() ==========
	 * 
	 * @param: Canvas canvas
	 * @returns:
	 * 
	 *           Sets unfocused layer opacity to 50%; Calls each layer to draw
	 *           their points.
	 * 
	 *           ==============================
	 */
	public void onDraw(Canvas canvas) {
		
		// TODO: if previewLayer is not being accessed by adding thread (thread is asleep (only woken by onMove for modifying the matrix))
		//previewLayer.drawPolygons(bitCanvas);

		// TODO: if touch down/move, draw previewlayer, else draw currentlayer (also move drawColor(clear) to before all drawPolygons (besides when drawing to bg canvas, where you will draw multiple layers)) 
		// TODO: Separate bg, current, and preview layers into different clear canvases

		if(_touched) {
			bitCanvas[PREVIEW].drawColor(0, PorterDuff.Mode.CLEAR);
			previewLayer.drawPolygons(bitCanvas[PREVIEW]);
		} /*else {-
			bitCanvas[CURR].drawColor(0, PorterDuff.Mode.CLEAR);
			//previewLayer.drawPolygons(bitCanvas[CURR]);
		}*/

		bitCanvas[CURR].drawColor(0, PorterDuff.Mode.CLEAR);
		currentLayer.drawPolygons(bitCanvas[CURR]);
		
		// TODO: Move this so that it is done in the UI thread instead of the worker thread
		
		canvas.drawColor(Color.BLACK);
		//canvas.drawBitmap(canvasBitmap[BG], identityMatrix, null);
		canvas.drawBitmap(canvasBitmap[CURR], identityMatrix, null);
		
		if(_touched)
			canvas.drawBitmap(canvasBitmap[PREVIEW], identityMatrix, null);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// drawThread must be started after the canvas has been created, to avoid null pointer exceptions.
		drawThread.setRunning(true);
		drawThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

		boolean retry = true;
		drawThread.setRunning(false);

		while (retry) {
			try {
				drawThread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}

}
