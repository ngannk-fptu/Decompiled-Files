/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import org.hibernate.bytecode.enhance.spi.CollectionTracker;

public interface SelfDirtinessTracker {
    public boolean $$_hibernate_hasDirtyAttributes();

    public String[] $$_hibernate_getDirtyAttributes();

    public void $$_hibernate_trackChange(String var1);

    public void $$_hibernate_clearDirtyAttributes();

    public void $$_hibernate_suspendDirtyTracking(boolean var1);

    public CollectionTracker $$_hibernate_getCollectionTracker();
}

