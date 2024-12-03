/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument.config;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.config.validate.Validated;
import io.micrometer.core.instrument.config.validate.ValidationException;

public interface MeterRegistryConfig {
    public String prefix();

    @Nullable
    public String get(String var1);

    default public Validated<?> validate() {
        return Validated.none();
    }

    default public void requireValid() throws ValidationException {
        this.validate().orThrow();
    }
}

