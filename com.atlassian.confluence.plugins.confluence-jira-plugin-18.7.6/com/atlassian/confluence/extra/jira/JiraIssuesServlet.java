/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.http.url.SameOrigin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugins.whitelist.OutboundWhitelist
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.util.concurrent.Lazy
 *  com.atlassian.util.concurrent.Supplier
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.jira;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.confluence.extra.jira.Channel;
import com.atlassian.confluence.extra.jira.FlexigridResponseGenerator;
import com.atlassian.confluence.extra.jira.JiraIssuesManager;
import com.atlassian.confluence.extra.jira.api.services.JiraIssuesUrlManager;
import com.atlassian.confluence.extra.jira.cache.CacheKey;
import com.atlassian.confluence.extra.jira.cache.CacheLoggingUtils;
import com.atlassian.confluence.extra.jira.cache.CompressingStringCache;
import com.atlassian.confluence.extra.jira.cache.JIMCache;
import com.atlassian.confluence.extra.jira.cache.JIMCacheProvider;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.http.url.SameOrigin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugins.whitelist.OutboundWhitelist;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.util.concurrent.Lazy;
import com.atlassian.util.concurrent.Supplier;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraIssuesServlet
extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(JiraIssuesServlet.class);
    private final JIMCacheProvider cacheProvider;
    private final JiraIssuesManager jiraIssuesManager;
    private final FlexigridResponseGenerator flexigridResponseGenerator;
    private final JiraIssuesUrlManager jiraIssuesUrlManager;
    private final OutboundWhitelist outboundWhitelist;
    private final ReadOnlyApplicationLinkService readOnlyApplicationLinkService;
    private final PermissionManager permissionManager;
    private final I18nResolver i18nResolver;
    private Supplier<String> version;

    @VisibleForTesting
    void setVersion(Supplier<String> version) {
        this.version = Objects.requireNonNull(version);
    }

    public JiraIssuesServlet(JIMCacheProvider cacheProvider, PluginAccessor pluginAccessor, JiraIssuesManager jiraIssuesManager, FlexigridResponseGenerator flexigridResponseGenerator, JiraIssuesUrlManager jiraIssuesUrlManager, OutboundWhitelist outboundWhitelist, ReadOnlyApplicationLinkService readOnlyApplicationLinkService, PermissionManager permissionManager, I18nResolver i18nResolver) {
        this.cacheProvider = cacheProvider;
        this.jiraIssuesManager = jiraIssuesManager;
        this.flexigridResponseGenerator = flexigridResponseGenerator;
        this.jiraIssuesUrlManager = jiraIssuesUrlManager;
        this.outboundWhitelist = outboundWhitelist;
        this.readOnlyApplicationLinkService = readOnlyApplicationLinkService;
        this.permissionManager = permissionManager;
        this.i18nResolver = i18nResolver;
        this.version = Lazy.supplier(() -> pluginAccessor.getPlugin("confluence.extra.jira").getPluginInformation().getVersion());
    }

    private int parsePageParam(String pageString) {
        int page;
        try {
            page = StringUtils.isNotBlank((CharSequence)pageString) ? Integer.parseInt(pageString) : 0;
        }
        catch (NumberFormatException nfe) {
            log.debug("Unable to parse page parameter to an int: " + pageString);
            page = 0;
        }
        return page;
    }

    /*
     * Exception decompiling
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 4 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    private void assertURLBelongToOutboundWhiteList(String url) throws URISyntaxException {
        if (StringUtils.isEmpty((CharSequence)url) || !this.outboundWhitelist.isAllowed(new URI(url))) {
            throw new IllegalArgumentException("The provided url is un-reachable");
        }
    }

    private void assertApplinkNotNull(ReadOnlyApplicationLink applink, String appIdStr) {
        if (applink == null) {
            throw new IllegalArgumentException("Could not find the application link with the appId: " + appIdStr);
        }
    }

    private void assertURLBelongToApplicationLink(ReadOnlyApplicationLink applink, String url) {
        if (StringUtils.isEmpty((CharSequence)url)) {
            throw new IllegalArgumentException("The url parameter is empty!");
        }
        try {
            URL input = new URL(url);
            if (!SameOrigin.isSameOrigin((URL)input, (URL)applink.getRpcUrl().toURL())) {
                throw new IllegalArgumentException("Could not find the application link with the provided url.");
            }
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("The url parameter is invalid!", e);
        }
    }

    private String formatErrorMessage(Exception e) {
        StringBuilder errorMessageBuilder = new StringBuilder();
        if (StringUtils.isNotBlank((CharSequence)e.getMessage())) {
            errorMessageBuilder.append(e.getMessage()).append("<br/>");
        }
        errorMessageBuilder.append(e.getClass().toString());
        return GeneralUtil.htmlEncode((String)errorMessageBuilder.toString());
    }

    protected String getResult(CacheKey key, ReadOnlyApplicationLink applink, boolean forceAnonymous, boolean useCache, int requestedPage, boolean showCount, boolean forFlexigrid, String url) throws Exception {
        CompressingStringCache subCacheForKey = this.getSubCacheForKey(key, !useCache);
        String jiraResponse = subCacheForKey.get(requestedPage);
        if (jiraResponse != null) {
            return jiraResponse;
        }
        log.debug("Retrieving issues from URL: " + url);
        if (forFlexigrid) {
            Channel channel = this.jiraIssuesManager.retrieveXMLAsChannel(url, key.getColumns(), applink, forceAnonymous, false);
            jiraResponse = this.flexigridResponseGenerator.generate(channel, key.getColumns(), requestedPage, showCount, applink != null);
        } else {
            jiraResponse = this.jiraIssuesManager.retrieveXMLAsString(url, key.getColumns(), applink, forceAnonymous, false);
        }
        if (applink != null) {
            jiraResponse = this.rebaseLinks(jiraResponse, applink);
        }
        subCacheForKey.put(requestedPage, jiraResponse);
        return jiraResponse;
    }

    private String rebaseLinks(String jiraResponse, ReadOnlyApplicationLink appLink) {
        return jiraResponse.replace(appLink.getRpcUrl().toString(), appLink.getDisplayUrl().toString());
    }

    private CompressingStringCache getSubCacheForKey(CacheKey key, boolean flush) {
        JIMCache<CompressingStringCache> cache = this.cacheProvider.getResponseCache();
        if (flush) {
            if (log.isDebugEnabled()) {
                log.debug("flushing cache for key: " + key);
            }
            JIMCache.fold(cache.remove(key.toKey()), (result, error) -> {
                CacheLoggingUtils.log(log, error, true);
                return null;
            });
        }
        CompressingStringCache subCacheForKey = JIMCache.fold(cache.get(key.toKey(), () -> new CompressingStringCache(new ConcurrentHashMap())), (compressingStringCache, throwable) -> {
            CacheLoggingUtils.log(log, throwable, false);
            return throwable != null ? new CompressingStringCache(new ConcurrentHashMap()) : compressingStringCache;
        });
        return subCacheForKey;
    }
}

