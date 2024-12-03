/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.pats.core.properties;

import com.atlassian.pats.core.properties.AbstractSystemProperty;
import java.util.Optional;
import javax.annotation.Nonnull;

public class BooleanSystemProperty
extends AbstractSystemProperty<Boolean> {
    BooleanSystemProperty(@Nonnull String propertyName, boolean defaultValue) {
        super(propertyName, defaultValue);
    }

    @Override
    @Nonnull
    public Boolean getValue() {
        return Optional.ofNullable(System.getProperty(this.propertyName)).map(Boolean::parseBoolean).orElse((Boolean)this.defaultValue);
    }
}

