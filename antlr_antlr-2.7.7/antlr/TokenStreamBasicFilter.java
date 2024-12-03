/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.ASdebug.ASDebugStream;
import antlr.ASdebug.IASDebugStream;
import antlr.ASdebug.TokenOffsetInfo;
import antlr.Token;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.collections.impl.BitSet;

public class TokenStreamBasicFilter
implements TokenStream,
IASDebugStream {
    protected BitSet discardMask;
    protected TokenStream input;

    public TokenStreamBasicFilter(TokenStream tokenStream) {
        this.input = tokenStream;
        this.discardMask = new BitSet();
    }

    public void discard(int n) {
        this.discardMask.add(n);
    }

    public void discard(BitSet bitSet) {
        this.discardMask = bitSet;
    }

    public Token nextToken() throws TokenStreamException {
        Token token = this.input.nextToken();
        while (token != null && this.discardMask.member(token.getType())) {
            token = this.input.nextToken();
        }
        return token;
    }

    public String getEntireText() {
        return ASDebugStream.getEntireText(this.input);
    }

    public TokenOffsetInfo getOffsetInfo(Token token) {
        return ASDebugStream.getOffsetInfo(this.input, token);
    }
}

