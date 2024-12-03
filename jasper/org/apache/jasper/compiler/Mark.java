/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jasper.compiler;

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.compiler.JspReader;

final class Mark {
    int cursor;
    int line;
    int col;
    char[] stream = null;
    private String fileName;
    private JspCompilationContext ctxt;

    Mark(JspReader reader, char[] inStream, String name) {
        this.ctxt = reader.getJspCompilationContext();
        this.stream = inStream;
        this.cursor = 0;
        this.line = 1;
        this.col = 1;
        this.fileName = name;
    }

    Mark(Mark other) {
        this.init(other, false);
    }

    void update(int cursor, int line, int col) {
        this.cursor = cursor;
        this.line = line;
        this.col = col;
    }

    void init(Mark other, boolean singleFile) {
        this.cursor = other.cursor;
        this.line = other.line;
        this.col = other.col;
        if (!singleFile) {
            this.ctxt = other.ctxt;
            this.stream = other.stream;
            this.fileName = other.fileName;
        }
    }

    Mark(JspCompilationContext ctxt, String filename, int line, int col) {
        this.ctxt = ctxt;
        this.stream = null;
        this.cursor = 0;
        this.line = line;
        this.col = col;
        this.fileName = filename;
    }

    public int getLineNumber() {
        return this.line;
    }

    public int getColumnNumber() {
        return this.col;
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

    public boolean equals(Object other) {
        if (other instanceof Mark) {
            Mark m = (Mark)other;
            return this.cursor == m.cursor && this.line == m.line && this.col == m.col;
        }
        return false;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.col;
        result = 31 * result + this.cursor;
        result = 31 * result + this.line;
        return result;
    }
}

