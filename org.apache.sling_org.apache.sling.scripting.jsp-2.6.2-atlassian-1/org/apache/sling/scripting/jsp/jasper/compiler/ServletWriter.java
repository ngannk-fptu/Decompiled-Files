/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import java.io.IOException;
import java.io.PrintWriter;
import org.apache.sling.scripting.jsp.jasper.compiler.Mark;

public class ServletWriter {
    public static int TAB_WIDTH = 2;
    public static String SPACES = "                              ";
    private int indent = 0;
    private int virtual_indent = 0;
    PrintWriter writer;
    private int javaLine = 1;

    public ServletWriter(PrintWriter writer) {
        this.writer = writer;
    }

    public void close() throws IOException {
        boolean hasErrors = this.writer.checkError();
        this.writer.close();
        if (hasErrors) {
            throw new IOException("IOException during writing.");
        }
    }

    public int getJavaLine() {
        return this.javaLine;
    }

    public void pushIndent() {
        this.virtual_indent += TAB_WIDTH;
        if (this.virtual_indent >= 0 && this.virtual_indent <= SPACES.length()) {
            this.indent = this.virtual_indent;
        }
    }

    public void popIndent() {
        this.virtual_indent -= TAB_WIDTH;
        if (this.virtual_indent >= 0 && this.virtual_indent <= SPACES.length()) {
            this.indent = this.virtual_indent;
        }
    }

    public void printComment(Mark start, Mark stop, char[] chars) {
        if (start != null && stop != null) {
            this.println("// from=" + start);
            this.println("//   to=" + stop);
        }
        if (chars != null) {
            int i = 0;
            while (i < chars.length) {
                this.printin();
                this.print("// ");
                while (chars[i] != '\n' && i < chars.length) {
                    this.writer.print(chars[i++]);
                }
            }
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

