/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import org.springframework.instrument.classloading.LoadTimeWeaver;

public interface LoadTimeWeavingConfigurer {
    public LoadTimeWeaver getLoadTimeWeaver();
}

