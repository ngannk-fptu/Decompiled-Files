/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr;

public class LineColumn {
    private int line;
    private int column;

    public LineColumn(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return this.line;
    }

    public int getColumn() {
        return this.column;
    }

    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null || this.getClass() != that.getClass()) {
            return false;
        }
        LineColumn lineColumn = (LineColumn)that;
        if (this.column != lineColumn.column) {
            return false;
        }
        return this.line == lineColumn.line;
    }

    public int hashCode() {
        int result = this.line;
        result = 29 * result + this.column;
        return result;
    }

    public String toString() {
        return "[" + this.line + "," + this.column + "]";
    }
}

