/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.OutputFormat;

public final class PlainTextOutputFormat
extends OutputFormat {
    public static final PlainTextOutputFormat INSTANCE = new PlainTextOutputFormat();

    private PlainTextOutputFormat() {
    }

    @Override
    public boolean isOutputFormatMixingAllowed() {
        return false;
    }

    @Override
    public String getName() {
        return "plainText";
    }

    @Override
    public String getMimeType() {
        return "text/plain";
    }
}

