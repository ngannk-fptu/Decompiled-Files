/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.ATNConfigSet;

public class NoViableAltException
extends RecognitionException {
    private final ATNConfigSet deadEndConfigs;
    private final Token startToken;

    public NoViableAltException(Parser recognizer) {
        this(recognizer, recognizer.getInputStream(), recognizer.getCurrentToken(), recognizer.getCurrentToken(), null, recognizer._ctx);
    }

    public NoViableAltException(Parser recognizer, TokenStream input, Token startToken, Token offendingToken, ATNConfigSet deadEndConfigs, ParserRuleContext ctx) {
        super(recognizer, input, ctx);
        this.deadEndConfigs = deadEndConfigs;
        this.startToken = startToken;
        this.setOffendingToken(offendingToken);
    }

    public Token getStartToken() {
        return this.startToken;
    }

    public ATNConfigSet getDeadEndConfigs() {
        return this.deadEndConfigs;
    }
}

