/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import org.hibernate.bytecode.enhance.internal.tracker.DirtyTracker;
import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoadingInterceptor;
import org.hibernate.engine.spi.SelfDirtinessTracker;

public interface ExtendedSelfDirtinessTracker
extends SelfDirtinessTracker {
    public static final String REMOVE_DIRTY_FIELDS_NAME = "$$_hibernate_removeDirtyFields";

    public void $$_hibernate_getCollectionFieldDirtyNames(DirtyTracker var1);

    public boolean $$_hibernate_areCollectionFieldsDirty();

    public void $$_hibernate_clearDirtyCollectionNames();

    public void $$_hibernate_removeDirtyFields(LazyAttributeLoadingInterceptor var1);
}

