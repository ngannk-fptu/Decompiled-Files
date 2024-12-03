/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 */
package com.atlassian.pats.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(value=XmlAccessType.FIELD)
public class RestError {
    @XmlElement
    private String exception;
    @XmlElement
    private String error;

    public static RestErrorBuilder builder() {
        return new RestErrorBuilder();
    }

    public String getException() {
        return this.exception;
    }

    public String getError() {
        return this.error;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RestError)) {
            return false;
        }
        RestError other = (RestError)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$exception = this.getException();
        String other$exception = other.getException();
        if (this$exception == null ? other$exception != null : !this$exception.equals(other$exception)) {
            return false;
        }
        String this$error = this.getError();
        String other$error = other.getError();
        return !(this$error == null ? other$error != null : !this$error.equals(other$error));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RestError;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $exception = this.getException();
        result = result * 59 + ($exception == null ? 43 : $exception.hashCode());
        String $error = this.getError();
        result = result * 59 + ($error == null ? 43 : $error.hashCode());
        return result;
    }

    public String toString() {
        return "RestError(exception=" + this.getException() + ", error=" + this.getError() + ")";
    }

    public RestError(String exception, String error) {
        this.exception = exception;
        this.error = error;
    }

    public RestError() {
    }

    public static class RestErrorBuilder {
        private String exception;
        private String error;

        RestErrorBuilder() {
        }

        public RestErrorBuilder exception(String exception) {
            this.exception = exception;
            return this;
        }

        public RestErrorBuilder error(String error) {
            this.error = error;
            return this;
        }

        public RestError build() {
            return new RestError(this.exception, this.error);
        }

        public String toString() {
            return "RestError.RestErrorBuilder(exception=" + this.exception + ", error=" + this.error + ")";
        }
    }
}

