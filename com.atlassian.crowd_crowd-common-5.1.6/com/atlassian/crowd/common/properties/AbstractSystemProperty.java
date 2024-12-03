/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.common.properties;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSystemProperty<T> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractSystemProperty.class);
    protected final String propertyName;
    protected final Supplier<T> defaultValueSupplier;

    protected AbstractSystemProperty(@Nonnull String propertyName, @Nonnull T value) {
        this(propertyName, () -> value);
    }

    protected AbstractSystemProperty(@Nonnull String propertyName, @Nonnull Supplier<T> defaultValueSupplier) {
        this.propertyName = Objects.requireNonNull(propertyName);
        this.defaultValueSupplier = Objects.requireNonNull(defaultValueSupplier);
    }

    @Nonnull
    public String getName() {
        return this.propertyName;
    }

    @Nonnull
    public T getValue() {
        String rawValue = System.getProperty(this.propertyName);
        try {
            return (T)Optional.ofNullable(rawValue).map(this::fromString).orElse(Objects.requireNonNull(this.defaultValueSupplier.get()));
        }
        catch (RuntimeException e) {
            logger.warn("Illegal value of system property {}, was {} ", (Object)this.propertyName, (Object)rawValue);
            return this.defaultValueSupplier.get();
        }
    }

    @Nullable
    protected abstract T fromString(@Nonnull String var1);

    public void setValue(@Nonnull T value) {
        this.setRawValue(value.toString());
    }

    public void setRawValue(@Nonnull String value) {
        System.setProperty(this.propertyName, value);
    }

    public void clearValue() {
        System.clearProperty(this.propertyName);
    }
}

