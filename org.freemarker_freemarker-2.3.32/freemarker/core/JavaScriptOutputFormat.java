/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.OutputFormat;

public class JavaScriptOutputFormat
extends OutputFormat {
    public static final JavaScriptOutputFormat INSTANCE = new JavaScriptOutputFormat();

    private JavaScriptOutputFormat() {
    }

    @Override
    public String getName() {
        return "JavaScript";
    }

    @Override
    public String getMimeType() {
        return "application/javascript";
    }

    @Override
    public boolean isOutputFormatMixingAllowed() {
        return false;
    }
}

