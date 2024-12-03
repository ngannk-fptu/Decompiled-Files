/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.plugins.whitelist.OutboundWhitelist
 *  com.atlassian.spring.container.ContainerManager
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.httpclient.Header
 *  org.apache.commons.httpclient.HttpClient
 *  org.apache.commons.httpclient.HttpMethod
 *  org.apache.commons.httpclient.methods.GetMethod
 *  org.apache.commons.httpclient.params.HttpConnectionManagerParams
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.http.httpclient;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.confluence.api.impl.sal.util.OutboundWhiteListBypassUtil;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.http.BaseHttpRetrievalService;
import com.atlassian.confluence.util.http.ConfluenceHttpParameters;
import com.atlassian.confluence.util.http.HttpProxyConfiguration;
import com.atlassian.confluence.util.http.HttpRequest;
import com.atlassian.confluence.util.http.HttpResponse;
import com.atlassian.confluence.util.http.httpclient.HttpClientAuthenticator;
import com.atlassian.confluence.util.http.httpclient.HttpClientHttpResponse;
import com.atlassian.confluence.util.http.httpclient.HttpClientProxyConfiguration;
import com.atlassian.confluence.util.http.httpclient.HttpClientUnAuthorisedResponse;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.plugins.whitelist.OutboundWhitelist;
import com.atlassian.spring.container.ContainerManager;
import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated(forRemoval=true)
public class HttpClientHttpRetrievalService
extends BaseHttpRetrievalService {
    public static final int MAX_REDIRECTS = 20;
    private static final Logger log = LoggerFactory.getLogger(HttpClientHttpRetrievalService.class);
    private final int maxRedirects = Integer.getInteger("http.protocol.max-redirects", 20);
    @Deprecated
    public static final String DEFAULT_SSL_PROTOCOL = "TLSv1,SSLv3";

    @Override
    public HttpResponse get(HttpRequest httpRequest) throws IOException {
        int attempt;
        String requestUrl = httpRequest.getUrl();
        URI remoteUrl = URI.create(requestUrl);
        if (!OutboundWhiteListBypassUtil.byPassOutboundWhitelist(requestUrl, (String)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, "synchrony_collaborative_editor_app_base_url")) && this.isUrlOutboundWhitelisted(requestUrl)) {
            return new HttpClientUnAuthorisedResponse();
        }
        HttpClient client = this.newHttpClient(remoteUrl);
        String url = remoteUrl.toURL().getFile();
        HttpMethod method = this.makeMethod(httpRequest, client, url);
        HttpClientProxyConfiguration proxyConfiguration = HttpClientProxyConfiguration.getInstance(HttpProxyConfiguration.fromSystemProperties());
        for (attempt = 0; attempt <= this.maxRedirects; ++attempt) {
            int statusCode;
            if (httpRequest.getAuthenticator() != null) {
                ((HttpClientAuthenticator)httpRequest.getAuthenticator()).preprocess(client, method);
            }
            if ((statusCode = client.executeMethod(method)) < 300 || statusCode > 399) break;
            Header locationHeader = method.getResponseHeader("location");
            if (locationHeader == null) {
                method.releaseConnection();
                throw new IOException("Invalid redirect location for URL: " + httpRequest.getUrl());
            }
            URI redirectUrl = URI.create(locationHeader.getValue());
            String host = redirectUrl.getHost();
            if (StringUtils.isEmpty((CharSequence)host)) {
                host = remoteUrl.getHost();
            }
            if (proxyConfiguration.shouldProxy(host)) {
                proxyConfiguration.configureClient(client);
            }
            method.releaseConnection();
            method = this.makeMethod(httpRequest, client, locationHeader.getValue());
        }
        if (attempt > this.maxRedirects) {
            String traceMessage = "Failed to call to URL " + httpRequest.getUrl() + " after " + this.maxRedirects + " attempts";
            log.trace(traceMessage);
            throw new IOException(traceMessage);
        }
        return new HttpClientHttpResponse(httpRequest, method);
    }

    private void setTimeouts(HttpClient client, ConfluenceHttpParameters parameters) {
        HttpConnectionManagerParams params = client.getHttpConnectionManager().getParams();
        params.setSoTimeout(parameters.getSocketTimeout());
        params.setConnectionTimeout(parameters.getConnectionTimeout());
    }

    private HttpMethod makeMethod(HttpRequest httpRequest, HttpClient client, String url) throws IOException {
        HttpMethod method = this.doMakeMethod(httpRequest, client, url);
        int includeStackDepth = this.calculateIncludeStackDepth();
        method.addRequestHeader("X-Confluence-HTTP-Stack-Depth", Integer.toString(includeStackDepth));
        method.setRequestHeader("User-agent", MessageFormat.format("Confluence/{0} (http://www.atlassian.com/software/confluence)", GeneralUtil.getVersionNumber()));
        method.setFollowRedirects(false);
        for (Map.Entry<String, String> entry : httpRequest.getHeaders()) {
            method.setRequestHeader(entry.getKey(), entry.getValue());
        }
        return method;
    }

    private HttpMethod doMakeMethod(HttpRequest httpRequest, HttpClient client, String url) {
        if (httpRequest.getAuthenticator() != null) {
            return ((HttpClientAuthenticator)httpRequest.getAuthenticator()).makeMethod(client, url);
        }
        return new GetMethod(url);
    }

    private int calculateIncludeStackDepth() throws IOException {
        int includeStackDepth = 1;
        HttpServletRequest request = ServletContextThreadLocal.getRequest();
        if (request != null) {
            String stackDepthString = request.getHeader("X-Confluence-HTTP-Stack-Depth");
            int existingDepth = NumberUtils.toInt((String)stackDepthString);
            if (existingDepth >= 1) {
                throw new IOException("HTTP include stack depth of more than 1!");
            }
            if (existingDepth > 0) {
                includeStackDepth = existingDepth + 1;
            }
        }
        return includeStackDepth;
    }

    @Override
    public Class[] getAvailableAuthenticators() {
        return new Class[0];
    }

    private HttpClient newHttpClient(URI url) throws IOException {
        ConfluenceHttpParameters httpParameters;
        HttpClient client = new HttpClient();
        String host = url.getHost();
        int port = url.getPort();
        if (port == -1) {
            port = url.getScheme().equals("https") ? 443 : 80;
        }
        client.getHostConfiguration().setHost(host, port, url.getScheme());
        HttpClientProxyConfiguration proxyConfiguration = HttpClientProxyConfiguration.getInstance(HttpProxyConfiguration.fromSystemProperties());
        if (proxyConfiguration.shouldProxy(host)) {
            proxyConfiguration.configureClient(client);
        }
        if (!(httpParameters = this.getConnectionParameters()).isEnabled()) {
            throw new IOException("External connections have been disabled");
        }
        this.setTimeouts(client, httpParameters);
        return client;
    }

    private boolean isUrlOutboundWhitelisted(String url) {
        if (!NET_REQUEST_ALLOW_ALL_HOSTS) {
            if (null == this.outboundWhitelist) {
                this.outboundWhitelist = (OutboundWhitelist)ContainerManager.getComponent((String)"outboundWhitelist");
            }
            if (null != this.outboundWhitelist && !this.outboundWhitelist.isAllowed(URI.create(url))) {
                log.warn(String.format("The provided url- %s is not included in the whitelist!.Please add the url to whitelist to allow access", url));
                return true;
            }
        }
        return false;
    }
}

