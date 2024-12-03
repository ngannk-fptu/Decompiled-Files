/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.TypeAttribute
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.search.contentnames.lucene;

import com.atlassian.confluence.search.contentnames.QueryToken;
import com.atlassian.confluence.search.contentnames.QueryTokenizer;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneQueryTokenizer
implements QueryTokenizer {
    private static final Logger log = LoggerFactory.getLogger(LuceneQueryTokenizer.class);
    private final Analyzer unstemmedAnalyzer;

    public LuceneQueryTokenizer(Analyzer unstemmedAnalyzer) {
        this.unstemmedAnalyzer = unstemmedAnalyzer;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    @Override
    public List<QueryToken> tokenize(String query) {
        LinkedList<QueryToken> queryTokens;
        block21: {
            if (StringUtils.isBlank((CharSequence)query)) {
                return Collections.emptyList();
            }
            queryTokens = new LinkedList<QueryToken>();
            try (TokenStream tokenStream = this.unstemmedAnalyzer.tokenStream(null, (Reader)new StringReader(query));){
                CharTermAttribute charTermAtt = (CharTermAttribute)tokenStream.addAttribute(CharTermAttribute.class);
                TypeAttribute typeAttribute = (TypeAttribute)tokenStream.addAttribute(TypeAttribute.class);
                tokenStream.reset();
                while (tokenStream.incrementToken()) {
                    if ("EXTENDED_DELIMITER_SPLIT_TOKEN".equals(typeAttribute.type())) continue;
                    queryTokens.offer(new QueryToken(charTermAtt.toString(), QueryToken.Type.FULL));
                }
                try {
                    tokenStream.end();
                }
                catch (IOException iOException) {}
                break block21;
                catch (IOException iOException) {
                    try {
                        tokenStream.end();
                    }
                    catch (IOException iOException2) {}
                    break block21;
                    catch (Throwable throwable) {
                        try {
                            tokenStream.end();
                        }
                        catch (IOException iOException3) {
                            // empty catch block
                        }
                        throw throwable;
                    }
                }
            }
            catch (IOException e) {
                log.error("Failed to takenize query: {}", (Object)e.getMessage());
            }
        }
        if (!queryTokens.isEmpty()) {
            ((QueryToken)queryTokens.getLast()).setType(QueryToken.Type.PARTIAL);
        }
        return queryTokens;
    }
}

