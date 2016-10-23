package cloud1001.antidictionary;

import android.text.TextUtils;

/**
 * Created by aylish on 10/22/16.
 */

public class WordFinder {
    private String[] keyWords;
    private int[] dictScores;

    public WordFinder(String description) {
        keyWords = DictionaryUtils.getKeyWords(description);
    }

    private void scoreWords() {

    }

}
