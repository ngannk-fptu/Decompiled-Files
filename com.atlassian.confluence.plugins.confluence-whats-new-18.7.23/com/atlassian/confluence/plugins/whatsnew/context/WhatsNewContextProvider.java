/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.CoreFeaturesManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.web.UrlBuilder
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.user.User
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.confluence.plugins.whatsnew.context;

import com.atlassian.confluence.plugins.whatsnew.service.BuildInformationService;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.CoreFeaturesManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.user.User;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class WhatsNewContextProvider
implements ContextProvider {
    private String WHATS_NEW_BTF_URL = "https://confluence.atlassian.com/display/DOC/Confluence+%1s+Release+Notes";
    private static final String WHATS_NEW_OD_URL = "https://confluence.atlassian.com/x/cLPwK";
    private final PermissionManager permissionManager;
    private String baseFullLinkUrl;

    public WhatsNewContextProvider(CoreFeaturesManager featuresManager, PermissionManager permissionManager, BuildInformationService buildInformationService) {
        this.WHATS_NEW_BTF_URL = String.format(this.WHATS_NEW_BTF_URL, buildInformationService.getMajorVersion());
        this.permissionManager = permissionManager;
        this.baseFullLinkUrl = new UrlBuilder(featuresManager.isOnDemand() ? WHATS_NEW_OD_URL : this.WHATS_NEW_BTF_URL).toUrl();
    }

    public void init(Map<String, String> stringStringMap) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> stringObjectMap) {
        return ImmutableMap.of((Object)"whatsnewFullLink", (Object)this.getFullLinkUrl((User)AuthenticatedUserThreadLocal.get()));
    }

    private String getFullLinkUrl(User user) {
        UrlBuilder fullLinkBuilder = new UrlBuilder(this.baseFullLinkUrl);
        if (user != null && !Strings.isNullOrEmpty((String)user.getEmail())) {
            fullLinkBuilder.add("a", this.permissionManager.isConfluenceAdministrator(user));
        }
        return fullLinkBuilder.toUrl();
    }
}

