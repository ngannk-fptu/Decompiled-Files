/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.generic;

import org.apache.velocity.tools.generic.LocaleConfig;
import org.apache.velocity.tools.generic.ValueParser;

public class FormatConfig
extends LocaleConfig {
    public static final String DEFAULT_FORMAT = "default";
    public static final String FORMAT_KEY = "format";
    private String format = "default";

    @Override
    protected void configure(ValueParser values) {
        super.configure(values);
        String format = values.getString(FORMAT_KEY);
        if (format != null) {
            this.setFormat(format);
        }
    }

    public String getFormat() {
        return this.format;
    }

    protected void setFormat(String format) {
        this.format = format;
    }
}

