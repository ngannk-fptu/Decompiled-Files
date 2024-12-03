/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.transaction.backend.jta.internal.synchronization;

public interface SynchronizationCallbackTarget {
    public boolean isActive();

    public void beforeCompletion();

    public void afterCompletion(boolean var1, boolean var2);
}

