/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context;

import org.springframework.context.Lifecycle;
import org.springframework.context.Phased;

public interface SmartLifecycle
extends Lifecycle,
Phased {
    public boolean isAutoStartup();

    public void stop(Runnable var1);
}

