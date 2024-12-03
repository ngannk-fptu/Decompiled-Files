/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.CommonHiddenStreamToken;
import antlr.Token;
import antlr.TokenStream;
import antlr.TokenStreamBasicFilter;
import antlr.TokenStreamException;
import antlr.collections.impl.BitSet;

public class TokenStreamHiddenTokenFilter
extends TokenStreamBasicFilter
implements TokenStream {
    protected BitSet hideMask = new BitSet();
    protected CommonHiddenStreamToken nextMonitoredToken;
    protected CommonHiddenStreamToken lastHiddenToken;
    protected CommonHiddenStreamToken firstHidden = null;

    public TokenStreamHiddenTokenFilter(TokenStream tokenStream) {
        super(tokenStream);
    }

    protected void consume() throws TokenStreamException {
        this.nextMonitoredToken = (CommonHiddenStreamToken)this.input.nextToken();
    }

    private void consumeFirst() throws TokenStreamException {
        this.consume();
        CommonHiddenStreamToken commonHiddenStreamToken = null;
        while (this.hideMask.member(this.LA(1).getType()) || this.discardMask.member(this.LA(1).getType())) {
            if (this.hideMask.member(this.LA(1).getType())) {
                if (commonHiddenStreamToken == null) {
                    commonHiddenStreamToken = this.LA(1);
                } else {
                    commonHiddenStreamToken.setHiddenAfter(this.LA(1));
                    this.LA(1).setHiddenBefore(commonHiddenStreamToken);
                    commonHiddenStreamToken = this.LA(1);
                }
                this.lastHiddenToken = commonHiddenStreamToken;
                if (this.firstHidden == null) {
                    this.firstHidden = commonHiddenStreamToken;
                }
            }
            this.consume();
        }
    }

    public BitSet getDiscardMask() {
        return this.discardMask;
    }

    public CommonHiddenStreamToken getHiddenAfter(CommonHiddenStreamToken commonHiddenStreamToken) {
        return commonHiddenStreamToken.getHiddenAfter();
    }

    public CommonHiddenStreamToken getHiddenBefore(CommonHiddenStreamToken commonHiddenStreamToken) {
        return commonHiddenStreamToken.getHiddenBefore();
    }

    public BitSet getHideMask() {
        return this.hideMask;
    }

    public CommonHiddenStreamToken getInitialHiddenToken() {
        return this.firstHidden;
    }

    public void hide(int n) {
        this.hideMask.add(n);
    }

    public void hide(BitSet bitSet) {
        this.hideMask = bitSet;
    }

    protected CommonHiddenStreamToken LA(int n) {
        return this.nextMonitoredToken;
    }

    public Token nextToken() throws TokenStreamException {
        if (this.LA(1) == null) {
            this.consumeFirst();
        }
        CommonHiddenStreamToken commonHiddenStreamToken = this.LA(1);
        commonHiddenStreamToken.setHiddenBefore(this.lastHiddenToken);
        this.lastHiddenToken = null;
        this.consume();
        CommonHiddenStreamToken commonHiddenStreamToken2 = commonHiddenStreamToken;
        while (this.hideMask.member(this.LA(1).getType()) || this.discardMask.member(this.LA(1).getType())) {
            if (this.hideMask.member(this.LA(1).getType())) {
                commonHiddenStreamToken2.setHiddenAfter(this.LA(1));
                if (commonHiddenStreamToken2 != commonHiddenStreamToken) {
                    this.LA(1).setHiddenBefore(commonHiddenStreamToken2);
                }
                commonHiddenStreamToken2 = this.lastHiddenToken = this.LA(1);
            }
            this.consume();
        }
        return commonHiddenStreamToken;
    }
}

