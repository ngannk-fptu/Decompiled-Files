/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.service.exporter.support.internal.controller;

import org.eclipse.gemini.blueprint.service.exporter.support.internal.controller.ExporterInternalActions;
import org.springframework.util.Assert;

public class ExporterController
implements ExporterInternalActions {
    private ExporterInternalActions executor;

    public ExporterController(ExporterInternalActions executor) {
        Assert.notNull((Object)executor);
        this.executor = executor;
    }

    @Override
    public void registerService() {
        this.executor.registerService();
    }

    @Override
    public void registerServiceAtStartup(boolean register) {
        this.executor.registerServiceAtStartup(register);
    }

    @Override
    public void unregisterService() {
        this.executor.unregisterService();
    }

    @Override
    public void callUnregisterOnStartup() {
        this.executor.callUnregisterOnStartup();
    }
}

