/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Token
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 *  org.apache.lucene.index.DocsAndPositionsEnum
 *  org.apache.lucene.index.Terms
 *  org.apache.lucene.index.TermsEnum
 *  org.apache.lucene.util.BytesRef
 *  org.apache.lucene.util.CollectionUtil
 */
package org.apache.lucene.search.highlight;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.CollectionUtil;

public final class TokenStreamFromTermPositionVector
extends TokenStream {
    private final List<Token> positionedTokens = new ArrayList<Token>();
    private Iterator<Token> tokensAtCurrentPosition;
    private CharTermAttribute termAttribute = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private PositionIncrementAttribute positionIncrementAttribute = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);
    private OffsetAttribute offsetAttribute = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);
    private static final Comparator<Token> tokenComparator = new Comparator<Token>(){

        @Override
        public int compare(Token o1, Token o2) {
            return o1.getPositionIncrement() - o2.getPositionIncrement();
        }
    };

    public TokenStreamFromTermPositionVector(Terms vector) throws IOException {
        BytesRef text;
        boolean hasOffsets = vector.hasOffsets();
        TermsEnum termsEnum = vector.iterator(null);
        DocsAndPositionsEnum dpEnum = null;
        while ((text = termsEnum.next()) != null) {
            dpEnum = termsEnum.docsAndPositions(null, dpEnum);
            assert (dpEnum != null);
            dpEnum.nextDoc();
            int freq = dpEnum.freq();
            for (int j = 0; j < freq; ++j) {
                Token token;
                int pos = dpEnum.nextPosition();
                if (hasOffsets) {
                    token = new Token(text.utf8ToString(), dpEnum.startOffset(), dpEnum.endOffset());
                } else {
                    token = new Token();
                    token.setEmpty().append(text.utf8ToString());
                }
                token.setPositionIncrement(pos);
                this.positionedTokens.add(token);
            }
        }
        CollectionUtil.timSort(this.positionedTokens, tokenComparator);
        int lastPosition = -1;
        for (Token token : this.positionedTokens) {
            int thisPosition = token.getPositionIncrement();
            token.setPositionIncrement(thisPosition - lastPosition);
            lastPosition = thisPosition;
        }
        this.tokensAtCurrentPosition = this.positionedTokens.iterator();
    }

    public boolean incrementToken() {
        if (this.tokensAtCurrentPosition.hasNext()) {
            Token next = this.tokensAtCurrentPosition.next();
            this.clearAttributes();
            this.termAttribute.setEmpty().append((CharTermAttribute)next);
            this.positionIncrementAttribute.setPositionIncrement(next.getPositionIncrement());
            this.offsetAttribute.setOffset(next.startOffset(), next.endOffset());
            return true;
        }
        return false;
    }

    public void reset() {
        this.tokensAtCurrentPosition = this.positionedTokens.iterator();
    }
}

