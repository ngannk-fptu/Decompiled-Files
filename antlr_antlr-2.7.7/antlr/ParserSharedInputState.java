/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.TokenBuffer;

public class ParserSharedInputState {
    protected TokenBuffer input;
    public int guessing = 0;
    protected String filename;

    public void reset() {
        this.guessing = 0;
        this.filename = null;
        this.input.reset();
    }

    public String getFilename() {
        return this.filename;
    }

    public TokenBuffer getInput() {
        return this.input;
    }
}

