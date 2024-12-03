/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

public final class RowSelection {
    private Integer firstRow;
    private Integer maxRows;
    private Integer timeout;
    private Integer fetchSize;

    public void setFirstRow(Integer firstRow) {
        if (firstRow != null && firstRow < 0) {
            throw new IllegalArgumentException("first-row value cannot be negative : " + firstRow);
        }
        this.firstRow = firstRow;
    }

    public void setFirstRow(int firstRow) {
        this.firstRow = firstRow;
    }

    public Integer getFirstRow() {
        return this.firstRow;
    }

    public void setMaxRows(Integer maxRows) {
        this.maxRows = maxRows;
    }

    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    public Integer getMaxRows() {
        return this.maxRows;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public Integer getTimeout() {
        return this.timeout;
    }

    public Integer getFetchSize() {
        return this.fetchSize;
    }

    public void setFetchSize(Integer fetchSize) {
        this.fetchSize = fetchSize;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public boolean definesLimits() {
        return this.maxRows != null || this.firstRow != null && this.firstRow <= 0;
    }
}

