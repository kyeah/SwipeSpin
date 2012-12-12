package com.kyeah.android.animake;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.kyeah.android.math.EdgeMatrix;
import com.kyeah.android.math.Matrix;

/* Add-on: Extra EdgeMatrix for 2D graphics 
 * 		> import addCircle, addLine etc. from graphics/final/EdgeMatrix
 * 		> Figure out a way to rotate the scene 45-90 degrees? to allow for freehand z-extensions during shape creation
 * */

public class Layer {

	Matrix origin;
	EdgeMatrix pm;
	Paint paint;
	ArrayList<Integer> endIndices;
	ArrayList<Integer> colors;
	Canvas canvas;
	Frame frame;

	public Layer(Frame frame) {
		origin = new Matrix();
		origin.ident();
		
		pm = new EdgeMatrix();
		this.frame = frame;

		paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);

		endIndices = new ArrayList<Integer>();
		colors = new ArrayList<Integer>();
	}

	public Matrix getOrigin() {
		return origin;
	}

	public double getZ() {
		return origin.getZ();
	}

	public void setAlpha(int a) {
		paint.setAlpha(a);
	}

	public EdgeMatrix getEdgeMat() {
		return pm;
	}

	public void drawPoints(Canvas canvas) {
		for (int i = 0; i < pm.getLastCol(); i++)
			canvas.drawPoint((int) pm.getX(i), (int) pm.getY(i), paint);

	}

	public int calculateShade(EdgeMatrix pm, int i, int r, int g, int b) {

		double iDiffuse, iSpec, iNet, iR, iG, iB;
		iR = iG = iB = 0;

		if (frame.interp == R.id.shade_flat
				|| frame.interp == R.id.shade_gouraud
				|| frame.interp == R.id.shade_phong) {

			iDiffuse = pm
					.calculateDiffuse(i, frame.lights.get(0), frame.interp); /* TODO:
																			 * If
																			 * multiple
																			 * lights
																			 * ,
																			 * need
																			 * to
																			 * find
																			 * max
																			 * iDiffuse
																			 * out
																			 * of
																			 * them
																			 */

			iSpec = pm.calculateSpecular(i, frame.lights.get(0), frame.view,
					frame.interp);
			iNet = iDiffuse + iSpec;

			iR = frame.lights.get(0).getRed() * iNet;
			iG = frame.lights.get(0).getGreen() * iNet;
			iB = frame.lights.get(0).getBlue() * iNet;

		}

		else if (frame.interp == R.id.shade_disco) {
			r = (r + 30) % 255;
			g = (g + 50) % 255;
			b = (b + 70) % 255;
			return Color.rgb(r, g, b);
		} else {
			throw new IllegalArgumentException("Shading method not found.");
		}

		// Add ambient
		iR += frame.ambientR;
		iG += frame.ambientG;
		iB += frame.ambientB;

		r *= iR;
		g *= iG;
		b *= iB;

		if (r > 255)
			r = 255;
		if (g > 255)
			g = 255;
		if (b > 255)
			b = 255;
		if (r < 0)
			r = 0;
		if (g < 0)
			g = 0;
		if (b < 0)
			b = 0;

		return Color.rgb(r, g, b);

	}

	public void drawPolygons(Canvas canvas) {
		this.canvas = canvas;
		int i = 0;

		int r, g, b;
		int shade, shade0, shade1, shade2;

		for (int j = 0; j < colors.size()/* i < pm.getLastCol() - 2 */; j++) {

			r = Color.red(colors.get(j));
			g = Color.green(colors.get(j));
			b = Color.blue(colors.get(j));

			while (i < endIndices.get(j) - 2) {

				if (pm.calculateDot(i, frame.view, R.id.shade_flat) > 0) {

					// Shaded
					if (frame.interp == R.id.shade_gouraud
							|| frame.interp == R.id.shade_phong) {
						shade0 = calculateShade(pm, i, r, g, b);
						shade1 = calculateShade(pm, i + 1, r, g, b);
						shade2 = calculateShade(pm, i + 2, r, g, b);

						drawLine((int) pm.getX(i), (int) pm.getY(i),
								(int) pm.getZ(i), (int) pm.getX(i + 1),
								(int) pm.getY(i + 1), (int) pm.getZ(i + 1),
								shade0, shade1);
						drawLine((int) pm.getX(i + 1), (int) pm.getY(i + 1),
								(int) pm.getZ(i + 1), (int) pm.getX(i + 2),
								(int) pm.getY(i + 2), (int) pm.getZ(i + 2),
								shade1, shade2);
						drawLine((int) pm.getX(i + 2), (int) pm.getY(i + 2),
								(int) pm.getZ(i + 2), (int) pm.getX(i),
								(int) pm.getY(i), (int) pm.getZ(i), shade2,
								shade0);

						scanLine((int) pm.getX(i), (int) pm.getY(i),
								(int) pm.getZ(i), (int) pm.getX(i + 1),
								(int) pm.getY(i + 1), (int) pm.getZ(i + 1),
								(int) pm.getX(i + 2), (int) pm.getY(i + 2),
								(int) pm.getZ(i + 2), shade0, shade1, shade2);

						/*
						 * Color redsss = new Color(255, 0, 0); drawLine(
						 * (int)pm.getX(i), (int)pm.getY(i), (int)pm.getZ(i),
						 * (int)pm.getX(i+1), (int)pm.getY(i+1),
						 * (int)pm.getZ(i+1), redsss, redsss); drawLine(
						 * (int)pm.getX(i+1), (int)pm.getY(i+1),
						 * (int)pm.getZ(i+1), (int)pm.getX(i+2),
						 * (int)pm.getY(i+2), (int)pm.getZ(i+2), redsss,
						 * redsss); drawLine( (int)pm.getX(i+2),
						 * (int)pm.getY(i+2), (int)pm.getZ(i+2),
						 * (int)pm.getX(i), (int)pm.getY(i), (int)pm.getZ(i),
						 * redsss, redsss);
						 */
						continue;
					}

					else if (frame.interp == R.id.shade_flat
							|| frame.interp == R.id.shade_disco) {
						shade = calculateShade(pm, i, r, g, b);

						// TODO: Fix this thing
						if (frame.interp == R.id.shade_disco) {
							r = Color.red(shade);
							g = Color.green(shade);
							b = Color.blue(shade);
						}

						scanLine((int) pm.getX(i), (int) pm.getY(i),
								(int) pm.getZ(i), (int) pm.getX(i + 1),
								(int) pm.getY(i + 1), (int) pm.getZ(i + 1),
								(int) pm.getX(i + 2), (int) pm.getY(i + 2),
								(int) pm.getZ(i + 2), shade, shade, shade);
					}

					// Wireframe
					else {
						shade = Color.rgb(r, g, b);
					}

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
	
	public EdgeMatrix sortPoints(int x0, int y0, int z0, int x1, int y1,
			int z1, int x2, int y2, int z2) {

		EdgeMatrix mat = new EdgeMatrix(3);
		int yMin;

		yMin = (int) Math.min(Math.min(y0, y1), y2);

		if (yMin == y0) {
			mat.addPoint(x0, y0, z0);
			if (y1 < y2) {
				mat.addPoint(x1, y1, z1);
				mat.addPoint(x2, y2, z2);
			} else {
				mat.addPoint(x2, y2, z2);
				mat.addPoint(x1, y1, z1);
			}
		}

		else if (yMin == y1) {
			mat.addPoint(x1, y1, z1);
			if (y0 < y2) {
				mat.addPoint(x0, y0, z0);
				mat.addPoint(x2, y2, z2);
			} else {
				mat.addPoint(x2, y2, z2);
				mat.addPoint(x0, y0, z0);
			}
		} else {
			mat.addPoint(x2, y2, z2);
			if (y1 < y0) {
				mat.addPoint(x1, y1, z1);
				mat.addPoint(x0, y0, z0);
			} else {
				mat.addPoint(x0, y0, z0);
				mat.addPoint(x1, y1, z1);
			}
		}

		return mat;
	}

	public void scanLine(int x0, int y0, int z0, int x1, int y1, int z1,
			int x2, int y2, int z2, int c0, int c1, int c2) {

		EdgeMatrix points;
		int xMid, xTop, y, yMid, yMax, zMid, zTop;
		double x, rowEnd, z, zEnd, slopeBM, slopeBT, slopeMT, zBT, zBM, zMT;

		double red, green, blue, 
		
			   redMid, greenMid, blueMid, 
			   redTop, greenTop, blueTop, 
			   redEnd, greenEnd, blueEnd, 
			   
			   redBT, redBM, redMT, 
			   greenBT, greenBM, greenMT, 
			   blueBT, blueBM, blueMT;
		
		int cMin, cMid, cMax;

		// Instantiate to get compiler out of your patootie
		xMid = xTop = y = yMid = zMid = zTop = 0;
		rowEnd = x = z = zEnd = 0.0;
		cMin = -1;
		cMid = -1;
		cMax = -1;

		/* SORT POINTS */
		points = sortPoints(x0, y0, z0, x1, y1, z1, x2, y2, z2);

		x = rowEnd = (int) points.getX(0);
		xMid = (int) points.getX(1);
		xTop = (int) points.getX(2);

		y = (int) points.getY(0);
		yMid = (int) points.getY(1);
		yMax = (int) points.getY(2);

		z = zEnd = (int) points.getZ(0);
		zMid = (int) points.getZ(1);
		zTop = (int) points.getZ(2);
		/* END SORTING */

		/* SORT COLORS */
		if (x == x0 && y == y0 && z == z0) {
			cMin = c0;
			c0 = -1;
		} else if (x == x1 && y == y1 && z == z1) {
			cMin = c1;
			c1 = -1;
		} else {
			cMin = c2;
			c2 = -1;
		}

		if (c0 != -1 && xMid == x0 && yMid == y0 && zMid == z0) {
			cMid = c0;
			c0 = -1;
		} else if (c1 != -1 && xMid == x1 && yMid == y1 && zMid == z1) {
			cMid = c1;
			c1 = -1;
		} else if (c2 != -1 && xMid == x2 && yMid == y2 && zMid == z2) {
			cMid = c2;
			c2 = -1;
		}

		if (c0 != -1 && xTop == x0 && yMax == y0 && zTop == z0)
			cMax = c0;
		else if (c1 != -1 && xTop == x1 && yMax == y1 && zTop == z1)
			cMax = c1;
		else if (c2 != -1 && xTop == x2 && yMax == y2 && zTop == z2)
			cMax = c2;

		red = redEnd = Color.red(cMin);
		redMid = Color.red(cMid);
		redTop = Color.red(cMax);

		green = greenEnd = Color.green(cMin);
		greenMid = Color.green(cMid);
		greenTop = Color.green(cMax);

		blue = blueEnd = Color.blue(cMin);
		blueMid = Color.blue(cMid);
		blueTop = Color.blue(cMax);

		/* END COLOR SORT */

		slopeBT = (xTop - x) / (yMax - y);
		zBT = (zTop - z) / (yMax - y);

		redBT = (redTop - red) / (yMax - y);
		greenBT = (greenTop - green) / (yMax - y);
		blueBT = (blueTop - blue) / (yMax - y);

		// Scanline upper portion of triangle
		if (yMid != y) {

			// Find slope and inverse it to find change in x per increment in y
			// direction
			slopeBM = (xMid - x) / (yMid - y);
			zBM = (zMid - z) / (yMid - y);

			redBM = (redMid - red) / (yMax - y);
			greenBM = (greenMid - green) / (yMax - y);
			blueBM = (blueMid - blue) / (yMax - y);

			// Scanline
			for (y = y + 1; y < yMid; y++) {

				x += slopeBT;
				rowEnd += slopeBM;

				z += zBT;
				zEnd += zBM;

				red += redBT;
				redEnd += redBM;

				green += greenBT;
				greenEnd += greenBM;

				blue += blueBT;
				blueEnd += blueBM;

				c0 = Color.rgb((int) red, (int) green, (int) blue);
				c1 = Color.rgb((int) redEnd, (int) greenEnd, (int) blueEnd);

				drawLine((int) x, y, (int) z, (int) rowEnd, y, (int) zEnd, c0,
						c1);

			}
		}

		// Scanline lower portion of triangle
		if (yMax != yMid) {
			slopeMT = (xTop - (double) xMid) / (yMax - yMid);
			zMT = (zTop - (double) zMid) / (yMax - yMid);

			redMT = (redTop - (double) redMid) / (yMax - yMid);
			greenMT = (greenTop - (double) greenMid) / (yMax - yMid);
			blueMT = (blueTop - (double) blueMid) / (yMax - yMid);

			rowEnd = xMid - slopeMT;
			zEnd = zMid - zMT;

			redEnd = redMid - redMT;
			greenEnd = greenMid - greenMT;
			blueEnd = blueMid - blueMT;

			for (y = yMid; y < yMax; y++) {
				x += slopeBT;
				rowEnd += slopeMT;

				z += zBT;
				zEnd += zMT;

				red += redBT;
				redEnd += redMT;

				green += greenBT;
				greenEnd += greenMT;

				blue += blueBT;
				blueEnd += blueMT;

				c0 = Color.rgb((int) red, (int) green, (int) blue);
				c1 = Color.rgb((int) redEnd, (int) greenEnd, (int) blueEnd);

				drawLine((int) x, y, (int) z, (int) rowEnd, y, (int) zEnd, c0,
						c1);

			}
		}

	}

	public boolean inBounds(int x, int y) {
		return x < frame.maxx && y < frame.maxy && x >= 0 && y >= 0;

	}

	/**
	 * Per-pixel implementation to accommodate Z-Buffering.
	 */
	public void drawLine(int x0, int y0, int z0, int x1, int y1, int z1,
			int c0, int c1) {

		int x, y, dx, dy, d;
		double z, dz, zBuf, dr, dg, db, red, green, blue;

		paint.setColor(c0);

		x = x0;
		y = y0;
		z = z0;

		if (inBounds(x, y))
			zBuf = frame.zBuffer[x][y];
		else
			zBuf = Double.POSITIVE_INFINITY;

		// swap points so we're always drawing left to right
		if (x0 > x1) {
			x = x1;
			y = y1;
			z = z1;
			x1 = x0;
			y1 = y0;
			z1 = z0;

			red = Color.red(c1);
			green = Color.green(c1);
			blue = Color.blue(c1);

			dr = Color.red(c0) - red;
			dg = Color.green(c0) - green;
			db = Color.blue(c0) - blue;
		} else {
			red = Color.red(c0);
			green = Color.green(c0);
			blue = Color.blue(c0);

			dr = Color.red(c1) - red;
			dg = Color.green(c1) - green;
			db = Color.blue(c1) - blue;
		}

		dx = x1 - x;
		dy = y1 - y;
		dz = z1 - z;

		// positive slope: Octants 1, 2 (5 and 6)
		if (dy > 0) {

			// slope < 1: Octant 1 (5)
			if (dx > dy) {
				d = 2 * dy - dx;

				while (x < x1) {

					if (c0 != c1)
						paint.setColor(Color.rgb((int) red, (int) green,
								(int) blue));

					// Check Z value and draw pixel if it is in view
					if (inBounds(x, y)) {
						zBuf = frame.zBuffer[x][y];

						if (z > zBuf) {
							canvas.drawPoint(x, y, paint);
							frame.zBuffer[x][y] = z;
						}
					}

					// Find next pixel
					if (d < 0) {
						x = x + 1;
						d = d + dy;

					} else {
						x = x + 1;
						y = y + 1;
						d = d + dy - dx;

					}

					z += dz / dx;

					red += dr / dx;
					green += dg / dx;
					blue += db / dx;

					if (red > 255)
						red = 255;
					if (green > 255)
						green = 255;
					if (blue > 255)
						blue = 255;
					if (red < 0)
						red = 0;
					if (green < 0)
						green = 0;
					if (blue < 0)
						blue = 0;

				}
			}

			// slope > 1: Octant 2 (6)
			else {
				d = dy - 2 * dx;
				while (y < y1) {

					if (c0 != c1)
						paint.setColor(Color.rgb((int) red, (int) green,
								(int) blue));

					if (inBounds(x, y)) {
						zBuf = frame.zBuffer[x][y];

						if (z > zBuf) {
							canvas.drawPoint(x, y, paint);
							frame.zBuffer[x][y] = z;
						}
					}

					if (d > 0) {
						y = y + 1;
						d = d - dx;
					} else {
						y = y + 1;
						x = x + 1;
						d = d + dy - dx;
					}

					z += dz / dy;

					red += dr / dy;
					green += dg / dy;
					blue += db / dy;

					if (red > 255)
						red = 255;
					if (green > 255)
						green = 255;
					if (blue > 255)
						blue = 255;
					if (red < 0)
						red = 0;
					if (green < 0)
						green = 0;
					if (blue < 0)
						blue = 0;
				}
			}
		}

		// negative slope: Octants 7, 8 (3 and 4)
		else {

			// slope > -1: Octant 8 (4)
			if (dx > Math.abs(dy)) {
				d = 2 * dy + dx;

				while (x < x1) {

					if (c0 != c1)
						paint.setColor(Color.rgb((int) red, (int) green,
								(int) blue));

					if (inBounds(x, y)) {
						zBuf = frame.zBuffer[x][y];

						if (z > zBuf) {
							canvas.drawPoint(x, y, paint);
							frame.zBuffer[x][y] = z;
						}
					}

					if (d > 0) {
						x = x + 1;
						d = d + dy;

					} else {
						x = x + 1;
						y = y - 1;
						d = d + dy + dx;

					}

					z += dz / dx;

					red += dr / dx;
					green += dg / dx;
					blue += db / dx;

					if (red > 255)
						red = 255;
					if (green > 255)
						green = 255;
					if (blue > 255)
						blue = 255;
					if (red < 0)
						red = 0;
					if (green < 0)
						green = 0;
					if (blue < 0)
						blue = 0;
				}
			}

			// slope < -1: Octant 7 (3)
			else {

				d = dy + 2 * dx;
				while (y > y1) {

					if (c0 != c1)
						paint.setColor(Color.rgb((int) red, (int) green,
								(int) blue));

					if (inBounds(x, y)) {
						zBuf = frame.zBuffer[x][y];

						if (z > zBuf) {
							canvas.drawPoint(x, y, paint);
							frame.zBuffer[x][y] = z;
						}
					}

					if (d < 0) {
						y = y - 1;
						d = d + dx;
					} else {
						y = y - 1;
						x = x + 1;
						d = d + dy + dx;
					}

					z -= dz / dy;

					red -= dr / dy;
					green -= dg / dy;
					blue -= db / dy;

					if (red > 255)
						red = 255;
					if (green > 255)
						green = 255;
					if (blue > 255)
						blue = 255;
					if (red < 0)
						red = 0;
					if (green < 0)
						green = 0;
					if (blue < 0)
						blue = 0;
				}
			}
		}

	}

	// TODO: Introduce multi-threading to add shapes while drawing and UI continues (make sure the matrix is not accessed by both threads)
	// TODO: FIGURE OUT HOW TO SPEED UP DRAWING BY NOT MESSING WITH VIEWS IN DRAWTHREAD AHHHHHHHHHHHHHHHHHHHHHHHHHHHH
	public void addSphere(double cx, double cy, double cz, double r, int color) {
		pm.addSphere(cx, cy, cz, r);
		endIndices.add(pm.getLastCol());
		colors.add(color);

	}

	public void addTorus(double cx, double cy, double cz, double r1, double r2,
			int color) {
		pm.addTorus(cx, cy, cz, r1, r2);
		endIndices.add(pm.getLastCol());
		colors.add(color);

	}

	public void addBox(double x, double y, double z, double width,
			double height, double depth, int color) {

		pm.addBox(x, y, z, width, height, depth);
		endIndices.add(pm.getLastCol());
		colors.add(color);

	}

	public void addSphereMesh(double cx, double cy, double r, int color) {
		pm.addSphereMesh(cx, cy, r);
		endIndices.add(pm.getLastCol());
		colors.add(color);

	}

	public void addTorusMesh(double cx, double cy, double r1, double r2,
			int color) {
		pm.addTorusMesh(cx, cy, r1, r2);
		endIndices.add(pm.getLastCol());
		colors.add(color);
	}

	public void addBoxMesh(double x, double y, double z, double width,
			double height, double depth, int color) {
		pm.addBoxMesh(x, y, z, width, height, depth);
		endIndices.add(pm.getLastCol());
		colors.add(color);

	}

	public void append(EdgeMatrix em, int color) {
		for (int i = 0; i < em.getLastCol(); i++)
			pm.addPoint(em.getX(i), em.getY(i), em.getZ(i));

		endIndices.add(pm.getLastCol());
		colors.add(color);
	}

	public void convert(EdgeMatrix em, Matrix origin) {
		if (origin != null)
			this.origin = origin.copy();

		if (em != null)
			pm = em.copy();

	}
}
