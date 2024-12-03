/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.web.model;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public interface WebPanel {
    public String getHtml(Map<String, Object> var1);

    public void writeHtml(Writer var1, Map<String, Object> var2) throws IOException;
}

