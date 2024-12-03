/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.module;

public interface PluginProvidedDecoratorModule {
    public boolean matches(String var1);

    public String getTemplate();

    public String key();
}

