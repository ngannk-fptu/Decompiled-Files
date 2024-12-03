/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.components.compiler;

public class CompilerError {
    private boolean error;
    private int startline;
    private int startcolumn;
    private int endline;
    private int endcolumn;
    private String file;
    private String message;

    public CompilerError(String file, boolean error, int startline, int startcolumn, int endline, int endcolumn, String message) {
        this.file = file;
        this.error = error;
        this.startline = startline;
        this.startcolumn = startcolumn;
        this.endline = endline;
        this.endcolumn = endcolumn;
        this.message = message;
    }

    public CompilerError(String message) {
        this.message = message;
    }

    public String getFile() {
        return this.file;
    }

    public boolean isError() {
        return this.error;
    }

    public int getStartLine() {
        return this.startline;
    }

    public int getStartColumn() {
        return this.startcolumn;
    }

    public int getEndLine() {
        return this.endline;
    }

    public int getEndColumn() {
        return this.endcolumn;
    }

    public String getMessage() {
        return this.message;
    }
}

