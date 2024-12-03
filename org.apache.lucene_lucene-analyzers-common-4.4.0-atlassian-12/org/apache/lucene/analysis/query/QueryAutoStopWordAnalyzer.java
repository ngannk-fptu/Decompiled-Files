/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.Analyzer$TokenStreamComponents
 *  org.apache.lucene.analysis.AnalyzerWrapper
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.MultiFields
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.index.Terms
 *  org.apache.lucene.index.TermsEnum
 *  org.apache.lucene.util.BytesRef
 *  org.apache.lucene.util.CharsRef
 *  org.apache.lucene.util.UnicodeUtil
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.AnalyzerWrapper;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.UnicodeUtil;
import org.apache.lucene.util.Version;

public final class QueryAutoStopWordAnalyzer
extends AnalyzerWrapper {
    private final Analyzer delegate;
    private final Map<String, Set<String>> stopWordsPerField = new HashMap<String, Set<String>>();
    public static final float defaultMaxDocFreqPercent = 0.4f;
    private final Version matchVersion;

    public QueryAutoStopWordAnalyzer(Version matchVersion, Analyzer delegate, IndexReader indexReader) throws IOException {
        this(matchVersion, delegate, indexReader, 0.4f);
    }

    public QueryAutoStopWordAnalyzer(Version matchVersion, Analyzer delegate, IndexReader indexReader, int maxDocFreq) throws IOException {
        this(matchVersion, delegate, indexReader, (Collection<String>)MultiFields.getIndexedFields((IndexReader)indexReader), maxDocFreq);
    }

    public QueryAutoStopWordAnalyzer(Version matchVersion, Analyzer delegate, IndexReader indexReader, float maxPercentDocs) throws IOException {
        this(matchVersion, delegate, indexReader, (Collection<String>)MultiFields.getIndexedFields((IndexReader)indexReader), maxPercentDocs);
    }

    public QueryAutoStopWordAnalyzer(Version matchVersion, Analyzer delegate, IndexReader indexReader, Collection<String> fields, float maxPercentDocs) throws IOException {
        this(matchVersion, delegate, indexReader, fields, (int)((float)indexReader.numDocs() * maxPercentDocs));
    }

    public QueryAutoStopWordAnalyzer(Version matchVersion, Analyzer delegate, IndexReader indexReader, Collection<String> fields, int maxDocFreq) throws IOException {
        this.matchVersion = matchVersion;
        this.delegate = delegate;
        for (String field : fields) {
            HashSet<String> stopWords = new HashSet<String>();
            Terms terms = MultiFields.getTerms((IndexReader)indexReader, (String)field);
            CharsRef spare = new CharsRef();
            if (terms != null) {
                BytesRef text;
                TermsEnum te = terms.iterator(null);
                while ((text = te.next()) != null) {
                    if (te.docFreq() <= maxDocFreq) continue;
                    UnicodeUtil.UTF8toUTF16((BytesRef)text, (CharsRef)spare);
                    stopWords.add(spare.toString());
                }
            }
            this.stopWordsPerField.put(field, stopWords);
        }
    }

    protected Analyzer getWrappedAnalyzer(String fieldName) {
        return this.delegate;
    }

    protected Analyzer.TokenStreamComponents wrapComponents(String fieldName, Analyzer.TokenStreamComponents components) {
        Set<String> stopWords = this.stopWordsPerField.get(fieldName);
        if (stopWords == null) {
            return components;
        }
        StopFilter stopFilter = new StopFilter(this.matchVersion, components.getTokenStream(), new CharArraySet(this.matchVersion, stopWords, false));
        return new Analyzer.TokenStreamComponents(components.getTokenizer(), (TokenStream)stopFilter);
    }

    public String[] getStopWords(String fieldName) {
        Set<String> stopWords = this.stopWordsPerField.get(fieldName);
        return stopWords != null ? stopWords.toArray(new String[stopWords.size()]) : new String[]{};
    }

    public Term[] getStopWords() {
        ArrayList<Term> allStopWords = new ArrayList<Term>();
        for (String fieldName : this.stopWordsPerField.keySet()) {
            Set<String> stopWords = this.stopWordsPerField.get(fieldName);
            for (String text : stopWords) {
                allStopWords.add(new Term(fieldName, text));
            }
        }
        return allStopWords.toArray(new Term[allStopWords.size()]);
    }
}

