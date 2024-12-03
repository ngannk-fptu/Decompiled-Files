/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.debug;

import groovyjarjarantlr.CharStreamException;
import groovyjarjarantlr.InputBuffer;
import groovyjarjarantlr.debug.InputBufferEventSupport;
import groovyjarjarantlr.debug.InputBufferListener;
import java.util.Vector;

public class DebuggingInputBuffer
extends InputBuffer {
    private InputBuffer buffer;
    private InputBufferEventSupport inputBufferEventSupport;
    private boolean debugMode = true;

    public DebuggingInputBuffer(InputBuffer inputBuffer) {
        this.buffer = inputBuffer;
        this.inputBufferEventSupport = new InputBufferEventSupport(this);
    }

    public void addInputBufferListener(InputBufferListener inputBufferListener) {
        this.inputBufferEventSupport.addInputBufferListener(inputBufferListener);
    }

    public void consume() {
        char c = ' ';
        try {
            c = this.buffer.LA(1);
        }
        catch (CharStreamException charStreamException) {
            // empty catch block
        }
        this.buffer.consume();
        if (this.debugMode) {
            this.inputBufferEventSupport.fireConsume(c);
        }
    }

    public void fill(int n) throws CharStreamException {
        this.buffer.fill(n);
    }

    public Vector getInputBufferListeners() {
        return this.inputBufferEventSupport.getInputBufferListeners();
    }

    public boolean isDebugMode() {
        return this.debugMode;
    }

    public boolean isMarked() {
        return this.buffer.isMarked();
    }

    public char LA(int n) throws CharStreamException {
        char c = this.buffer.LA(n);
        if (this.debugMode) {
            this.inputBufferEventSupport.fireLA(c, n);
        }
        return c;
    }

    public int mark() {
        int n = this.buffer.mark();
        this.inputBufferEventSupport.fireMark(n);
        return n;
    }

    public void removeInputBufferListener(InputBufferListener inputBufferListener) {
        if (this.inputBufferEventSupport != null) {
            this.inputBufferEventSupport.removeInputBufferListener(inputBufferListener);
        }
    }

    public void rewind(int n) {
        this.buffer.rewind(n);
        this.inputBufferEventSupport.fireRewind(n);
    }

    public void setDebugMode(boolean bl) {
        this.debugMode = bl;
    }
}

