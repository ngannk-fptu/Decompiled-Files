/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.export;

import com.atlassian.confluence.plugins.gatekeeper.export.Exporter;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.ExportSettings;

public interface ExporterFactory {
    public Exporter createExporter(ExportSettings var1);
}

