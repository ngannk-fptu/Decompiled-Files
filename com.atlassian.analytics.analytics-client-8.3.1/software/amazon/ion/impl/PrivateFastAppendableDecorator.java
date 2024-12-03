/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import software.amazon.ion.util.PrivateFastAppendable;

@Deprecated
public abstract class PrivateFastAppendableDecorator
implements PrivateFastAppendable,
Closeable,
Flushable {
    private final PrivateFastAppendable myOutput;

    public PrivateFastAppendableDecorator(PrivateFastAppendable output) {
        this.myOutput = output;
    }

    public void flush() throws IOException {
        if (this.myOutput instanceof Flushable) {
            ((Flushable)((Object)this.myOutput)).flush();
        }
    }

    public void close() throws IOException {
        if (this.myOutput instanceof Closeable) {
            ((Closeable)((Object)this.myOutput)).close();
        }
    }

    public Appendable append(char c) throws IOException {
        this.myOutput.append(c);
        return this;
    }

    public Appendable append(CharSequence csq) throws IOException {
        this.myOutput.append(csq);
        return this;
    }

    public Appendable append(CharSequence csq, int start, int end) throws IOException {
        this.myOutput.append(csq, start, end);
        return this;
    }

    public void appendAscii(char c) throws IOException {
        this.myOutput.appendAscii(c);
    }

    public void appendAscii(CharSequence csq) throws IOException {
        this.myOutput.appendAscii(csq);
    }

    public void appendAscii(CharSequence csq, int start, int end) throws IOException {
        this.myOutput.appendAscii(csq, start, end);
    }

    public void appendUtf16(char c) throws IOException {
        this.myOutput.appendUtf16(c);
    }

    public void appendUtf16Surrogate(char leadSurrogate, char trailSurrogate) throws IOException {
        this.myOutput.appendUtf16Surrogate(leadSurrogate, trailSurrogate);
    }
}

