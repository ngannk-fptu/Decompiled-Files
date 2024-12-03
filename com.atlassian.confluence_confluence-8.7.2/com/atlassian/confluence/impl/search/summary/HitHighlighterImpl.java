/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 *  net.jcip.annotations.NotThreadSafe
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.highlight.Encoder
 *  org.apache.lucene.search.highlight.Formatter
 *  org.apache.lucene.search.highlight.Fragmenter
 *  org.apache.lucene.search.highlight.Highlighter
 *  org.apache.lucene.search.highlight.InvalidTokenOffsetsException
 *  org.apache.lucene.search.highlight.NullFragmenter
 *  org.apache.lucene.search.highlight.QueryScorer
 *  org.apache.lucene.search.highlight.Scorer
 *  org.apache.lucene.search.highlight.SimpleFragmenter
 *  org.apache.lucene.search.highlight.TextFragment
 *  org.apache.lucene.search.highlight.WeightedSpanTerm
 *  org.apache.lucene.search.highlight.WeightedSpanTermExtractor
 */
package com.atlassian.confluence.impl.search.summary;

import com.atlassian.confluence.impl.search.summary.HtmlEncoder;
import com.atlassian.confluence.impl.search.summary.WrappingFormatter;
import com.atlassian.confluence.impl.search.v2.lucene.WrappingQuery;
import com.atlassian.confluence.search.summary.HitHighlighter;
import com.atlassian.velocity.htmlsafe.HtmlSafe;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;
import net.jcip.annotations.NotThreadSafe;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Encoder;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.NullFragmenter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.WeightedSpanTerm;
import org.apache.lucene.search.highlight.WeightedSpanTermExtractor;

@NotThreadSafe
public class HitHighlighterImpl
implements HitHighlighter {
    private static final Pattern SPACE_PATTERN = Pattern.compile("(&nbsp;|\u00a0)");
    private static final Pattern MARKUP_PATTERN = Pattern.compile("[\\\\#]|(\\{\\{|}})");
    private final Analyzer analyzer;
    private final Highlighter highlighter;
    private final Encoder encoder;
    private final Fragmenter originalFragmenter;
    private static final int MAX_FRAGMENTS = 2;
    private static final int FRAGMENT_SIZE = 160;
    private static final int NO_MATCH_EXCERPT_SIZE = 320;
    private static final String FRAGMENT_SEPARATOR = " &hellip; ";

    public HitHighlighterImpl(Query query, Analyzer analyzer) {
        this(query, analyzer, new WrappingFormatter("<span class=\"search-highlight\">", "</span>"));
    }

    public HitHighlighterImpl(Query query, Analyzer analyzer, Formatter formatter) {
        this(query, analyzer, formatter, new HtmlEncoder());
    }

    public HitHighlighterImpl(Query query, Analyzer analyzer, Formatter formatter, Encoder encoder) {
        this.analyzer = analyzer;
        this.highlighter = new Highlighter(formatter, encoder, (Scorer)new HitHighlighterScorer(query));
        this.encoder = encoder;
        this.originalFragmenter = new SimpleFragmenter(160);
        this.setFragmenter(this.originalFragmenter);
    }

    private void setFragmenter(Fragmenter originalFragmenter) {
        this.highlighter.setTextFragmenter(originalFragmenter);
    }

    private void setNoFragments() {
        this.setFragmenter((Fragmenter)new NullFragmenter());
    }

    @Override
    @HtmlSafe
    public String getSummary(String text) {
        if (StringUtils.isBlank((CharSequence)text)) {
            return "";
        }
        String summary = this.getBestFragments(text);
        if (StringUtils.isNotBlank((CharSequence)summary)) {
            return summary;
        }
        if (text.length() <= 320) {
            return this.encoder.encodeText(text);
        }
        return this.encoder.encodeText(text.substring(0, 320));
    }

    @Override
    @HtmlSafe
    public String highlightText(String text) {
        this.setNoFragments();
        String highlighted = this.getBestFragments(text);
        this.setFragmenter(this.originalFragmenter);
        if (StringUtils.isNotBlank((CharSequence)highlighted)) {
            return highlighted;
        }
        return this.encoder.encodeText(text);
    }

    @HtmlSafe
    private String getBestFragments(String text) {
        return StringUtils.join((Object[])this.getBestFragmentsArray(text), (String)FRAGMENT_SEPARATOR);
    }

    private String[] getBestFragmentsArray(String text) {
        TextFragment[] fragments;
        TokenStream tokenStream;
        try {
            tokenStream = this.analyzer.tokenStream(null, (Reader)new StringReader(text));
        }
        catch (IOException e) {
            return new String[0];
        }
        try {
            fragments = this.highlighter.getBestTextFragments(tokenStream, text, true, 2);
        }
        catch (IOException | InvalidTokenOffsetsException e) {
            throw new RuntimeException(e);
        }
        return (String[])Arrays.stream(fragments).map(TextFragment::toString).toArray(String[]::new);
    }

    static class WrappingQueryAwareWeightedSpanTermExtractor
    extends WeightedSpanTermExtractor {
        public WrappingQueryAwareWeightedSpanTermExtractor() {
        }

        public WrappingQueryAwareWeightedSpanTermExtractor(String defaultField) {
            super(defaultField);
        }

        protected void extractUnknownQuery(Query query, Map<String, WeightedSpanTerm> terms) throws IOException {
            if (query instanceof WrappingQuery) {
                this.extract(((WrappingQuery)query).getWrappedQuery(), terms);
            } else {
                super.extractUnknownQuery(query, terms);
            }
        }
    }

    static class HitHighlighterScorer
    extends QueryScorer {
        public HitHighlighterScorer(Query query) {
            super(query);
        }

        protected WeightedSpanTermExtractor newTermExtractor(String defaultField) {
            return defaultField == null ? new WrappingQueryAwareWeightedSpanTermExtractor() : new WrappingQueryAwareWeightedSpanTermExtractor(defaultField);
        }
    }
}

