/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.Aware
 */
package org.springframework.context.weaving;

import org.springframework.beans.factory.Aware;
import org.springframework.instrument.classloading.LoadTimeWeaver;

public interface LoadTimeWeaverAware
extends Aware {
    public void setLoadTimeWeaver(LoadTimeWeaver var1);
}

