/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.api;

public interface WebResourcesService {
    public void requireResourcesForContext(String var1);

    public void requireResource(String var1);

    public String getStaticPluginResource(String var1, String var2);
}

