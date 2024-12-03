/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.pats.core.properties;

import com.atlassian.pats.core.properties.AbstractSystemProperty;
import javax.annotation.Nonnull;

public class StringSystemProperty
extends AbstractSystemProperty<String> {
    StringSystemProperty(@Nonnull String propertyName, @Nonnull String defaultValue) {
        super(propertyName, defaultValue);
    }

    @Override
    @Nonnull
    public String getValue() {
        return System.getProperty(this.propertyName, (String)this.defaultValue);
    }
}

