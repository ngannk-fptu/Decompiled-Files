/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.interceptor.debugging;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Stack;

public class PrettyPrintWriter {
    private final PrintWriter writer;
    private final Stack<String> elementStack = new Stack();
    private final char[] lineIndenter;
    private boolean tagInProgress;
    private int depth;
    private boolean readyForNewLine;
    private boolean tagIsEmpty;
    private String newLine;
    private boolean escape = true;
    private static final char[] NULL = "&#x0;".toCharArray();
    private static final char[] AMP = "&amp;".toCharArray();
    private static final char[] LT = "&lt;".toCharArray();
    private static final char[] GT = "&gt;".toCharArray();
    private static final char[] SLASH_R = "&#x0D;".toCharArray();
    private static final char[] QUOT = "&quot;".toCharArray();
    private static final char[] APOS = "&apos;".toCharArray();
    private static final char[] CLOSE = "</".toCharArray();

    public PrettyPrintWriter(Writer writer, char[] lineIndenter, String newLine) {
        this.writer = new PrintWriter(writer);
        this.lineIndenter = lineIndenter;
        this.newLine = newLine;
    }

    public PrettyPrintWriter(Writer writer, char[] lineIndenter) {
        this(writer, lineIndenter, "\n");
    }

    public PrettyPrintWriter(Writer writer, String lineIndenter, String newLine) {
        this(writer, lineIndenter.toCharArray(), newLine);
    }

    public PrettyPrintWriter(Writer writer, String lineIndenter) {
        this(writer, lineIndenter.toCharArray());
    }

    public PrettyPrintWriter(Writer writer) {
        this(writer, new char[]{' ', ' '});
    }

    public void startNode(String name) {
        this.tagIsEmpty = false;
        this.finishTag();
        this.writer.write(60);
        this.writer.write(name);
        this.elementStack.push(name);
        this.tagInProgress = true;
        ++this.depth;
        this.readyForNewLine = true;
        this.tagIsEmpty = true;
    }

    public void setValue(String text) {
        this.readyForNewLine = false;
        this.tagIsEmpty = false;
        this.finishTag();
        this.writeText(this.writer, text);
    }

    public void addAttribute(String key, String value) {
        this.writer.write(32);
        this.writer.write(key);
        this.writer.write(61);
        this.writer.write(34);
        this.writeAttributeValue(this.writer, value);
        this.writer.write(34);
    }

    protected void writeAttributeValue(PrintWriter writer, String text) {
        this.writeText(text);
    }

    protected void writeText(PrintWriter writer, String text) {
        this.writeText(text);
    }

    private void writeText(String text) {
        int length = text.length();
        block9: for (int i = 0; i < length; ++i) {
            char c = text.charAt(i);
            switch (c) {
                case '\u0000': {
                    this.writer.write(NULL);
                    continue block9;
                }
                case '&': {
                    this.writer.write(AMP);
                    continue block9;
                }
                case '<': {
                    this.writer.write(LT);
                    continue block9;
                }
                case '>': {
                    this.writer.write(GT);
                    continue block9;
                }
                case '\"': {
                    this.writer.write(QUOT);
                    continue block9;
                }
                case '\'': {
                    if (this.escape) {
                        this.writer.write(APOS);
                        continue block9;
                    }
                    this.writer.write(c);
                    continue block9;
                }
                case '\r': {
                    this.writer.write(SLASH_R);
                    continue block9;
                }
                default: {
                    this.writer.write(c);
                }
            }
        }
    }

    public void endNode() {
        --this.depth;
        if (this.tagIsEmpty) {
            this.writer.write(47);
            this.readyForNewLine = false;
            this.finishTag();
            this.elementStack.pop();
        } else {
            this.finishTag();
            this.writer.write(CLOSE);
            this.writer.write(this.elementStack.pop());
            this.writer.write(62);
        }
        this.readyForNewLine = true;
        if (this.depth == 0) {
            this.writer.flush();
        }
    }

    private void finishTag() {
        if (this.tagInProgress) {
            this.writer.write(62);
        }
        this.tagInProgress = false;
        if (this.readyForNewLine) {
            this.endOfLine();
        }
        this.readyForNewLine = false;
        this.tagIsEmpty = false;
    }

    protected void endOfLine() {
        this.writer.write(this.newLine);
        for (int i = 0; i < this.depth; ++i) {
            this.writer.write(this.lineIndenter);
        }
    }

    public void flush() {
        this.writer.flush();
    }

    public void close() {
        this.writer.close();
    }

    public boolean isEscape() {
        return this.escape;
    }

    public void setEscape(boolean escape) {
        this.escape = escape;
    }
}

