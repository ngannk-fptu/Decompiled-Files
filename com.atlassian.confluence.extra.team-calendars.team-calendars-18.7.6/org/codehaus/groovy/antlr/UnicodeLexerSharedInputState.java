/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr;

import groovyjarjarantlr.LexerSharedInputState;
import org.codehaus.groovy.antlr.UnicodeEscapingReader;

public class UnicodeLexerSharedInputState
extends LexerSharedInputState {
    private final UnicodeEscapingReader escapingReader;
    private int prevUnescape;

    public UnicodeLexerSharedInputState(UnicodeEscapingReader in) {
        super(in);
        this.escapingReader = in;
    }

    @Override
    public int getColumn() {
        this.prevUnescape = this.escapingReader.getUnescapedUnicodeColumnCount();
        return super.getColumn() + this.prevUnescape;
    }

    @Override
    public int getTokenStartColumn() {
        if (this.line == this.tokenStartLine) {
            return super.getTokenStartColumn() + this.escapingReader.getUnescapedUnicodeColumnCount();
        }
        return super.getTokenStartColumn() + this.prevUnescape;
    }
}

