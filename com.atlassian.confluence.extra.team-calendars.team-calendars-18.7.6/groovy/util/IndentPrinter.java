/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.lang.GroovyRuntimeException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

public class IndentPrinter {
    private int indentLevel;
    private String indent;
    private Writer out;
    private final boolean addNewlines;
    private boolean autoIndent;

    public IndentPrinter() {
        this(new PrintWriter(System.out), "  ");
    }

    public IndentPrinter(Writer out) {
        this(out, "  ");
    }

    public IndentPrinter(Writer out, String indent) {
        this(out, indent, true);
    }

    public IndentPrinter(Writer out, String indent, boolean addNewlines) {
        this(out, indent, addNewlines, false);
    }

    public IndentPrinter(Writer out, String indent, boolean addNewlines, boolean autoIndent) {
        this.addNewlines = addNewlines;
        if (out == null) {
            throw new IllegalArgumentException("Must specify a Writer");
        }
        this.out = out;
        this.indent = indent;
        this.autoIndent = autoIndent;
    }

    public void println(String text) {
        try {
            if (this.autoIndent) {
                this.printIndent();
            }
            this.out.write(text);
            this.println();
        }
        catch (IOException ioe) {
            throw new GroovyRuntimeException(ioe);
        }
    }

    public void print(String text) {
        try {
            this.out.write(text);
        }
        catch (IOException ioe) {
            throw new GroovyRuntimeException(ioe);
        }
    }

    public void print(char c) {
        try {
            this.out.write(c);
        }
        catch (IOException ioe) {
            throw new GroovyRuntimeException(ioe);
        }
    }

    public void printIndent() {
        for (int i = 0; i < this.indentLevel; ++i) {
            try {
                this.out.write(this.indent);
                continue;
            }
            catch (IOException ioe) {
                throw new GroovyRuntimeException(ioe);
            }
        }
    }

    public void println() {
        if (this.addNewlines) {
            try {
                this.out.write("\n");
            }
            catch (IOException ioe) {
                throw new GroovyRuntimeException(ioe);
            }
        }
    }

    public void incrementIndent() {
        ++this.indentLevel;
    }

    public void decrementIndent() {
        --this.indentLevel;
    }

    public int getIndentLevel() {
        return this.indentLevel;
    }

    public void setIndentLevel(int indentLevel) {
        this.indentLevel = indentLevel;
    }

    public boolean getAutoIndent() {
        return this.autoIndent;
    }

    public void setAutoIndent(boolean autoIndent) {
        this.autoIndent = autoIndent;
    }

    public void flush() {
        try {
            this.out.flush();
        }
        catch (IOException ioe) {
            throw new GroovyRuntimeException(ioe);
        }
    }
}

