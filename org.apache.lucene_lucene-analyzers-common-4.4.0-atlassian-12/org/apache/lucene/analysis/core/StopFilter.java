/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.core;

import java.util.Arrays;
import java.util.List;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.FilteringTokenFilter;
import org.apache.lucene.util.Version;

public final class StopFilter
extends FilteringTokenFilter {
    private final CharArraySet stopWords;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);

    public StopFilter(Version matchVersion, TokenStream in, CharArraySet stopWords) {
        super(matchVersion, in);
        this.stopWords = stopWords;
    }

    public static CharArraySet makeStopSet(Version matchVersion, String ... stopWords) {
        return StopFilter.makeStopSet(matchVersion, stopWords, false);
    }

    public static CharArraySet makeStopSet(Version matchVersion, List<?> stopWords) {
        return StopFilter.makeStopSet(matchVersion, stopWords, false);
    }

    public static CharArraySet makeStopSet(Version matchVersion, String[] stopWords, boolean ignoreCase) {
        CharArraySet stopSet = new CharArraySet(matchVersion, stopWords.length, ignoreCase);
        stopSet.addAll(Arrays.asList(stopWords));
        return stopSet;
    }

    public static CharArraySet makeStopSet(Version matchVersion, List<?> stopWords, boolean ignoreCase) {
        CharArraySet stopSet = new CharArraySet(matchVersion, stopWords.size(), ignoreCase);
        stopSet.addAll(stopWords);
        return stopSet;
    }

    @Override
    protected boolean accept() {
        return !this.stopWords.contains(this.termAtt.buffer(), 0, this.termAtt.length());
    }
}

