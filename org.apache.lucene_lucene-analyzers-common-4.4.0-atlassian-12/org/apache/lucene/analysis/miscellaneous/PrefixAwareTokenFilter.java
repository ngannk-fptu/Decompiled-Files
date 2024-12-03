/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Token
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.FlagsAttribute
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 *  org.apache.lucene.analysis.tokenattributes.PayloadAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 *  org.apache.lucene.analysis.tokenattributes.TypeAttribute
 *  org.apache.lucene.util.AttributeSource
 *  org.apache.lucene.util.BytesRef
 */
package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.BytesRef;

public class PrefixAwareTokenFilter
extends TokenStream {
    private TokenStream prefix;
    private TokenStream suffix;
    private CharTermAttribute termAtt;
    private PositionIncrementAttribute posIncrAtt;
    private PayloadAttribute payloadAtt;
    private OffsetAttribute offsetAtt;
    private TypeAttribute typeAtt;
    private FlagsAttribute flagsAtt;
    private CharTermAttribute p_termAtt;
    private PositionIncrementAttribute p_posIncrAtt;
    private PayloadAttribute p_payloadAtt;
    private OffsetAttribute p_offsetAtt;
    private TypeAttribute p_typeAtt;
    private FlagsAttribute p_flagsAtt;
    private Token previousPrefixToken = new Token();
    private Token reusableToken = new Token();
    private boolean prefixExhausted;

    public PrefixAwareTokenFilter(TokenStream prefix, TokenStream suffix) {
        super((AttributeSource)suffix);
        this.suffix = suffix;
        this.prefix = prefix;
        this.prefixExhausted = false;
        this.termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
        this.posIncrAtt = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);
        this.payloadAtt = (PayloadAttribute)this.addAttribute(PayloadAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);
        this.typeAtt = (TypeAttribute)this.addAttribute(TypeAttribute.class);
        this.flagsAtt = (FlagsAttribute)this.addAttribute(FlagsAttribute.class);
        this.p_termAtt = (CharTermAttribute)prefix.addAttribute(CharTermAttribute.class);
        this.p_posIncrAtt = (PositionIncrementAttribute)prefix.addAttribute(PositionIncrementAttribute.class);
        this.p_payloadAtt = (PayloadAttribute)prefix.addAttribute(PayloadAttribute.class);
        this.p_offsetAtt = (OffsetAttribute)prefix.addAttribute(OffsetAttribute.class);
        this.p_typeAtt = (TypeAttribute)prefix.addAttribute(TypeAttribute.class);
        this.p_flagsAtt = (FlagsAttribute)prefix.addAttribute(FlagsAttribute.class);
    }

    public final boolean incrementToken() throws IOException {
        Token nextToken;
        if (!this.prefixExhausted) {
            nextToken = this.getNextPrefixInputToken(this.reusableToken);
            if (nextToken == null) {
                this.prefixExhausted = true;
            } else {
                this.previousPrefixToken.reinit(nextToken);
                BytesRef p = this.previousPrefixToken.getPayload();
                if (p != null) {
                    this.previousPrefixToken.setPayload(p.clone());
                }
                this.setCurrentToken(nextToken);
                return true;
            }
        }
        if ((nextToken = this.getNextSuffixInputToken(this.reusableToken)) == null) {
            return false;
        }
        nextToken = this.updateSuffixToken(nextToken, this.previousPrefixToken);
        this.setCurrentToken(nextToken);
        return true;
    }

    private void setCurrentToken(Token token) {
        if (token == null) {
            return;
        }
        this.clearAttributes();
        this.termAtt.copyBuffer(token.buffer(), 0, token.length());
        this.posIncrAtt.setPositionIncrement(token.getPositionIncrement());
        this.flagsAtt.setFlags(token.getFlags());
        this.offsetAtt.setOffset(token.startOffset(), token.endOffset());
        this.typeAtt.setType(token.type());
        this.payloadAtt.setPayload(token.getPayload());
    }

    private Token getNextPrefixInputToken(Token token) throws IOException {
        if (!this.prefix.incrementToken()) {
            return null;
        }
        token.copyBuffer(this.p_termAtt.buffer(), 0, this.p_termAtt.length());
        token.setPositionIncrement(this.p_posIncrAtt.getPositionIncrement());
        token.setFlags(this.p_flagsAtt.getFlags());
        token.setOffset(this.p_offsetAtt.startOffset(), this.p_offsetAtt.endOffset());
        token.setType(this.p_typeAtt.type());
        token.setPayload(this.p_payloadAtt.getPayload());
        return token;
    }

    private Token getNextSuffixInputToken(Token token) throws IOException {
        if (!this.suffix.incrementToken()) {
            return null;
        }
        token.copyBuffer(this.termAtt.buffer(), 0, this.termAtt.length());
        token.setPositionIncrement(this.posIncrAtt.getPositionIncrement());
        token.setFlags(this.flagsAtt.getFlags());
        token.setOffset(this.offsetAtt.startOffset(), this.offsetAtt.endOffset());
        token.setType(this.typeAtt.type());
        token.setPayload(this.payloadAtt.getPayload());
        return token;
    }

    public Token updateSuffixToken(Token suffixToken, Token lastPrefixToken) {
        suffixToken.setOffset(lastPrefixToken.endOffset() + suffixToken.startOffset(), lastPrefixToken.endOffset() + suffixToken.endOffset());
        return suffixToken;
    }

    public void end() throws IOException {
        this.prefix.end();
        this.suffix.end();
    }

    public void close() throws IOException {
        this.prefix.close();
        this.suffix.close();
    }

    public void reset() throws IOException {
        super.reset();
        if (this.prefix != null) {
            this.prefixExhausted = false;
            this.prefix.reset();
        }
        if (this.suffix != null) {
            this.suffix.reset();
        }
    }

    public TokenStream getPrefix() {
        return this.prefix;
    }

    public void setPrefix(TokenStream prefix) {
        this.prefix = prefix;
    }

    public TokenStream getSuffix() {
        return this.suffix;
    }

    public void setSuffix(TokenStream suffix) {
        this.suffix = suffix;
    }
}

