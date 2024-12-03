/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.CharArraySet;
import com.atlassian.lucene36.analysis.ReusableAnalyzerBase;
import com.atlassian.lucene36.analysis.WordlistLoader;
import com.atlassian.lucene36.util.IOUtils;
import com.atlassian.lucene36.util.Version;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class StopwordAnalyzerBase
extends ReusableAnalyzerBase {
    protected final CharArraySet stopwords;
    protected final Version matchVersion;

    public Set<?> getStopwordSet() {
        return this.stopwords;
    }

    protected StopwordAnalyzerBase(Version version, Set<?> stopwords) {
        this.matchVersion = version;
        this.stopwords = stopwords == null ? CharArraySet.EMPTY_SET : CharArraySet.unmodifiableSet(CharArraySet.copy(version, stopwords));
    }

    protected StopwordAnalyzerBase(Version version) {
        this(version, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected static CharArraySet loadStopwordSet(boolean ignoreCase, Class<? extends ReusableAnalyzerBase> aClass, String resource, String comment) throws IOException {
        CharArraySet charArraySet;
        Reader reader = null;
        try {
            reader = IOUtils.getDecodingReader(aClass.getResourceAsStream(resource), IOUtils.CHARSET_UTF_8);
            charArraySet = WordlistLoader.getWordSet(reader, comment, new CharArraySet(Version.LUCENE_31, 16, ignoreCase));
        }
        catch (Throwable throwable) {
            IOUtils.close(reader);
            throw throwable;
        }
        IOUtils.close(reader);
        return charArraySet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected static CharArraySet loadStopwordSet(File stopwords, Version matchVersion) throws IOException {
        CharArraySet charArraySet;
        Reader reader = null;
        try {
            reader = IOUtils.getDecodingReader(stopwords, IOUtils.CHARSET_UTF_8);
            charArraySet = WordlistLoader.getWordSet(reader, matchVersion);
        }
        catch (Throwable throwable) {
            IOUtils.close(reader);
            throw throwable;
        }
        IOUtils.close(reader);
        return charArraySet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected static CharArraySet loadStopwordSet(Reader stopwords, Version matchVersion) throws IOException {
        CharArraySet charArraySet;
        try {
            charArraySet = WordlistLoader.getWordSet(stopwords, matchVersion);
        }
        catch (Throwable throwable) {
            IOUtils.close(stopwords);
            throw throwable;
        }
        IOUtils.close(stopwords);
        return charArraySet;
    }
}

