/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro.api;

import java.io.IOException;
import java.io.Writer;
import org.radeox.macro.api.ApiConverter;

public abstract class BaseApiConverter
implements ApiConverter {
    protected String baseUrl;

    public abstract void appendUrl(Writer var1, String var2) throws IOException;

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public abstract String getName();
}

