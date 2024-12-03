/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.Header
 *  org.apache.http.HeaderElement
 *  org.apache.http.HttpException
 *  org.apache.http.HttpHost
 *  org.apache.http.HttpMessage
 *  org.apache.http.HttpRequest
 *  org.apache.http.HttpResponse
 *  org.apache.http.HttpVersion
 *  org.apache.http.ProtocolException
 *  org.apache.http.ProtocolVersion
 *  org.apache.http.RequestLine
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.client.methods.CloseableHttpResponse
 *  org.apache.http.client.methods.HttpExecutionAware
 *  org.apache.http.client.methods.HttpRequestWrapper
 *  org.apache.http.client.protocol.HttpClientContext
 *  org.apache.http.client.utils.DateUtils
 *  org.apache.http.client.utils.URIUtils
 *  org.apache.http.conn.routing.HttpRoute
 *  org.apache.http.conn.routing.RouteInfo
 *  org.apache.http.impl.execchain.ClientExecChain
 *  org.apache.http.message.BasicHttpResponse
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.util.Args
 *  org.apache.http.util.VersionInfo
 */
package org.apache.http.impl.client.cache;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpException;
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
import org.apache.http.client.cache.CacheResponseStatus;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.client.cache.ResourceFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.RouteInfo;
import org.apache.http.impl.client.cache.AsynchronousValidator;
import org.apache.http.impl.client.cache.BasicHttpCache;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CacheValidityPolicy;
import org.apache.http.impl.client.cache.CacheableRequestPolicy;
import org.apache.http.impl.client.cache.CachedHttpResponseGenerator;
import org.apache.http.impl.client.cache.CachedResponseSuitabilityChecker;
import org.apache.http.impl.client.cache.ConditionalRequestBuilder;
import org.apache.http.impl.client.cache.HttpCache;
import org.apache.http.impl.client.cache.IOUtils;
import org.apache.http.impl.client.cache.OptionsHttp11Response;
import org.apache.http.impl.client.cache.Proxies;
import org.apache.http.impl.client.cache.RequestProtocolCompliance;
import org.apache.http.impl.client.cache.RequestProtocolError;
import org.apache.http.impl.client.cache.ResponseCachingPolicy;
import org.apache.http.impl.client.cache.ResponseProtocolCompliance;
import org.apache.http.impl.client.cache.Variant;
import org.apache.http.impl.execchain.ClientExecChain;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.VersionInfo;

