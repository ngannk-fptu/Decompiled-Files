/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.rest.exception;

public class InvalidClientException
extends Exception {
    private static final String INVALID_CLIENT = "invalid_client";
    private final String error;
    private final String description;

    public InvalidClientException(String description) {
        this.error = INVALID_CLIENT;
        this.description = description;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InvalidClientException)) {
            return false;
        }
        InvalidClientException other = (InvalidClientException)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        String this$error = this.getError();
        String other$error = other.getError();
        if (this$error == null ? other$error != null : !this$error.equals(other$error)) {
            return false;
        }
        String this$description = this.getDescription();
        String other$description = other.getDescription();
        return !(this$description == null ? other$description != null : !this$description.equals(other$description));
    }

    protected boolean canEqual(Object other) {
        return other instanceof InvalidClientException;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        String $error = this.getError();
        result = result * 59 + ($error == null ? 43 : $error.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        return result;
    }

    public String getError() {
        return this.error;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return "InvalidClientException(error=" + this.getError() + ", description=" + this.getDescription() + ")";
    }
}

