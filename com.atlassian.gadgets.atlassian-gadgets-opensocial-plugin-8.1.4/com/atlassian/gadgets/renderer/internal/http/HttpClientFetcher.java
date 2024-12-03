/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.opensocial.spi.Whitelist
 *  com.atlassian.gadgets.util.IllegalHttpTargetHostException
 *  com.atlassian.sal.api.user.UserManager
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.core.Response$Status$Family
 *  org.apache.http.client.HttpClient
 *  org.apache.shindig.gadgets.http.HttpCache
 *  org.apache.shindig.gadgets.http.HttpCacheKey
 *  org.apache.shindig.gadgets.http.HttpFetcher
 *  org.apache.shindig.gadgets.http.HttpRequest
 *  org.apache.shindig.gadgets.http.HttpResponse
 *  org.apache.shindig.gadgets.http.HttpResponseBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.gadgets.renderer.internal.http;

import com.atlassian.gadgets.opensocial.spi.Whitelist;
import com.atlassian.gadgets.renderer.internal.http.HttpClientSpec;
import com.atlassian.gadgets.renderer.internal.http.ShindigApacheClientAdapter;
import com.atlassian.gadgets.renderer.internal.http.WhitelistAwareHttpClientFactory;
import com.atlassian.gadgets.util.IllegalHttpTargetHostException;
import com.atlassian.sal.api.user.UserManager;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Response;
import org.apache.http.client.HttpClient;
import org.apache.shindig.gadgets.http.HttpCache;
import org.apache.shindig.gadgets.http.HttpCacheKey;
import org.apache.shindig.gadgets.http.HttpFetcher;
import org.apache.shindig.gadgets.http.HttpRequest;
import org.apache.shindig.gadgets.http.HttpResponse;
import org.apache.shindig.gadgets.http.HttpResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class HttpClientFetcher
implements HttpFetcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientFetcher.class);
    private final HttpCache cache;
    private final WhitelistAwareHttpClientFactory whitelistAwareHttpClientFactory;
    private final Whitelist whitelist;
    private final UserManager userManager;

    @Inject
    public HttpClientFetcher(HttpCache cache, WhitelistAwareHttpClientFactory whitelistAwareHttpClientFactory, Whitelist whitelist, UserManager userManager) {
        this.cache = cache;
        this.whitelistAwareHttpClientFactory = whitelistAwareHttpClientFactory;
        this.whitelist = whitelist;
        this.userManager = userManager;
    }

    private static HttpResponse mapExceptionToResponse(IllegalHttpTargetHostException exception) {
        String responseMessage = String.format("Requests to %s are not allowed. See your administrator about configuring a whitelist entry for this destination (http://confluence.atlassian.com/x/KQfCDQ).", exception.getHost());
        return new HttpResponseBuilder().setHttpStatusCode(403).setHeader("Content-Type", "text/plain").setResponseString(responseMessage).create();
    }

    public HttpResponse fetch(HttpRequest request) {
        try {
            Optional<HttpResponse> cachedResponse = this.findCachedResponse(request);
            if (cachedResponse.isPresent()) {
                this.validateRequestTargetAgainstWhitelist(request);
                return cachedResponse.get();
            }
            return this.performRequest(request);
        }
        catch (IllegalHttpTargetHostException exception) {
            LOGGER.warn("A request to {} has been denied. To allow requests to this URL add the application URL to your whitelist (http://confluence.atlassian.com/x/KQfCDQ).", (Object)request.getUri());
            return HttpClientFetcher.mapExceptionToResponse(exception);
        }
        catch (SocketTimeoutException exception) {
            String errorMessage = String.format("Timeout performing a request to: %s", request.getUri());
            LOGGER.debug(errorMessage, (Throwable)exception);
            return HttpResponse.timeout();
        }
        catch (SocketException exception) {
            String errorMessage = String.format("Networking/connection error performing a request to: %s", request.getUri());
            LOGGER.debug(errorMessage, (Throwable)exception);
            return HttpResponse.error();
        }
        catch (IOException exception) {
            String errorMessage = String.format("Unable to perform a request to: %s", request.getUri());
            LOGGER.error(errorMessage, (Throwable)exception);
            return HttpResponse.error();
        }
    }

    private void validateRequestTargetAgainstWhitelist(HttpRequest request) {
        URI target = request.getUri().toJavaUri();
        if (!this.whitelist.allows(target, this.userManager.getRemoteUserKey())) {
            throw new IllegalHttpTargetHostException(target.toString());
        }
    }

    private HttpResponse addResponseToCacheIfSuccessful(HttpRequest request, HttpResponse response) {
        int statusCode = response.getHttpStatusCode();
        Response.Status responseStatus = Response.Status.fromStatusCode((int)statusCode);
        Response.Status.Family responseStatusFamily = responseStatus.getFamily();
        if (!Response.Status.Family.CLIENT_ERROR.equals((Object)responseStatusFamily) && !Response.Status.Family.SERVER_ERROR.equals((Object)responseStatusFamily)) {
            HttpCacheKey httpCacheKey = new HttpCacheKey(request);
            return this.cache.addResponse(httpCacheKey, request, response);
        }
        return response;
    }

    private Optional<HttpResponse> findCachedResponse(HttpRequest request) {
        HttpCacheKey cacheKey = new HttpCacheKey(request);
        return Optional.ofNullable(this.cache.getResponse(cacheKey, request));
    }

    private ShindigApacheClientAdapter newWhitelistAwareClient(HttpRequest request) {
        HttpClientSpec httpClientSpec = new HttpClientSpec(request.getFollowRedirects());
        HttpClient httpClient = this.whitelistAwareHttpClientFactory.getClient(httpClientSpec);
        return new ShindigApacheClientAdapter(httpClient);
    }

    private HttpResponse performRequest(HttpRequest request) throws IOException {
        HttpResponse response = this.newWhitelistAwareClient(request).execute(request);
        return this.addResponseToCacheIfSuccessful(request, response);
    }
}

