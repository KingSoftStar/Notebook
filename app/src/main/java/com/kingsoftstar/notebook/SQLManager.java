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

    static final int CURRENT_DATABASE_VERSION = 1;
    static final String DATABASE_FILE_NAME = "Notebook.db";
    private static final String TABLE_NAME = "notebook";
    private static final String KEY_IDENTIFY = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CREATE_TIME = "create_time";
    private static final String KEY_EDIT_TIME = "edit_time";
    private static final String KEY_CONTENT = "content";
    private static final String CREATE_NOTE = "create table " + TABLE_NAME + " ("
            + KEY_IDENTIFY + " text primary key,"
            + KEY_TITLE + " text,"
            + KEY_CREATE_TIME + " text,"
            + KEY_EDIT_TIME + " text,"
            + KEY_CONTENT + " text)";
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
     * 获取指定数据库中的全部数据列表
     * @param context
     * @param fileName
     * @param version
     * @return 空列表或数据库中的所有数据
     */
    static List<Note> GetNoteList(Context context, String fileName, int version) {
        List<Note> notes = new ArrayList<>();
        sqlManager = new SQLManager(context, fileName, null, version);
        sqLiteDatabase = sqlManager.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note(cursor.getString(cursor.getColumnIndex(KEY_IDENTIFY)),
                        cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndex(KEY_CREATE_TIME)),
                        cursor.getString(cursor.getColumnIndex(KEY_EDIT_TIME)),
                        cursor.getString(cursor.getColumnIndex(KEY_CONTENT)));
                notes.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }

    /**
     * 向数据库中添加数据
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
        values.put(KEY_IDENTIFY, note.getIdentify());
        values.put(KEY_TITLE, note.getTitle());
        values.put(KEY_CREATE_TIME, note.getCreateTime());
        values.put(KEY_EDIT_TIME, note.getEditTime());
        values.put(KEY_CONTENT, note.getContent());
        return sqLiteDatabase.insert(TABLE_NAME, null, values) != -1;
    }

    /**
     * 更新数据库中的指定数据信息
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
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, new String[]{KEY_IDENTIFY}, KEY_IDENTIFY + " = ?", new String[]{note.getIdentify()}, null, null, null);
        if (cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put(KEY_TITLE, note.getTitle());
            values.put(KEY_EDIT_TIME, note.getEditTime());
            try {
                values.put(KEY_CONTENT, note.getContent());
            } catch (Exception e) {
            }
            ret = sqLiteDatabase.update(TABLE_NAME, values, KEY_IDENTIFY + " = ?", new String[]{note.getIdentify()}) > 0;
        } else {
            ret = AddNote(context, fileName, version, note);
        }
        cursor.close();
        return ret;
    }

    /**
     * 删除数据库中的指定数据
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
        sqLiteDatabase.delete(TABLE_NAME, whereClause, whereArgs);
    }

    /**
     * 在数据库中查找指定标识对应的数据
     * @param context
     * @param fileName
     * @param version
     * @param noteIdentify
     * @return null或指定标识对应的数据
     */
    static Note QueryNote(Context context, String fileName, int version, String noteIdentify) {
        Note note = null;
        if (sqlManager == null) {
            sqlManager = new SQLManager(context, fileName, null, version);
        }
        sqLiteDatabase = sqlManager.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, null, KEY_IDENTIFY + " = ?", new String[]{noteIdentify}, null, null, null);
        if (cursor.moveToFirst()) {
            note = new Note(cursor.getString(cursor.getColumnIndex(KEY_IDENTIFY)),
                    cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                    cursor.getString(cursor.getColumnIndex(KEY_CREATE_TIME)),
                    cursor.getString(cursor.getColumnIndex(KEY_EDIT_TIME)),
                    cursor.getString(cursor.getColumnIndex(KEY_CONTENT)));
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
