/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.synchrony;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager;
import com.atlassian.confluence.plugins.synchrony.config.SynchronyDarkFeatureHelper;
import com.atlassian.confluence.plugins.synchrony.service.SynchronyJsonWebTokenGenerator;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.user.User;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SynchronyContextProvider
implements ContextProvider {
    private static final Logger log = LoggerFactory.getLogger(SynchronyContextProvider.class);
    private final PageManager pageManager;
    private final PermissionManager permissionManager;
    private final SynchronyDarkFeatureHelper darkFeatureHelper;
    private final SynchronyConfigurationManager synchronyConfigurationManager;
    private final SynchronyJsonWebTokenGenerator synchronyJsonWebTokenGenerator;

    public SynchronyContextProvider(@ComponentImport PageManager pageManager, @ComponentImport PermissionManager permissionManager, SynchronyDarkFeatureHelper darkFeatureHelper, SynchronyConfigurationManager synchronyConfigurationManager, SynchronyJsonWebTokenGenerator synchronyJsonWebTokenGenerator) {
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.darkFeatureHelper = darkFeatureHelper;
        this.synchronyConfigurationManager = synchronyConfigurationManager;
        this.synchronyJsonWebTokenGenerator = synchronyJsonWebTokenGenerator;
    }

    public void init(Map<String, String> stringStringMap) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> contextMap) {
        Object contentIdObject = contextMap.get("contentId");
        try {
            AbstractPage content;
            Long contentId = contentIdObject == null ? 0L : Long.parseLong(contentIdObject.toString());
            if (contentId != 0L && (content = this.pageManager.getAbstractPage(contentId.longValue())) != null) {
                String spaceId = content.getSpace().getKey();
                boolean featureEnabled = this.darkFeatureHelper.isSynchronyFeatureEnabled(spaceId);
                contextMap.put("synchronyDark", Boolean.toString(featureEnabled));
                if (featureEnabled) {
                    boolean userHasEditPermissions = this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.EDIT, (Object)content);
                    if (userHasEditPermissions) {
                        String token = this.synchronyJsonWebTokenGenerator.create(contentId, AuthenticatedUserThreadLocal.get());
                        long now = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                        contextMap.put("synchronyExpiry", Long.toString(now + SynchronyJsonWebTokenGenerator.TOKEN_EXPIRY_TIME - SynchronyJsonWebTokenGenerator.TOKEN_EXPIRY_LEEWAY));
                        contextMap.put("synchronyJWT", token);
                    }
                    contextMap.put("synchronyBaseUrl", this.synchronyConfigurationManager.getExternalBaseUrl());
                    contextMap.put("synchronyAppId", this.synchronyConfigurationManager.getConfiguredAppID());
                    contextMap.put("useXhrFallback", Boolean.toString(Boolean.valueOf(System.getProperty("synchrony.enable.xhr.fallback", "false"))));
                }
            }
        }
        catch (Exception e) {
            log.error(e.getMessage());
            log.debug("", (Throwable)e);
        }
        return contextMap;
    }
}

