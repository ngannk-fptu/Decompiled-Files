/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.createcontent;

import java.util.Map;

public interface TemplateRendererHelper {
    public String renderFromSoy(String var1, String var2, Map<String, Object> var3);

    public String renderMacroXhtml(String var1, Map<String, String> var2);
}

