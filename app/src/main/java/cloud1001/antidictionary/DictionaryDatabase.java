package cloud1001.antidictionary;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.IOException;

/**
 * Created by aylish on 10/22/16.
 */

public class DictionaryDatabase {
    private static final String TAG = "DictionaryDatabase";

    // Columns
    public static final String COL_WORD = "WORD";
    public static final String COL_DEFINITION = "DEFINITION";
    public static final String COL_KEY_WORDS = "KEY_WORDS";
    public static final String COL_SYNONYMS = "SYNONYMS";

    private static final String DATABASE_NAME = "DICTIONARY";
    private static final String FTS_VIRTUAL_TABLE = "FTS";
    private static final int DATABASE_VERSION = 1;

    private final DatabaseOpenHelper mDatabaseOpenHelper;

    public DictionaryDatabase(Context context) {
        mDatabaseOpenHelper = new DatabaseOpenHelper(context);
    }

    private static class DatabaseOpenHelper extends SQLiteOpenHelper {
        private final Context mHelperContext;
        private SQLiteDatabase mDatabase;

        private static final String FTS_TABLE_CREATE = "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE
                + " USING fts3 (" + COL_WORD + ", " + COL_DEFINITION + ", " + COL_KEY_WORDS + ", "
                + COL_SYNONYMS + ")";

        DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mHelperContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            mDatabase = db;
            mDatabase.execSQL(FTS_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
            onCreate(db);
        }

        private void loadDictionary() {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        loadWords();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        }

        private void loadWords() throws IOException {
            
        }

    }

}