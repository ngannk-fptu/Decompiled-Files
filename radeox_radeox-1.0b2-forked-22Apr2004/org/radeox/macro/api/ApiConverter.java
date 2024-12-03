/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro.api;

import java.io.IOException;
import java.io.Writer;

public interface ApiConverter {
    public void appendUrl(Writer var1, String var2) throws IOException;

    public void setBaseUrl(String var1);

    public String getBaseUrl();

    public String getName();
}

