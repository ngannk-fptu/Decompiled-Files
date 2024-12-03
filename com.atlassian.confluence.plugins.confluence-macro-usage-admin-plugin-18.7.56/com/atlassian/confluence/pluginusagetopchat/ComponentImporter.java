/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 */
package com.atlassian.confluence.pluginusagetopchat;

import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

public final class ComponentImporter {
    @ComponentImport
    private PermissionManager permissionManager;
    @ComponentImport
    private PluginAccessor pluginAccessor;
}

