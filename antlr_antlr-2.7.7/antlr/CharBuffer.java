/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.InputBuffer;
import java.io.IOException;
import java.io.Reader;

public class CharBuffer
extends InputBuffer {
    public transient Reader input;

    public CharBuffer(Reader reader) {
        this.input = reader;
    }

    public void fill(int n) throws CharStreamException {
        try {
            this.syncConsume();
            while (this.queue.nbrEntries < n + this.markerOffset) {
                this.queue.append((char)this.input.read());
            }
        }
        catch (IOException iOException) {
            throw new CharStreamIOException(iOException);
        }
    }
}

