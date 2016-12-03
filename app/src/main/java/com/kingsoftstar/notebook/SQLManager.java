package com.kingsoftstar.notebook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KingSoftStar on 2016/12/2.
 */

public class SQLManager extends SQLiteOpenHelper {

    private static final String CREATE_NOTE = "create table notebook ("
            + "id text primary key,"
            + "title text,"
            + "create_time text,"
            + "edit_time text,"
            + "content text)";
    private static SQLManager sqlManager;
    private static SQLiteDatabase sqLiteDatabase;
    private Context mContext;

    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use to open or create the database
     * @param name    of the database file, or null for an in-memory database
     * @param factory to use for creating cursor objects, or null for the default
     * @param version number of the database (starting at 1); if the database is older,
     *                {@link #onUpgrade} will be used to upgrade the database; if the database is
     *                newer, {@link #onDowngrade} will be used to downgrade the database
     */
    SQLManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    /**
     * @param context
     * @param fileName
     * @param version
     * @return
     */
    static List<Note> GetNoteList(Context context, String fileName, int version) {
        List<Note> notes = new ArrayList<>();
        sqlManager = new SQLManager(context, fileName, null, version);
        sqLiteDatabase = sqlManager.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("notebook", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note(cursor.getString(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("title")),
                        cursor.getString(cursor.getColumnIndex("create_time")),
                        cursor.getString(cursor.getColumnIndex("edit_time")),
                        cursor.getString(cursor.getColumnIndex("content")));
                notes.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }

    /**
     * @param context
     * @param fileName
     * @param version
     * @param note
     */
    static boolean AddNote(Context context, String fileName, int version, Note note) {
        if (sqlManager == null) {
            sqlManager = new SQLManager(context, fileName, null, version);
        }
        sqLiteDatabase = sqlManager.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", note.getIdentify());
        values.put("title", note.getTitle());
        values.put("create_time", note.getCreateTime());
        values.put("edit_time", note.getEditTime());
        values.put("content", note.getContent());
        return sqLiteDatabase.insert("notebook", null, values) != -1;
    }

    /**
     * @param context
     * @param fileName
     * @param version
     * @param note
     * @return 更新成功返回true，失败返回false
     */
    static boolean UpdateNote(Context context, String fileName, int version, Note note) {
        boolean ret = false;
        if (sqlManager == null) {
            sqlManager = new SQLManager(context, fileName, null, version);
        }
        sqLiteDatabase = sqlManager.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query("notebook", new String[]{"id"}, "id = ?", new String[]{note.getIdentify()}, null, null, null);
        if (cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put("title", note.getTitle());
            values.put("edit_time", note.getEditTime());
            try {
                values.put("content", note.getContent());
            } catch (Exception e) {
            }
            ret = sqLiteDatabase.update("notebook", values, "id = ?", new String[]{note.getIdentify()}) > 0;
        } else {
            ret = AddNote(context, fileName, version, note);
        }
        cursor.close();
        return ret;
    }

    /**
     * @param context
     * @param fileName
     * @param version
     * @param whereClause
     * @param whereArgs
     */
    static void DeleteNote(Context context, String fileName, int version, String whereClause, String[] whereArgs) {
        if (sqlManager == null) {
            sqlManager = new SQLManager(context, fileName, null, version);
        }
        sqLiteDatabase = sqlManager.getWritableDatabase();
        sqLiteDatabase.delete("notebook", whereClause, whereArgs);
    }

    /**
     * @param context
     * @param fileName
     * @param version
     * @param noteIdentify
     * @return
     */
    static Note QueryNote(Context context, String fileName, int version, String noteIdentify) {
        Note note = null;
        if (sqlManager == null) {
            sqlManager = new SQLManager(context, fileName, null, version);
        }
        sqLiteDatabase = sqlManager.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("notebook", null, "identify=?", new String[]{noteIdentify}, null, null, null);
        if (cursor.moveToFirst()) {
            note = new Note(cursor.getString(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("title")),
                    cursor.getString(cursor.getColumnIndex("create_time")),
                    cursor.getString(cursor.getColumnIndex("edit_time")),
                    cursor.getString(cursor.getColumnIndex("content")),
                    cursor.getInt(cursor.getColumnIndex("encryption")) != 0,
                    cursor.getString(cursor.getColumnIndex("password")));
        }
        cursor.close();
        return note;
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
        }
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NOTE);
        Toast.makeText(mContext, "SQL Create Succeed", Toast.LENGTH_SHORT).show();
    }
}
