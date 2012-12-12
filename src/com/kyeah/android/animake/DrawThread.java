package com.kyeah.android.animake;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.SurfaceHolder;

public class DrawThread extends Thread {

	private SurfaceHolder surfaceHolder;
	private Frame view;
	
	private boolean running = false;

	public DrawThread(SurfaceHolder surfaceHolder, Frame panel) {
		this.surfaceHolder = surfaceHolder;
		view = panel;

	}

	public void setRunning(boolean run) {
		running = run;
	}

	@Override
	public void run() {

		Canvas c;
		while (running) {

			c = null;
			try {
				c = surfaceHolder.lockCanvas(null);
				synchronized (surfaceHolder) {
					view.onDraw(c);
					// view.mHandler.post(view.mUpdateResults);
				}
			} finally {
				// do this in a finally so that if an exception is thrown
				// during the above, we don't leave the Surface in an
				// inconsistent state
				if (c != null) {
					surfaceHolder.unlockCanvasAndPost(c);
				}
			}
		}
	}
}
