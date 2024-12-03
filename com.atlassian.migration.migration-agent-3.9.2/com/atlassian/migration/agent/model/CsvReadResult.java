/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.model;

import lombok.Generated;

public class CsvReadResult<T> {
    private T result;
    private String errorMessage;

    @Generated
    public T getResult() {
        return this.result;
    }

    @Generated
    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Generated
    public void setResult(T result) {
        this.result = result;
    }

    @Generated
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Generated
    public CsvReadResult(T result, String errorMessage) {
        this.result = result;
        this.errorMessage = errorMessage;
    }
}

