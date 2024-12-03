/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.validator.ValidationError
 *  com.google.common.base.MoreObjects
 */
package com.atlassian.crowd.embedded.validator;

import com.atlassian.crowd.validator.ValidationError;
import com.google.common.base.MoreObjects;
import java.util.Objects;

public class FieldValidationError
implements ValidationError {
    private final String fieldName;
    private final String message;

    public static FieldValidationError of(String inputKey, String message) {
        return new FieldValidationError(inputKey, message);
    }

    private FieldValidationError(String inputKey, String message) {
        this.fieldName = inputKey;
        this.message = message;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public String getErrorMessage() {
        return this.message;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FieldValidationError that = (FieldValidationError)o;
        return Objects.equals(this.fieldName, that.fieldName) && Objects.equals(this.message, that.message);
    }

    public int hashCode() {
        return Objects.hash(this.fieldName, this.message);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("fieldName", (Object)this.fieldName).add("message", (Object)this.message).add("errorMessage", (Object)this.getErrorMessage()).toString();
    }
}

