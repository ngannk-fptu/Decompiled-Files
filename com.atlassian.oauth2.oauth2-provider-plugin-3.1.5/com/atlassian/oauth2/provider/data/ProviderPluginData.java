/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.oauth2.scopes.api.ScopeDescriptionService
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.message.HelpPathResolver
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.oauth2.provider.data;

import com.atlassian.json.marshal.Jsonable;
import com.atlassian.oauth2.scopes.api.ScopeDescriptionService;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.message.HelpPathResolver;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class ProviderPluginData
implements WebResourceDataProvider {
    private final ApplicationProperties applicationProperties;
    private final HelpPathResolver helpPathResolver;
    private final ScopeDescriptionService scopeDescriptionService;

    public ProviderPluginData(ApplicationProperties applicationProperties, HelpPathResolver helpPathResolver, ScopeDescriptionService scopeDescriptionService) {
        this.applicationProperties = applicationProperties;
        this.helpPathResolver = helpPathResolver;
        this.scopeDescriptionService = scopeDescriptionService;
    }

    public Jsonable get() {
        Gson gson = new Gson();
        return writer -> gson.toJson((Object)ImmutableMap.of((Object)"baseUrl", (Object)this.applicationProperties.getBaseUrl(UrlMode.CANONICAL), (Object)"isBitbucket", (Object)"bitbucket".equals(this.applicationProperties.getPlatformId()), (Object)"product", (Object)StringUtils.capitalize((String)this.applicationProperties.getDisplayName().toLowerCase()), (Object)"links", this.getHelpLinks(), (Object)"descriptions", (Object)ImmutableMap.of((Object)"consent", (Object)this.scopeDescriptionService.getScopeDescriptionsWithTitle(), (Object)"scopes", (Object)this.scopeDescriptionService.getScopeDescriptions())), (Appendable)writer);
    }

    private String getHelpPathForProduct(String helpPathKey) {
        String productHelpPathKey = helpPathKey + "." + this.applicationProperties.getDisplayName().toLowerCase();
        return StringUtils.defaultString((String)this.setJiraHelplink(this.helpPathResolver.getHelpPath(productHelpPathKey).getUrl()));
    }

    private String setJiraHelplink(String url) {
        if (this.applicationProperties.getPlatformId().equals("jira")) {
            if (url.contains("/jira/jcore")) {
                return url.replace("jcore", "jadm");
            }
            if (url.contains("/jira/jsw")) {
                return url.replace("jsw", "jadm");
            }
            if (url.contains("/jira/jsd")) {
                return url.replace("jsd", "jadm");
            }
            return url;
        }
        return url;
    }

    private Map<String, String> getHelpLinks() {
        return ImmutableMap.of((Object)"configureIncomingLink", (Object)this.getHelpPathForProduct("help.configure.incoming.link"));
    }
}

