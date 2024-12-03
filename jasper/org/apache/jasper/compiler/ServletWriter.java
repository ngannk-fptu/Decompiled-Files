/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jasper.compiler;

import java.io.PrintWriter;

public class ServletWriter
implements AutoCloseable {
    private static final int TAB_WIDTH = 2;
    private static final String SPACES = "                              ";
    private int indent = 0;
    private int virtual_indent = 0;
    private final PrintWriter writer;
    private int javaLine = 1;

    public ServletWriter(PrintWriter writer) {
        this.writer = writer;
    }

    @Override
    public void close() {
        this.writer.close();
    }

    public int getJavaLine() {
        return this.javaLine;
    }

    public void pushIndent() {
        this.virtual_indent += 2;
        if (this.virtual_indent >= 0 && this.virtual_indent <= SPACES.length()) {
            this.indent = this.virtual_indent;
        }
    }

    public void popIndent() {
        this.virtual_indent -= 2;
        if (this.virtual_indent >= 0 && this.virtual_indent <= SPACES.length()) {
            this.indent = this.virtual_indent;
        }
    }

    public void println(String s) {
        ++this.javaLine;
        this.writer.println(s);
    }

    public void println() {
        ++this.javaLine;
        this.writer.println("");
    }

    public void printin() {
        this.writer.print(SPACES.substring(0, this.indent));
    }

    public void printin(String s) {
        this.writer.print(SPACES.substring(0, this.indent));
        this.writer.print(s);
    }

    public void printil(String s) {
        ++this.javaLine;
        this.writer.print(SPACES.substring(0, this.indent));
        this.writer.println(s);
    }

    public void print(char c) {
        this.writer.print(c);
    }

    public void print(int i) {
        this.writer.print(i);
    }

    public void print(String s) {
        this.writer.print(s);
    }

    public void printMultiLn(String s) {
        int index = 0;
        while ((index = s.indexOf(10, index)) > -1) {
            ++this.javaLine;
            ++index;
        }
        this.writer.print(s);
    }
}

