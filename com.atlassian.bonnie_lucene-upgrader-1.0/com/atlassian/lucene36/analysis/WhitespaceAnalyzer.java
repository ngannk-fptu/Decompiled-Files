/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.ReusableAnalyzerBase;
import com.atlassian.lucene36.analysis.WhitespaceTokenizer;
import com.atlassian.lucene36.util.Version;
import java.io.Reader;

public final class WhitespaceAnalyzer
extends ReusableAnalyzerBase {
    private final Version matchVersion;

    public WhitespaceAnalyzer(Version matchVersion) {
        this.matchVersion = matchVersion;
    }

    @Deprecated
    public WhitespaceAnalyzer() {
        this(Version.LUCENE_30);
    }

    protected ReusableAnalyzerBase.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        return new ReusableAnalyzerBase.TokenStreamComponents(new WhitespaceTokenizer(this.matchVersion, reader));
    }
}

