/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.controller;

import org.eclipse.gemini.blueprint.service.importer.support.internal.dependency.ImporterStateListener;

public interface ImporterInternalActions {
    public void addStateListener(ImporterStateListener var1);

    public void removeStateListener(ImporterStateListener var1);

    public boolean isSatisfied();
}

