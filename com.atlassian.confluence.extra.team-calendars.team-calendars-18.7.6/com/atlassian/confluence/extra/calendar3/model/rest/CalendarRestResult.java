/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 */
package com.atlassian.confluence.extra.calendar3.model.rest;

import com.atlassian.confluence.extra.calendar3.model.rest.ErrorInfo;
import java.io.Serializable;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(value=XmlAccessType.PROPERTY)
public class CalendarRestResult
implements Serializable {
    private int statusCode;
    private ErrorInfo errorInfo;

    private CalendarRestResult() {
    }

    private CalendarRestResult(int statusCode, ErrorInfo errorInfo) {
        this.statusCode = statusCode;
        this.errorInfo = errorInfo;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    @Nullable
    public ErrorInfo getErrorInfo() {
        return this.errorInfo;
    }

    public static Builder Builder() {
        return new Builder();
    }

    public static class Builder {
        private int statusCode;
        private ErrorInfo errorInfo;

        public Builder withStatusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder withErrorInfo(ErrorInfo errorInfo) {
            this.errorInfo = errorInfo;
            return this;
        }

        public CalendarRestResult build() {
            return new CalendarRestResult(this.statusCode, this.errorInfo);
        }
    }
}

