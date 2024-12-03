/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.support;

public interface RetryCallback<T> {
    public T doWithRetry();

    public boolean isComplete(T var1);
}

