/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.oauth2.provider.rest.model;

import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonProperty;

@XmlRootElement
public class RestError {
    @JsonProperty
    private String error;
    @JsonProperty(value="error_description")
    private String errorDescription;

    public String getError() {
        return this.error;
    }

    public String getErrorDescription() {
        return this.errorDescription;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
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
        String this$error = this.getError();
        String other$error = other.getError();
        if (this$error == null ? other$error != null : !this$error.equals(other$error)) {
            return false;
        }
        String this$errorDescription = this.getErrorDescription();
        String other$errorDescription = other.getErrorDescription();
        return !(this$errorDescription == null ? other$errorDescription != null : !this$errorDescription.equals(other$errorDescription));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RestError;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $error = this.getError();
        result = result * 59 + ($error == null ? 43 : $error.hashCode());
        String $errorDescription = this.getErrorDescription();
        result = result * 59 + ($errorDescription == null ? 43 : $errorDescription.hashCode());
        return result;
    }

    public String toString() {
        return "RestError(error=" + this.getError() + ", errorDescription=" + this.getErrorDescription() + ")";
    }

    public RestError() {
    }

    public RestError(String error, String errorDescription) {
        this.error = error;
        this.errorDescription = errorDescription;
    }
}

