/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.applinks.api.application.jira.JiraApplicationType
 *  com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.integration.jira.JiraFeature
 *  com.atlassian.integration.jira.JiraService
 *  com.atlassian.sal.api.net.Request$MethodType
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.plugins.createjiracontent;

import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.applinks.api.application.jira.JiraApplicationType;
import com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.plugins.createjiracontent.JiraResourcesManager;
import com.atlassian.confluence.plugins.createjiracontent.rest.beans.CachableJiraServerBean;
import com.atlassian.integration.jira.JiraFeature;
import com.atlassian.integration.jira.JiraService;
import com.atlassian.sal.api.net.Request;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class CachedJiraResourcesManager
implements JiraResourcesManager,
InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(CachedJiraResourcesManager.class);
    private static final String CACHE_NAME = CachedJiraResourcesManager.class.getName();
    private final ReadOnlyApplicationLinkService readOnlyAppLinkService;
    private final AuthenticationConfigurationManager authenticationConfigurationManager;
    private final JiraService jiraService;
    private final Cache<String, CachableJiraServerBean> cache;

    public CachedJiraResourcesManager(ReadOnlyApplicationLinkService applicationLinkService, CacheFactory cacheFactory, AuthenticationConfigurationManager authenticationConfigurationManager, JiraService jiraService) {
        this.readOnlyAppLinkService = Objects.requireNonNull(applicationLinkService);
        this.authenticationConfigurationManager = Objects.requireNonNull(authenticationConfigurationManager);
        this.jiraService = Objects.requireNonNull(jiraService);
        this.cache = cacheFactory.getCache(CACHE_NAME, null, new CacheSettingsBuilder().remote().replicateViaInvalidation().build());
    }

    @Override
    @Nonnull
    public List<CachableJiraServerBean> getJiraServers() {
        return this.getJiraServer(null);
    }

    @Override
    @Nonnull
    public List<CachableJiraServerBean> getSupportedJiraServers() {
        return this.getJiraServer(CachableJiraServerBean::isSupportedVersion);
    }

    private Stream<CachableJiraServerBean> getServersFromAppLinks() {
        return this.allJiraApplinks().map(link -> (CachableJiraServerBean)this.cache.get((Object)CachedJiraResourcesManager.cacheKey(link), () -> this.getRemoteJiraServer((ReadOnlyApplicationLink)link)));
    }

    private Stream<ReadOnlyApplicationLink> allJiraApplinks() {
        return StreamSupport.stream(this.readOnlyAppLinkService.getApplicationLinks(JiraApplicationType.class).spliterator(), false);
    }

    private static String cacheKey(ReadOnlyApplicationLink link) {
        return link.getId().toString();
    }

    private CachableJiraServerBean getRemoteJiraServer(ReadOnlyApplicationLink appLink) {
        Optional<String> authUrl = this.getAuthUrl(appLink);
        boolean supportedServer = false;
        try {
            supportedServer = authUrl.isPresent() || this.jiraService.getSupportedFeatures(appLink.getId()).contains(JiraFeature.CREATE_ISSUE);
        }
        catch (RuntimeException e) {
            log.warn("Could not detect issue creation supported or not in Jira server {}", (Object)e.getMessage());
            log.debug("Could not detect issue creation supported or not in Jira server", (Throwable)e);
        }
        return new CachableJiraServerBean(appLink.getId().toString(), appLink.getDisplayUrl().toString(), appLink.getName(), appLink.isPrimary(), authUrl.orElse(null), supportedServer);
    }

    @Nonnull
    private Optional<String> getAuthUrl(ReadOnlyApplicationLink appLink) {
        if (this.authenticationConfigurationManager.isConfigured(appLink.getId(), OAuthAuthenticationProvider.class)) {
            try {
                appLink.createAuthenticatedRequestFactory().createRequest(Request.MethodType.GET, "");
            }
            catch (CredentialsRequiredException e) {
                return Optional.ofNullable(e.getAuthorisationURI().toString());
            }
        }
        return Optional.empty();
    }

    public void afterPropertiesSet() throws Exception {
        this.cleanAllCache();
    }

    private void cleanAllCache() throws Exception {
        this.cache.removeAll();
    }

    private List<CachableJiraServerBean> getJiraServer(@Nullable Predicate<CachableJiraServerBean> filter) {
        List<CachableJiraServerBean> listJiraServers = Collections.unmodifiableList(this.getServersFromAppLinks().filter(filter == null ? test -> true : filter).collect(Collectors.toList()));
        if (listJiraServers.isEmpty()) {
            try {
                this.cleanAllCache();
            }
            catch (Exception ex) {
                log.warn("There is something wrong when remove all cache");
            }
            listJiraServers = Collections.unmodifiableList(this.getServersFromAppLinks().filter(filter == null ? test -> true : filter).collect(Collectors.toList()));
        }
        return listJiraServers;
    }
}

