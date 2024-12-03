/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.themes.ThemeManager
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 */
package com.atlassian.confluence.extra.flyingpdf.config;

import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

public class SpringScannerJavaConfig {
    public SpringScannerJavaConfig(@ComponentImport PluginAccessor pluginAccessor, @ComponentImport ThemeManager themeManager, @ComponentImport LicenseService licenseService, @ComponentImport PermissionManager permissionManager) {
    }
}

