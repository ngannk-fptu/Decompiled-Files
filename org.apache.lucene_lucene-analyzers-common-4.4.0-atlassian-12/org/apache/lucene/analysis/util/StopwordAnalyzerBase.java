/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.util.IOUtils
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.WordlistLoader;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.Version;

public abstract class StopwordAnalyzerBase
extends Analyzer {
    protected final CharArraySet stopwords;
    protected final Version matchVersion;

    public CharArraySet getStopwordSet() {
        return this.stopwords;
    }

    protected StopwordAnalyzerBase(Version version, CharArraySet stopwords) {
        this.matchVersion = version;
        this.stopwords = stopwords == null ? CharArraySet.EMPTY_SET : CharArraySet.unmodifiableSet(CharArraySet.copy(version, stopwords));
    }

    protected StopwordAnalyzerBase(Version version) {
        this(version, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected static CharArraySet loadStopwordSet(boolean ignoreCase, Class<? extends Analyzer> aClass, String resource, String comment) throws IOException {
        CharArraySet charArraySet;
        Reader reader = null;
        try {
            reader = IOUtils.getDecodingReader((InputStream)aClass.getResourceAsStream(resource), (Charset)IOUtils.CHARSET_UTF_8);
            charArraySet = WordlistLoader.getWordSet(reader, comment, new CharArraySet(Version.LUCENE_31, 16, ignoreCase));
        }
        catch (Throwable throwable) {
            IOUtils.close((Closeable[])new Closeable[]{reader});
            throw throwable;
        }
        IOUtils.close((Closeable[])new Closeable[]{reader});
        return charArraySet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected static CharArraySet loadStopwordSet(File stopwords, Version matchVersion) throws IOException {
        CharArraySet charArraySet;
        Reader reader = null;
        try {
            reader = IOUtils.getDecodingReader((File)stopwords, (Charset)IOUtils.CHARSET_UTF_8);
            charArraySet = WordlistLoader.getWordSet(reader, matchVersion);
        }
        catch (Throwable throwable) {
            IOUtils.close((Closeable[])new Closeable[]{reader});
            throw throwable;
        }
        IOUtils.close((Closeable[])new Closeable[]{reader});
        return charArraySet;
    }

    protected static CharArraySet loadStopwordSet(Reader stopwords, Version matchVersion) throws IOException {
        CharArraySet charArraySet;
        try {
            charArraySet = WordlistLoader.getWordSet(stopwords, matchVersion);
        }
        catch (Throwable throwable) {
            IOUtils.close((Closeable[])new Closeable[]{stopwords});
            throw throwable;
        }
        IOUtils.close((Closeable[])new Closeable[]{stopwords});
        return charArraySet;
    }
}

