/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserManager
 *  javax.inject.Inject
 *  javax.inject.Named
 */
package com.atlassian.confluence.image.effects;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import javax.inject.Inject;
import javax.inject.Named;

@Named(value="pluginComponentImports")
public class PluginComponentImports {
    @Inject
    public PluginComponentImports(@ComponentImport UserManager userManager) {
    }
}

