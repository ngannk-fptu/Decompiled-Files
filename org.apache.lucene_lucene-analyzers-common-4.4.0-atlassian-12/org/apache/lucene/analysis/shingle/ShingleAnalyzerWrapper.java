/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.Analyzer$TokenStreamComponents
 *  org.apache.lucene.analysis.AnalyzerWrapper
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.shingle;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.AnalyzerWrapper;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

public final class ShingleAnalyzerWrapper
extends AnalyzerWrapper {
    private final Analyzer defaultAnalyzer;
    private final int maxShingleSize;
    private final int minShingleSize;
    private final String tokenSeparator;
    private final boolean outputUnigrams;
    private final boolean outputUnigramsIfNoShingles;

    public ShingleAnalyzerWrapper(Analyzer defaultAnalyzer) {
        this(defaultAnalyzer, 2);
    }

    public ShingleAnalyzerWrapper(Analyzer defaultAnalyzer, int maxShingleSize) {
        this(defaultAnalyzer, 2, maxShingleSize);
    }

    public ShingleAnalyzerWrapper(Analyzer defaultAnalyzer, int minShingleSize, int maxShingleSize) {
        this(defaultAnalyzer, minShingleSize, maxShingleSize, " ", true, false);
    }

    public ShingleAnalyzerWrapper(Analyzer defaultAnalyzer, int minShingleSize, int maxShingleSize, String tokenSeparator, boolean outputUnigrams, boolean outputUnigramsIfNoShingles) {
        this.defaultAnalyzer = defaultAnalyzer;
        if (maxShingleSize < 2) {
            throw new IllegalArgumentException("Max shingle size must be >= 2");
        }
        this.maxShingleSize = maxShingleSize;
        if (minShingleSize < 2) {
            throw new IllegalArgumentException("Min shingle size must be >= 2");
        }
        if (minShingleSize > maxShingleSize) {
            throw new IllegalArgumentException("Min shingle size must be <= max shingle size");
        }
        this.minShingleSize = minShingleSize;
        this.tokenSeparator = tokenSeparator == null ? "" : tokenSeparator;
        this.outputUnigrams = outputUnigrams;
        this.outputUnigramsIfNoShingles = outputUnigramsIfNoShingles;
    }

    public ShingleAnalyzerWrapper(Version matchVersion) {
        this(matchVersion, 2, 2);
    }

    public ShingleAnalyzerWrapper(Version matchVersion, int minShingleSize, int maxShingleSize) {
        this(new StandardAnalyzer(matchVersion), minShingleSize, maxShingleSize);
    }

    public int getMaxShingleSize() {
        return this.maxShingleSize;
    }

    public int getMinShingleSize() {
        return this.minShingleSize;
    }

    public String getTokenSeparator() {
        return this.tokenSeparator;
    }

    public boolean isOutputUnigrams() {
        return this.outputUnigrams;
    }

    public boolean isOutputUnigramsIfNoShingles() {
        return this.outputUnigramsIfNoShingles;
    }

    protected Analyzer getWrappedAnalyzer(String fieldName) {
        return this.defaultAnalyzer;
    }

    protected Analyzer.TokenStreamComponents wrapComponents(String fieldName, Analyzer.TokenStreamComponents components) {
        ShingleFilter filter = new ShingleFilter(components.getTokenStream(), this.minShingleSize, this.maxShingleSize);
        filter.setMinShingleSize(this.minShingleSize);
        filter.setMaxShingleSize(this.maxShingleSize);
        filter.setTokenSeparator(this.tokenSeparator);
        filter.setOutputUnigrams(this.outputUnigrams);
        filter.setOutputUnigramsIfNoShingles(this.outputUnigramsIfNoShingles);
        return new Analyzer.TokenStreamComponents(components.getTokenizer(), (TokenStream)filter);
    }
}

