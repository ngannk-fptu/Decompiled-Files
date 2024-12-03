/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.support;

import org.eclipse.gemini.blueprint.service.importer.support.internal.support.RetryCallback;

public abstract class DefaultRetryCallback<T>
implements RetryCallback<T> {
    @Override
    public boolean isComplete(T result) {
        return result != null;
    }
}

