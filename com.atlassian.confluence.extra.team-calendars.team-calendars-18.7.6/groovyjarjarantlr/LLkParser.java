/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.Parser;
import groovyjarjarantlr.ParserSharedInputState;
import groovyjarjarantlr.Token;
import groovyjarjarantlr.TokenBuffer;
import groovyjarjarantlr.TokenStream;
import groovyjarjarantlr.TokenStreamException;

public class LLkParser
extends Parser {
    int k;

    public LLkParser(int n) {
        this.k = n;
    }

    public LLkParser(ParserSharedInputState parserSharedInputState, int n) {
        super(parserSharedInputState);
        this.k = n;
    }

    public LLkParser(TokenBuffer tokenBuffer, int n) {
        this.k = n;
        this.setTokenBuffer(tokenBuffer);
    }

    public LLkParser(TokenStream tokenStream, int n) {
        this.k = n;
        TokenBuffer tokenBuffer = new TokenBuffer(tokenStream);
        this.setTokenBuffer(tokenBuffer);
    }

    public void consume() throws TokenStreamException {
        this.inputState.input.consume();
    }

    public int LA(int n) throws TokenStreamException {
        return this.inputState.input.LA(n);
    }

    public Token LT(int n) throws TokenStreamException {
        return this.inputState.input.LT(n);
    }

    private void trace(String string, String string2) throws TokenStreamException {
        this.traceIndent();
        System.out.print(string + string2 + (this.inputState.guessing > 0 ? "; [guessing]" : "; "));
        for (int i = 1; i <= this.k; ++i) {
            if (i != 1) {
                System.out.print(", ");
            }
            if (this.LT(i) != null) {
                System.out.print("LA(" + i + ")==" + this.LT(i).getText());
                continue;
            }
            System.out.print("LA(" + i + ")==null");
        }
        System.out.println("");
    }

    public void traceIn(String string) throws TokenStreamException {
        ++this.traceDepth;
        this.trace("> ", string);
    }

    public void traceOut(String string) throws TokenStreamException {
        this.trace("< ", string);
        --this.traceDepth;
    }
}

