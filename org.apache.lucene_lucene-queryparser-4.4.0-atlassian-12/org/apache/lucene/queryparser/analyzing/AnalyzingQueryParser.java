/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.queryparser.analyzing;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

public class AnalyzingQueryParser
extends QueryParser {
    private final Pattern wildcardPattern = Pattern.compile("(\\.)|([?*]+)");

    public AnalyzingQueryParser(Version matchVersion, String field, Analyzer analyzer) {
        super(matchVersion, field, analyzer);
        this.setAnalyzeRangeTerms(true);
    }

    @Override
    protected Query getWildcardQuery(String field, String termStr) throws ParseException {
        if (termStr == null) {
            throw new ParseException("Passed null value as term to getWildcardQuery");
        }
        if (!this.getAllowLeadingWildcard() && (termStr.startsWith("*") || termStr.startsWith("?"))) {
            throw new ParseException("'*' or '?' not allowed as first character in WildcardQuery unless getAllowLeadingWildcard() returns true");
        }
        Matcher wildcardMatcher = this.wildcardPattern.matcher(termStr);
        StringBuilder sb = new StringBuilder();
        int last = 0;
        while (wildcardMatcher.find()) {
            if (wildcardMatcher.group(1) != null) continue;
            if (wildcardMatcher.start() > 0) {
                String chunk = termStr.substring(last, wildcardMatcher.start());
                String analyzed = this.analyzeSingleChunk(field, termStr, chunk);
                sb.append(analyzed);
            }
            sb.append(wildcardMatcher.group(2));
            last = wildcardMatcher.end();
        }
        if (last < termStr.length()) {
            sb.append(this.analyzeSingleChunk(field, termStr, termStr.substring(last)));
        }
        return super.getWildcardQuery(field, sb.toString());
    }

    @Override
    protected Query getPrefixQuery(String field, String termStr) throws ParseException {
        String analyzed = this.analyzeSingleChunk(field, termStr, termStr);
        return super.getPrefixQuery(field, analyzed);
    }

    @Override
    protected Query getFuzzyQuery(String field, String termStr, float minSimilarity) throws ParseException {
        String analyzed = this.analyzeSingleChunk(field, termStr, termStr);
        return super.getFuzzyQuery(field, analyzed, minSimilarity);
    }

    protected String analyzeSingleChunk(String field, String termStr, String chunk) throws ParseException {
        String analyzed;
        block6: {
            analyzed = null;
            TokenStream stream = null;
            try {
                stream = this.getAnalyzer().tokenStream(field, chunk);
                stream.reset();
                CharTermAttribute termAtt = (CharTermAttribute)stream.getAttribute(CharTermAttribute.class);
                if (stream.incrementToken()) {
                    analyzed = termAtt.toString();
                    StringBuilder multipleOutputs = null;
                    while (stream.incrementToken()) {
                        if (null == multipleOutputs) {
                            multipleOutputs = new StringBuilder();
                            multipleOutputs.append('\"');
                            multipleOutputs.append(analyzed);
                            multipleOutputs.append('\"');
                        }
                        multipleOutputs.append(',');
                        multipleOutputs.append('\"');
                        multipleOutputs.append(termAtt.toString());
                        multipleOutputs.append('\"');
                    }
                    stream.end();
                    stream.close();
                    if (null != multipleOutputs) {
                        throw new ParseException(String.format(this.getLocale(), "Analyzer created multiple terms for \"%s\": %s", chunk, multipleOutputs.toString()));
                    }
                    break block6;
                }
                stream.end();
                stream.close();
                throw new ParseException(String.format(this.getLocale(), "Analyzer returned nothing for \"%s\"", chunk));
            }
            catch (IOException e) {
                throw new ParseException(String.format(this.getLocale(), "IO error while trying to analyze single term: \"%s\"", termStr));
            }
        }
        return analyzed;
    }
}

