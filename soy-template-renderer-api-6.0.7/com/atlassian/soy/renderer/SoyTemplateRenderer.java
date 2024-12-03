/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.soy.renderer;

import com.atlassian.annotations.PublicApi;
import com.atlassian.soy.renderer.SoyException;
import java.util.Map;

@PublicApi
public interface SoyTemplateRenderer {
    public void clearAllCaches();

    public void clearCache(String var1);

    public String render(String var1, String var2, Map<String, Object> var3) throws SoyException;

    public void render(Appendable var1, String var2, String var3, Map<String, Object> var4) throws SoyException;

    public void render(Appendable var1, String var2, String var3, Map<String, Object> var4, Map<String, Object> var5) throws SoyException;
}

