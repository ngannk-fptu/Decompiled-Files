/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.Header
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpHost
 *  org.apache.http.HttpRequest
 *  org.apache.http.HttpResponse
 *  org.apache.http.HttpVersion
 *  org.apache.http.ProtocolVersion
 *  org.apache.http.client.methods.CloseableHttpResponse
 *  org.apache.http.client.methods.HttpRequestWrapper
 *  org.apache.http.entity.ByteArrayEntity
 *  org.apache.http.message.BasicHttpResponse
 */
package org.apache.http.impl.client.cache;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheInvalidator;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.client.cache.HttpCacheUpdateCallback;
import org.apache.http.client.cache.HttpCacheUpdateException;
import org.apache.http.client.cache.Resource;
import org.apache.http.client.cache.ResourceFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.cache.BasicHttpCacheStorage;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CacheEntryUpdater;
import org.apache.http.impl.client.cache.CacheInvalidator;
import org.apache.http.impl.client.cache.CacheKeyGenerator;
import org.apache.http.impl.client.cache.CachedHttpResponseGenerator;
import org.apache.http.impl.client.cache.HeapResourceFactory;
import org.apache.http.impl.client.cache.HttpCache;
import org.apache.http.impl.client.cache.Proxies;
import org.apache.http.impl.client.cache.SizeLimitedResponseReader;
import org.apache.http.impl.client.cache.Variant;
import org.apache.http.message.BasicHttpResponse;

