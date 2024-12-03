/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.templaterenderer;

import java.util.Map;

public interface TemplateContextFactory {
    public Map<String, Object> createContext(String var1, Map<String, Object> var2);
}

