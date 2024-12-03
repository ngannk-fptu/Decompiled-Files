/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.content.collab;

public interface ReconcileContentRegisterTask<T>
extends Runnable {
    public void registerReconcileContent(T var1);
}