class BasicHttpCache
implements HttpCache {
    private static final Set<String> safeRequestMethods = new HashSet<String>(Arrays.asList("HEAD", "GET", "OPTIONS", "TRACE"));
    private final CacheKeyGenerator uriExtractor;
    private final ResourceFactory resourceFactory;
    private final long maxObjectSizeBytes;
    private final CacheEntryUpdater cacheEntryUpdater;
    private final CachedHttpResponseGenerator responseGenerator;
    private final HttpCacheInvalidator cacheInvalidator;
    private final HttpCacheStorage storage;
    private final Log log = LogFactory.getLog(this.getClass());

    public BasicHttpCache(ResourceFactory resourceFactory, HttpCacheStorage storage, CacheConfig config, CacheKeyGenerator uriExtractor, HttpCacheInvalidator cacheInvalidator) {
        this.resourceFactory = resourceFactory;
        this.uriExtractor = uriExtractor;
        this.cacheEntryUpdater = new CacheEntryUpdater(resourceFactory);
        this.maxObjectSizeBytes = config.getMaxObjectSize();
        this.responseGenerator = new CachedHttpResponseGenerator();
        this.storage = storage;
        this.cacheInvalidator = cacheInvalidator;
    }

    public BasicHttpCache(ResourceFactory resourceFactory, HttpCacheStorage storage, CacheConfig config, CacheKeyGenerator uriExtractor) {
        this(resourceFactory, storage, config, uriExtractor, new CacheInvalidator(uriExtractor, storage));
    }

    public BasicHttpCache(ResourceFactory resourceFactory, HttpCacheStorage storage, CacheConfig config) {
        this(resourceFactory, storage, config, new CacheKeyGenerator());
    }

    public BasicHttpCache(CacheConfig config) {
        this(new HeapResourceFactory(), new BasicHttpCacheStorage(config), config);
    }

    public BasicHttpCache() {
        this(CacheConfig.DEFAULT);
    }

    @Override
    public void flushCacheEntriesFor(HttpHost host, HttpRequest request) throws IOException {
        if (!safeRequestMethods.contains(request.getRequestLine().getMethod())) {
            String uri = this.uriExtractor.getURI(host, request);
            this.storage.removeEntry(uri);
        }
    }

    @Override
    public void flushInvalidatedCacheEntriesFor(HttpHost host, HttpRequest request, HttpResponse response) {
        if (!safeRequestMethods.contains(request.getRequestLine().getMethod())) {
            this.cacheInvalidator.flushInvalidatedCacheEntries(host, request, response);
        }
    }

    void storeInCache(HttpHost target, HttpRequest request, HttpCacheEntry entry) throws IOException {
        if (entry.hasVariants()) {
            this.storeVariantEntry(target, request, entry);
        } else {
            this.storeNonVariantEntry(target, request, entry);
        }
    }

    void storeNonVariantEntry(HttpHost target, HttpRequest req, HttpCacheEntry entry) throws IOException {
        String uri = this.uriExtractor.getURI(target, req);
        this.storage.putEntry(uri, entry);
    }

    void storeVariantEntry(HttpHost target, final HttpRequest req, final HttpCacheEntry entry) throws IOException {
        String parentURI = this.uriExtractor.getURI(target, req);
        final String variantURI = this.uriExtractor.getVariantURI(target, req, entry);
        this.storage.putEntry(variantURI, entry);
        HttpCacheUpdateCallback callback = new HttpCacheUpdateCallback(){

            @Override
            public HttpCacheEntry update(HttpCacheEntry existing) throws IOException {
                return BasicHttpCache.this.doGetUpdatedParentEntry(req.getRequestLine().getUri(), existing, entry, BasicHttpCache.this.uriExtractor.getVariantKey(req, entry), variantURI);
            }
        };
        try {
            this.storage.updateEntry(parentURI, callback);
        }
        catch (HttpCacheUpdateException e) {
            this.log.warn((Object)("Could not update key [" + parentURI + "]"), (Throwable)e);
        }
    }

    @Override
    public void reuseVariantEntryFor(HttpHost target, final HttpRequest req, Variant variant) throws IOException {
        String parentCacheKey = this.uriExtractor.getURI(target, req);
        final HttpCacheEntry entry = variant.getEntry();
        final String variantKey = this.uriExtractor.getVariantKey(req, entry);
        final String variantCacheKey = variant.getCacheKey();
        HttpCacheUpdateCallback callback = new HttpCacheUpdateCallback(){

            @Override
            public HttpCacheEntry update(HttpCacheEntry existing) throws IOException {
                return BasicHttpCache.this.doGetUpdatedParentEntry(req.getRequestLine().getUri(), existing, entry, variantKey, variantCacheKey);
            }
        };
        try {
            this.storage.updateEntry(parentCacheKey, callback);
        }
        catch (HttpCacheUpdateException e) {
            this.log.warn((Object)("Could not update key [" + parentCacheKey + "]"), (Throwable)e);
        }
    }

    boolean isIncompleteResponse(HttpResponse resp, Resource resource) {
        int contentLength;
        int status = resp.getStatusLine().getStatusCode();
        if (status != 200 && status != 206) {
            return false;
        }
        Header hdr = resp.getFirstHeader("Content-Length");
        if (hdr == null) {
            return false;
        }
        try {
            contentLength = Integer.parseInt(hdr.getValue());
        }
        catch (NumberFormatException nfe) {
            return false;
        }
        if (resource == null) {
            return false;
        }
        return resource.length() < (long)contentLength;
    }

    CloseableHttpResponse generateIncompleteResponseError(HttpResponse response, Resource resource) {
        Integer contentLength = Integer.valueOf(response.getFirstHeader("Content-Length").getValue());
        BasicHttpResponse error = new BasicHttpResponse((ProtocolVersion)HttpVersion.HTTP_1_1, 502, "Bad Gateway");
        error.setHeader("Content-Type", "text/plain;charset=UTF-8");
        String msg = String.format("Received incomplete response with Content-Length %d but actual body length %d", contentLength, resource.length());
        byte[] msgBytes = msg.getBytes();
        error.setHeader("Content-Length", Integer.toString(msgBytes.length));
        error.setEntity((HttpEntity)new ByteArrayEntity(msgBytes));
        return Proxies.enhanceResponse((HttpResponse)error);
    }

    HttpCacheEntry doGetUpdatedParentEntry(String requestId, HttpCacheEntry existing, HttpCacheEntry entry, String variantKey, String variantCacheKey) throws IOException {
        HttpCacheEntry src = existing;
        if (src == null) {
            src = entry;
        }
        Resource resource = null;
        if (src.getResource() != null) {
            resource = this.resourceFactory.copy(requestId, src.getResource());
        }
        HashMap<String, String> variantMap = new HashMap<String, String>(src.getVariantMap());
        variantMap.put(variantKey, variantCacheKey);
        return new HttpCacheEntry(src.getRequestDate(), src.getResponseDate(), src.getStatusLine(), src.getAllHeaders(), resource, variantMap, src.getRequestMethod());
    }

    @Override
    public HttpCacheEntry updateCacheEntry(HttpHost target, HttpRequest request, HttpCacheEntry stale, HttpResponse originResponse, Date requestSent, Date responseReceived) throws IOException {
        HttpCacheEntry updatedEntry = this.cacheEntryUpdater.updateCacheEntry(request.getRequestLine().getUri(), stale, requestSent, responseReceived, originResponse);
        this.storeInCache(target, request, updatedEntry);
        return updatedEntry;
    }

    @Override
    public HttpCacheEntry updateVariantCacheEntry(HttpHost target, HttpRequest request, HttpCacheEntry stale, HttpResponse originResponse, Date requestSent, Date responseReceived, String cacheKey) throws IOException {
        HttpCacheEntry updatedEntry = this.cacheEntryUpdater.updateCacheEntry(request.getRequestLine().getUri(), stale, requestSent, responseReceived, originResponse);
        this.storage.putEntry(cacheKey, updatedEntry);
        return updatedEntry;
    }

    @Override
    public HttpResponse cacheAndReturnResponse(HttpHost host, HttpRequest request, HttpResponse originResponse, Date requestSent, Date responseReceived) throws IOException {
        return this.cacheAndReturnResponse(host, request, Proxies.enhanceResponse(originResponse), requestSent, responseReceived);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CloseableHttpResponse cacheAndReturnResponse(HttpHost host, HttpRequest request, CloseableHttpResponse originResponse, Date requestSent, Date responseReceived) throws IOException {
        boolean closeOriginResponse = true;
        SizeLimitedResponseReader responseReader = this.getResponseReader(request, originResponse);
        try {
            responseReader.readResponse();
            if (responseReader.isLimitReached()) {
                closeOriginResponse = false;
                CloseableHttpResponse closeableHttpResponse = responseReader.getReconstructedResponse();
                return closeableHttpResponse;
            }
            Resource resource = responseReader.getResource();
            if (this.isIncompleteResponse((HttpResponse)originResponse, resource)) {
                CloseableHttpResponse closeableHttpResponse = this.generateIncompleteResponseError((HttpResponse)originResponse, resource);
                return closeableHttpResponse;
            }
            HttpCacheEntry entry = new HttpCacheEntry(requestSent, responseReceived, originResponse.getStatusLine(), originResponse.getAllHeaders(), resource, request.getRequestLine().getMethod());
            this.storeInCache(host, request, entry);
            CloseableHttpResponse closeableHttpResponse = this.responseGenerator.generateResponse(HttpRequestWrapper.wrap((HttpRequest)request, (HttpHost)host), entry);
            return closeableHttpResponse;
        }
        finally {
            if (closeOriginResponse) {
                originResponse.close();
            }
        }
    }

    SizeLimitedResponseReader getResponseReader(HttpRequest request, CloseableHttpResponse backEndResponse) {
        return new SizeLimitedResponseReader(this.resourceFactory, this.maxObjectSizeBytes, request, backEndResponse);
    }

    @Override
    public HttpCacheEntry getCacheEntry(HttpHost host, HttpRequest request) throws IOException {
        HttpCacheEntry root = this.storage.getEntry(this.uriExtractor.getURI(host, request));
        if (root == null) {
            return null;
        }
        if (!root.hasVariants()) {
            return root;
        }
        String variantCacheKey = root.getVariantMap().get(this.uriExtractor.getVariantKey(request, root));
        if (variantCacheKey == null) {
            return null;
        }
        return this.storage.getEntry(variantCacheKey);
    }

    @Override
    public void flushInvalidatedCacheEntriesFor(HttpHost host, HttpRequest request) throws IOException {
        this.cacheInvalidator.flushInvalidatedCacheEntries(host, request);
    }

    @Override
    public Map<String, Variant> getVariantCacheEntriesWithEtags(HttpHost host, HttpRequest request) throws IOException {
        HashMap<String, Variant> variants = new HashMap<String, Variant>();
        HttpCacheEntry root = this.storage.getEntry(this.uriExtractor.getURI(host, request));
        if (root == null || !root.hasVariants()) {
            return variants;
        }
        for (Map.Entry<String, String> variant : root.getVariantMap().entrySet()) {
            String variantKey = variant.getKey();
            String variantCacheKey = variant.getValue();
            this.addVariantWithEtag(variantKey, variantCacheKey, variants);
        }
        return variants;
    }

    private void addVariantWithEtag(String variantKey, String variantCacheKey, Map<String, Variant> variants) throws IOException {
        HttpCacheEntry entry = this.storage.getEntry(variantCacheKey);
        if (entry == null) {
            return;
        }
        Header etagHeader = entry.getFirstHeader("ETag");
        if (etagHeader == null) {
            return;
        }
        variants.put(etagHeader.getValue(), new Variant(variantKey, variantCacheKey, entry));
    }
}

