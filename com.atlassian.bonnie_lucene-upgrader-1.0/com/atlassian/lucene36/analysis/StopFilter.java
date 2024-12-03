/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.CharArraySet;
import com.atlassian.lucene36.analysis.FilteringTokenFilter;
import com.atlassian.lucene36.analysis.TokenStream;
import com.atlassian.lucene36.analysis.tokenattributes.CharTermAttribute;
import com.atlassian.lucene36.util.Version;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class StopFilter
extends FilteringTokenFilter {
    private final CharArraySet stopWords;
    private final CharTermAttribute termAtt = this.addAttribute(CharTermAttribute.class);

    @Deprecated
    public StopFilter(boolean enablePositionIncrements, TokenStream input, Set<?> stopWords, boolean ignoreCase) {
        this(Version.LUCENE_30, enablePositionIncrements, input, stopWords, ignoreCase);
    }

    @Deprecated
    public StopFilter(Version matchVersion, TokenStream input, Set<?> stopWords, boolean ignoreCase) {
        this(matchVersion, matchVersion.onOrAfter(Version.LUCENE_29), input, stopWords, ignoreCase);
    }

    private StopFilter(Version matchVersion, boolean enablePositionIncrements, TokenStream input, Set<?> stopWords, boolean ignoreCase) {
        super(enablePositionIncrements, input);
        this.stopWords = stopWords instanceof CharArraySet ? (CharArraySet)stopWords : new CharArraySet(matchVersion, stopWords, ignoreCase);
    }

    @Deprecated
    public StopFilter(boolean enablePositionIncrements, TokenStream in, Set<?> stopWords) {
        this(Version.LUCENE_30, enablePositionIncrements, in, stopWords, false);
    }

    public StopFilter(Version matchVersion, TokenStream in, Set<?> stopWords) {
        this(matchVersion, in, stopWords, false);
    }

    @Deprecated
    public static final Set<Object> makeStopSet(String ... stopWords) {
        return StopFilter.makeStopSet(Version.LUCENE_30, stopWords, false);
    }

    public static final Set<Object> makeStopSet(Version matchVersion, String ... stopWords) {
        return StopFilter.makeStopSet(matchVersion, stopWords, false);
    }

    @Deprecated
    public static final Set<Object> makeStopSet(List<?> stopWords) {
        return StopFilter.makeStopSet(Version.LUCENE_30, stopWords, false);
    }

    public static final Set<Object> makeStopSet(Version matchVersion, List<?> stopWords) {
        return StopFilter.makeStopSet(matchVersion, stopWords, false);
    }

    @Deprecated
    public static final Set<Object> makeStopSet(String[] stopWords, boolean ignoreCase) {
        return StopFilter.makeStopSet(Version.LUCENE_30, stopWords, ignoreCase);
    }

    public static final Set<Object> makeStopSet(Version matchVersion, String[] stopWords, boolean ignoreCase) {
        CharArraySet stopSet = new CharArraySet(matchVersion, stopWords.length, ignoreCase);
        stopSet.addAll(Arrays.asList(stopWords));
        return stopSet;
    }

    @Deprecated
    public static final Set<Object> makeStopSet(List<?> stopWords, boolean ignoreCase) {
        return StopFilter.makeStopSet(Version.LUCENE_30, stopWords, ignoreCase);
    }

    public static final Set<Object> makeStopSet(Version matchVersion, List<?> stopWords, boolean ignoreCase) {
        CharArraySet stopSet = new CharArraySet(matchVersion, stopWords.size(), ignoreCase);
        stopSet.addAll(stopWords);
        return stopSet;
    }

    @Override
    protected boolean accept() throws IOException {
        return !this.stopWords.contains(this.termAtt.buffer(), 0, this.termAtt.length());
    }

    @Deprecated
    public static boolean getEnablePositionIncrementsVersionDefault(Version matchVersion) {
        return matchVersion.onOrAfter(Version.LUCENE_29);
    }
}

