/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.docs.KeyName
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument.docs;

import io.micrometer.common.docs.KeyName;
import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.Meter;

public interface MeterDocumentation {
    public static final KeyName[] EMPTY = new KeyName[0];

    public String getName();

    @Nullable
    default public String getBaseUnit() {
        return null;
    }

    public Meter.Type getType();

    default public String getName(String ... vars) {
        if (this.getName().contains("%s")) {
            return String.format(this.getName(), vars);
        }
        return this.getName();
    }

    default public KeyName[] getKeyNames() {
        return EMPTY;
    }

    default public KeyName[] getAdditionalKeyNames() {
        return EMPTY;
    }

    default public Enum<?> overridesDefaultMetricFrom() {
        return null;
    }

    default public String getPrefix() {
        return "";
    }
}

