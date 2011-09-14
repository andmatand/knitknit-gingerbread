/*
 * Copyright 2011 Andrew Anderson
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 	-Redistributions of source code must retain the above copyright notice,
 * 	 this list of conditions and the following disclaimer.
 * 
 * 	-Redistributions in binary form must reproduce the above copyright
 * 	 notice, this list of conditions and the following disclaimer in the
 * 	 documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

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
	private DatabaseHelper mDatabaseHelper;
	private static final int ADD_ID = Menu.FIRST;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Open database
		mDatabaseHelper = new DatabaseHelper(this);
		mDatabaseHelper.open();

		setContentView(R.layout.projectlist);

		//Log.w("spam", "set content view");

		// Fill the list with the projects from the database
		fillList();
	}

	private void fillList() {
		// Get a cursor over a list of all projects
		Cursor cursor = mDatabaseHelper.selectAllProjects();
		startManagingCursor(cursor);

		// Create an array to specify the fields we want to display in
		// the list
		String[] from = new String[] {
			DatabaseHelper.PROJECT_KEY_NAME};

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
			createProject();
			return true;
		}

		return super.onMenuItemSelected(featureID, item);
	}

	private void createProject() {
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
				mDatabaseHelper.insertProject(
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
