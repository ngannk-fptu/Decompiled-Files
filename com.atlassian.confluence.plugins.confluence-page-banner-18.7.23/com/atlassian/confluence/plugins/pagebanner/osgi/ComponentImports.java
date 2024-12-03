/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.pages.DraftsTransitionHelper
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.themes.ThemeManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.web.context.HttpContext
 */
package com.atlassian.confluence.plugins.pagebanner.osgi;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.pages.DraftsTransitionHelper;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.web.context.HttpContext;

public class ComponentImports {
    public ComponentImports(@ComponentImport ThemeManager themeManager, @ComponentImport WebInterfaceManager webInterfaceManager, @ComponentImport WebResourceUrlProvider webResourceUrlProvider, @ComponentImport SpacePermissionManager spacePermissionManager, @ComponentImport ContentEntityManager contentEntityManager, @ComponentImport DarkFeatureManager darkFeatureManager, @ComponentImport DraftsTransitionHelper draftsTransitionHelper, @ComponentImport HttpContext httpContext) {
    }
}

