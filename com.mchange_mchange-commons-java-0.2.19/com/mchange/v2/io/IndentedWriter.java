/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.io;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

public class IndentedWriter
extends FilterWriter {
    static final String EOL;
    int indent_level = 0;
    boolean at_line_start = true;
    String indentSpacing;

    public IndentedWriter(Writer writer, String string) {
        super(writer);
        this.indentSpacing = string;
    }

    public IndentedWriter(Writer writer) {
        this(writer, "\t");
    }

    private boolean isEol(char c) {
        return c == '\r' || c == '\n';
    }

    public void upIndent() {
        ++this.indent_level;
    }

    public void downIndent() {
        --this.indent_level;
    }

    @Override
    public void write(int n) throws IOException {
        this.out.write(n);
        this.at_line_start = this.isEol((char)n);
    }

    @Override
    public void write(char[] cArray, int n, int n2) throws IOException {
        this.out.write(cArray, n, n2);
        this.at_line_start = this.isEol(cArray[n + n2 - 1]);
    }

    @Override
    public void write(String string, int n, int n2) throws IOException {
        if (n2 > 0) {
            this.out.write(string, n, n2);
            this.at_line_start = this.isEol(string.charAt(n + n2 - 1));
        }
    }

    private void printIndent() throws IOException {
        for (int i = 0; i < this.indent_level; ++i) {
            this.out.write(this.indentSpacing);
        }
    }

    public void print(String string) throws IOException {
        if (this.at_line_start) {
            this.printIndent();
        }
        this.out.write(string);
        char c = string.charAt(string.length() - 1);
        this.at_line_start = this.isEol(c);
    }

    public void println(String string) throws IOException {
        if (this.at_line_start) {
            this.printIndent();
        }
        this.out.write(string);
        this.out.write(EOL);
        this.at_line_start = true;
    }

    public void print(boolean bl) throws IOException {
        this.print(String.valueOf(bl));
    }

    public void print(byte by) throws IOException {
        this.print(String.valueOf(by));
    }

    public void print(char c) throws IOException {
        this.print(String.valueOf(c));
    }

    public void print(short s) throws IOException {
        this.print(String.valueOf(s));
    }

    public void print(int n) throws IOException {
        this.print(String.valueOf(n));
    }

    public void print(long l) throws IOException {
        this.print(String.valueOf(l));
    }

    public void print(float f) throws IOException {
        this.print(String.valueOf(f));
    }

    public void print(double d) throws IOException {
        this.print(String.valueOf(d));
    }

    public void print(Object object) throws IOException {
        this.print(String.valueOf(object));
    }

    public void println(boolean bl) throws IOException {
        this.println(String.valueOf(bl));
    }

    public void println(byte by) throws IOException {
        this.println(String.valueOf(by));
    }

    public void println(char c) throws IOException {
        this.println(String.valueOf(c));
    }

    public void println(short s) throws IOException {
        this.println(String.valueOf(s));
    }

    public void println(int n) throws IOException {
        this.println(String.valueOf(n));
    }

    public void println(long l) throws IOException {
        this.println(String.valueOf(l));
    }

    public void println(float f) throws IOException {
        this.println(String.valueOf(f));
    }

    public void println(double d) throws IOException {
        this.println(String.valueOf(d));
    }

    public void println(Object object) throws IOException {
        this.println(String.valueOf(object));
    }

    public void println() throws IOException {
        this.println("");
    }

    static {
        String string = System.getProperty("line.separator");
        EOL = string != null ? string : "\r\n";
    }
}

