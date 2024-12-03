/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.CharArrayMap;
import com.atlassian.lucene36.analysis.CharArraySet;
import com.atlassian.lucene36.util.IOUtils;
import com.atlassian.lucene36.util.Version;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class WordlistLoader {
    private static final int INITITAL_CAPACITY = 16;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static CharArraySet getWordSet(Reader reader, CharArraySet result) throws IOException {
        BufferedReader br = null;
        try {
            br = WordlistLoader.getBufferedReader(reader);
            String word = null;
            while ((word = br.readLine()) != null) {
                result.add(word.trim());
            }
        }
        catch (Throwable throwable) {
            IOUtils.close(br);
            throw throwable;
        }
        IOUtils.close(br);
        return result;
    }

    public static CharArraySet getWordSet(Reader reader, Version matchVersion) throws IOException {
        return WordlistLoader.getWordSet(reader, new CharArraySet(matchVersion, 16, false));
    }

    public static CharArraySet getWordSet(Reader reader, String comment, Version matchVersion) throws IOException {
        return WordlistLoader.getWordSet(reader, comment, new CharArraySet(matchVersion, 16, false));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static CharArraySet getWordSet(Reader reader, String comment, CharArraySet result) throws IOException {
        BufferedReader br = null;
        try {
            br = WordlistLoader.getBufferedReader(reader);
            String word = null;
            while ((word = br.readLine()) != null) {
                if (word.startsWith(comment)) continue;
                result.add(word.trim());
            }
        }
        catch (Throwable throwable) {
            IOUtils.close(br);
            throw throwable;
        }
        IOUtils.close(br);
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static CharArraySet getSnowballWordSet(Reader reader, CharArraySet result) throws IOException {
        BufferedReader br = null;
        try {
            br = WordlistLoader.getBufferedReader(reader);
            String line = null;
            while ((line = br.readLine()) != null) {
                int comment = line.indexOf(124);
                if (comment >= 0) {
                    line = line.substring(0, comment);
                }
                String[] words = line.split("\\s+");
                for (int i = 0; i < words.length; ++i) {
                    if (words[i].length() <= 0) continue;
                    result.add(words[i]);
                }
            }
        }
        catch (Throwable throwable) {
            IOUtils.close(br);
            throw throwable;
        }
        IOUtils.close(br);
        return result;
    }

    public static CharArraySet getSnowballWordSet(Reader reader, Version matchVersion) throws IOException {
        return WordlistLoader.getSnowballWordSet(reader, new CharArraySet(matchVersion, 16, false));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static CharArrayMap<String> getStemDict(Reader reader, CharArrayMap<String> result) throws IOException {
        BufferedReader br = null;
        try {
            String line;
            br = WordlistLoader.getBufferedReader(reader);
            while ((line = br.readLine()) != null) {
                String[] wordstem = line.split("\t", 2);
                result.put(wordstem[0], wordstem[1]);
            }
        }
        catch (Throwable throwable) {
            IOUtils.close(br);
            throw throwable;
        }
        IOUtils.close(br);
        return result;
    }

    private static BufferedReader getBufferedReader(Reader reader) {
        return reader instanceof BufferedReader ? (BufferedReader)reader : new BufferedReader(reader);
    }
}

