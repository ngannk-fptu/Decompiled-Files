/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.pats.core.properties;

import java.util.Objects;
import javax.annotation.Nonnull;

public abstract class AbstractSystemProperty<T> {
    protected final String propertyName;
    protected final T defaultValue;

    protected AbstractSystemProperty(@Nonnull String propertyName, @Nonnull T defaultValue) {
        this.propertyName = Objects.requireNonNull(propertyName);
        this.defaultValue = Objects.requireNonNull(defaultValue);
    }

    @Nonnull
    public String getName() {
        return this.propertyName;
    }

    @Nonnull
    public abstract T getValue();

    public void setValue(@Nonnull T value) {
        System.setProperty(this.propertyName, value.toString());
    }

    public void clearValue() {
        System.clearProperty(this.propertyName);
    }
}

