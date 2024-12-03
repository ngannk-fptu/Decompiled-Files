/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.crowd.common.properties;

import com.atlassian.crowd.common.properties.AbstractSystemProperty;
import javax.annotation.Nonnull;

public class StringSystemProperty
extends AbstractSystemProperty<String> {
    public StringSystemProperty(@Nonnull String propertyName, @Nonnull String defaultValue) {
        super(propertyName, defaultValue);
    }

    @Override
    public String fromString(@Nonnull String stringValue) {
        return stringValue;
    }
}

