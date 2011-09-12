package com.example.knitknit;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;

public class ProjectList extends ListActivity {
	private DBAdapter mDBAdapter;
	private static final int ADD_ID = Menu.FIRST;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Open database
		mDBAdapter = new DBAdapter(this);
		mDBAdapter.open();

		setContentView(R.layout.projectlist);

		// Fill the list with the projects from the database
		fillList();
	}

	private void fillList() {
		// Get a cursor over a list of all projects
		Cursor cursor = mDBAdapter.selectAllProjects();
		startManagingCursor(cursor);

		// Create an array to specify the fields we want to display in
		// the list
		String[] from = new String[] {
			DBAdapter.PROJECT_KEY_NAME};

		// Create and an array of the fields we want to bind those
		// fields to
		int[] to = new int[] {
			R.id.projectlist_row_name};

		// Now create a simple cursor adapter and set it to display
		SimpleCursorAdapter projects = 
			new SimpleCursorAdapter(
				this, R.layout.projectlist_row, cursor,
				from, to);

		setListAdapter(projects);

		//Log.w("spam", "filled list");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, ADD_ID, 0, R.string.projectlist_add);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureID, MenuItem item) {
		switch(item.getItemId()) {
		case ADD_ID:
			createProject(this);
			return true;
		}

		return super.onMenuItemSelected(featureID, item);
	}

	private void createProject(Context context) {
		// Instantiate a view of projectlist_namedialog.xml
		LayoutInflater inflater = (LayoutInflater)
			this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.projectlist_namedialog,
			null, false);

		// Find the name EditText field
		final EditText name = (EditText)
			view.findViewById(R.id.projectlist_namedialog_name);

		DialogInterface.OnClickListener listener =
			new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				mDBAdapter.insertProject(
					name.getText().toString());
				fillList();
				return;
			}
			};

		// Create an AlertDialog and set its attributes
		AlertDialog.Builder nameDialog =
			new AlertDialog.Builder(this);
		nameDialog.setCancelable(true);
		nameDialog.setTitle(R.string.projectlist_namedialog_title);
		nameDialog.setPositiveButton(
			R.string.projectlist_namedialog_create,
			listener);

		// Fill the dialog with the view
		nameDialog.setView(view);
		nameDialog.create();

		// Show the dialog
		nameDialog.show();
	}
}