/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.OutputFormat;

public class JSONOutputFormat
extends OutputFormat {
    public static final JSONOutputFormat INSTANCE = new JSONOutputFormat();

    private JSONOutputFormat() {
    }

    @Override
    public String getName() {
        return "JSON";
    }

    @Override
    public String getMimeType() {
        return "application/json";
    }

    @Override
    public boolean isOutputFormatMixingAllowed() {
        return false;
    }
}

