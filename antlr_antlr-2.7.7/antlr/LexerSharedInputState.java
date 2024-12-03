/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.InputBuffer;
import java.io.InputStream;
import java.io.Reader;

public class LexerSharedInputState {
    protected int column = 1;
    protected int line = 1;
    protected int tokenStartColumn = 1;
    protected int tokenStartLine = 1;
    protected InputBuffer input;
    protected String filename;
    public int guessing = 0;

    public LexerSharedInputState(InputBuffer inputBuffer) {
        this.input = inputBuffer;
    }

    public LexerSharedInputState(InputStream inputStream) {
        this(new ByteBuffer(inputStream));
    }

    public LexerSharedInputState(Reader reader) {
        this(new CharBuffer(reader));
    }

    public String getFilename() {
        return this.filename;
    }

    public InputBuffer getInput() {
        return this.input;
    }

    public int getLine() {
        return this.line;
    }

    public int getTokenStartColumn() {
        return this.tokenStartColumn;
    }

    public int getTokenStartLine() {
        return this.tokenStartLine;
    }

    public int getColumn() {
        return this.column;
    }

    public void reset() {
        this.column = 1;
        this.line = 1;
        this.tokenStartColumn = 1;
        this.tokenStartLine = 1;
        this.guessing = 0;
        this.filename = null;
        this.input.reset();
    }
}

