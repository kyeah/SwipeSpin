package com.kyeah.android.animake;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

/* TODO: Add layer button, Color picker function, Share button */
/* Add light buttons, Add ambient options menu (sliders / input) */
/* IDEA: Allow polygon mesh imports */

public class Animake extends Activity implements OnNavigationListener {

	private Frame frame;
	private int lastLayer;
	private ArrayAdapter<String> spinnerAdapter;
	// private ArrayList<Frame> frames;
	
	private static final int MAX_LAYERS = 16;
	
	private final int FILE_GROUP = 10;
	private final int SHADE_GROUP = 20;
	private final int TT_GROUP = 30;
	private final int INPUT_GROUP = 40;
	
	
	/** ========== public onCreate() ==========
	 * 
	 * @param: Bundle savedInstanceState
	 * @returns: void
	 * 
	 * 			Sets up a new Animake application.
	 * 			
	 * 			===============================
	 */
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// Set full screen view
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// Set up the main layout and add the base origin DrawSurface
		frame = new Frame(this);
		setContentView(frame);
		frame.requestFocus();

		lastLayer = 0;

		actionBarSetup();
		addLayer();
		frame.setLayer(0);

	}

	
	/**
	 * Sets the actionBar to hide application name and icon 
	 * Creates the origin spinner
	 */
	public void actionBarSetup() {

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item);

		actionBar.setListNavigationCallbacks(spinnerAdapter, this);

	}

	
	/**
	 * Adds a new DrawSurface to the top of the screen 
	 * Adds the new origin to the origin spinner
	 * 
	 * @returns true if successful
	 */
	public boolean addLayer() {

		if (lastLayer >= MAX_LAYERS)
			return false;

		frame.addLayer( );

		// Add new layer to the Spinner
		spinnerAdapter.add("Layer: " + lastLayer);
		spinnerAdapter.notifyDataSetChanged();

		lastLayer++;

		return true;
	}

	
	/**
	 * Sets the selected origin surface to max opacity 
	 * Sets all other surfaces to half opacity
	 * 
	 * @returns true
	 */
	public boolean onNavigationItemSelected(int position, long itemId) {

		return frame.setLayer(position);
	}

	
	/**
	 * Sets up the overflow menu items 
	 * Inflates the action items defined in R.layout.menu
	 * 
	 * @returns true if the options menu is successfully created
	 */
	public boolean onCreateOptionsMenu(Menu menu) {

		// Possible to add share menu in the future

		SubMenu fileMenu = menu.addSubMenu("File");
		SubMenu ttMenu = menu.addSubMenu("Transform Target");
		SubMenu shadeMenu = menu.addSubMenu("Shading");
		SubMenu inputMenu = menu.addSubMenu("Input Method");

		fileMenu.add(FILE_GROUP, R.id.file_new, 0, "New");
		fileMenu.add(FILE_GROUP, R.id.file_open, 1, "Open");
		fileMenu.add(FILE_GROUP, R.id.file_save, 2, "Save");

		// Should add Light option and change to checkbox buttons
		// Should also add "add Light" option somewhere...
		// and figure out Light rgb * constants thing... (ambient intensity: 0-1, diffuse 0-1, spec 0-1 etc)
		// AHA! Use light R * Diffconstant or light R * specConstant, but solve for base color use ambient light first (object R * ambientConstant * light R)
		// I think
		
		ttMenu.add(TT_GROUP, R.id.tt_points, 0, "Points");
		ttMenu.add(TT_GROUP, R.id.tt_origin, 1, "Origin");
		ttMenu.add(TT_GROUP, R.id.tt_both, 2, "Origin & Points");
		ttMenu.setGroupCheckable(TT_GROUP, true, true);

		shadeMenu.add(SHADE_GROUP, R.id.shade_wireframe, 0, "Wireframe");
		shadeMenu.add(SHADE_GROUP, R.id.shade_disco, 1, "Disco");
		shadeMenu.add(SHADE_GROUP, R.id.shade_flat, 2, "Flat");
		shadeMenu.add(SHADE_GROUP, R.id.shade_gouraud, 3, "Gouraud");
		shadeMenu.add(SHADE_GROUP, R.id.shade_phong, 4, "Phong");
		shadeMenu.setGroupCheckable(SHADE_GROUP, true, true);

		// Might not need this setting; ask for input value in action view and
		// allow freehand if no value is given (could be better to have both for
		// quicker freehand)

		inputMenu.add(INPUT_GROUP, R.id.input_freehand, 0,
				"Freehand / Through Point");
		inputMenu.add(INPUT_GROUP, R.id.input_by_value, 1, "Input Value");
		inputMenu.setGroupCheckable(INPUT_GROUP, true, true);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.menu, menu);

		return true;
	}

	
	/** 
	 * Handles options menu selections
	 * 
	 * @returns true if valid item was selected; false otherwise
	 */
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		/* ACTION ITEM SELECTED */
		case R.id.menu_box:
		case R.id.menu_sphere:
		case R.id.menu_torus:
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
			frame.setTool( item.getItemId() );
			break;
			
		/* OVERFLOW ITEM SELECTED */
		case R.id.file_new:
			break;

		case R.id.file_open:
			break;

		case R.id.file_save:
			break;

		case R.id.shade_wireframe:
		case R.id.shade_disco:
		case R.id.shade_flat:
		case R.id.shade_gouraud:
		case R.id.shade_phong:
			frame.setInterp(item.getItemId());
		case R.id.tt_points:
		case R.id.tt_origin:
		case R.id.tt_both:
			if (!item.isChecked())
				item.setChecked(true);
			break;

		default:
			return false;

		}
		
		return true;
	}
}