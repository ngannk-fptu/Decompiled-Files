/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.plugin.exception;

import com.atlassian.annotations.Internal;
import com.atlassian.plugin.Plugin;

@Internal
public interface PluginExceptionInterception {
    public boolean onEnableException(Plugin var1, Exception var2);
}

