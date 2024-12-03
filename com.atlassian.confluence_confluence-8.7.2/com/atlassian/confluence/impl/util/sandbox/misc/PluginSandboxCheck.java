/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.util.sandbox.misc;

public class PluginSandboxCheck {
    public static boolean documentConversionSandboxExplicitlyDisabled() {
        return Boolean.getBoolean("document.conversion.sandbox.disable");
    }

    public static boolean pdfExportSandboxExplicitlyDisabled() {
        return Boolean.getBoolean("pdf.export.sandbox.disable");
    }
}

