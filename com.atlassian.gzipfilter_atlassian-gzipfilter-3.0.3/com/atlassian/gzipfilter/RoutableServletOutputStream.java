/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 */
package com.atlassian.gzipfilter;

import java.io.IOException;
import javax.servlet.ServletOutputStream;

public class RoutableServletOutputStream
extends ServletOutputStream {
    private ServletOutputStream destination;
    private DestinationFactory factory;
    private Runnable callBeforeUse;
    private static final Runnable doNothing = new Runnable(){

        @Override
        public void run() {
        }
    };

    public RoutableServletOutputStream(DestinationFactory factory, Runnable callBeforeUse) {
        this.factory = factory;
        this.callBeforeUse = callBeforeUse;
    }

    public RoutableServletOutputStream(DestinationFactory factory) {
        this(factory, doNothing);
    }

    private ServletOutputStream getDestination() throws IOException {
        if (this.destination == null) {
            this.destination = this.factory.create();
        }
        this.callBeforeUse.run();
        return this.destination;
    }

    public void updateDestination(DestinationFactory factory) {
        this.destination = null;
        this.factory = factory;
    }

    public void close() throws IOException {
        this.getDestination().close();
    }

    public void write(int b) throws IOException {
        this.getDestination().write(b);
    }

    public void print(String s) throws IOException {
        this.getDestination().print(s);
    }

    public void print(boolean b) throws IOException {
        this.getDestination().print(b);
    }

    public void print(char c) throws IOException {
        this.getDestination().print(c);
    }

    public void print(int i) throws IOException {
        this.getDestination().print(i);
    }

    public void print(long l) throws IOException {
        this.getDestination().print(l);
    }

    public void print(float v) throws IOException {
        this.getDestination().print(v);
    }

    public void print(double v) throws IOException {
        this.getDestination().print(v);
    }

    public void println() throws IOException {
        this.getDestination().println();
    }

    public void println(String s) throws IOException {
        this.getDestination().println(s);
    }

    public void println(boolean b) throws IOException {
        this.getDestination().println(b);
    }

    public void println(char c) throws IOException {
        this.getDestination().println(c);
    }

    public void println(int i) throws IOException {
        this.getDestination().println(i);
    }

    public void println(long l) throws IOException {
        this.getDestination().println(l);
    }

    public void println(float v) throws IOException {
        this.getDestination().println(v);
    }

    public void println(double v) throws IOException {
        this.getDestination().println(v);
    }

    public void write(byte[] b) throws IOException {
        this.getDestination().write(b);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        this.getDestination().write(b, off, len);
    }

    public void flush() throws IOException {
        this.getDestination().flush();
    }

    public static interface DestinationFactory {
        public ServletOutputStream create() throws IOException;
    }
}

