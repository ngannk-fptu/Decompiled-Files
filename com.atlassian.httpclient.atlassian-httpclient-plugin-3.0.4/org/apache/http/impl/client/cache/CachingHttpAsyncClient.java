/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.client.cache;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpHost;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolException;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.cache.CacheResponseStatus;
import org.apache.http.client.cache.HttpCacheContext;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.client.cache.ResourceFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.concurrent.BasicFuture;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.cache.AsynchronousAsyncValidator;
import org.apache.http.impl.client.cache.BasicHttpCache;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CacheValidityPolicy;
import org.apache.http.impl.client.cache.CacheableRequestPolicy;
import org.apache.http.impl.client.cache.CachedHttpResponseGenerator;
import org.apache.http.impl.client.cache.CachedResponseSuitabilityChecker;
import org.apache.http.impl.client.cache.ChainedFutureCallback;
import org.apache.http.impl.client.cache.ConditionalRequestBuilder;
import org.apache.http.impl.client.cache.HeapResourceFactory;
import org.apache.http.impl.client.cache.HttpCache;
import org.apache.http.impl.client.cache.OptionsHttp11Response;
import org.apache.http.impl.client.cache.Proxies;
import org.apache.http.impl.client.cache.RequestProtocolCompliance;
import org.apache.http.impl.client.cache.RequestProtocolError;
import org.apache.http.impl.client.cache.ResponseCachingPolicy;
import org.apache.http.impl.client.cache.ResponseProtocolCompliance;
import org.apache.http.impl.client.cache.Variant;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.VersionInfo;

