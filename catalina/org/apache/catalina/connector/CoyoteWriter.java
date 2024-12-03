/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.connector;

import java.io.IOException;
import java.io.PrintWriter;
import org.apache.catalina.connector.OutputBuffer;

public class CoyoteWriter
extends PrintWriter {
    private static final char[] LINE_SEP = System.lineSeparator().toCharArray();
    protected OutputBuffer ob;
    protected boolean error = false;

    public CoyoteWriter(OutputBuffer ob) {
        super(ob);
        this.ob = ob;
    }

    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    void clear() {
        this.ob = null;
    }

    void recycle() {
        this.error = false;
    }

    @Override
    public void flush() {
        if (this.error) {
            return;
        }
        try {
            this.ob.flush();
        }
        catch (IOException e) {
            this.error = true;
        }
    }

    @Override
    public void close() {
        try {
            this.ob.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        this.error = false;
    }

    @Override
    public boolean checkError() {
        this.flush();
        return this.error;
    }

    @Override
    public void write(int c) {
        if (this.error) {
            return;
        }
        try {
            this.ob.write(c);
        }
        catch (IOException e) {
            this.error = true;
        }
    }

    @Override
    public void write(char[] buf, int off, int len) {
        if (this.error) {
            return;
        }
        try {
            this.ob.write(buf, off, len);
        }
        catch (IOException e) {
            this.error = true;
        }
    }

    @Override
    public void write(char[] buf) {
        this.write(buf, 0, buf.length);
    }

    @Override
    public void write(String s, int off, int len) {
        if (this.error) {
            return;
        }
        try {
            this.ob.write(s, off, len);
        }
        catch (IOException e) {
            this.error = true;
        }
    }

    @Override
    public void write(String s) {
        this.write(s, 0, s.length());
    }

    @Override
    public void print(boolean b) {
        if (b) {
            this.write("true");
        } else {
            this.write("false");
        }
    }

    @Override
    public void print(char c) {
        this.write(c);
    }

    @Override
    public void print(int i) {
        this.write(String.valueOf(i));
    }

    @Override
    public void print(long l) {
        this.write(String.valueOf(l));
    }

    @Override
    public void print(float f) {
        this.write(String.valueOf(f));
    }

    @Override
    public void print(double d) {
        this.write(String.valueOf(d));
    }

    @Override
    public void print(char[] s) {
        this.write(s);
    }

    @Override
    public void print(String s) {
        if (s == null) {
            s = "null";
        }
        this.write(s);
    }

    @Override
    public void print(Object obj) {
        this.write(String.valueOf(obj));
    }

    @Override
    public void println() {
        this.write(LINE_SEP);
    }

    @Override
    public void println(boolean b) {
        this.print(b);
        this.println();
    }

    @Override
    public void println(char c) {
        this.print(c);
        this.println();
    }

    @Override
    public void println(int i) {
        this.print(i);
        this.println();
    }

    @Override
    public void println(long l) {
        this.print(l);
        this.println();
    }

    @Override
    public void println(float f) {
        this.print(f);
        this.println();
    }

    @Override
    public void println(double d) {
        this.print(d);
        this.println();
    }

    @Override
    public void println(char[] c) {
        this.print(c);
        this.println();
    }

    @Override
    public void println(String s) {
        this.print(s);
        this.println();
    }

    @Override
    public void println(Object o) {
        this.print(o);
        this.println();
    }
}

