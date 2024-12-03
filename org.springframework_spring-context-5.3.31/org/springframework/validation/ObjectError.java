/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.validation;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ObjectError
extends DefaultMessageSourceResolvable {
    private final String objectName;
    @Nullable
    private transient Object source;

    public ObjectError(String objectName, String defaultMessage) {
        this(objectName, null, null, defaultMessage);
    }

    public ObjectError(String objectName, @Nullable String[] codes, @Nullable Object[] arguments, @Nullable String defaultMessage) {
        super(codes, arguments, defaultMessage);
        Assert.notNull((Object)objectName, (String)"Object name must not be null");
        this.objectName = objectName;
    }

    public String getObjectName() {
        return this.objectName;
    }

    public void wrap(Object source) {
        if (this.source != null) {
            throw new IllegalStateException("Already wrapping " + this.source);
        }
        this.source = source;
    }

    public <T> T unwrap(Class<T> sourceType) {
        Throwable cause;
        if (sourceType.isInstance(this.source)) {
            return sourceType.cast(this.source);
        }
        if (this.source instanceof Throwable && sourceType.isInstance(cause = ((Throwable)this.source).getCause())) {
            return sourceType.cast(cause);
        }
        throw new IllegalArgumentException("No source object of the given type available: " + sourceType);
    }

    public boolean contains(Class<?> sourceType) {
        return sourceType.isInstance(this.source) || this.source instanceof Throwable && sourceType.isInstance(((Throwable)this.source).getCause());
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || other.getClass() != this.getClass() || !super.equals(other)) {
            return false;
        }
        ObjectError otherError = (ObjectError)other;
        return this.getObjectName().equals(otherError.getObjectName());
    }

    @Override
    public int hashCode() {
        return 29 * super.hashCode() + this.getObjectName().hashCode();
    }

    @Override
    public String toString() {
        return "Error in object '" + this.objectName + "': " + this.resolvableToString();
    }
}

