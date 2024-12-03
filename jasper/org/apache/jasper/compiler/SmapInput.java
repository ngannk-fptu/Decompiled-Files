/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jasper.compiler;

public class SmapInput {
    private final String fileName;
    private final int lineNumber;

    public SmapInput(String fileName, int lineNumber) {
        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }

    public String getFileName() {
        return this.fileName;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }
}

