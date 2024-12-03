/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sourcemap;

import com.atlassian.sourcemap.Mapping;

class MappingImpl
implements Mapping {
    private int generatedLine;
    private final int generatedColumn;
    private final int sourceLine;
    private final int sourceColumn;
    private String sourceFileName;
    private final String sourceSymbolName;

    public MappingImpl(int generatedLine, int generatedColumn, int sourceLine, int sourceColumn, String sourceFileName, String sourceSymbolName) {
        this.generatedLine = generatedLine;
        this.generatedColumn = generatedColumn;
        this.sourceLine = sourceLine;
        this.sourceColumn = sourceColumn;
        this.sourceFileName = sourceFileName;
        this.sourceSymbolName = sourceSymbolName;
    }

    public String toString() {
        return "Mapping " + this.generatedLine + ":" + this.generatedColumn + " -> " + this.sourceFileName + ":" + this.sourceLine + ":" + this.sourceColumn;
    }

    @Override
    public int getGeneratedLine() {
        return this.generatedLine;
    }

    @Override
    public void setGeneratedLine(int newLine) {
        this.generatedLine = newLine;
    }

    @Override
    public int getGeneratedColumn() {
        return this.generatedColumn;
    }

    @Override
    public int getSourceLine() {
        return this.sourceLine;
    }

    @Override
    public int getSourceColumn() {
        return this.sourceColumn;
    }

    @Override
    public String getSourceFileName() {
        return this.sourceFileName;
    }

    @Override
    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    @Override
    public String getSourceSymbolName() {
        return this.sourceSymbolName;
    }
}

