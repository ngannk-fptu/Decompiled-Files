/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.crowd.common.properties;

import com.atlassian.crowd.common.properties.AbstractSystemProperty;
import javax.annotation.Nonnull;

public class IntegerSystemProperty
extends AbstractSystemProperty<Integer> {
    public IntegerSystemProperty(@Nonnull String propertyName, int defaultValue) {
        super(propertyName, defaultValue);
    }

    @Override
    public Integer fromString(@Nonnull String stringValue) {
        return Integer.parseInt(stringValue);
    }
}