@Contract(threading=ThreadingBehavior.SAFE_CONDITIONAL)
public class CachingExec
implements ClientExecChain {
    private static final boolean SUPPORTS_RANGE_AND_CONTENT_RANGE_HEADERS = false;
    private final AtomicLong cacheHits = new AtomicLong();
    private final AtomicLong cacheMisses = new AtomicLong();
    private final AtomicLong cacheUpdates = new AtomicLong();
    private final Map<ProtocolVersion, String> viaHeaders = new HashMap<ProtocolVersion, String>(4);
    private final CacheConfig cacheConfig;
    private final ClientExecChain backend;
    private final HttpCache responseCache;
    private final CacheValidityPolicy validityPolicy;
    private final CachedHttpResponseGenerator responseGenerator;
    private final CacheableRequestPolicy cacheableRequestPolicy;
    private final CachedResponseSuitabilityChecker suitabilityChecker;
    private final ConditionalRequestBuilder conditionalRequestBuilder;
    private final ResponseProtocolCompliance responseCompliance;
    private final RequestProtocolCompliance requestCompliance;
    private final ResponseCachingPolicy responseCachingPolicy;
    private final AsynchronousValidator asynchRevalidator;
    private final Log log = LogFactory.getLog(this.getClass());

    public CachingExec(ClientExecChain backend, HttpCache cache, CacheConfig config) {
        this(backend, cache, config, null);
    }

    public CachingExec(ClientExecChain backend, HttpCache cache, CacheConfig config, AsynchronousValidator asynchRevalidator) {
        Args.notNull((Object)backend, (String)"HTTP backend");
        Args.notNull((Object)cache, (String)"HttpCache");
        this.cacheConfig = config != null ? config : CacheConfig.DEFAULT;
        this.backend = backend;
        this.responseCache = cache;
        this.validityPolicy = new CacheValidityPolicy();
        this.responseGenerator = new CachedHttpResponseGenerator(this.validityPolicy);
        this.cacheableRequestPolicy = new CacheableRequestPolicy();
        this.suitabilityChecker = new CachedResponseSuitabilityChecker(this.validityPolicy, this.cacheConfig);
        this.conditionalRequestBuilder = new ConditionalRequestBuilder();
        this.responseCompliance = new ResponseProtocolCompliance();
        this.requestCompliance = new RequestProtocolCompliance(this.cacheConfig.isWeakETagOnPutDeleteAllowed());
        this.responseCachingPolicy = new ResponseCachingPolicy(this.cacheConfig.getMaxObjectSize(), this.cacheConfig.isSharedCache(), this.cacheConfig.isNeverCacheHTTP10ResponsesWithQuery(), this.cacheConfig.is303CachingEnabled());
        this.asynchRevalidator = asynchRevalidator;
    }

    public CachingExec(ClientExecChain backend, ResourceFactory resourceFactory, HttpCacheStorage storage, CacheConfig config) {
        this(backend, new BasicHttpCache(resourceFactory, storage, config), config);
    }

    public CachingExec(ClientExecChain backend) {
        this(backend, new BasicHttpCache(), CacheConfig.DEFAULT);
    }

    CachingExec(ClientExecChain backend, HttpCache responseCache, CacheValidityPolicy validityPolicy, ResponseCachingPolicy responseCachingPolicy, CachedHttpResponseGenerator responseGenerator, CacheableRequestPolicy cacheableRequestPolicy, CachedResponseSuitabilityChecker suitabilityChecker, ConditionalRequestBuilder conditionalRequestBuilder, ResponseProtocolCompliance responseCompliance, RequestProtocolCompliance requestCompliance, CacheConfig config, AsynchronousValidator asynchRevalidator) {
        this.cacheConfig = config != null ? config : CacheConfig.DEFAULT;
        this.backend = backend;
        this.responseCache = responseCache;
        this.validityPolicy = validityPolicy;
        this.responseCachingPolicy = responseCachingPolicy;
        this.responseGenerator = responseGenerator;
        this.cacheableRequestPolicy = cacheableRequestPolicy;
        this.suitabilityChecker = suitabilityChecker;
        this.conditionalRequestBuilder = conditionalRequestBuilder;
        this.responseCompliance = responseCompliance;
        this.requestCompliance = requestCompliance;
        this.asynchRevalidator = asynchRevalidator;
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

    public CloseableHttpResponse execute(HttpRoute route, HttpRequestWrapper request) throws IOException, HttpException {
        return this.execute(route, request, HttpClientContext.create(), null);
    }

    public CloseableHttpResponse execute(HttpRoute route, HttpRequestWrapper request, HttpClientContext context) throws IOException, HttpException {
        return this.execute(route, request, context, null);
    }

    public CloseableHttpResponse execute(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware) throws IOException, HttpException {
        HttpHost target = context.getTargetHost();
        String via = this.generateViaHeader((HttpMessage)request.getOriginal());
        this.setResponseStatus((HttpContext)context, CacheResponseStatus.CACHE_MISS);
        if (this.clientRequestsOurOptions((HttpRequest)request)) {
            this.setResponseStatus((HttpContext)context, CacheResponseStatus.CACHE_MODULE_RESPONSE);
            return Proxies.enhanceResponse(new OptionsHttp11Response());
        }
        HttpResponse fatalErrorResponse = this.getFatallyNoncompliantResponse(request, (HttpContext)context);
        if (fatalErrorResponse != null) {
            return Proxies.enhanceResponse(fatalErrorResponse);
        }
        this.requestCompliance.makeRequestCompliant(request);
        request.addHeader("Via", via);
        if (!this.cacheableRequestPolicy.isServableFromCache((HttpRequest)request)) {
            this.log.debug((Object)"Request is not servable from cache");
            this.flushEntriesInvalidatedByRequest(context.getTargetHost(), request);
            return this.callBackend(route, request, context, execAware);
        }
        HttpCacheEntry entry = this.satisfyFromCache(target, request);
        if (entry == null) {
            this.log.debug((Object)"Cache miss");
            return this.handleCacheMiss(route, request, context, execAware);
        }
        return this.handleCacheHit(route, request, context, execAware, entry);
    }

    private CloseableHttpResponse handleCacheHit(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware, HttpCacheEntry entry) throws IOException, HttpException {
        HttpHost target = context.getTargetHost();
        this.recordCacheHit(target, request);
        CloseableHttpResponse out = null;
        Date now = this.getCurrentDate();
        if (this.suitabilityChecker.canCachedResponseBeUsed(target, (HttpRequest)request, entry, now)) {
            this.log.debug((Object)"Cache hit");
            out = this.generateCachedResponse(request, (HttpContext)context, entry, now);
        } else if (!this.mayCallBackend(request)) {
            this.log.debug((Object)"Cache entry not suitable but only-if-cached requested");
            out = this.generateGatewayTimeout((HttpContext)context);
        } else {
            if (entry.getStatusCode() != 304 || this.suitabilityChecker.isConditional((HttpRequest)request)) {
                this.log.debug((Object)"Revalidating cache entry");
                return this.revalidateCacheEntry(route, request, context, execAware, entry, now);
            }
            this.log.debug((Object)"Cache entry not usable; calling backend");
            return this.callBackend(route, request, context, execAware);
        }
        context.setAttribute("http.route", (Object)route);
        context.setAttribute("http.target_host", (Object)target);
        context.setAttribute("http.request", (Object)request);
        context.setAttribute("http.response", (Object)out);
        context.setAttribute("http.request_sent", (Object)Boolean.TRUE);
        return out;
    }

    private CloseableHttpResponse revalidateCacheEntry(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware, HttpCacheEntry entry, Date now) throws HttpException {
        try {
            if (this.asynchRevalidator != null && !this.staleResponseNotAllowed(request, entry, now) && this.validityPolicy.mayReturnStaleWhileRevalidating(entry, now)) {
                this.log.trace((Object)"Serving stale with asynchronous revalidation");
                CloseableHttpResponse resp = this.generateCachedResponse(request, (HttpContext)context, entry, now);
                this.asynchRevalidator.revalidateCacheEntry(this, route, request, context, execAware, entry);
                return resp;
            }
            return this.revalidateCacheEntry(route, request, context, execAware, entry);
        }
        catch (IOException ioex) {
            return this.handleRevalidationFailure(request, (HttpContext)context, entry, now);
        }
    }

    private CloseableHttpResponse handleCacheMiss(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware) throws IOException, HttpException {
        HttpHost target = context.getTargetHost();
        this.recordCacheMiss(target, request);
        if (!this.mayCallBackend(request)) {
            return Proxies.enhanceResponse((HttpResponse)new BasicHttpResponse((ProtocolVersion)HttpVersion.HTTP_1_1, 504, "Gateway Timeout"));
        }
        Map<String, Variant> variants = this.getExistingCacheVariants(target, request);
        if (variants != null && !variants.isEmpty()) {
            return this.negotiateResponseFromVariants(route, request, context, execAware, variants);
        }
        return this.callBackend(route, request, context, execAware);
    }

    private HttpCacheEntry satisfyFromCache(HttpHost target, HttpRequestWrapper request) {
        HttpCacheEntry entry = null;
        try {
            entry = this.responseCache.getCacheEntry(target, (HttpRequest)request);
        }
        catch (IOException ioe) {
            this.log.warn((Object)"Unable to retrieve entries from cache", (Throwable)ioe);
        }
        return entry;
    }

    private HttpResponse getFatallyNoncompliantResponse(HttpRequestWrapper request, HttpContext context) {
        HttpResponse fatalErrorResponse = null;
        List<RequestProtocolError> fatalError = this.requestCompliance.requestIsFatallyNonCompliant((HttpRequest)request);
        for (RequestProtocolError error : fatalError) {
            this.setResponseStatus(context, CacheResponseStatus.CACHE_MODULE_RESPONSE);
            fatalErrorResponse = this.requestCompliance.getErrorForRequest(error);
        }
        return fatalErrorResponse;
    }

    private Map<String, Variant> getExistingCacheVariants(HttpHost target, HttpRequestWrapper request) {
        Map<String, Variant> variants = null;
        try {
            variants = this.responseCache.getVariantCacheEntriesWithEtags(target, (HttpRequest)request);
        }
        catch (IOException ioe) {
            this.log.warn((Object)"Unable to retrieve variant entries from cache", (Throwable)ioe);
        }
        return variants;
    }

    private void recordCacheMiss(HttpHost target, HttpRequestWrapper request) {
        this.cacheMisses.getAndIncrement();
        if (this.log.isTraceEnabled()) {
            RequestLine rl = request.getRequestLine();
            this.log.trace((Object)("Cache miss [host: " + target + "; uri: " + rl.getUri() + "]"));
        }
    }

    private void recordCacheHit(HttpHost target, HttpRequestWrapper request) {
        this.cacheHits.getAndIncrement();
        if (this.log.isTraceEnabled()) {
            RequestLine rl = request.getRequestLine();
            this.log.trace((Object)("Cache hit [host: " + target + "; uri: " + rl.getUri() + "]"));
        }
    }

    private void recordCacheUpdate(HttpContext context) {
        this.cacheUpdates.getAndIncrement();
        this.setResponseStatus(context, CacheResponseStatus.VALIDATED);
    }

    private void flushEntriesInvalidatedByRequest(HttpHost target, HttpRequestWrapper request) {
        try {
            this.responseCache.flushInvalidatedCacheEntriesFor(target, (HttpRequest)request);
        }
        catch (IOException ioe) {
            this.log.warn((Object)"Unable to flush invalidated entries from cache", (Throwable)ioe);
        }
    }

    private CloseableHttpResponse generateCachedResponse(HttpRequestWrapper request, HttpContext context, HttpCacheEntry entry, Date now) {
        CloseableHttpResponse cachedResponse = request.containsHeader("If-None-Match") || request.containsHeader("If-Modified-Since") ? this.responseGenerator.generateNotModifiedResponse(entry) : this.responseGenerator.generateResponse(request, entry);
        this.setResponseStatus(context, CacheResponseStatus.CACHE_HIT);
        if (this.validityPolicy.getStalenessSecs(entry, now) > 0L) {
            cachedResponse.addHeader("Warning", "110 localhost \"Response is stale\"");
        }
        return cachedResponse;
    }

    private CloseableHttpResponse handleRevalidationFailure(HttpRequestWrapper request, HttpContext context, HttpCacheEntry entry, Date now) {
        if (this.staleResponseNotAllowed(request, entry, now)) {
            return this.generateGatewayTimeout(context);
        }
        return this.unvalidatedCacheHit(request, context, entry);
    }

    private CloseableHttpResponse generateGatewayTimeout(HttpContext context) {
        this.setResponseStatus(context, CacheResponseStatus.CACHE_MODULE_RESPONSE);
        return Proxies.enhanceResponse((HttpResponse)new BasicHttpResponse((ProtocolVersion)HttpVersion.HTTP_1_1, 504, "Gateway Timeout"));
    }

    private CloseableHttpResponse unvalidatedCacheHit(HttpRequestWrapper request, HttpContext context, HttpCacheEntry entry) {
        CloseableHttpResponse cachedResponse = this.responseGenerator.generateResponse(request, entry);
        this.setResponseStatus(context, CacheResponseStatus.CACHE_HIT);
        cachedResponse.addHeader("Warning", "111 localhost \"Revalidation failed\"");
        return cachedResponse;
    }

    private boolean staleResponseNotAllowed(HttpRequestWrapper request, HttpCacheEntry entry, Date now) {
        return this.validityPolicy.mustRevalidate(entry) || this.cacheConfig.isSharedCache() && this.validityPolicy.proxyRevalidate(entry) || this.explicitFreshnessRequest(request, entry, now);
    }

    private boolean mayCallBackend(HttpRequestWrapper request) {
        for (Header h : request.getHeaders("Cache-Control")) {
            for (HeaderElement elt : h.getElements()) {
                if (!"only-if-cached".equals(elt.getName())) continue;
                this.log.trace((Object)"Request marked only-if-cached");
                return false;
            }
        }
        return true;
    }

    private boolean explicitFreshnessRequest(HttpRequestWrapper request, HttpCacheEntry entry, Date now) {
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
        VersionInfo vi = VersionInfo.loadVersionInfo((String)"org.apache.http.client", (ClassLoader)this.getClass().getClassLoader());
        String release = vi != null ? vi.getRelease() : "UNAVAILABLE";
        int major = pv.getMajor();
        int minor = pv.getMinor();
        String value = "http".equalsIgnoreCase(pv.getProtocol()) ? String.format("%d.%d localhost (Apache-HttpClient/%s (cache))", major, minor, release) : String.format("%s/%d.%d localhost (Apache-HttpClient/%s (cache))", pv.getProtocol(), major, minor, release);
        this.viaHeaders.put(pv, value);
        return value;
    }

    private void setResponseStatus(HttpContext context, CacheResponseStatus value) {
        if (context != null) {
            context.setAttribute("http.cache.response.status", (Object)value);
        }
    }

    public boolean supportsRangeAndContentRangeHeaders() {
        return false;
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

    CloseableHttpResponse callBackend(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware) throws IOException, HttpException {
        Date requestDate = this.getCurrentDate();
        this.log.trace((Object)"Calling the backend");
        CloseableHttpResponse backendResponse = this.backend.execute(route, request, context, execAware);
        try {
            backendResponse.addHeader("Via", this.generateViaHeader((HttpMessage)backendResponse));
            return this.handleBackendResponse(request, context, requestDate, this.getCurrentDate(), backendResponse);
        }
        catch (IOException ex) {
            backendResponse.close();
            throw ex;
        }
        catch (RuntimeException ex) {
            backendResponse.close();
            throw ex;
        }
    }

    private boolean revalidationResponseIsTooOld(HttpResponse backendResponse, HttpCacheEntry cacheEntry) {
        Header entryDateHeader = cacheEntry.getFirstHeader("Date");
        Header responseDateHeader = backendResponse.getFirstHeader("Date");
        if (entryDateHeader != null && responseDateHeader != null) {
            Date entryDate = DateUtils.parseDate((String)entryDateHeader.getValue());
            Date respDate = DateUtils.parseDate((String)responseDateHeader.getValue());
            if (entryDate == null || respDate == null) {
                return false;
            }
            if (respDate.before(entryDate)) {
                return true;
            }
        }
        return false;
    }

    CloseableHttpResponse negotiateResponseFromVariants(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware, Map<String, Variant> variants) throws IOException, HttpException {
        HttpRequestWrapper conditionalRequest = this.conditionalRequestBuilder.buildConditionalRequestFromVariants(request, variants);
        Date requestDate = this.getCurrentDate();
        CloseableHttpResponse backendResponse = this.backend.execute(route, conditionalRequest, context, execAware);
        try {
            Date responseDate = this.getCurrentDate();
            backendResponse.addHeader("Via", this.generateViaHeader((HttpMessage)backendResponse));
            if (backendResponse.getStatusLine().getStatusCode() != 304) {
                return this.handleBackendResponse(request, context, requestDate, responseDate, backendResponse);
            }
            Header resultEtagHeader = backendResponse.getFirstHeader("ETag");
            if (resultEtagHeader == null) {
                this.log.warn((Object)"304 response did not contain ETag");
                IOUtils.consume(backendResponse.getEntity());
                backendResponse.close();
                return this.callBackend(route, request, context, execAware);
            }
            String resultEtag = resultEtagHeader.getValue();
            Variant matchingVariant = variants.get(resultEtag);
            if (matchingVariant == null) {
                this.log.debug((Object)"304 response did not contain ETag matching one sent in If-None-Match");
                IOUtils.consume(backendResponse.getEntity());
                backendResponse.close();
                return this.callBackend(route, request, context, execAware);
            }
            HttpCacheEntry matchedEntry = matchingVariant.getEntry();
            if (this.revalidationResponseIsTooOld((HttpResponse)backendResponse, matchedEntry)) {
                IOUtils.consume(backendResponse.getEntity());
                backendResponse.close();
                return this.retryRequestUnconditionally(route, request, context, execAware, matchedEntry);
            }
            this.recordCacheUpdate((HttpContext)context);
            HttpCacheEntry responseEntry = this.getUpdatedVariantEntry(context.getTargetHost(), conditionalRequest, requestDate, responseDate, backendResponse, matchingVariant, matchedEntry);
            backendResponse.close();
            CloseableHttpResponse resp = this.responseGenerator.generateResponse(request, responseEntry);
            this.tryToUpdateVariantMap(context.getTargetHost(), request, matchingVariant);
            if (this.shouldSendNotModifiedResponse(request, responseEntry)) {
                return this.responseGenerator.generateNotModifiedResponse(responseEntry);
            }
            return resp;
        }
        catch (IOException ex) {
            backendResponse.close();
            throw ex;
        }
        catch (RuntimeException ex) {
            backendResponse.close();
            throw ex;
        }
    }

    private CloseableHttpResponse retryRequestUnconditionally(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware, HttpCacheEntry matchedEntry) throws IOException, HttpException {
        HttpRequestWrapper unconditional = this.conditionalRequestBuilder.buildUnconditionalRequest(request, matchedEntry);
        return this.callBackend(route, unconditional, context, execAware);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private HttpCacheEntry getUpdatedVariantEntry(HttpHost target, HttpRequestWrapper conditionalRequest, Date requestDate, Date responseDate, CloseableHttpResponse backendResponse, Variant matchingVariant, HttpCacheEntry matchedEntry) throws IOException {
        HttpCacheEntry responseEntry = matchedEntry;
        try {
            responseEntry = this.responseCache.updateVariantCacheEntry(target, (HttpRequest)conditionalRequest, matchedEntry, (HttpResponse)backendResponse, requestDate, responseDate, matchingVariant.getCacheKey());
        }
        catch (IOException ioe) {
            this.log.warn((Object)"Could not update cache entry", (Throwable)ioe);
        }
        finally {
            backendResponse.close();
        }
        return responseEntry;
    }

    private void tryToUpdateVariantMap(HttpHost target, HttpRequestWrapper request, Variant matchingVariant) {
        try {
            this.responseCache.reuseVariantEntryFor(target, (HttpRequest)request, matchingVariant);
        }
        catch (IOException ioe) {
            this.log.warn((Object)"Could not update cache entry to reuse variant", (Throwable)ioe);
        }
    }

    private boolean shouldSendNotModifiedResponse(HttpRequestWrapper request, HttpCacheEntry responseEntry) {
        return this.suitabilityChecker.isConditional((HttpRequest)request) && this.suitabilityChecker.allConditionalsMatch((HttpRequest)request, responseEntry, new Date());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    CloseableHttpResponse revalidateCacheEntry(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware, HttpCacheEntry cacheEntry) throws IOException, HttpException {
        HttpRequestWrapper conditionalRequest = this.conditionalRequestBuilder.buildConditionalRequest(request, cacheEntry);
        URI uri = conditionalRequest.getURI();
        if (uri != null) {
            try {
                conditionalRequest.setURI(URIUtils.rewriteURIForRoute((URI)uri, (RouteInfo)route, (boolean)context.getRequestConfig().isNormalizeUri()));
            }
            catch (URISyntaxException ex) {
                throw new ProtocolException("Invalid URI: " + uri, (Throwable)ex);
            }
        }
        Date requestDate = this.getCurrentDate();
        CloseableHttpResponse backendResponse = this.backend.execute(route, conditionalRequest, context, execAware);
        Date responseDate = this.getCurrentDate();
        if (this.revalidationResponseIsTooOld((HttpResponse)backendResponse, cacheEntry)) {
            backendResponse.close();
            HttpRequestWrapper unconditional = this.conditionalRequestBuilder.buildUnconditionalRequest(request, cacheEntry);
            requestDate = this.getCurrentDate();
            backendResponse = this.backend.execute(route, unconditional, context, execAware);
            responseDate = this.getCurrentDate();
        }
        backendResponse.addHeader("Via", this.generateViaHeader((HttpMessage)backendResponse));
        int statusCode = backendResponse.getStatusLine().getStatusCode();
        if (statusCode == 304 || statusCode == 200) {
            this.recordCacheUpdate((HttpContext)context);
        }
        if (statusCode == 304) {
            HttpCacheEntry updatedEntry = this.responseCache.updateCacheEntry(context.getTargetHost(), (HttpRequest)request, cacheEntry, (HttpResponse)backendResponse, requestDate, responseDate);
            if (this.suitabilityChecker.isConditional((HttpRequest)request) && this.suitabilityChecker.allConditionalsMatch((HttpRequest)request, updatedEntry, new Date())) {
                return this.responseGenerator.generateNotModifiedResponse(updatedEntry);
            }
            return this.responseGenerator.generateResponse(request, updatedEntry);
        }
        if (this.staleIfErrorAppliesTo(statusCode) && !this.staleResponseNotAllowed(request, cacheEntry, this.getCurrentDate()) && this.validityPolicy.mayReturnStaleIfError((HttpRequest)request, cacheEntry, responseDate)) {
            try {
                CloseableHttpResponse cachedResponse = this.responseGenerator.generateResponse(request, cacheEntry);
                cachedResponse.addHeader("Warning", "110 localhost \"Response is stale\"");
                CloseableHttpResponse closeableHttpResponse = cachedResponse;
                return closeableHttpResponse;
            }
            finally {
                backendResponse.close();
            }
        }
        return this.handleBackendResponse(conditionalRequest, context, requestDate, responseDate, backendResponse);
    }

    private boolean staleIfErrorAppliesTo(int statusCode) {
        return statusCode == 500 || statusCode == 502 || statusCode == 503 || statusCode == 504;
    }

    CloseableHttpResponse handleBackendResponse(HttpRequestWrapper request, HttpClientContext context, Date requestDate, Date responseDate, CloseableHttpResponse backendResponse) throws IOException {
        this.log.trace((Object)"Handling Backend response");
        this.responseCompliance.ensureProtocolCompliance(request, (HttpResponse)backendResponse);
        HttpHost target = context.getTargetHost();
        boolean cacheable = this.responseCachingPolicy.isResponseCacheable((HttpRequest)request, (HttpResponse)backendResponse);
        this.responseCache.flushInvalidatedCacheEntriesFor(target, (HttpRequest)request, (HttpResponse)backendResponse);
        if (cacheable && !this.alreadyHaveNewerCacheEntry(target, request, (HttpResponse)backendResponse)) {
            this.storeRequestIfModifiedSinceFor304Response((HttpRequest)request, (HttpResponse)backendResponse);
            return this.responseCache.cacheAndReturnResponse(target, (HttpRequest)request, backendResponse, requestDate, responseDate);
        }
        if (!cacheable) {
            try {
                this.responseCache.flushCacheEntriesFor(target, (HttpRequest)request);
            }
            catch (IOException ioe) {
                this.log.warn((Object)"Unable to flush invalid cache entries", (Throwable)ioe);
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

    private boolean alreadyHaveNewerCacheEntry(HttpHost target, HttpRequestWrapper request, HttpResponse backendResponse) {
        HttpCacheEntry existing = null;
        try {
            existing = this.responseCache.getCacheEntry(target, (HttpRequest)request);
        }
        catch (IOException iOException) {
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
        Date entryDate = DateUtils.parseDate((String)entryDateHeader.getValue());
        Date responseDate = DateUtils.parseDate((String)responseDateHeader.getValue());
        if (entryDate == null || responseDate == null) {
            return false;
        }
        return responseDate.before(entryDate);
    }
}

