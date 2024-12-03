/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.filter;

import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.SitemeshWriter;
import com.opensymphony.module.sitemesh.filter.NullWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

public class RoutablePrintWriter
extends PrintWriter
implements SitemeshWriter {
    private PrintWriter destination;
    private DestinationFactory factory;

    public RoutablePrintWriter(DestinationFactory factory) {
        super(new NullWriter());
        this.factory = factory;
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
        return this.destination;
    }

    public void updateDestination(DestinationFactory factory) {
        this.destination = null;
        this.factory = factory;
    }

    public Writer getUnderlyingWriter() {
        return this.getDestination();
    }

    public void close() {
        this.getDestination().close();
    }

    public void println(Object x) {
        this.getDestination().println(x);
    }

    public void println(String x) {
        this.getDestination().println(x);
    }

    public void println(char[] x) {
        this.getDestination().println(x);
    }

    public void println(double x) {
        this.getDestination().println(x);
    }

    public void println(float x) {
        this.getDestination().println(x);
    }

    public void println(long x) {
        this.getDestination().println(x);
    }

    public void println(int x) {
        this.getDestination().println(x);
    }

    public void println(char x) {
        this.getDestination().println(x);
    }

    public void println(boolean x) {
        this.getDestination().println(x);
    }

    public void println() {
        this.getDestination().println();
    }

    public void print(Object obj) {
        this.getDestination().print(obj);
    }

    public void print(String s) {
        this.getDestination().print(s);
    }

    public void print(char[] s) {
        this.getDestination().print(s);
    }

    public void print(double d) {
        this.getDestination().print(d);
    }

    public void print(float f) {
        this.getDestination().print(f);
    }

    public void print(long l) {
        this.getDestination().print(l);
    }

    public void print(int i) {
        this.getDestination().print(i);
    }

    public void print(char c) {
        this.getDestination().print(c);
    }

    public void print(boolean b) {
        this.getDestination().print(b);
    }

    public void write(String s) {
        this.getDestination().write(s);
    }

    public void write(String s, int off, int len) {
        this.getDestination().write(s, off, len);
    }

    public void write(char[] buf) {
        this.getDestination().write(buf);
    }

    public void write(char[] buf, int off, int len) {
        this.getDestination().write(buf, off, len);
    }

    public void write(int c) {
        this.getDestination().write(c);
    }

    public boolean checkError() {
        return this.getDestination().checkError();
    }

    public void flush() {
        this.getDestination().flush();
    }

    public boolean writeSitemeshBufferFragment(SitemeshBufferFragment bufferFragment) throws IOException {
        PrintWriter destination = this.getDestination();
        if (destination instanceof SitemeshWriter) {
            return ((SitemeshWriter)((Object)destination)).writeSitemeshBufferFragment(bufferFragment);
        }
        bufferFragment.writeTo(destination);
        return true;
    }

    public SitemeshBuffer getSitemeshBuffer() {
        PrintWriter destination = this.getDestination();
        if (destination instanceof SitemeshWriter) {
            return ((SitemeshWriter)((Object)destination)).getSitemeshBuffer();
        }
        throw new IllegalStateException("Print writer is not a sitemesh buffer");
    }

    public static interface DestinationFactory {
        public PrintWriter activateDestination() throws IOException;
    }
}

