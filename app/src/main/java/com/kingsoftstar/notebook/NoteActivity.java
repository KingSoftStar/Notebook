package com.kingsoftstar.notebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NoteActivity extends AppCompatActivity implements View.OnClickListener {

    Note mNote;
    EditText mET_Title, mET_Content;
    boolean isChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnCreateContextMenuListener(this);

        FloatingActionButton save = (FloatingActionButton) findViewById(R.id.note_save);
        save.setOnClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mET_Title = (EditText) findViewById(R.id.edit_text_note_title);

        mET_Content = (EditText) findViewById(R.id.edit_text_note_content);

        Intent intent = getIntent();
        mNote = (Note) intent.getSerializableExtra("noteIdentify");
        if (mNote == null) {
            mNote = new Note();
        }
        mET_Title.setText(mNote.getTitle());
        try {
            mET_Content.setText(mNote.getContent(null));
        } catch (Exception e) {
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        saveNote();
    }

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.
     * <p>
     * <p>This is only called once, the first time the options menu is
     * displayed.  To update the menu every time it is displayed, see
     * {@link #onPrepareOptionsMenu}.
     * <p>
     * <p>The default implementation populates the menu with standard system
     * menu items.  These are placed in the {@link Menu#CATEGORY_SYSTEM} group so that
     * they will be correctly ordered with application-defined menu items.
     * Deriving classes should always call through to the base implementation.
     * <p>
     * <p>You can safely hold on to <var>menu</var> (and any items created
     * from it), making modifications to it as desired, until the next
     * time onCreateOptionsMenu() is called.
     * <p>
     * <p>When you add items to the menu, you can implement the Activity's
     * {@link #onOptionsItemSelected} method to handle them there.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_menu, menu);
        return true;
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        if (isChanged) {
            Intent intent = new Intent();
            intent.putExtra("return", mNote.getIdentify());
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     * <p>
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.</p>
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.note_menu_save:
                saveNote();
                break;
            case R.id.note_menu_encryption:
                Toast.makeText(this, getString(R.string.unDevelopment), Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void saveNote() {
        if (String.valueOf(mET_Title.getText()).trim().isEmpty()) {
            mNote.setTitle(getString(R.string.empty_title));
        } else {
            mNote.setTitle(String.valueOf(mET_Title.getText()));
        }
        mNote.setContent(String.valueOf(mET_Content.getText()));
        SQLManager.UpdateNote(this, "notebook.db", 1, mNote);
        isChanged = true;
        Toast.makeText(this, getString(R.string.save_succeed), Toast.LENGTH_SHORT).show();
    }
}
