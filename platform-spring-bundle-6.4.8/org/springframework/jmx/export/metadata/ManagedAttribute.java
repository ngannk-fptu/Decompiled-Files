/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jmx.export.metadata;

import org.springframework.jmx.export.metadata.AbstractJmxAttribute;
import org.springframework.lang.Nullable;

public class ManagedAttribute
extends AbstractJmxAttribute {
    public static final ManagedAttribute EMPTY = new ManagedAttribute();
    @Nullable
    private Object defaultValue;
    @Nullable
    private String persistPolicy;
    private int persistPeriod = -1;

    public void setDefaultValue(@Nullable Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Nullable
    public Object getDefaultValue() {
        return this.defaultValue;
    }

    public void setPersistPolicy(@Nullable String persistPolicy) {
        this.persistPolicy = persistPolicy;
    }

    @Nullable
    public String getPersistPolicy() {
        return this.persistPolicy;
    }

    public void setPersistPeriod(int persistPeriod) {
        this.persistPeriod = persistPeriod;
    }

    public int getPersistPeriod() {
        return this.persistPeriod;
    }
}

