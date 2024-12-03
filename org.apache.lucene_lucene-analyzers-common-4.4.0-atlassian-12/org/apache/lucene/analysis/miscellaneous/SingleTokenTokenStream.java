/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Token
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.util.AttributeImpl
 */
package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeImpl;

public final class SingleTokenTokenStream
extends TokenStream {
    private boolean exhausted = false;
    private Token singleToken;
    private final AttributeImpl tokenAtt;

    public SingleTokenTokenStream(Token token) {
        super(Token.TOKEN_ATTRIBUTE_FACTORY);
        assert (token != null);
        this.singleToken = token.clone();
        this.tokenAtt = (AttributeImpl)this.addAttribute(CharTermAttribute.class);
        assert (this.tokenAtt instanceof Token);
    }

    public final boolean incrementToken() {
        if (this.exhausted) {
            return false;
        }
        this.clearAttributes();
        this.singleToken.copyTo(this.tokenAtt);
        this.exhausted = true;
        return true;
    }

    public void reset() {
        this.exhausted = false;
    }

    public Token getToken() {
        return this.singleToken.clone();
    }

    public void setToken(Token token) {
        this.singleToken = token.clone();
    }
}

