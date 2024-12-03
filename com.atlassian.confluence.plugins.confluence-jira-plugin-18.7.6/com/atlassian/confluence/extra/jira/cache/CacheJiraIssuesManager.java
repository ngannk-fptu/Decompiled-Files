/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugins.whitelist.OutboundWhitelist
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.tenancy.api.event.TenantArrivedEvent
 *  com.atlassian.util.concurrent.Lazy
 *  com.atlassian.util.concurrent.Supplier
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.jira.cache;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.confluence.extra.jira.api.services.JiraIssuesColumnManager;
import com.atlassian.confluence.extra.jira.api.services.JiraIssuesUrlManager;
import com.atlassian.confluence.extra.jira.api.services.JiraResponseHandler;
import com.atlassian.confluence.extra.jira.cache.CacheKey;
import com.atlassian.confluence.extra.jira.cache.CacheLoggingUtils;
import com.atlassian.confluence.extra.jira.cache.JIMCache;
import com.atlassian.confluence.extra.jira.cache.JIMCacheProvider;
import com.atlassian.confluence.extra.jira.columns.DefaultJiraIssuesManager;
import com.atlassian.confluence.extra.jira.request.JiraChannelResponseHandler;
import com.atlassian.confluence.extra.jira.request.JiraStringResponseHandler;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugins.whitelist.OutboundWhitelist;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.tenancy.api.event.TenantArrivedEvent;
import com.atlassian.util.concurrent.Lazy;
import com.atlassian.util.concurrent.Supplier;
import java.io.IOException;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheJiraIssuesManager
extends DefaultJiraIssuesManager {
    private static final Logger log = LoggerFactory.getLogger(CacheJiraIssuesManager.class);
    private JIMCache<JiraChannelResponseHandler> responseChannelHandlerCache;
    private JIMCache<JiraStringResponseHandler> responseStringHandlerCache;
    private final Supplier<String> version;
    private final EventPublisher eventPublisher;
    private final JIMCacheProvider cacheProvider;

    public CacheJiraIssuesManager(JiraIssuesColumnManager jiraIssuesColumnManager, JiraIssuesUrlManager jiraIssuesUrlManager, RequestFactory<?> requestFactory, JIMCacheProvider cacheProvider, PluginAccessor pluginAccessor, EventPublisher eventPublisher, OutboundWhitelist outboundWhitelist) {
        super(jiraIssuesColumnManager, jiraIssuesUrlManager, requestFactory, outboundWhitelist);
        this.eventPublisher = eventPublisher;
        this.cacheProvider = cacheProvider;
        this.version = Lazy.supplier(() -> pluginAccessor.getPlugin("confluence.extra.jira").getPluginInformation().getVersion());
    }

    @Override
    public JiraResponseHandler retrieveXML(String url, Set<String> columns, ReadOnlyApplicationLink appLink, boolean forceAnonymous, boolean isAnonymous, JiraResponseHandler.HandlerType handlerType, boolean checkCacheBeforeLookup) throws IOException, CredentialsRequiredException, ResponseException {
        return this.retrieveXMLResponse(url, columns, appLink, forceAnonymous, isAnonymous, handlerType, checkCacheBeforeLookup, false);
    }

    @Override
    public JiraResponseHandler retrieveXML(String url, Set<String> columns, ReadOnlyApplicationLink appLink, boolean forceAnonymous, boolean isAnonymous, JiraResponseHandler.HandlerType handlerType, boolean checkCacheBeforeLookup, boolean updateCacheAfterLookup) throws IOException, CredentialsRequiredException, ResponseException {
        return this.retrieveXMLResponse(url, columns, appLink, forceAnonymous, isAnonymous, handlerType, checkCacheBeforeLookup, updateCacheAfterLookup);
    }

    @VisibleForTesting
    protected JiraResponseHandler retrieveXMLResponse(String url, Set<String> columns, ReadOnlyApplicationLink appLink, boolean forceAnonymous, boolean isAnonymous, JiraResponseHandler.HandlerType handlerType, boolean checkCacheBeforeLookup, boolean updateCacheAfterLookup) throws IOException, CredentialsRequiredException, ResponseException {
        boolean userIsMapped;
        boolean bl = userIsMapped = !isAnonymous && AuthenticatedUserThreadLocal.getUsername() != null;
        if (!checkCacheBeforeLookup || appLink == null) {
            JiraResponseHandler responseHandler = super.retrieveXML(url, columns, appLink, forceAnonymous, isAnonymous, handlerType, checkCacheBeforeLookup, updateCacheAfterLookup);
            if (updateCacheAfterLookup) {
                String applinkId = appLink == null ? null : appLink.getId().toString();
                CacheKey cacheKey = new CacheKey(url, applinkId, columns, false, forceAnonymous, false, userIsMapped, (String)this.version.get());
                this.populateCache(cacheKey, responseHandler);
            }
            return responseHandler;
        }
        ApplicationLinkRequestFactory requestFactory = this.createRequestFactory(appLink, isAnonymous);
        requestFactory.createRequest(Request.MethodType.GET, url);
        CacheKey mappedCacheKey = new CacheKey(url, appLink.getId().toString(), columns, false, forceAnonymous, false, true, (String)this.version.get());
        CacheKey unmappedCacheKey = new CacheKey(url, appLink.getId().toString(), columns, false, forceAnonymous, false, false, (String)this.version.get());
        JiraResponseHandler cachedResponseHandler = this.tryToFindResponseHandlerInAllCaches(mappedCacheKey, unmappedCacheKey, userIsMapped);
        if (cachedResponseHandler == null) {
            CacheKey cacheKey = new CacheKey(url, appLink.getId().toString(), columns, false, forceAnonymous, false, userIsMapped, (String)this.version.get());
            log.debug("building cache: " + cacheKey);
            JiraResponseHandler responseHandler = super.retrieveXML(url, columns, appLink, forceAnonymous, isAnonymous, handlerType, checkCacheBeforeLookup, updateCacheAfterLookup);
            this.populateCache(cacheKey, responseHandler);
            return responseHandler;
        }
        log.debug("returning cached version");
        return cachedResponseHandler;
    }

    private JiraResponseHandler tryToFindResponseHandlerInAllCaches(CacheKey mappedCacheKey, CacheKey unmappedCacheKey, boolean userIsMapped) {
        JiraChannelResponseHandler responseHandler;
        if (this.responseChannelHandlerCache == null || this.responseStringHandlerCache == null) {
            this.initializeCache();
        }
        return (responseHandler = CacheJiraIssuesManager.tryCache(mappedCacheKey, unmappedCacheKey, userIsMapped, this.responseChannelHandlerCache)) == null ? CacheJiraIssuesManager.tryCache(mappedCacheKey, unmappedCacheKey, userIsMapped, this.responseStringHandlerCache) : responseHandler;
    }

    private static <T extends JiraResponseHandler> T tryCache(CacheKey mappedCacheKey, CacheKey unmappedCacheKey, boolean userIsMapped, JIMCache<T> cache) {
        return (T)JIMCache.fold(cache.get(mappedCacheKey.toKey()), t -> t.orElseGet(() -> {
            if (!userIsMapped) {
                return JIMCache.fold(cache.get(unmappedCacheKey.toKey()), r -> r.orElse(null), throwable -> {
                    CacheLoggingUtils.log(log, throwable, false);
                    return null;
                });
            }
            return null;
        }), throwable -> {
            CacheLoggingUtils.log(log, throwable, false);
            return null;
        });
    }

    private void populateCache(CacheKey cacheKey, JiraResponseHandler responseHandler) {
        if (this.responseChannelHandlerCache == null || this.responseStringHandlerCache == null) {
            this.initializeCache();
        }
        if (responseHandler instanceof JiraChannelResponseHandler) {
            JIMCache.fold(this.responseChannelHandlerCache.putIfAbsent(cacheKey.toKey(), (JiraChannelResponseHandler)responseHandler), (result, error) -> {
                CacheLoggingUtils.log(log, error, false);
                return result;
            });
        } else if (responseHandler instanceof JiraStringResponseHandler) {
            JIMCache.fold(this.responseStringHandlerCache.putIfAbsent(cacheKey.toKey(), (JiraStringResponseHandler)responseHandler), (result, error) -> {
                CacheLoggingUtils.log(log, error, false);
                return result;
            });
        } else {
            throw new IllegalArgumentException("Cached value should be either JiraChannelResponseHandler or JiraStringResponseHandler. " + responseHandler.getClass().getName() + " is not supported.");
        }
    }

    @PostConstruct
    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onTenantArrived(TenantArrivedEvent event) {
        this.initializeCache();
    }

    @Override
    public void initializeCache() {
        this.responseChannelHandlerCache = this.cacheProvider.getChannelResponseHandlersCache();
        this.responseStringHandlerCache = this.cacheProvider.getStringResponseHandlersCache();
    }
}

