/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.util;

import java.io.Serializable;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CsvContext
implements Serializable {
    private static final long serialVersionUID = 1L;
    private int lineNumber;
    private int rowNumber;
    private int columnNumber;
    private List<Object> rowSource;

    public CsvContext(int lineNumber, int rowNumber, int columnNumber) {
        this.lineNumber = lineNumber;
        this.rowNumber = rowNumber;
        this.columnNumber = columnNumber;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getRowNumber() {
        return this.rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public int getColumnNumber() {
        return this.columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    public List<Object> getRowSource() {
        return this.rowSource;
    }

    public void setRowSource(List<Object> rowSource) {
        this.rowSource = rowSource;
    }

    public String toString() {
        return String.format("{lineNo=%d, rowNo=%d, columnNo=%d, rowSource=%s}", this.lineNumber, this.rowNumber, this.columnNumber, this.rowSource);
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.columnNumber;
        result = 31 * result + this.rowNumber;
        result = 31 * result + this.lineNumber;
        result = 31 * result + (this.rowSource == null ? 0 : this.rowSource.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        CsvContext other = (CsvContext)obj;
        if (this.columnNumber != other.columnNumber) {
            return false;
        }
        if (this.rowNumber != other.rowNumber) {
            return false;
        }
        if (this.lineNumber != other.lineNumber) {
            return false;
        }
        return !(this.rowSource == null ? other.rowSource != null : !this.rowSource.equals(other.rowSource));
    }
}

