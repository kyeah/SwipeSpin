package com.kyeah.android.animake;

import com.kyeah.android.math.EdgeMatrix;
import com.kyeah.android.math.Matrix;

import android.graphics.Canvas;
import android.graphics.Color;

public class PreviewLayer extends Layer {
	private double[] xs, ys;
	private int clickcount;
	private Matrix transform, transform2;

	// Use these variables when tweening frames and displaying transformation
	// values (during onMove)
	private int transformX, transformY, transformZ;

	public PreviewLayer(Frame frame) {
		super(frame);
		transform = new Matrix(4);
		transform2 = new Matrix(4);

		paint.setColor(Color.RED);

		xs = new double[10];
		ys = new double[10];
		clickcount = 0;

	}

	public void clear() {
		pm.clear();
		clickcount = 0;
		transformX = 0;
		transformY = 0;
		transformZ = 0;
	}

	public void onDown(double x, double y, int tool, Matrix o,
			EdgeMatrix currMat) {

		if (clickcount == 0 || clickcount == 2 && tool != R.id.menu_torus) {

			origin = o.copy();
			xs[clickcount] = x;
			ys[clickcount] = y;
			clickcount++;

			if (tool != R.id.menu_box && tool != R.id.menu_sphere
					&& tool != R.id.menu_torus)
				pm = currMat.copy();
		}

	}

	// TODO:SCALE AFTER CREATING NEW SHAPE INSTEAD OF ADDBOXING EVERY TIME? (based on distance from point to last point)
	// STARTING WITH SPHERE OR W/E OF RADIUS 1?
	public void onMove(double x, double y, int tool) {
		double r, r2;

		switch (tool) {

		case R.id.menu_box:
			pm.clear();
			double width,
			height,
			depth;

			width = Math.abs(x - getX(0));
			height = Math.abs(y - getY(0));
			depth = height; // TODO: rotate preview and use mouse move/click as
							// in TORUS to add depth //Default depth = 0

			this.addBox(getX(0), getY(0), getZ(), width, height, depth,
					paint.getColor());
			pm.matrixMult(origin);
			break;

		case R.id.menu_sphere:
			pm.clear();
			r = EdgeMatrix.distance(getX(0), getY(0), x, y);
			this.addSphere(getX(0), getY(0), getZ(), r, paint.getColor());
			pm.matrixMult(origin);

			break;

		case R.id.menu_torus:
			if (clickcount == 1) {
				pm.clear();
				r2 = EdgeMatrix.distance(getX(0), getY(0), x, y);
				this.addTorus(getX(0), getY(0), getZ(), 20.0, r2,
						paint.getColor());
				pm.matrixMult(origin);
			}

			break;

		case R.id.menu_move_x:
			r = x - getX(0);

			transform.makeTranslate(r, 0.0, 0.0);
			pm.matrixMult(transform);

			xs[0] = x;
			transformX += r;

			break;

		case R.id.menu_move_y:
			r = y - getY(0); // TODO: Maybe judge negativity based on what
								// quadrant the cursor is in relative to the
								// origin (inverse if y - originY <0)

			transform.makeTranslate(0.0, r, 0.0);
			pm.matrixMult(transform);

			ys[0] = y;
			transformY += r;

			break;
		/*
		 * case R.id.menu_move_z: TODO: Add z-functionality by rotating 45
		 * degrees onDown
		 * 
		 * double r = EdgeMatrix.distance(get(0), 0, x, 0);
		 * 
		 * if(x < getX(0)) r *= -1;
		 * 
		 * transform.ident(); transform.makeTranslate(r, 0.0, 0.0);
		 * pm.matrixMult(transform);
		 * 
		 * x[0] = x; break;
		 */
		case R.id.menu_move_all:
			r = x - getX(0);
			r2 = y - getY(0);

			transform.makeTranslate(r, r2, 0.0);
			pm.matrixMult(transform);

			xs[0] = x;
			ys[0] = y;
			transformX += r;
			transformY += r2;

			break;

		case R.id.menu_scale_x:
			r = x - getX(0);

			transform.makeScaleX(r);
			pm.matrixMult(transform);

			xs[0] = x;
			transformX += r;

			break;

		case R.id.menu_scale_y:
			r = y - getY(0);

			transform.makeScaleY(r);
			pm.matrixMult(transform);

			ys[0] = y;
			transformY += r;

			break;
		/*
		 * case R.id.menu_scale_z:
		 */

		case R.id.menu_scale_all:
			r = x - getX(0);
			r2 = y - getY(0);

			transform.makeScaleX(r);
			transform2.makeScaleY(r2);
			transform.matrixMult(transform2);

			pm.matrixMult(transform);

			xs[0] = x;
			ys[0] = y;
			transformX += r;
			transformY += r2;

			break;

		case R.id.menu_rotate_x:
			r = y - getY(0);

			transform.makeScaleX(r);
			pm.matrixMult(transform);

			ys[0] = y;
			transformX += r;

			break;

		case R.id.menu_rotate_y:
			r = x - getX(0);

			transform.makeScaleY(r);
			pm.matrixMult(transform);

			xs[0] = x;
			transformY += r;

			break;
		/*
		 * case R.id.menu_rotate_z:
		 */
		case R.id.menu_rotate_all:
			r = x - getX(0);
			r2 = y - getY(0);

			transform.makeRotX(r2);
			transform2.makeRotY(r);
			transform.matrixMult(transform2);

			pm.matrixMult(transform);

			xs[0] = x;
			ys[0] = y;
			transformX += r2;
			transformY += r;

			break;
		}

	}

	public void onUp(double x, double y, int tool) {

	}

	public double getX(int i) {
		return xs[i];
	}

	public double getY(int i) {
		return ys[i];
	}

	
	// To cut down on processing, the preview layer only draws polygonal wireframes.
	public void drawPolygons(Canvas canvas) {
		this.canvas = canvas;
		int i = 0;

		int shade;

		for (int j = 0; j < colors.size()/* i < pm.getLastCol() - 2 */; j++) {

			while (i < endIndices.get(j) - 2) {

				if (pm.calculateDot(i, frame.view, R.id.shade_flat) > 0) {

					shade = colors.get(j);

					drawLine((int) pm.getX(i), (int) pm.getY(i),
							(int) pm.getZ(i), (int) pm.getX(i + 1),
							(int) pm.getY(i + 1), (int) pm.getZ(i + 1), shade,
							shade);
					drawLine((int) pm.getX(i + 1), (int) pm.getY(i + 1),
							(int) pm.getZ(i + 1), (int) pm.getX(i + 2),
							(int) pm.getY(i + 2), (int) pm.getZ(i + 2), shade,
							shade);
					drawLine((int) pm.getX(i + 2), (int) pm.getY(i + 2),
							(int) pm.getZ(i + 2), (int) pm.getX(i),
							(int) pm.getY(i), (int) pm.getZ(i), shade, shade);

				}

				i += 3;
			}

		}
	}

}
