/*
 * Decompiled with CFR 0.152.
 */
package groovy.text.markup;

import java.io.IOException;
import java.io.Writer;

public class DelegatingIndentWriter
extends Writer {
    public static final String SPACES = "    ";
    public static final String TAB = "\t";
    private final Writer delegate;
    private final String indentString;
    private int level;

    public DelegatingIndentWriter(Writer delegate) {
        this(delegate, SPACES);
    }

    public DelegatingIndentWriter(Writer delegate, String indentString) {
        this.delegate = delegate;
        this.indentString = indentString;
    }

    @Override
    public void write(int c) throws IOException {
        this.delegate.write(c);
    }

    @Override
    public void write(char[] cbuf) throws IOException {
        this.delegate.write(cbuf);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        this.delegate.write(cbuf, off, len);
    }

    @Override
    public void write(String str) throws IOException {
        this.delegate.write(str);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        this.delegate.write(str, off, len);
    }

    @Override
    public Writer append(CharSequence csq) throws IOException {
        return this.delegate.append(csq);
    }

    @Override
    public Writer append(CharSequence csq, int start, int end) throws IOException {
        return this.delegate.append(csq, start, end);
    }

    @Override
    public Writer append(char c) throws IOException {
        return this.delegate.append(c);
    }

    @Override
    public void flush() throws IOException {
        this.delegate.flush();
    }

    @Override
    public void close() throws IOException {
        this.delegate.close();
    }

    public int next() {
        return ++this.level;
    }

    public int previous() {
        return --this.level;
    }

    public void writeIndent() throws IOException {
        for (int i = 0; i < this.level; ++i) {
            this.delegate.write(this.indentString);
        }
    }
}

