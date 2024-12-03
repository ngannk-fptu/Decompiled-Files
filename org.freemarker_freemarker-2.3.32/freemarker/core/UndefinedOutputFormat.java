/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.OutputFormat;

public final class UndefinedOutputFormat
extends OutputFormat {
    public static final UndefinedOutputFormat INSTANCE = new UndefinedOutputFormat();

    private UndefinedOutputFormat() {
    }

    @Override
    public boolean isOutputFormatMixingAllowed() {
        return true;
    }

    @Override
    public String getName() {
        return "undefined";
    }

    @Override
    public String getMimeType() {
        return null;
    }
}

