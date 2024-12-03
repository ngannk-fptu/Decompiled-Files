/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 *  org.apache.lucene.index.Fields
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.MultiFields
 *  org.apache.lucene.index.Terms
 *  org.apache.lucene.index.TermsEnum
 *  org.apache.lucene.util.BytesRef
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.search.v2.lucene.summary;

import com.atlassian.confluence.internal.search.v2.lucene.ILuceneConnection;
import com.atlassian.confluence.search.v2.summary.Excerpt;
import com.atlassian.confluence.search.v2.summary.Summarizer;
import com.atlassian.confluence.search.v2.summary.Summary;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSummarizer
implements Summarizer {
    private static final Logger log = LoggerFactory.getLogger(DefaultSummarizer.class);
    private static final int DEFAULT_SUM_CONTEXT = 10;
    private static final int DEFAULT_SUM_LENGTH = 30;
    private Analyzer analyzer;
    private int sumContext = 10;
    private int sumLength = 30;
    private ILuceneConnection luceneConnection;

    public DefaultSummarizer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public DefaultSummarizer(Analyzer analyzer, int sumContext, int sumLength, ILuceneConnection luceneConnection) {
        this.analyzer = analyzer;
        this.sumContext = sumContext;
        this.sumLength = sumLength;
        this.luceneConnection = luceneConnection;
    }

    @Override
    public Summary getSummary(String text) throws IOException {
        return this.getSummary(text, null);
    }

    @Override
    public Summary getSummary(String text, String query) throws IOException {
        log.debug("text = {} \n\n query = {}", (Object)text, (Object)query);
        Attributes[] tokens = this.parseText(text);
        if (log.isDebugEnabled()) {
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < tokens.length; ++i) {
                buf.append(tokens[i].getText());
                if (i == tokens.length - 1) continue;
                buf.append(", ");
            }
            log.debug("tokens = {}", (Object)buf);
        }
        if (tokens.length == 0) {
            return new Summary();
        }
        TreeSet<Excerpt> excerptSet = new TreeSet<Excerpt>(new ExcerptComparator());
        Set highlight = this.getTerms(query);
        log.debug("highlight = {}", (Object)highlight);
        int lastExcerptPos = 0;
        if (highlight.size() > 0) {
            int i = -1;
            while (i < tokens.length - 1) {
                if (!highlight.contains(tokens[++i].getText())) continue;
                int startToken = i > this.sumContext ? i - this.sumContext : 0;
                int endToken = Math.min(i + this.sumContext, tokens.length);
                int startOffset = tokens[startToken].getStartOffset();
                int currentToken = startToken;
                Excerpt excerpt = new Excerpt();
                if (startOffset != 0) {
                    excerpt.add(new Summary.Ellipsis());
                }
                while (currentToken < endToken && currentToken - startToken < this.sumLength) {
                    Attributes t = tokens[currentToken];
                    if (highlight.contains(t.getText())) {
                        excerpt.addToken(t.getText());
                        excerpt.add(new Summary.Fragment(text.substring(startOffset, t.getStartOffset())));
                        excerpt.add(new Summary.Highlight(text.substring(t.getStartOffset(), t.getEndOffset())));
                        startOffset = t.getEndOffset();
                        endToken = Math.min(currentToken + this.sumContext, tokens.length);
                    }
                    ++currentToken;
                }
                lastExcerptPos = endToken;
                if (currentToken < tokens.length) {
                    excerpt.add(new Summary.Fragment(text.substring(startOffset, tokens[currentToken].getEndOffset())));
                } else {
                    int endOffset = tokens[tokens.length - 1].getEndOffset();
                    String trailingFragment = text.substring(startOffset, endOffset);
                    if (!StringUtils.isEmpty((CharSequence)trailingFragment)) {
                        excerpt.add(new Summary.Fragment(trailingFragment));
                    }
                }
                excerpt.setNumTerms(currentToken - startToken);
                excerptSet.add(excerpt);
                i = currentToken + this.sumContext;
            }
        }
        if (excerptSet.size() == 0) {
            int excerptLen;
            Excerpt excerpt = new Excerpt();
            lastExcerptPos = excerptLen = Math.min(this.sumLength, tokens.length);
            excerpt.add(new Summary.Fragment(text.substring(tokens[0].getStartOffset(), tokens[excerptLen - 1].getEndOffset())));
            excerpt.setNumTerms(excerptLen);
            excerptSet.add(excerpt);
        }
        log.debug("Found excerpts = {}", (Object)excerptSet.size());
        return this.extractFromExcerpts(excerptSet, tokens, lastExcerptPos);
    }

    private Summary extractFromExcerpts(SortedSet<Excerpt> excerptSet, Attributes[] tokens, int lastExcerptPos) {
        double tokenCount = 0.0;
        Summary s = new Summary();
        while (tokenCount <= (double)this.sumLength && excerptSet.size() > 0) {
            Excerpt excerpt = excerptSet.last();
            excerptSet.remove(excerpt);
            double tokenFraction = 1.0 * (double)excerpt.getNumTerms() / (double)excerpt.numFragments();
            Enumeration e = excerpt.elements();
            while (e.hasMoreElements()) {
                Summary.Fragment f = (Summary.Fragment)e.nextElement();
                if (tokenCount + tokenFraction <= (double)this.sumLength) {
                    s.add(f);
                }
                tokenCount += tokenFraction;
            }
        }
        if (tokenCount > 0.0 && lastExcerptPos < tokens.length) {
            s.add(new Summary.Ellipsis());
        }
        return s;
    }

    private Set getTerms(String query) {
        if (StringUtils.isNotEmpty((CharSequence)query)) {
            try {
                HashSet<String> tokens = new HashSet<String>();
                if (this.luceneConnection != null && query.indexOf(42) > -1) {
                    Set set = (Set)this.luceneConnection.withReader(reader -> {
                        String transformedQuery = query.replaceAll("\\.", "\\.");
                        transformedQuery = transformedQuery.replaceAll("\\*", ".*");
                        transformedQuery = transformedQuery.replaceAll("\\?", ".");
                        HashSet<String> innerSet = new HashSet<String>();
                        Fields fields = MultiFields.getFields((IndexReader)reader);
                        if (fields != null) {
                            for (String field : fields) {
                                BytesRef text;
                                Terms terms = fields.terms(field);
                                if (terms == null) continue;
                                TermsEnum termEnum = terms.iterator(null);
                                String[] innerTokens = transformedQuery.split(" ");
                                while ((text = termEnum.next()) != null) {
                                    String termString = text.utf8ToString();
                                    for (int i = 0; i < innerTokens.length; ++i) {
                                        if (!Pattern.matches(innerTokens[i], termString)) continue;
                                        innerSet.add(termString);
                                    }
                                }
                            }
                        }
                        return innerSet;
                    });
                    tokens.addAll(set);
                }
                TokenStream ts = this.analyzer.tokenStream("contentBody", (Reader)new StringReader(query));
                ts.reset();
                CharTermAttribute charTermAttribute = (CharTermAttribute)ts.addAttribute(CharTermAttribute.class);
                while (ts.incrementToken()) {
                    tokens.add(charTermAttribute.toString());
                }
                ts.end();
                ts.close();
                return tokens;
            }
            catch (IOException e) {
                log.error(e.getMessage(), (Throwable)e);
            }
        }
        return Collections.EMPTY_SET;
    }

    private Attributes[] parseText(String text) throws IOException {
        TokenStream ts;
        if (text == null || text.trim().equals("")) {
            return new Attributes[0];
        }
        LinkedList<Attributes> result = new LinkedList<Attributes>();
        try (TokenStream tokenStream = ts = this.analyzer.tokenStream("contentBody", (Reader)new StringReader(text));){
            OffsetAttribute offsetAttribute = (OffsetAttribute)ts.addAttribute(OffsetAttribute.class);
            CharTermAttribute charTermAttribute = (CharTermAttribute)ts.addAttribute(CharTermAttribute.class);
            ts.reset();
            while (ts.incrementToken()) {
                result.add(new Attributes(charTermAttribute.toString(), offsetAttribute.startOffset(), offsetAttribute.endOffset()));
            }
            ts.end();
        }
        return result.toArray(new Attributes[0]);
    }

    private static class ExcerptComparator
    implements Comparator<Excerpt> {
        private ExcerptComparator() {
        }

        @Override
        public int compare(Excerpt excerpt1, Excerpt excerpt2) {
            int numToks2;
            if (excerpt1 == null && excerpt2 != null) {
                return -1;
            }
            if (excerpt1 != null && excerpt2 == null) {
                return 1;
            }
            if (excerpt1 == null) {
                return 0;
            }
            int numToks1 = excerpt1.numUniqueTokens();
            if (numToks1 < (numToks2 = excerpt2.numUniqueTokens())) {
                return -1;
            }
            if (numToks1 == numToks2) {
                return excerpt1.numFragments() - excerpt2.numFragments();
            }
            return 1;
        }
    }

    private static class Attributes {
        private final String text;
        private final int startOffset;
        private final int endOffset;

        private Attributes(String text, int startOffset, int endOffset) {
            this.text = text;
            this.startOffset = startOffset;
            this.endOffset = endOffset;
        }

        public String getText() {
            return this.text;
        }

        public int getStartOffset() {
            return this.startOffset;
        }

        public int getEndOffset() {
            return this.endOffset;
        }
    }
}

