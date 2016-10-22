package cloud1001.antidictionary;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Created by aylish on 10/22/16.
 * Modified version of code used in Android tutorial
 * https://developer.android.com/training/search/search.html
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

        private static final String[] NON_KEY_WORDS = {"a", "an", "and", "as", "at", "but", "by", "for", "from",
            "in", "is", "nor", "of", "on", "or", "so", "the", "to"};

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
            InputStream input = mHelperContext.getAssets().open("dictionary.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] info = TextUtils.split(line, "|");
                    if (info.length < 3) {
                        continue;
                    }
                    long id = addWord(info[0].trim(), info[1].trim(), info[2].trim());
                    if (id < 0) {
                        Log.e(TAG, "unable to add word: " + info[0].trim());
                    }
                }
            }
            finally {
                reader.close();
            }
        }

        public long addWord(String word, String definition, String synonyms) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(COL_WORD, word);
            initialValues.put(COL_DEFINITION, definition);
            String keyWords = getKeyWords(definition);
            initialValues.put(COL_KEY_WORDS, keyWords);
            initialValues.put(COL_SYNONYMS, synonyms);

            return mDatabase.insert(FTS_VIRTUAL_TABLE, null, initialValues);
        }

        public String getKeyWords (String definition) {
            String[] words = TextUtils.split(definition, " ");
            StringBuilder keyWords = new StringBuilder();

            for(int i = 0; i < words.length; i++) {
                String currentWord = cleanUpWord(words[i]);
                boolean isKey = isKeyWord(currentWord);
                if(isKey) {
                    keyWords.append(", " + currentWord);
                }
            }

            keyWords.deleteCharAt(0);
            return keyWords.toString();
        }

        public String cleanUpWord(String word) {
            word = word.replace(",", "");
            word = word.replace(";", "");
            return word.toLowerCase();
        }

        public boolean isKeyWord (String currentWord) {
            int index = Arrays.binarySearch(NON_KEY_WORDS, currentWord);
            if(index == -1) {
                return true;
            }
            return false;
        }

    }

}
