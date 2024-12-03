/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context;

import org.springframework.context.Lifecycle;

public interface LifecycleProcessor
extends Lifecycle {
    public void onRefresh();

    public void onClose();
}

