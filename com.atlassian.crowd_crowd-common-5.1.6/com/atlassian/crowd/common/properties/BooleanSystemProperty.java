/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.crowd.common.properties;

import com.atlassian.crowd.common.properties.AbstractSystemProperty;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;

public class BooleanSystemProperty
extends AbstractSystemProperty<Boolean> {
    public BooleanSystemProperty(@Nonnull String propertyName, boolean defaultValue) {
        super(propertyName, defaultValue);
    }

    public BooleanSystemProperty(@Nonnull String propertyName, @Nonnull BooleanSupplier defaultValueSupplier) {
        super(propertyName, defaultValueSupplier::getAsBoolean);
    }

    @Override
    public Boolean fromString(@Nonnull String stringValue) {
        return Boolean.parseBoolean(stringValue);
    }
}

