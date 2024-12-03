/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.pages.templates.PageTemplateManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.templates;

import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.pages.templates.PageTemplateManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.user.User;
import java.util.Map;

public class ListGlobalTemplatesContextProvider
implements ContextProvider {
    private final PageTemplateManager pageTemplateManager;
    private final I18nResolver i18nResolver;
    private final ContextPathHolder contextPathHolder;
    private final PermissionManager permissionManager;

    public ListGlobalTemplatesContextProvider(PageTemplateManager pageTemplateManager, I18nResolver i18nResolver, ContextPathHolder contextPathHolder, PermissionManager permissionManager) {
        this.pageTemplateManager = pageTemplateManager;
        this.i18nResolver = i18nResolver;
        this.contextPathHolder = contextPathHolder;
        this.permissionManager = permissionManager;
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> context) {
        context.put("pageTemplates", this.pageTemplateManager.getGlobalPageTemplates());
        context.put("i18nResolver", this.i18nResolver);
        context.put("contextPath", this.contextPathHolder.getContextPath());
        context.put("isConfluenceAdministrator", this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION));
        return context;
    }
}

