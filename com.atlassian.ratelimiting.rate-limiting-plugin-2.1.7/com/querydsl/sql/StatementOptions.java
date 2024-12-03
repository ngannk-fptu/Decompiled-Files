/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.Immutable
 */
package com.querydsl.sql;

import javax.annotation.concurrent.Immutable;

@Immutable
public class StatementOptions {
    public static final StatementOptions DEFAULT = new StatementOptions(null, null, null, null);
    private final Integer maxFieldSize;
    private final Integer maxRows;
    private final Integer queryTimeout;
    private final Integer fetchSize;

    public StatementOptions(Integer maxFieldSize, Integer maxRows, Integer queryTimeout, Integer fetchSize) {
        this.maxFieldSize = maxFieldSize;
        this.maxRows = maxRows;
        this.queryTimeout = queryTimeout;
        this.fetchSize = fetchSize;
    }

    public Integer getMaxFieldSize() {
        return this.maxFieldSize;
    }

    public Integer getMaxRows() {
        return this.maxRows;
    }

    public Integer getQueryTimeout() {
        return this.queryTimeout;
    }

    public Integer getFetchSize() {
        return this.fetchSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Integer maxFieldSize;
        private Integer maxRows;
        private Integer queryTimeout;
        private Integer fetchSize;

        private Builder() {
        }

        public Builder setMaxFieldSize(Integer maxFieldSize) {
            this.maxFieldSize = maxFieldSize;
            return this;
        }

        public Builder setMaxRows(Integer maxRows) {
            this.maxRows = maxRows;
            return this;
        }

        public Builder setQueryTimeout(Integer queryTimeout) {
            this.queryTimeout = queryTimeout;
            return this;
        }

        public Builder setFetchSize(Integer fetchSize) {
            this.fetchSize = fetchSize;
            return this;
        }

        public StatementOptions build() {
            return new StatementOptions(this.maxFieldSize, this.maxRows, this.queryTimeout, this.fetchSize);
        }
    }
}

