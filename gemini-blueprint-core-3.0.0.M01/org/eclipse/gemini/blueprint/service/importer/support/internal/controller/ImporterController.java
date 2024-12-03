/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.controller;

import org.eclipse.gemini.blueprint.service.importer.support.internal.controller.ImporterInternalActions;
import org.eclipse.gemini.blueprint.service.importer.support.internal.dependency.ImporterStateListener;
import org.springframework.util.Assert;

public class ImporterController
implements ImporterInternalActions {
    private ImporterInternalActions executor;

    public ImporterController(ImporterInternalActions executor) {
        Assert.notNull((Object)executor);
        this.executor = executor;
    }

    @Override
    public void addStateListener(ImporterStateListener stateListener) {
        this.executor.addStateListener(stateListener);
    }

    @Override
    public void removeStateListener(ImporterStateListener stateListener) {
        this.executor.removeStateListener(stateListener);
    }

    @Override
    public boolean isSatisfied() {
        return this.executor.isSatisfied();
    }
}

