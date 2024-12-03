/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.common.properties;

import com.atlassian.oauth2.common.properties.AbstractSystemProperty;
import java.util.Optional;
import javax.annotation.Nonnull;

public class BooleanSystemProperty
extends AbstractSystemProperty<Boolean> {
    public BooleanSystemProperty(@Nonnull String propertyName, boolean defaultValue) {
        super(propertyName, defaultValue);
    }

    @Override
    @Nonnull
    public Boolean getValue() {
        return Optional.ofNullable(System.getProperty(this.propertyName)).map(Boolean::parseBoolean).orElse((Boolean)this.defaultValue);
    }
}

