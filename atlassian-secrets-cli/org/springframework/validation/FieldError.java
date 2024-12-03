/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.validation;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.ObjectError;

public class FieldError
extends ObjectError {
    private final String field;
    @Nullable
    private final Object rejectedValue;
    private final boolean bindingFailure;

    public FieldError(String objectName, String field, String defaultMessage) {
        this(objectName, field, null, false, null, null, defaultMessage);
    }

    public FieldError(String objectName, String field, @Nullable Object rejectedValue, boolean bindingFailure, @Nullable String[] codes, @Nullable Object[] arguments, @Nullable String defaultMessage) {
        super(objectName, codes, arguments, defaultMessage);
        Assert.notNull((Object)field, "Field must not be null");
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.bindingFailure = bindingFailure;
    }

    public String getField() {
        return this.field;
    }

    @Nullable
    public Object getRejectedValue() {
        return this.rejectedValue;
    }

    public boolean isBindingFailure() {
        return this.bindingFailure;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!super.equals(other)) {
            return false;
        }
        FieldError otherError = (FieldError)other;
        return this.getField().equals(otherError.getField()) && ObjectUtils.nullSafeEquals(this.getRejectedValue(), otherError.getRejectedValue()) && this.isBindingFailure() == otherError.isBindingFailure();
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashCode();
        hashCode = 29 * hashCode + this.getField().hashCode();
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.getRejectedValue());
        hashCode = 29 * hashCode + (this.isBindingFailure() ? 1 : 0);
        return hashCode;
    }

    @Override
    public String toString() {
        return "Field error in object '" + this.getObjectName() + "' on field '" + this.field + "': rejected value [" + ObjectUtils.nullSafeToString(this.rejectedValue) + "]; " + this.resolvableToString();
    }
}

