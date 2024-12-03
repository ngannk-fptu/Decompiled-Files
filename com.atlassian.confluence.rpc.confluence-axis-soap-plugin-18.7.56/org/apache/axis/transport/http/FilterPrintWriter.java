/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.axis.transport.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;

public class FilterPrintWriter
extends PrintWriter {
    private PrintWriter _writer = null;
    private HttpServletResponse _response = null;
    private static OutputStream _sink = new NullOutputStream();

    public FilterPrintWriter(HttpServletResponse aResponse) {
        super(_sink);
        this._response = aResponse;
    }

    private PrintWriter getPrintWriter() {
        if (this._writer == null) {
            try {
                this._writer = this._response.getWriter();
            }
            catch (IOException e) {
                throw new RuntimeException(e.toString());
            }
        }
        return this._writer;
    }

    public void write(int i) {
        this.getPrintWriter().write(i);
    }

    public void write(char[] chars) {
        this.getPrintWriter().write(chars);
    }

    public void write(char[] chars, int i, int i1) {
        this.getPrintWriter().write(chars, i, i1);
    }

    public void write(String string) {
        this.getPrintWriter().write(string);
    }

    public void write(String string, int i, int i1) {
        this.getPrintWriter().write(string, i, i1);
    }

    public void flush() {
        this.getPrintWriter().flush();
    }

    public void close() {
        this.getPrintWriter().close();
    }

    public boolean checkError() {
        return this.getPrintWriter().checkError();
    }

    public void print(boolean b) {
        this.getPrintWriter().print(b);
    }

    public void print(char c) {
        this.getPrintWriter().print(c);
    }

    public void print(int i) {
        this.getPrintWriter().print(i);
    }

    public void print(long l) {
        this.getPrintWriter().print(l);
    }

    public void print(float v) {
        this.getPrintWriter().print(v);
    }

    public void print(double v) {
        this.getPrintWriter().print(v);
    }

    public void print(char[] chars) {
        this.getPrintWriter().print(chars);
    }

    public void print(String string) {
        this.getPrintWriter().print(string);
    }

    public void print(Object object) {
        this.getPrintWriter().print(object);
    }

    public void println() {
        this.getPrintWriter().println();
    }

    public void println(boolean b) {
        this.getPrintWriter().println(b);
    }

    public void println(char c) {
        this.getPrintWriter().println(c);
    }

    public void println(int i) {
        this.getPrintWriter().println(i);
    }

    public void println(long l) {
        this.getPrintWriter().println(l);
    }

    public void println(float v) {
        this.getPrintWriter().println(v);
    }

    public void println(double v) {
        this.getPrintWriter().println(v);
    }

    public void println(char[] chars) {
        this.getPrintWriter().println(chars);
    }

    public void println(String string) {
        this.getPrintWriter().println(string);
    }

    public void println(Object object) {
        this.getPrintWriter().println(object);
    }

    public static class NullOutputStream
    extends OutputStream {
        public void write(int b) {
        }
    }
}

