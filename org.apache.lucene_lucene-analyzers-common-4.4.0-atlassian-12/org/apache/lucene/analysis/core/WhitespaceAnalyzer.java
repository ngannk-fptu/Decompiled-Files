/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.Analyzer$TokenStreamComponents
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.core;

import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.util.Version;

public final class WhitespaceAnalyzer
extends Analyzer {
    private final Version matchVersion;

    public WhitespaceAnalyzer(Version matchVersion) {
        this.matchVersion = matchVersion;
    }

    protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        return new Analyzer.TokenStreamComponents((Tokenizer)new WhitespaceTokenizer(this.matchVersion, reader));
    }
}

