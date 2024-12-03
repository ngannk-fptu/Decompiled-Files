/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gzipfilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

public class RoutablePrintWriter
extends PrintWriter {
    private PrintWriter destination;
    private DestinationFactory factory;
    private Runnable callBeforeUse;
    private static final Runnable doNothing = new Runnable(){

        @Override
        public void run() {
        }
    };

    public RoutablePrintWriter(DestinationFactory factory, Runnable callBeforeUse) {
        super(new NullWriter());
        this.factory = factory;
        this.callBeforeUse = callBeforeUse;
    }

    public RoutablePrintWriter(DestinationFactory factory) {
        this(factory, doNothing);
    }

    private PrintWriter getDestination() {
        if (this.destination == null) {
            try {
                this.destination = this.factory.activateDestination();
            }
            catch (IOException e) {
                this.setError();
            }
        }
        this.callBeforeUse.run();
        return this.destination;
    }

    public void updateDestination(DestinationFactory factory) {
        this.destination = null;
        this.factory = factory;
    }

    @Override
    public void close() {
        this.getDestination().close();
    }

    @Override
    public void println(Object x) {
        this.getDestination().println(x);
    }

    @Override
    public void println(String x) {
        this.getDestination().println(x);
    }

    @Override
    public void println(char[] x) {
        this.getDestination().println(x);
    }

    @Override
    public void println(double x) {
        this.getDestination().println(x);
    }

    @Override
    public void println(float x) {
        this.getDestination().println(x);
    }

    @Override
    public void println(long x) {
        this.getDestination().println(x);
    }

    @Override
    public void println(int x) {
        this.getDestination().println(x);
    }

    @Override
    public void println(char x) {
        this.getDestination().println(x);
    }

    @Override
    public void println(boolean x) {
        this.getDestination().println(x);
    }

    @Override
    public void println() {
        this.getDestination().println();
    }

    @Override
    public void print(Object obj) {
        this.getDestination().print(obj);
    }

    @Override
    public void print(String s) {
        this.getDestination().print(s);
    }

    @Override
    public void print(char[] s) {
        this.getDestination().print(s);
    }

    @Override
    public void print(double d) {
        this.getDestination().print(d);
    }

    @Override
    public void print(float f) {
        this.getDestination().print(f);
    }

    @Override
    public void print(long l) {
        this.getDestination().print(l);
    }

    @Override
    public void print(int i) {
        this.getDestination().print(i);
    }

    @Override
    public void print(char c) {
        this.getDestination().print(c);
    }

    @Override
    public void print(boolean b) {
        this.getDestination().print(b);
    }

    @Override
    public void write(String s) {
        this.getDestination().write(s);
    }

    @Override
    public void write(String s, int off, int len) {
        this.getDestination().write(s, off, len);
    }

    @Override
    public void write(char[] buf) {
        this.getDestination().write(buf);
    }

    @Override
    public void write(char[] buf, int off, int len) {
        this.getDestination().write(buf, off, len);
    }

    @Override
    public void write(int c) {
        this.getDestination().write(c);
    }

    @Override
    public boolean checkError() {
        return this.getDestination().checkError();
    }

    @Override
    public void flush() {
        this.getDestination().flush();
    }

    private static class NullWriter
    extends Writer {
        protected NullWriter() {
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void flush() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void close() throws IOException {
            throw new UnsupportedOperationException();
        }
    }

    public static interface DestinationFactory {
        public PrintWriter activateDestination() throws IOException;
    }
}

