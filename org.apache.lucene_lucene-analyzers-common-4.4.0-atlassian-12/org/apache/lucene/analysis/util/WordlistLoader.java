/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.IOUtils
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.util.CharArrayMap;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.Version;

public class WordlistLoader {
    private static final int INITIAL_CAPACITY = 16;

    private WordlistLoader() {
    }

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
            IOUtils.close((Closeable[])new Closeable[]{br});
            throw throwable;
        }
        IOUtils.close((Closeable[])new Closeable[]{br});
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
            IOUtils.close((Closeable[])new Closeable[]{br});
            throw throwable;
        }
        IOUtils.close((Closeable[])new Closeable[]{br});
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
            IOUtils.close((Closeable[])new Closeable[]{br});
            throw throwable;
        }
        IOUtils.close((Closeable[])new Closeable[]{br});
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
            IOUtils.close((Closeable[])new Closeable[]{br});
            throw throwable;
        }
        IOUtils.close((Closeable[])new Closeable[]{br});
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static List<String> getLines(InputStream stream, Charset charset) throws IOException {
        ArrayList<String> arrayList;
        block7: {
            BufferedReader input;
            block6: {
                input = null;
                boolean success = false;
                try {
                    input = WordlistLoader.getBufferedReader(IOUtils.getDecodingReader((InputStream)stream, (Charset)charset));
                    ArrayList<String> lines = new ArrayList<String>();
                    String word = null;
                    while ((word = input.readLine()) != null) {
                        if (lines.isEmpty() && word.length() > 0 && word.charAt(0) == '\ufeff') {
                            word = word.substring(1);
                        }
                        if (word.startsWith("#") || (word = word.trim()).length() == 0) continue;
                        lines.add(word);
                    }
                    success = true;
                    arrayList = lines;
                    if (!success) break block6;
                }
                catch (Throwable throwable) {
                    if (success) {
                        IOUtils.close((Closeable[])new Closeable[]{input});
                    } else {
                        IOUtils.closeWhileHandlingException((Closeable[])new Closeable[]{input});
                    }
                    throw throwable;
                }
                IOUtils.close((Closeable[])new Closeable[]{input});
                break block7;
            }
            IOUtils.closeWhileHandlingException((Closeable[])new Closeable[]{input});
        }
        return arrayList;
    }

    private static BufferedReader getBufferedReader(Reader reader) {
        return reader instanceof BufferedReader ? (BufferedReader)reader : new BufferedReader(reader);
    }
}

