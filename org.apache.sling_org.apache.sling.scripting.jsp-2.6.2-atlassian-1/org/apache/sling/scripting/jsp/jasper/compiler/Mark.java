/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Stack;
import org.apache.sling.scripting.jsp.jasper.JspCompilationContext;
import org.apache.sling.scripting.jsp.jasper.compiler.JspReader;

final class Mark {
    int cursor;
    int line;
    int col;
    String baseDir;
    char[] stream = null;
    private int fileId;
    private String fileName;
    private Stack includeStack = null;
    private String encoding = null;
    private JspReader reader;
    private JspCompilationContext ctxt;

    Mark(JspReader reader, char[] inStream, int fileId, String name, String inBaseDir, String inEncoding) {
        this.reader = reader;
        this.ctxt = reader.getJspCompilationContext();
        this.stream = inStream;
        this.cursor = 0;
        this.line = 1;
        this.col = 1;
        this.fileId = fileId;
        this.fileName = name;
        this.baseDir = inBaseDir;
        this.encoding = inEncoding;
        this.includeStack = new Stack();
    }

    Mark(Mark other) {
        this.reader = other.reader;
        this.ctxt = other.reader.getJspCompilationContext();
        this.stream = other.stream;
        this.fileId = other.fileId;
        this.fileName = other.fileName;
        this.cursor = other.cursor;
        this.line = other.line;
        this.col = other.col;
        this.baseDir = other.baseDir;
        this.encoding = other.encoding;
        this.includeStack = new Stack();
        for (int i = 0; i < other.includeStack.size(); ++i) {
            this.includeStack.addElement(other.includeStack.elementAt(i));
        }
    }

    Mark(JspCompilationContext ctxt, String filename, int line, int col) {
        this.reader = null;
        this.ctxt = ctxt;
        this.stream = null;
        this.cursor = 0;
        this.line = line;
        this.col = col;
        this.fileId = -1;
        this.fileName = filename;
        this.baseDir = "le-basedir";
        this.encoding = "le-endocing";
        this.includeStack = null;
    }

    public void pushStream(char[] inStream, int inFileId, String name, String inBaseDir, String inEncoding) {
        this.includeStack.push(new IncludeState(this.cursor, this.line, this.col, this.fileId, this.fileName, this.baseDir, this.encoding, this.stream));
        this.cursor = 0;
        this.line = 1;
        this.col = 1;
        this.fileId = inFileId;
        this.fileName = name;
        this.baseDir = inBaseDir;
        this.encoding = inEncoding;
        this.stream = inStream;
    }

    public Mark popStream() {
        if (this.includeStack.size() <= 0) {
            return null;
        }
        IncludeState state = (IncludeState)this.includeStack.pop();
        this.cursor = state.cursor;
        this.line = state.line;
        this.col = state.col;
        this.fileId = state.fileId;
        this.fileName = state.fileName;
        this.baseDir = state.baseDir;
        this.stream = state.stream;
        return this;
    }

    public int getLineNumber() {
        return this.line;
    }

    public int getColumnNumber() {
        return this.col;
    }

    public String getSystemId() {
        return this.getFile();
    }

    public String getPublicId() {
        return null;
    }

    public String toString() {
        return this.getFile() + "(" + this.line + "," + this.col + ")";
    }

    public String getFile() {
        return this.fileName;
    }

    public URL getURL() throws MalformedURLException {
        return this.ctxt.getResource(this.getFile());
    }

    public String toShortString() {
        return "(" + this.line + "," + this.col + ")";
    }

    public boolean equals(Object other) {
        if (other instanceof Mark) {
            Mark m = (Mark)other;
            return this.reader == m.reader && this.fileId == m.fileId && this.cursor == m.cursor && this.line == m.line && this.col == m.col;
        }
        return false;
    }

    public boolean isGreater(Mark other) {
        boolean greater = false;
        if (this.line > other.line) {
            greater = true;
        } else if (this.line == other.line && this.col > other.col) {
            greater = true;
        }
        return greater;
    }

    class IncludeState {
        int cursor;
        int line;
        int col;
        int fileId;
        String fileName;
        String baseDir;
        String encoding;
        char[] stream = null;

        IncludeState(int inCursor, int inLine, int inCol, int inFileId, String name, String inBaseDir, String inEncoding, char[] inStream) {
            this.cursor = inCursor;
            this.line = inLine;
            this.col = inCol;
            this.fileId = inFileId;
            this.fileName = name;
            this.baseDir = inBaseDir;
            this.encoding = inEncoding;
            this.stream = inStream;
        }
    }
}

