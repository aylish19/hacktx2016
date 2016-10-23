package cloud1001.antidictionary;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by aylish on 10/22/16.
 */

public class DictionaryUtils {
    private static final String[] NON_KEY_WORDS = {"a", "an", "and", "as", "at", "but", "by", "for", "from",
            "in", "is", "nor", "of", "on", "or", "so", "the", "to"};

    private DictionaryUtils() {

    }

    public static String[] getKeyWords (String definition) {
        String[] words = TextUtils.split(definition, " ");
        ArrayList<String> keyWords = new ArrayList<String>();

        for(int i = 0; i < words.length; i++) {
            String currentWord = cleanUpWord(words[i]);
            boolean isKey = isKeyWord(currentWord);
            if(isKey) {
                keyWords.add(currentWord);
            }
        }
        return (String[]) keyWords.toArray();
    }

    public static String keyWordsToString(String[] keyWords) {
        StringBuilder kW = new StringBuilder();
        kW.append(keyWords[0]);
        for(int i = 1; i < keyWords.length; i++) {
            kW.append(", " + keyWords[i]);
        }
        return kW.toString();
    }

    public static String cleanUpWord(String word) {
        word = word.replace(",", "");
        word = word.replace(";", "");
        return word.toLowerCase();
    }

    public static boolean isKeyWord (String currentWord) {
        int index = Arrays.binarySearch(NON_KEY_WORDS, currentWord);
        if(index == -1) {
            return true;
        }
        return false;
    }
}
