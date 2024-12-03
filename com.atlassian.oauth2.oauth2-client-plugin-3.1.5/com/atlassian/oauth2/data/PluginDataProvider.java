/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.HelpPathResolver
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.oauth2.data;

import com.atlassian.json.marshal.Jsonable;
import com.atlassian.oauth2.client.util.ClientHttpsValidator;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.HelpPathResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class PluginDataProvider
implements WebResourceDataProvider {
    private final UserManager userManager;
    private final ClientHttpsValidator clientHttpsValidator;
    private final ApplicationProperties applicationProperties;
    private final HelpPathResolver helpPathResolver;

    public PluginDataProvider(UserManager userManager, ClientHttpsValidator clientHttpsValidator, ApplicationProperties applicationProperties, HelpPathResolver helpPathResolver) {
        this.userManager = userManager;
        this.clientHttpsValidator = clientHttpsValidator;
        this.applicationProperties = applicationProperties;
        this.helpPathResolver = helpPathResolver;
    }

    public Jsonable get() {
        Gson gson = new Gson();
        ImmutableMap data = ImmutableMap.builder().put((Object)"isAuthorized", (Object)this.isSystemAdmin()).put((Object)"isHttpsRequired", (Object)this.clientHttpsValidator.isOAuthProviderUrlHttpsRequired()).put((Object)"isBaseUrlHttpsRequired", (Object)this.clientHttpsValidator.isBaseUrlHttpsRequired()).put((Object)"isBaseUrlHttps", (Object)this.clientHttpsValidator.isBaseUrlHttps()).put((Object)"baseUrl", (Object)this.clientHttpsValidator.getBaseUrl()).put((Object)"product", (Object)this.applicationProperties.getDisplayName()).put((Object)"links", this.getHelpLinks()).build();
        return writer -> gson.toJson((Object)data, (Appendable)writer);
    }

    private boolean isSystemAdmin() {
        return this.userManager.isSystemAdmin(this.userManager.getRemoteUserKey());
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
        return ImmutableMap.of((Object)"configureOutgoingLink", (Object)this.getHelpPathForProduct("help.configure.outgoing.link"));
    }
}

