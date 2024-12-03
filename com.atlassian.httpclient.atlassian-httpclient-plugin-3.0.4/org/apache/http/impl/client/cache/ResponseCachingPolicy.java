/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.client.cache;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.utils.DateUtils;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
class ResponseCachingPolicy {
    private static final String[] AUTH_CACHEABLE_PARAMS = new String[]{"s-maxage", "must-revalidate", "public"};
    private final long maxObjectSizeBytes;
    private final boolean sharedCache;
    private final boolean neverCache1_0ResponsesWithQueryString;
    private final Log log = LogFactory.getLog(this.getClass());
    private static final Set<Integer> cacheableStatuses = new HashSet<Integer>(Arrays.asList(200, 203, 300, 301, 410));
    private final Set<Integer> uncacheableStatuses;

    public ResponseCachingPolicy(long maxObjectSizeBytes, boolean sharedCache, boolean neverCache1_0ResponsesWithQueryString, boolean allow303Caching) {
        this.maxObjectSizeBytes = maxObjectSizeBytes;
        this.sharedCache = sharedCache;
        this.neverCache1_0ResponsesWithQueryString = neverCache1_0ResponsesWithQueryString;
        this.uncacheableStatuses = allow303Caching ? new HashSet<Integer>(Arrays.asList(206)) : new HashSet<Integer>(Arrays.asList(206, 303));
    }

    public boolean isResponseCacheable(String httpMethod, HttpResponse response) {
        long contentLengthValue;
        boolean cacheable = false;
        if (!"GET".equals(httpMethod) && !"HEAD".equals(httpMethod)) {
            this.log.debug("Response was not cacheable.");
            return false;
        }
        int status = response.getStatusLine().getStatusCode();
        if (cacheableStatuses.contains(status)) {
            cacheable = true;
        } else {
            if (this.uncacheableStatuses.contains(status)) {
                return false;
            }
            if (this.unknownStatusCode(status)) {
                return false;
            }
        }
        Header contentLength = response.getFirstHeader("Content-Length");
        if (contentLength != null && (contentLengthValue = Long.parseLong(contentLength.getValue())) > this.maxObjectSizeBytes) {
            return false;
        }
        Header[] ageHeaders = response.getHeaders("Age");
        if (ageHeaders.length > 1) {
            return false;
        }
        Header[] expiresHeaders = response.getHeaders("Expires");
        if (expiresHeaders.length > 1) {
            return false;
        }
        Header[] dateHeaders = response.getHeaders("Date");
        if (dateHeaders.length != 1) {
            return false;
        }
        Date date = DateUtils.parseDate(dateHeaders[0].getValue());
        if (date == null) {
            return false;
        }
        for (Header varyHdr : response.getHeaders("Vary")) {
            for (HeaderElement elem : varyHdr.getElements()) {
                if (!"*".equals(elem.getName())) continue;
                return false;
            }
        }
        if (this.isExplicitlyNonCacheable(response)) {
            return false;
        }
        return cacheable || this.isExplicitlyCacheable(response);
    }

    private boolean unknownStatusCode(int status) {
        if (status >= 100 && status <= 101) {
            return false;
        }
        if (status >= 200 && status <= 206) {
            return false;
        }
        if (status >= 300 && status <= 307) {
            return false;
        }
        if (status >= 400 && status <= 417) {
            return false;
        }
        return status < 500 || status > 505;
    }

    protected boolean isExplicitlyNonCacheable(HttpResponse response) {
        Header[] cacheControlHeaders;
        for (Header header : cacheControlHeaders = response.getHeaders("Cache-Control")) {
            for (HeaderElement elem : header.getElements()) {
                if (!"no-store".equals(elem.getName()) && !"no-cache".equals(elem.getName()) && (!this.sharedCache || !"private".equals(elem.getName()))) continue;
                return true;
            }
        }
        return false;
    }

    protected boolean hasCacheControlParameterFrom(HttpMessage msg, String[] params) {
        Header[] cacheControlHeaders;
        for (Header header : cacheControlHeaders = msg.getHeaders("Cache-Control")) {
            for (HeaderElement elem : header.getElements()) {
                for (String param : params) {
                    if (!param.equalsIgnoreCase(elem.getName())) continue;
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isExplicitlyCacheable(HttpResponse response) {
        if (response.getFirstHeader("Expires") != null) {
            return true;
        }
        String[] cacheableParams = new String[]{"max-age", "s-maxage", "must-revalidate", "proxy-revalidate", "public"};
        return this.hasCacheControlParameterFrom(response, cacheableParams);
    }

    public boolean isResponseCacheable(HttpRequest request, HttpResponse response) {
        Header[] authNHeaders;
        if (this.requestProtocolGreaterThanAccepted(request)) {
            this.log.debug("Response was not cacheable.");
            return false;
        }
        String[] uncacheableRequestDirectives = new String[]{"no-store"};
        if (this.hasCacheControlParameterFrom(request, uncacheableRequestDirectives)) {
            return false;
        }
        if (request.getRequestLine().getUri().contains("?")) {
            if (this.neverCache1_0ResponsesWithQueryString && this.from1_0Origin(response)) {
                this.log.debug("Response was not cacheable as it had a query string.");
                return false;
            }
            if (!this.isExplicitlyCacheable(response)) {
                this.log.debug("Response was not cacheable as it is missing explicit caching headers.");
                return false;
            }
        }
        if (this.expiresHeaderLessOrEqualToDateHeaderAndNoCacheControl(response)) {
            return false;
        }
        if (this.sharedCache && (authNHeaders = request.getHeaders("Authorization")) != null && authNHeaders.length > 0 && !this.hasCacheControlParameterFrom(response, AUTH_CACHEABLE_PARAMS)) {
            return false;
        }
        String method = request.getRequestLine().getMethod();
        return this.isResponseCacheable(method, response);
    }

    private boolean expiresHeaderLessOrEqualToDateHeaderAndNoCacheControl(HttpResponse response) {
        if (response.getFirstHeader("Cache-Control") != null) {
            return false;
        }
        Header expiresHdr = response.getFirstHeader("Expires");
        Header dateHdr = response.getFirstHeader("Date");
        if (expiresHdr == null || dateHdr == null) {
            return false;
        }
        Date expires = DateUtils.parseDate(expiresHdr.getValue());
        Date date = DateUtils.parseDate(dateHdr.getValue());
        if (expires == null || date == null) {
            return false;
        }
        return expires.equals(date) || expires.before(date);
    }

    private boolean from1_0Origin(HttpResponse response) {
        HeaderElement[] arr$;
        int len$;
        int i$;
        Header via = response.getFirstHeader("Via");
        if (via != null && (i$ = 0) < (len$ = (arr$ = via.getElements()).length)) {
            HeaderElement elt = arr$[i$];
            String proto = elt.toString().split("\\s")[0];
            if (proto.contains("/")) {
                return proto.equals("HTTP/1.0");
            }
            return proto.equals("1.0");
        }
        return HttpVersion.HTTP_1_0.equals(response.getProtocolVersion());
    }

    private boolean requestProtocolGreaterThanAccepted(HttpRequest req) {
        return req.getProtocolVersion().compareToVersion(HttpVersion.HTTP_1_1) > 0;
    }
}

