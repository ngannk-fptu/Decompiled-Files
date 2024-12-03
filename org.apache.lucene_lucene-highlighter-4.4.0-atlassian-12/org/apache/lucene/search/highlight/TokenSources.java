/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.Token
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 *  org.apache.lucene.document.Document
 *  org.apache.lucene.index.DocsAndPositionsEnum
 *  org.apache.lucene.index.Fields
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.Terms
 *  org.apache.lucene.index.TermsEnum
 *  org.apache.lucene.util.ArrayUtil
 *  org.apache.lucene.util.BytesRef
 */
package org.apache.lucene.search.highlight;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.highlight.TokenStreamFromTermPositionVector;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.BytesRef;

public class TokenSources {
    public static TokenStream getAnyTokenStream(IndexReader reader, int docId, String field, Document doc, Analyzer analyzer) throws IOException {
        Terms vector;
        TokenStream ts = null;
        Fields vectors = reader.getTermVectors(docId);
        if (vectors != null && (vector = vectors.terms(field)) != null) {
            ts = TokenSources.getTokenStream(vector);
        }
        if (ts == null) {
            ts = TokenSources.getTokenStream(doc, field, analyzer);
        }
        return ts;
    }

    public static TokenStream getAnyTokenStream(IndexReader reader, int docId, String field, Analyzer analyzer) throws IOException {
        Terms vector;
        TokenStream ts = null;
        Fields vectors = reader.getTermVectors(docId);
        if (vectors != null && (vector = vectors.terms(field)) != null) {
            ts = TokenSources.getTokenStream(vector);
        }
        if (ts == null) {
            ts = TokenSources.getTokenStream(reader, docId, field, analyzer);
        }
        return ts;
    }

    public static TokenStream getTokenStream(Terms vector) throws IOException {
        return TokenSources.getTokenStream(vector, false);
    }

    public static TokenStream getTokenStream(Terms tpv, boolean tokenPositionsGuaranteedContiguous) throws IOException {
        BytesRef text;
        if (!tpv.hasOffsets()) {
            throw new IllegalArgumentException("Cannot create TokenStream from Terms without offsets");
        }
        if (!tokenPositionsGuaranteedContiguous && tpv.hasPositions()) {
            return new TokenStreamFromTermPositionVector(tpv);
        }
        TermsEnum termsEnum = tpv.iterator(null);
        int totalTokens = 0;
        while (termsEnum.next() != null) {
            totalTokens += (int)termsEnum.totalTermFreq();
        }
        Object[] tokensInOriginalOrder = new Token[totalTokens];
        ArrayList<Token> unsortedTokens = null;
        termsEnum = tpv.iterator(null);
        DocsAndPositionsEnum dpEnum = null;
        while ((text = termsEnum.next()) != null) {
            if ((dpEnum = termsEnum.docsAndPositions(null, dpEnum)) == null) {
                throw new IllegalArgumentException("Required TermVector Offset information was not found");
            }
            String term = text.utf8ToString();
            dpEnum.nextDoc();
            int freq = dpEnum.freq();
            for (int posUpto = 0; posUpto < freq; ++posUpto) {
                int pos = dpEnum.nextPosition();
                if (dpEnum.startOffset() < 0) {
                    throw new IllegalArgumentException("Required TermVector Offset information was not found");
                }
                Token token = new Token(term, dpEnum.startOffset(), dpEnum.endOffset());
                if (tokenPositionsGuaranteedContiguous && pos != -1) {
                    tokensInOriginalOrder[pos] = token;
                    continue;
                }
                if (unsortedTokens == null) {
                    unsortedTokens = new ArrayList<Token>();
                }
                unsortedTokens.add(token);
            }
        }
        if (unsortedTokens != null) {
            tokensInOriginalOrder = unsortedTokens.toArray(new Token[unsortedTokens.size()]);
            ArrayUtil.timSort((Object[])tokensInOriginalOrder, (Comparator)new Comparator<Token>(){

                @Override
                public int compare(Token t1, Token t2) {
                    if (t1.startOffset() == t2.startOffset()) {
                        return t1.endOffset() - t2.endOffset();
                    }
                    return t1.startOffset() - t2.startOffset();
                }
            });
        }
        final class StoredTokenStream
        extends TokenStream {
            Token[] tokens;
            int currentToken = 0;
            CharTermAttribute termAtt;
            OffsetAttribute offsetAtt;
            PositionIncrementAttribute posincAtt;

            StoredTokenStream(Token[] tokens) {
                this.tokens = tokens;
                this.termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
                this.offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);
                this.posincAtt = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);
            }

            public boolean incrementToken() {
                if (this.currentToken >= this.tokens.length) {
                    return false;
                }
                Token token = this.tokens[this.currentToken++];
                this.clearAttributes();
                this.termAtt.setEmpty().append((CharTermAttribute)token);
                this.offsetAtt.setOffset(token.startOffset(), token.endOffset());
                this.posincAtt.setPositionIncrement(this.currentToken <= 1 || this.tokens[this.currentToken - 1].startOffset() > this.tokens[this.currentToken - 2].startOffset() ? 1 : 0);
                return true;
            }
        }
        return new StoredTokenStream((Token[])tokensInOriginalOrder);
    }

    public static TokenStream getTokenStreamWithOffsets(IndexReader reader, int docId, String field) throws IOException {
        Fields vectors = reader.getTermVectors(docId);
        if (vectors == null) {
            return null;
        }
        Terms vector = vectors.terms(field);
        if (vector == null) {
            return null;
        }
        if (!vector.hasPositions() || !vector.hasOffsets()) {
            return null;
        }
        return TokenSources.getTokenStream(vector);
    }

    public static TokenStream getTokenStream(IndexReader reader, int docId, String field, Analyzer analyzer) throws IOException {
        Document doc = reader.document(docId);
        return TokenSources.getTokenStream(doc, field, analyzer);
    }

    public static TokenStream getTokenStream(Document doc, String field, Analyzer analyzer) {
        String contents = doc.get(field);
        if (contents == null) {
            throw new IllegalArgumentException("Field " + field + " in document is not stored and cannot be analyzed");
        }
        return TokenSources.getTokenStream(field, contents, analyzer);
    }

    public static TokenStream getTokenStream(String field, String contents, Analyzer analyzer) {
        try {
            return analyzer.tokenStream(field, contents);
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}

