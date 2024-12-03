/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.internal.search.v2.lucene.LuceneException
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 *  org.apache.lucene.queryparser.flexible.standard.QueryParserUtil
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneException;
import com.atlassian.confluence.search.v2.QueryUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil;

public class LuceneQueryUtil {
    public static List<String> tokenize(Analyzer analyzer, String field, String value) {
        TokenStream tokenStream;
        try {
            tokenStream = analyzer.tokenStream(field, value);
        }
        catch (IOException e) {
            throw new LuceneException((Throwable)e);
        }
        return LuceneQueryUtil.tokenize(tokenStream);
    }

    public static List<String> tokenize(TokenStream tokenStream) {
        ArrayList<String> tokens = new ArrayList<String>();
        try {
            CharTermAttribute termAttribute = (CharTermAttribute)tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                tokens.add(termAttribute.toString());
            }
            tokenStream.end();
            tokenStream.close();
        }
        catch (IOException e) {
            throw new LuceneException((Throwable)e);
        }
        return tokens;
    }

    public static List<List<String>> tokenizeWithPositions(TokenStream tokenStream) {
        ArrayList<List<String>> result = new ArrayList<List<String>>();
        CharTermAttribute termAtt = (CharTermAttribute)tokenStream.addAttribute(CharTermAttribute.class);
        PositionIncrementAttribute posAtt = (PositionIncrementAttribute)tokenStream.addAttribute(PositionIncrementAttribute.class);
        try {
            tokenStream.reset();
            int prevIncrement = -1;
            ArrayList<String> samePosList = new ArrayList<String>();
            while (tokenStream.incrementToken()) {
                if (posAtt.getPositionIncrement() != 0 && prevIncrement != -1) {
                    for (int nGaps = 0; nGaps < posAtt.getPositionIncrement(); ++nGaps) {
                        result.add(samePosList);
                        samePosList = new ArrayList();
                    }
                }
                samePosList.add(termAtt.toString());
                prevIncrement = posAtt.getPositionIncrement();
            }
            if (!samePosList.isEmpty()) {
                result.add(samePosList);
            }
        }
        catch (IOException e) {
            throw new LuceneException((Throwable)e);
        }
        return result;
    }

    public static String safeEscape(String query) {
        return QueryUtil.escape(QueryParserUtil.escape((String)query), '<', '>');
    }
}

