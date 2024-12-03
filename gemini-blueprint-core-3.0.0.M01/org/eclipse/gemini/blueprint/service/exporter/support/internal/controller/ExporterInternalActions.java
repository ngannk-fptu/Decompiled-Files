/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.service.exporter.support.internal.controller;

public interface ExporterInternalActions {
    public void registerServiceAtStartup(boolean var1);

    public void registerService();

    public void unregisterService();

    public void callUnregisterOnStartup();
}

