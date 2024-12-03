/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import org.hibernate.event.spi.EventSource;

public interface EntityCopyObserver {
    public void entityCopyDetected(Object var1, Object var2, Object var3, EventSource var4);

    public void topLevelMergeComplete(EventSource var1);

    public void clear();
}

