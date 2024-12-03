/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import software.amazon.ion.util.PrivateFastAppendable;

final class AppendableFastAppendable
implements PrivateFastAppendable,
Closeable,
Flushable {
    private final Appendable _out;

    AppendableFastAppendable(Appendable out) {
        out.getClass();
        this._out = out;
    }

    public Appendable append(CharSequence csq) throws IOException {
        this._out.append(csq);
        return this;
    }

    public Appendable append(char c) throws IOException {
        this._out.append(c);
        return this;
    }

    public Appendable append(CharSequence csq, int start, int end) throws IOException {
        this._out.append(csq, start, end);
        return this;
    }

    public final void appendAscii(char c) throws IOException {
        this._out.append(c);
    }

    public final void appendAscii(CharSequence csq) throws IOException {
        this._out.append(csq);
    }

    public final void appendAscii(CharSequence csq, int start, int end) throws IOException {
        this._out.append(csq, start, end);
    }

    public final void appendUtf16(char c) throws IOException {
        this._out.append(c);
    }

    public final void appendUtf16Surrogate(char leadSurrogate, char trailSurrogate) throws IOException {
        this._out.append(leadSurrogate);
        this._out.append(trailSurrogate);
    }

    public void flush() throws IOException {
        if (this._out instanceof Flushable) {
            ((Flushable)((Object)this._out)).flush();
        }
    }

    public void close() throws IOException {
        if (this._out instanceof Closeable) {
            ((Closeable)((Object)this._out)).close();
        }
    }
}

