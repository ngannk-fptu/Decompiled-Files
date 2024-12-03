/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.OutputFormat;

public class CSSOutputFormat
extends OutputFormat {
    public static final CSSOutputFormat INSTANCE = new CSSOutputFormat();

    private CSSOutputFormat() {
    }

    @Override
    public String getName() {
        return "CSS";
    }

    @Override
    public String getMimeType() {
        return "text/css";
    }

    @Override
    public boolean isOutputFormatMixingAllowed() {
        return false;
    }
}

