package com.kingsoftstar.notebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    NoteAdapter noteAdapter;
    ListView listView;
    private List<Note> mNotes = new ArrayList<>();
    private int mNoteIndex = -1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setOnCreateContextMenuListener(this);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.note_add);
        floatingActionButton.setOnClickListener(this);

        mNotes.addAll(SQLManager.GetNoteList(this, "Notebook.db", 1));
        Collections.sort(mNotes);
        noteAdapter = new NoteAdapter(MainActivity.this, R.layout.listview_cell_note, mNotes);
        listView = (ListView) findViewById(R.id.activity_main_listview);
        listView.setAdapter(noteAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Note note = mNotes.get(i);
                mNoteIndex = i;
                Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                intent.putExtra("noteIdentify", note);
                startActivityForResult(intent, 1);
            }
        });
        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 0, 0, R.string.list_context_menu_delete_note);
                menu.add(0, 1, 0, R.string.list_context_menu_delete_all_notes);
            }
        });
    }

    /**
     * This hook is called whenever an item in a context menu is selected. The
     * default implementation simply returns false to have the normal processing
     * happen (calling the item's Runnable or sending a message to its Handler
     * as appropriate). You can use this method for any items for which you
     * would like to do processing without those other facilities.
     * <p>
     * Use {@link MenuItem#getMenuInfo()} to get extra information set by the
     * View that added this menu item.
     * <p>
     * Derived classes should call through to the base class for it to perform
     * the default menu handling.
     *
     * @param item The context menu item that was selected.
     * @return boolean Return false to allow normal context menu processing to
     * proceed, true to consume it here.
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = menuInfo.position;
        switch (item.getItemId()) {
            case 0:
                SQLManager.DeleteNote(this, "Notebook.db", 1, "id = ?", new String[]{mNotes.get(position).getIdentify()});
                mNotes.remove(position);
                noteAdapter.notifyDataSetChanged();
                break;
            case 1:
                mNotes.clear();
                SQLManager.DeleteNote(this, "Notebook.db", 1, null, null);
                noteAdapter.notifyDataSetChanged();
                break;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Note note = (Note) data.getSerializableExtra("result");
                    if (mNoteIndex != -1) {
                        mNotes.set(mNoteIndex, note);
                    } else {
                        mNotes.add(note);
                    }
                    Collections.sort(mNotes);
                    noteAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.add:
                mNoteIndex = -1;
                intent = new Intent(MainActivity.this, NoteActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.setting:
            case R.id.about:
            default:
                Toast.makeText(this, String.format("%s %s", item.getTitle(), getString(R.string.unDevelopment)), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        mNoteIndex = -1;
        Intent intent = new Intent(MainActivity.this, NoteActivity.class);
        startActivityForResult(intent, 1);
    }
}
