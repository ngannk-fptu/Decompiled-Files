/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.weaving;

import org.springframework.beans.factory.Aware;
import org.springframework.instrument.classloading.LoadTimeWeaver;

public interface LoadTimeWeaverAware
extends Aware {
    public void setLoadTimeWeaver(LoadTimeWeaver var1);
}

