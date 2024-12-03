/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 */
package com.atlassian.confluence.extra.calendar3.model.rest;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(value=XmlAccessType.PROPERTY)
public class ErrorInfo
implements Serializable {
    private String errorMessage;
    private String errorDetail;

    private ErrorInfo() {
    }

    private ErrorInfo(String errorMessage, String errorDetail) {
        this.errorMessage = errorMessage;
        this.errorDetail = errorDetail;
    }

    public String getErrorDetail() {
        return this.errorDetail;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public static Builder Builder() {
        return new Builder();
    }

    public static class Builder {
        private String errorMessage;
        private String errorDetail;

        public Builder withErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public Builder withErrorDetail(String errorDetail) {
            this.errorDetail = errorDetail;
            return this;
        }

        public ErrorInfo build() {
            return new ErrorInfo(this.errorMessage, this.errorDetail);
        }
    }
}

