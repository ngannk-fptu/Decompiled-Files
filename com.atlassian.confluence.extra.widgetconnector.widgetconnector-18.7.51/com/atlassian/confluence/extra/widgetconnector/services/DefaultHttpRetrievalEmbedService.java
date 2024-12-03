/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.util.http.ConfluenceHttpParameters
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.whitelist.OutboundWhitelist
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.client.methods.CloseableHttpResponse
 *  org.apache.http.client.methods.HttpHead
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.apache.http.impl.client.HttpClientBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.widgetconnector.services;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.extra.widgetconnector.exceptions.EmbedRetrievalException;
import com.atlassian.confluence.extra.widgetconnector.services.HttpRetrievalEmbedService;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.http.ConfluenceHttpParameters;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.whitelist.OutboundWhitelist;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={HttpRetrievalEmbedService.class})
public class DefaultHttpRetrievalEmbedService
implements HttpRetrievalEmbedService {
    private static final Logger log = LoggerFactory.getLogger(DefaultHttpRetrievalEmbedService.class);
    private static final CacheSettings CACHE_SETTINGS = new CacheSettingsBuilder().local().build();
    private final SettingsManager settingsManager;
    private final CacheFactory cacheFactory;
    private final RequestFactory<?> requestFactory;
    private final OutboundWhitelist outboundWhitelist;
    private final I18nResolver i18nResolver;
    private final UserManager userManager;

    public DefaultHttpRetrievalEmbedService(@ComponentImport SettingsManager settingsManager, @ComponentImport CacheManager cacheManager, @ComponentImport RequestFactory<?> requestFactory, @ComponentImport OutboundWhitelist outboundWhitelist, @ComponentImport I18nResolver i18nResolver, @ComponentImport UserManager userManager) {
        this.settingsManager = settingsManager;
        this.cacheFactory = cacheManager;
        this.requestFactory = requestFactory;
        this.outboundWhitelist = outboundWhitelist;
        this.i18nResolver = i18nResolver;
        this.userManager = userManager;
    }

    @Override
    public String getEmbedData(String url, Pattern pattern, String cacheName) throws EmbedRetrievalException {
        UserKey userKey = this.userManager.getRemoteUserKey();
        if (!this.outboundWhitelist.isAllowed(URI.create(url), userKey)) {
            throw new EmbedRetrievalException(this.i18nResolver.getText("com.atlassian.confluence.extra.widgetconnector.services.error.not.whitelisted"));
        }
        String result = (String)this.getCache(cacheName).get((Object)url);
        if (result != null) {
            return result;
        }
        Response response = this.getResponse(url);
        this.checkForErrorResponse(url, response);
        try {
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(response.getResponseBodyAsStream()));
            while ((line = br.readLine()) != null) {
                Matcher m = pattern.matcher(line);
                if (!m.find()) continue;
                br.close();
                String embedId = m.group(1);
                this.getCache(cacheName).put((Object)url, (Object)embedId);
                return embedId;
            }
            br.close();
        }
        catch (ResponseException | IOException e) {
            log.error("An error occurred parsing the response from: " + url);
        }
        return null;
    }

    private Response getResponse(String url) throws EmbedRetrievalException {
        try {
            return (Response)this.requestFactory.createRequest(Request.MethodType.GET, url).executeAndReturn(response -> response);
        }
        catch (ResponseException e) {
            throw new EmbedRetrievalException(this.i18nResolver.getText("com.atlassian.confluence.extra.widgetconnector.services.error.failed.retrieval", new Serializable[]{url}));
        }
    }

    private void checkForErrorResponse(String url, Response response) throws EmbedRetrievalException {
        if (response == null || !response.isSuccessful()) {
            throw new EmbedRetrievalException(this.i18nResolver.getText("com.atlassian.confluence.extra.widgetconnector.services.error.failed.retrieval", new Serializable[]{url}));
        }
        switch (response.getStatusCode()) {
            case 404: {
                throw new EmbedRetrievalException(this.i18nResolver.getText("com.atlassian.confluence.extra.widgetconnector.services.error.not.found", new Serializable[]{url}));
            }
            case 401: 
            case 403: {
                throw new EmbedRetrievalException(this.i18nResolver.getText("com.atlassian.confluence.extra.widgetconnector.services.error.not.permitted", new Serializable[]{url}));
            }
        }
    }

    private Cache<String, String> getCache(String cacheName) {
        return this.cacheFactory.getCache(cacheName, null, CACHE_SETTINGS);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public String getNewLocation(String oldUrl) {
        try (CloseableHttpClient httpClient = this.getHttpClient();){
            HttpHead httpHead = new HttpHead(oldUrl);
            httpHead.setConfig(RequestConfig.custom().setRedirectsEnabled(false).build());
            CloseableHttpResponse response = httpClient.execute((HttpUriRequest)httpHead);
            if (301 != response.getStatusLine().getStatusCode()) return oldUrl;
            String string = response.getFirstHeader("Location").getValue();
            return string;
        }
        catch (IOException ioError) {
            log.error(String.format("IO error while trying to make a HEAD request to %s", oldUrl), (Throwable)ioError);
        }
        return oldUrl;
    }

    private CloseableHttpClient getHttpClient() {
        ConfluenceHttpParameters confluenceHttpParameters = this.settingsManager.getGlobalSettings().getConfluenceHttpParameters();
        RequestConfig config = RequestConfig.custom().setConnectTimeout(confluenceHttpParameters.getConnectionTimeout()).setSocketTimeout(confluenceHttpParameters.getSocketTimeout()).build();
        return HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    }
}