@Contract(threading=ThreadingBehavior.SAFE)
public class CachingHttpAsyncClient
implements HttpAsyncClient {
    private static final boolean SUPPORTS_RANGE_AND_CONTENT_RANGE_HEADERS = false;
    private final AtomicLong cacheHits = new AtomicLong();
    private final AtomicLong cacheMisses = new AtomicLong();
    private final AtomicLong cacheUpdates = new AtomicLong();
    private final Map<ProtocolVersion, String> viaHeaders = new HashMap<ProtocolVersion, String>(4);
    private final HttpAsyncClient backend;
    private final HttpCache responseCache;
    private final CacheValidityPolicy validityPolicy;
    private final ResponseCachingPolicy responseCachingPolicy;
    private final CachedHttpResponseGenerator responseGenerator;
    private final CacheableRequestPolicy cacheableRequestPolicy;
    private final CachedResponseSuitabilityChecker suitabilityChecker;
    private final ConditionalRequestBuilder conditionalRequestBuilder;
    private final long maxObjectSizeBytes;
    private final boolean sharedCache;
    private final ResponseProtocolCompliance responseCompliance;
    private final RequestProtocolCompliance requestCompliance;
    private final AsynchronousAsyncValidator asynchAsyncRevalidator;
    private final Log log = LogFactory.getLog(this.getClass());

    CachingHttpAsyncClient(HttpAsyncClient client, HttpCache cache, CacheConfig config) {
        Args.notNull(client, "HttpClient");
        Args.notNull(cache, "HttpCache");
        Args.notNull(config, "CacheConfig");
        this.maxObjectSizeBytes = config.getMaxObjectSize();
        this.sharedCache = config.isSharedCache();
        this.backend = client;
        this.responseCache = cache;
        this.validityPolicy = new CacheValidityPolicy();
        this.responseCachingPolicy = new ResponseCachingPolicy(this.maxObjectSizeBytes, this.sharedCache, false, config.is303CachingEnabled());
        this.responseGenerator = new CachedHttpResponseGenerator(this.validityPolicy);
        this.cacheableRequestPolicy = new CacheableRequestPolicy();
        this.suitabilityChecker = new CachedResponseSuitabilityChecker(this.validityPolicy, config);
        this.conditionalRequestBuilder = new ConditionalRequestBuilder();
        this.responseCompliance = new ResponseProtocolCompliance();
        this.requestCompliance = new RequestProtocolCompliance(config.isWeakETagOnPutDeleteAllowed());
        this.asynchAsyncRevalidator = this.makeAsynchronousValidator(config);
    }

    public CachingHttpAsyncClient() throws IOReactorException {
        this((HttpAsyncClient)HttpAsyncClients.createDefault(), new BasicHttpCache(), CacheConfig.DEFAULT);
    }

    public CachingHttpAsyncClient(CacheConfig config) throws IOReactorException {
        this((HttpAsyncClient)HttpAsyncClients.createDefault(), new BasicHttpCache(config), config);
    }

    public CachingHttpAsyncClient(HttpAsyncClient client) {
        this(client, new BasicHttpCache(), CacheConfig.DEFAULT);
    }

    public CachingHttpAsyncClient(HttpAsyncClient client, CacheConfig config) {
        this(client, new BasicHttpCache(config), config);
    }

    public CachingHttpAsyncClient(HttpAsyncClient client, ResourceFactory resourceFactory, HttpCacheStorage storage, CacheConfig config) {
        this(client, new BasicHttpCache(resourceFactory, storage, config), config);
    }

    public CachingHttpAsyncClient(HttpAsyncClient client, HttpCacheStorage storage, CacheConfig config) {
        this(client, new BasicHttpCache(new HeapResourceFactory(), storage, config), config);
    }

    CachingHttpAsyncClient(HttpAsyncClient backend, CacheValidityPolicy validityPolicy, ResponseCachingPolicy responseCachingPolicy, HttpCache responseCache, CachedHttpResponseGenerator responseGenerator, CacheableRequestPolicy cacheableRequestPolicy, CachedResponseSuitabilityChecker suitabilityChecker, ConditionalRequestBuilder conditionalRequestBuilder, ResponseProtocolCompliance responseCompliance, RequestProtocolCompliance requestCompliance) {
        CacheConfig config = CacheConfig.DEFAULT;
        this.maxObjectSizeBytes = config.getMaxObjectSize();
        this.sharedCache = config.isSharedCache();
        this.backend = backend;
        this.validityPolicy = validityPolicy;
        this.responseCachingPolicy = responseCachingPolicy;
        this.responseCache = responseCache;
        this.responseGenerator = responseGenerator;
        this.cacheableRequestPolicy = cacheableRequestPolicy;
        this.suitabilityChecker = suitabilityChecker;
        this.conditionalRequestBuilder = conditionalRequestBuilder;
        this.responseCompliance = responseCompliance;
        this.requestCompliance = requestCompliance;
        this.asynchAsyncRevalidator = this.makeAsynchronousValidator(config);
    }

    private AsynchronousAsyncValidator makeAsynchronousValidator(CacheConfig config) {
        if (config.getAsynchronousWorkersMax() > 0) {
            return new AsynchronousAsyncValidator(this, config);
        }
        return null;
    }

    public long getCacheHits() {
        return this.cacheHits.get();
    }

    public long getCacheMisses() {
        return this.cacheMisses.get();
    }

    public long getCacheUpdates() {
        return this.cacheUpdates.get();
    }

    @Override
    public Future<HttpResponse> execute(HttpHost target, HttpRequest request, FutureCallback<HttpResponse> callback) {
        return this.execute(target, request, null, callback);
    }

    @Override
    public <T> Future<T> execute(HttpAsyncRequestProducer requestProducer, HttpAsyncResponseConsumer<T> responseConsumer, FutureCallback<T> callback) {
        return this.execute(requestProducer, responseConsumer, null, callback);
    }

    @Override
    public <T> Future<T> execute(HttpAsyncRequestProducer requestProducer, HttpAsyncResponseConsumer<T> responseConsumer, HttpContext context, FutureCallback<T> callback) {
        this.log.warn("CachingHttpAsyncClient does not support caching for streaming HTTP exchanges");
        return this.backend.execute(requestProducer, responseConsumer, context, callback);
    }

    @Override
    public Future<HttpResponse> execute(HttpUriRequest request, FutureCallback<HttpResponse> callback) {
        return this.execute(request, null, callback);
    }

    @Override
    public Future<HttpResponse> execute(HttpUriRequest request, HttpContext context, FutureCallback<HttpResponse> callback) {
        URI uri = request.getURI();
        HttpHost httpHost = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        return this.execute(httpHost, request, context, callback);
    }

    @Override
    public Future<HttpResponse> execute(HttpHost target, HttpRequest originalRequest, HttpContext context, FutureCallback<HttpResponse> futureCallback) {
        BasicFuture<HttpResponse> future = new BasicFuture<HttpResponse>(futureCallback);
        HttpRequestWrapper request = HttpRequestWrapper.wrap(originalRequest);
        HttpCacheContext clientContext = context != null ? HttpCacheContext.adapt(context) : HttpCacheContext.create();
        this.setResponseStatus(clientContext, CacheResponseStatus.CACHE_MISS);
        String via = this.generateViaHeader(request);
        if (this.clientRequestsOurOptions(request)) {
            this.setResponseStatus(clientContext, CacheResponseStatus.CACHE_MODULE_RESPONSE);
            future.completed(new OptionsHttp11Response());
            return future;
        }
        HttpResponse fatalErrorResponse = this.getFatallyNoncompliantResponse(request, clientContext);
        if (fatalErrorResponse != null) {
            future.completed(fatalErrorResponse);
            return future;
        }
        try {
            this.requestCompliance.makeRequestCompliant(request);
        }
        catch (ClientProtocolException e) {
            future.failed(e);
            return future;
        }
        request.addHeader("Via", via);
        this.flushEntriesInvalidatedByRequest(target, request);
        if (!this.cacheableRequestPolicy.isServableFromCache(request)) {
            this.log.debug("Request is not servable from cache");
            this.callBackend(future, target, request, clientContext);
            return future;
        }
        HttpCacheEntry entry = this.satisfyFromCache(target, request);
        if (entry == null) {
            this.log.debug("Cache miss");
            this.handleCacheMiss(future, target, request, clientContext);
        } else {
            try {
                this.handleCacheHit(future, target, request, clientContext, entry);
            }
            catch (IOException e) {
                future.failed(e);
            }
        }
        return future;
    }

    private void handleCacheHit(BasicFuture<HttpResponse> future, HttpHost target, HttpRequestWrapper request, HttpCacheContext clientContext, HttpCacheEntry entry) throws IOException {
        HttpResponse out;
        this.recordCacheHit(target, request);
        Date now = this.getCurrentDate();
        if (this.suitabilityChecker.canCachedResponseBeUsed(target, request, entry, now)) {
            this.log.debug("Cache hit");
            out = this.generateCachedResponse(request, clientContext, entry, now);
        } else if (!this.mayCallBackend(request)) {
            this.log.debug("Cache entry not suitable but only-if-cached requested");
            out = this.generateGatewayTimeout(clientContext);
        } else {
            if (this.validityPolicy.isRevalidatable(entry) && (entry.getStatusCode() != 304 || this.suitabilityChecker.isConditional(request))) {
                this.log.debug("Revalidating cache entry");
                this.revalidateCacheEntry(future, target, request, clientContext, entry, now);
                return;
            }
            this.log.debug("Cache entry not usable; calling backend");
            this.callBackend(future, target, request, clientContext);
            return;
        }
        clientContext.setAttribute("http.route", new HttpRoute(target));
        clientContext.setAttribute("http.target_host", target);
        clientContext.setAttribute("http.request", request);
        clientContext.setAttribute("http.response", out);
        clientContext.setAttribute("http.request_sent", Boolean.TRUE);
        future.completed(out);
    }

    private void revalidateCacheEntry(BasicFuture<HttpResponse> future, HttpHost target, final HttpRequestWrapper request, final HttpCacheContext clientContext, final HttpCacheEntry entry, final Date now) throws ClientProtocolException {
        try {
            if (this.asynchAsyncRevalidator != null && !this.staleResponseNotAllowed(request, entry, now) && this.validityPolicy.mayReturnStaleWhileRevalidating(entry, now)) {
                this.log.debug("Serving stale with asynchronous revalidation");
                CloseableHttpResponse resp = this.responseGenerator.generateResponse(request, entry);
                resp.addHeader("Warning", "110 localhost \"Response is stale\"");
                this.asynchAsyncRevalidator.revalidateCacheEntry(target, request, clientContext, entry);
                future.completed(resp);
                return;
            }
            ChainedFutureCallback<HttpResponse> chainedFutureCallback = new ChainedFutureCallback<HttpResponse>(future){

                @Override
                public void failed(Exception ex) {
                    if (ex instanceof IOException) {
                        super.completed(CachingHttpAsyncClient.this.handleRevalidationFailure(request, clientContext, entry, now));
                    } else {
                        super.failed(ex);
                    }
                }
            };
            BasicFuture<HttpResponse> compositeFuture = new BasicFuture<HttpResponse>(chainedFutureCallback);
            this.revalidateCacheEntry(compositeFuture, target, request, clientContext, entry);
        }
        catch (ProtocolException e) {
            throw new ClientProtocolException(e);
        }
    }

    private void handleCacheMiss(BasicFuture<HttpResponse> future, HttpHost target, HttpRequestWrapper request, HttpCacheContext clientContext) {
        this.recordCacheMiss(target, request);
        if (!this.mayCallBackend(request)) {
            future.completed(new BasicHttpResponse(HttpVersion.HTTP_1_1, 504, "Gateway Timeout"));
            return;
        }
        Map<String, Variant> variants = this.getExistingCacheVariants(target, request);
        if (variants != null && variants.size() > 0) {
            this.negotiateResponseFromVariants(future, target, request, clientContext, variants);
            return;
        }
        this.callBackend(future, target, request, clientContext);
    }

    private HttpCacheEntry satisfyFromCache(HttpHost target, HttpRequest request) {
        HttpCacheEntry entry = null;
        try {
            entry = this.responseCache.getCacheEntry(target, request);
        }
        catch (IOException ioe) {
            this.log.warn("Unable to retrieve entries from cache", ioe);
        }
        return entry;
    }

    private HttpResponse getFatallyNoncompliantResponse(HttpRequest request, HttpCacheContext clientContext) {
        HttpResponse fatalErrorResponse = null;
        List<RequestProtocolError> fatalError = this.requestCompliance.requestIsFatallyNonCompliant(request);
        for (RequestProtocolError error : fatalError) {
            this.setResponseStatus(clientContext, CacheResponseStatus.CACHE_MODULE_RESPONSE);
            fatalErrorResponse = this.requestCompliance.getErrorForRequest(error);
        }
        return fatalErrorResponse;
    }

    private Map<String, Variant> getExistingCacheVariants(HttpHost target, HttpRequest request) {
        Map<String, Variant> variants = null;
        try {
            variants = this.responseCache.getVariantCacheEntriesWithEtags(target, request);
        }
        catch (IOException ioe) {
            this.log.warn("Unable to retrieve variant entries from cache", ioe);
        }
        return variants;
    }

    private void recordCacheMiss(HttpHost target, HttpRequest request) {
        this.cacheMisses.getAndIncrement();
        if (this.log.isDebugEnabled()) {
            RequestLine rl = request.getRequestLine();
            this.log.debug("Cache miss [host: " + target + "; uri: " + rl.getUri() + "]");
        }
    }

    private void recordCacheHit(HttpHost target, HttpRequest request) {
        this.cacheHits.getAndIncrement();
        if (this.log.isDebugEnabled()) {
            RequestLine rl = request.getRequestLine();
            this.log.debug("Cache hit [host: " + target + "; uri: " + rl.getUri() + "]");
        }
    }

    private void recordCacheUpdate(HttpCacheContext clientContext) {
        this.cacheUpdates.getAndIncrement();
        this.setResponseStatus(clientContext, CacheResponseStatus.VALIDATED);
    }

    private void flushEntriesInvalidatedByRequest(HttpHost target, HttpRequest request) {
        try {
            this.responseCache.flushInvalidatedCacheEntriesFor(target, request);
        }
        catch (IOException ioe) {
            this.log.warn("Unable to flush invalidated entries from cache", ioe);
        }
    }

    private HttpResponse generateCachedResponse(HttpRequestWrapper request, HttpCacheContext clientContext, HttpCacheEntry entry, Date now) {
        CloseableHttpResponse cachedResponse = request.containsHeader("If-None-Match") || request.containsHeader("If-Modified-Since") ? this.responseGenerator.generateNotModifiedResponse(entry) : this.responseGenerator.generateResponse(request, entry);
        this.setResponseStatus(clientContext, CacheResponseStatus.CACHE_HIT);
        if (this.validityPolicy.getStalenessSecs(entry, now) > 0L) {
            cachedResponse.addHeader("Warning", "110 localhost \"Response is stale\"");
        }
        return cachedResponse;
    }

    private HttpResponse handleRevalidationFailure(HttpRequestWrapper request, HttpCacheContext clientContext, HttpCacheEntry entry, Date now) {
        if (this.staleResponseNotAllowed(request, entry, now)) {
            return this.generateGatewayTimeout(clientContext);
        }
        return this.unvalidatedCacheHit(clientContext, request, entry);
    }

    private HttpResponse generateGatewayTimeout(HttpCacheContext clientContext) {
        this.setResponseStatus(clientContext, CacheResponseStatus.CACHE_MODULE_RESPONSE);
        return new BasicHttpResponse(HttpVersion.HTTP_1_1, 504, "Gateway Timeout");
    }

    private HttpResponse unvalidatedCacheHit(HttpCacheContext clientContext, HttpRequestWrapper request, HttpCacheEntry entry) {
        CloseableHttpResponse cachedResponse = this.responseGenerator.generateResponse(request, entry);
        this.setResponseStatus(clientContext, CacheResponseStatus.CACHE_HIT);
        cachedResponse.addHeader("Warning", "111 localhost \"Revalidation failed\"");
        return cachedResponse;
    }

    private boolean staleResponseNotAllowed(HttpRequest request, HttpCacheEntry entry, Date now) {
        return this.validityPolicy.mustRevalidate(entry) || this.isSharedCache() && this.validityPolicy.proxyRevalidate(entry) || this.explicitFreshnessRequest(request, entry, now);
    }

    private boolean mayCallBackend(HttpRequest request) {
        for (Header h : request.getHeaders("Cache-Control")) {
            for (HeaderElement elt : h.getElements()) {
                if (!"only-if-cached".equals(elt.getName())) continue;
                this.log.debug("Request marked only-if-cached");
                return false;
            }
        }
        return true;
    }

    private boolean explicitFreshnessRequest(HttpRequest request, HttpCacheEntry entry, Date now) {
        for (Header h : request.getHeaders("Cache-Control")) {
            for (HeaderElement elt : h.getElements()) {
                if ("max-stale".equals(elt.getName())) {
                    try {
                        int maxstale = Integer.parseInt(elt.getValue());
                        long age = this.validityPolicy.getCurrentAgeSecs(entry, now);
                        long lifetime = this.validityPolicy.getFreshnessLifetimeSecs(entry);
                        if (age - lifetime <= (long)maxstale) continue;
                        return true;
                    }
                    catch (NumberFormatException nfe) {
                        return true;
                    }
                }
                if (!"min-fresh".equals(elt.getName()) && !"max-age".equals(elt.getName())) continue;
                return true;
            }
        }
        return false;
    }

    private String generateViaHeader(HttpMessage msg) {
        ProtocolVersion pv = msg.getProtocolVersion();
        String existingEntry = this.viaHeaders.get(pv);
        if (existingEntry != null) {
            return existingEntry;
        }
        VersionInfo vi = VersionInfo.loadVersionInfo("org.apache.http.client", this.getClass().getClassLoader());
        String release = vi != null ? vi.getRelease() : "UNAVAILABLE";
        String value = "http".equalsIgnoreCase(pv.getProtocol()) ? String.format("%d.%d localhost (Apache-HttpClient/%s (cache))", pv.getMajor(), pv.getMinor(), release) : String.format("%s/%d.%d localhost (Apache-HttpClient/%s (cache))", pv.getProtocol(), pv.getMajor(), pv.getMinor(), release);
        this.viaHeaders.put(pv, value);
        return value;
    }

    private void setResponseStatus(HttpCacheContext clientContext, CacheResponseStatus value) {
        if (clientContext != null) {
            clientContext.setAttribute("http.cache.response.status", (Object)value);
        }
    }

    public boolean supportsRangeAndContentRangeHeaders() {
        return false;
    }

    public boolean isSharedCache() {
        return this.sharedCache;
    }

    Date getCurrentDate() {
        return new Date();
    }

    boolean clientRequestsOurOptions(HttpRequest request) {
        RequestLine line = request.getRequestLine();
        if (!"OPTIONS".equals(line.getMethod())) {
            return false;
        }
        if (!"*".equals(line.getUri())) {
            return false;
        }
        return "0".equals(request.getFirstHeader("Max-Forwards").getValue());
    }

    void callBackend(BasicFuture<HttpResponse> future, final HttpHost target, final HttpRequestWrapper request, HttpCacheContext clientContext) {
        final Date requestDate = this.getCurrentDate();
        this.log.trace("Calling the backend");
        ChainedFutureCallback<HttpResponse> chainedFutureCallback = new ChainedFutureCallback<HttpResponse>(future){

            @Override
            public void completed(HttpResponse httpResponse) {
                httpResponse.addHeader("Via", CachingHttpAsyncClient.this.generateViaHeader(httpResponse));
                try {
                    CloseableHttpResponse backendResponse = CachingHttpAsyncClient.this.handleBackendResponse(target, request, requestDate, CachingHttpAsyncClient.this.getCurrentDate(), Proxies.enhanceResponse(httpResponse));
                    super.completed(backendResponse);
                }
                catch (IOException e) {
                    super.failed(e);
                }
            }
        };
        this.backend.execute(target, request, (HttpContext)clientContext, (FutureCallback<HttpResponse>)chainedFutureCallback);
    }

    private boolean revalidationResponseIsTooOld(HttpResponse backendResponse, HttpCacheEntry cacheEntry) {
        Header entryDateHeader = cacheEntry.getFirstHeader("Date");
        Header responseDateHeader = backendResponse.getFirstHeader("Date");
        if (entryDateHeader != null && responseDateHeader != null) {
            Date entryDate = DateUtils.parseDate(entryDateHeader.getValue());
            Date respDate = DateUtils.parseDate(responseDateHeader.getValue());
            if (respDate != null && respDate.before(entryDate)) {
                return true;
            }
        }
        return false;
    }

    void negotiateResponseFromVariants(final BasicFuture<HttpResponse> future, final HttpHost target, final HttpRequestWrapper request, final HttpCacheContext clientContext, final Map<String, Variant> variants) {
        final HttpRequestWrapper conditionalRequest = this.conditionalRequestBuilder.buildConditionalRequestFromVariants(request, variants);
        final Date requestDate = this.getCurrentDate();
        ChainedFutureCallback<HttpResponse> chainedFutureCallback = new ChainedFutureCallback<HttpResponse>(future){

            @Override
            public void completed(HttpResponse httpResponse) {
                HttpCacheEntry matchedEntry;
                Date responseDate = CachingHttpAsyncClient.this.getCurrentDate();
                httpResponse.addHeader("Via", CachingHttpAsyncClient.this.generateViaHeader(httpResponse));
                if (httpResponse.getStatusLine().getStatusCode() != 304) {
                    try {
                        future.completed(CachingHttpAsyncClient.this.handleBackendResponse(target, request, requestDate, responseDate, Proxies.enhanceResponse(httpResponse)));
                        return;
                    }
                    catch (IOException e) {
                        future.failed(e);
                        return;
                    }
                }
                Header resultEtagHeader = httpResponse.getFirstHeader("ETag");
                if (resultEtagHeader == null) {
                    CachingHttpAsyncClient.this.log.warn("304 response did not contain ETag");
                    CachingHttpAsyncClient.this.callBackend(future, target, request, clientContext);
                    return;
                }
                String resultEtag = resultEtagHeader.getValue();
                Variant matchingVariant = (Variant)variants.get(resultEtag);
                if (matchingVariant == null) {
                    CachingHttpAsyncClient.this.log.debug("304 response did not contain ETag matching one sent in If-None-Match");
                    CachingHttpAsyncClient.this.callBackend(future, target, request, clientContext);
                }
                if (CachingHttpAsyncClient.this.revalidationResponseIsTooOld(httpResponse, matchedEntry = matchingVariant.getEntry())) {
                    EntityUtils.consumeQuietly(httpResponse.getEntity());
                    CachingHttpAsyncClient.this.retryRequestUnconditionally(future, target, request, clientContext, matchedEntry);
                    return;
                }
                CachingHttpAsyncClient.this.recordCacheUpdate(clientContext);
                HttpCacheEntry responseEntry = CachingHttpAsyncClient.this.getUpdatedVariantEntry(target, conditionalRequest, requestDate, responseDate, httpResponse, matchingVariant, matchedEntry);
                CloseableHttpResponse resp = CachingHttpAsyncClient.this.responseGenerator.generateResponse(request, responseEntry);
                CachingHttpAsyncClient.this.tryToUpdateVariantMap(target, request, matchingVariant);
                if (CachingHttpAsyncClient.this.shouldSendNotModifiedResponse(request, responseEntry)) {
                    future.completed(CachingHttpAsyncClient.this.responseGenerator.generateNotModifiedResponse(responseEntry));
                    return;
                }
                future.completed(resp);
            }
        };
        this.backend.execute(target, conditionalRequest, (HttpContext)clientContext, (FutureCallback<HttpResponse>)chainedFutureCallback);
    }

    private void retryRequestUnconditionally(BasicFuture<HttpResponse> future, HttpHost target, HttpRequestWrapper request, HttpCacheContext clientContext, HttpCacheEntry matchedEntry) {
        HttpRequestWrapper unconditional = this.conditionalRequestBuilder.buildUnconditionalRequest(request, matchedEntry);
        this.callBackend(future, target, unconditional, clientContext);
    }

    private HttpCacheEntry getUpdatedVariantEntry(HttpHost target, HttpRequest conditionalRequest, Date requestDate, Date responseDate, HttpResponse backendResponse, Variant matchingVariant, HttpCacheEntry matchedEntry) {
        HttpCacheEntry responseEntry = matchedEntry;
        try {
            responseEntry = this.responseCache.updateVariantCacheEntry(target, conditionalRequest, matchedEntry, backendResponse, requestDate, responseDate, matchingVariant.getCacheKey());
        }
        catch (IOException ioe) {
            this.log.warn("Could not update cache entry", ioe);
        }
        return responseEntry;
    }

    private void tryToUpdateVariantMap(HttpHost target, HttpRequest request, Variant matchingVariant) {
        try {
            this.responseCache.reuseVariantEntryFor(target, request, matchingVariant);
        }
        catch (IOException ioe) {
            this.log.warn("Could not update cache entry to reuse variant", ioe);
        }
    }

    private boolean shouldSendNotModifiedResponse(HttpRequest request, HttpCacheEntry responseEntry) {
        return this.suitabilityChecker.isConditional(request) && this.suitabilityChecker.allConditionalsMatch(request, responseEntry, new Date());
    }

    void revalidateCacheEntry(final BasicFuture<HttpResponse> future, final HttpHost target, final HttpRequestWrapper request, final HttpCacheContext clientContext, final HttpCacheEntry cacheEntry) throws ProtocolException {
        final HttpRequestWrapper conditionalRequest = this.conditionalRequestBuilder.buildConditionalRequest(request, cacheEntry);
        final Date requestDate = this.getCurrentDate();
        ChainedFutureCallback<HttpResponse> chainedFutureCallback = new ChainedFutureCallback<HttpResponse>(future){

            @Override
            public void completed(HttpResponse httpResponse) {
                Date responseDate = CachingHttpAsyncClient.this.getCurrentDate();
                if (CachingHttpAsyncClient.this.revalidationResponseIsTooOld(httpResponse, cacheEntry)) {
                    HttpRequestWrapper unconditional = CachingHttpAsyncClient.this.conditionalRequestBuilder.buildUnconditionalRequest(request, cacheEntry);
                    final Date innerRequestDate = CachingHttpAsyncClient.this.getCurrentDate();
                    ChainedFutureCallback<HttpResponse> chainedFutureCallback2 = new ChainedFutureCallback<HttpResponse>(future){

                        @Override
                        public void completed(HttpResponse innerHttpResponse) {
                            Date innerResponseDate = CachingHttpAsyncClient.this.getCurrentDate();
                            CachingHttpAsyncClient.this.revalidateCacheEntryCompleted(future, target, request, clientContext, cacheEntry, conditionalRequest, innerRequestDate, innerHttpResponse, innerResponseDate);
                        }
                    };
                    CachingHttpAsyncClient.this.backend.execute(target, unconditional, (HttpContext)clientContext, (FutureCallback<HttpResponse>)chainedFutureCallback2);
                }
                CachingHttpAsyncClient.this.revalidateCacheEntryCompleted(future, target, request, clientContext, cacheEntry, conditionalRequest, requestDate, httpResponse, responseDate);
            }
        };
        this.backend.execute(target, conditionalRequest, (HttpContext)clientContext, (FutureCallback<HttpResponse>)chainedFutureCallback);
    }

    private void revalidateCacheEntryCompleted(BasicFuture<HttpResponse> future, HttpHost target, HttpRequestWrapper request, HttpCacheContext clientContext, HttpCacheEntry cacheEntry, HttpRequestWrapper conditionalRequest, Date requestDate, HttpResponse httpResponse, Date responseDate) {
        httpResponse.addHeader("Via", this.generateViaHeader(httpResponse));
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode == 304 || statusCode == 200) {
            this.recordCacheUpdate(clientContext);
        }
        if (statusCode == 304) {
            HttpCacheEntry updatedEntry;
            try {
                updatedEntry = this.responseCache.updateCacheEntry(target, request, cacheEntry, httpResponse, requestDate, responseDate);
            }
            catch (IOException e) {
                future.failed(e);
                return;
            }
            if (this.suitabilityChecker.isConditional(request) && this.suitabilityChecker.allConditionalsMatch(request, updatedEntry, new Date())) {
                future.completed(this.responseGenerator.generateNotModifiedResponse(updatedEntry));
                return;
            }
            future.completed(this.responseGenerator.generateResponse(request, updatedEntry));
            return;
        }
        if (this.staleIfErrorAppliesTo(statusCode) && !this.staleResponseNotAllowed(request, cacheEntry, this.getCurrentDate()) && this.validityPolicy.mayReturnStaleIfError(request, cacheEntry, responseDate)) {
            CloseableHttpResponse cachedResponse = this.responseGenerator.generateResponse(request, cacheEntry);
            cachedResponse.addHeader("Warning", "110 localhost \"Response is stale\"");
            future.completed(cachedResponse);
            return;
        }
        try {
            CloseableHttpResponse backendResponse = this.handleBackendResponse(target, conditionalRequest, requestDate, responseDate, Proxies.enhanceResponse(httpResponse));
            future.completed(backendResponse);
        }
        catch (IOException e) {
            future.failed(e);
        }
    }

    private boolean staleIfErrorAppliesTo(int statusCode) {
        return statusCode == 500 || statusCode == 502 || statusCode == 503 || statusCode == 504;
    }

    CloseableHttpResponse handleBackendResponse(HttpHost target, HttpRequestWrapper request, Date requestDate, Date responseDate, CloseableHttpResponse backendResponse) throws IOException {
        this.log.debug("Handling Backend response");
        this.responseCompliance.ensureProtocolCompliance(request, backendResponse);
        boolean cacheable = this.responseCachingPolicy.isResponseCacheable(request, (HttpResponse)backendResponse);
        this.responseCache.flushInvalidatedCacheEntriesFor(target, request, backendResponse);
        if (cacheable && !this.alreadyHaveNewerCacheEntry(target, request, backendResponse)) {
            this.storeRequestIfModifiedSinceFor304Response(request, backendResponse);
            return this.responseCache.cacheAndReturnResponse(target, (HttpRequest)request, backendResponse, requestDate, responseDate);
        }
        if (!cacheable) {
            try {
                this.responseCache.flushCacheEntriesFor(target, request);
            }
            catch (IOException ioe) {
                this.log.warn("Unable to flush invalid cache entries", ioe);
            }
        }
        return backendResponse;
    }

    private void storeRequestIfModifiedSinceFor304Response(HttpRequest request, HttpResponse backendResponse) {
        Header h;
        if (backendResponse.getStatusLine().getStatusCode() == 304 && (h = request.getFirstHeader("If-Modified-Since")) != null) {
            backendResponse.addHeader("Last-Modified", h.getValue());
        }
    }

    private boolean alreadyHaveNewerCacheEntry(HttpHost target, HttpRequest request, HttpResponse backendResponse) {
        HttpCacheEntry existing = null;
        try {
            existing = this.responseCache.getCacheEntry(target, request);
        }
        catch (IOException ioe) {
            // empty catch block
        }
        if (existing == null) {
            return false;
        }
        Header entryDateHeader = existing.getFirstHeader("Date");
        if (entryDateHeader == null) {
            return false;
        }
        Header responseDateHeader = backendResponse.getFirstHeader("Date");
        if (responseDateHeader == null) {
            return false;
        }
        Date entryDate = DateUtils.parseDate(entryDateHeader.getValue());
        Date responseDate = DateUtils.parseDate(responseDateHeader.getValue());
        return responseDate != null && responseDate.before(entryDate);
    }
}

