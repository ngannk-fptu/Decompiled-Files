/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.soy.renderer.SoyException
 */
package com.atlassian.soy.impl;

import com.atlassian.soy.renderer.SoyException;
import java.util.Map;

public interface SoyManager {
    public String compile(CharSequence var1, String var2);

    public void render(Appendable var1, String var2, String var3, Map<String, Object> var4, Map<String, Object> var5) throws SoyException;

    public void clearCaches(String var1);
}

