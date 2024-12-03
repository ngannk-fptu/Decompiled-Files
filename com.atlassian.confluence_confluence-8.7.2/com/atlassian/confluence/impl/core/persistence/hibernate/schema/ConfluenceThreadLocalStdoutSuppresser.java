/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.google.common.base.Throwables
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.core.persistence.hibernate.schema;

import com.atlassian.confluence.util.Cleanup;
import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ConfluenceThreadLocalStdoutSuppresser
extends PrintStream {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceThreadLocalStdoutSuppresser.class);
    private static ThreadLocal<Boolean> suppressed = ThreadLocal.withInitial(() -> false);
    private final PrintStream original;

    public static Cleanup temporarilySuppressStdout() {
        ConfluenceThreadLocalStdoutSuppresser replaced;
        PrintStream original = System.out;
        try {
            replaced = new ConfluenceThreadLocalStdoutSuppresser(original);
        }
        catch (UnsupportedEncodingException ex) {
            throw Throwables.propagate((Throwable)ex);
        }
        System.setOut(replaced);
        suppressed.set(true);
        return () -> {
            suppressed.set(false);
            if (System.out == replaced) {
                System.setOut(original);
            } else {
                log.warn("System.out was modified during thread-local suppression of stdout");
            }
        };
    }

    private ConfluenceThreadLocalStdoutSuppresser(PrintStream delegate) throws UnsupportedEncodingException {
        super((OutputStream)delegate, false, Charsets.UTF_8.name());
        this.original = delegate;
    }

    @Override
    public void write(int b) {
        if (!suppressed.get().booleanValue()) {
            this.original.write(b);
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        if (!suppressed.get().booleanValue()) {
            this.original.write(b);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) {
        if (!suppressed.get().booleanValue()) {
            this.original.write(b, off, len);
        }
    }

    @Override
    public void print(boolean b) {
        if (!suppressed.get().booleanValue()) {
            this.original.print(b);
        }
    }

    @Override
    public void print(char c) {
        if (!suppressed.get().booleanValue()) {
            this.original.print(c);
        }
    }

    @Override
    public void print(char[] s) {
        if (!suppressed.get().booleanValue()) {
            this.original.print(s);
        }
    }

    @Override
    public void print(double d) {
        if (!suppressed.get().booleanValue()) {
            this.original.print(d);
        }
    }

    @Override
    public void print(float f) {
        if (!suppressed.get().booleanValue()) {
            this.original.print(f);
        }
    }

    @Override
    public void print(int i) {
        if (!suppressed.get().booleanValue()) {
            this.original.print(i);
        }
    }

    @Override
    public void print(long l) {
        if (!suppressed.get().booleanValue()) {
            this.original.print(l);
        }
    }

    @Override
    public void print(Object obj) {
        if (!suppressed.get().booleanValue()) {
            this.original.print(obj);
        }
    }

    @Override
    public void print(String s) {
        if (!suppressed.get().booleanValue()) {
            this.original.print(s);
        }
    }

    @Override
    public void println() {
        if (!suppressed.get().booleanValue()) {
            this.original.println();
        }
    }

    @Override
    public void println(boolean b) {
        if (!suppressed.get().booleanValue()) {
            this.original.println(b);
        }
    }

    @Override
    public void println(char c) {
        if (!suppressed.get().booleanValue()) {
            this.original.println(c);
        }
    }

    @Override
    public void println(char[] s) {
        if (!suppressed.get().booleanValue()) {
            this.original.println(s);
        }
    }

    @Override
    public void println(double d) {
        if (!suppressed.get().booleanValue()) {
            this.original.println(d);
        }
    }

    @Override
    public void println(float f) {
        if (!suppressed.get().booleanValue()) {
            this.original.println(f);
        }
    }

    @Override
    public void println(int i) {
        if (!suppressed.get().booleanValue()) {
            this.original.println(i);
        }
    }

    @Override
    public void println(long l) {
        if (!suppressed.get().booleanValue()) {
            this.original.println(l);
        }
    }

    @Override
    public void println(Object obj) {
        if (!suppressed.get().booleanValue()) {
            this.original.println(obj);
        }
    }

    @Override
    public void println(String s) {
        if (!suppressed.get().booleanValue()) {
            this.original.println(s);
        }
    }

    @Override
    public PrintStream format(Locale l, String format, Object ... args) {
        if (!suppressed.get().booleanValue()) {
            this.original.format(l, format, args);
        }
        return this;
    }
}

