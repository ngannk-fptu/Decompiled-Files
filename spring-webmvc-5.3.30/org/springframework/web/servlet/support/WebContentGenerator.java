/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.http.CacheControl
 *  org.springframework.http.HttpMethod
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 *  org.springframework.web.HttpRequestMethodNotSupportedException
 *  org.springframework.web.HttpSessionRequiredException
 *  org.springframework.web.context.support.WebApplicationObjectSupport
 */
package org.springframework.web.servlet.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.context.support.WebApplicationObjectSupport;

public abstract class WebContentGenerator
extends WebApplicationObjectSupport {
    public static final String METHOD_GET = "GET";
    public static final String METHOD_HEAD = "HEAD";
    public static final String METHOD_POST = "POST";
    private static final String HEADER_PRAGMA = "Pragma";
    private static final String HEADER_EXPIRES = "Expires";
    protected static final String HEADER_CACHE_CONTROL = "Cache-Control";
    @Nullable
    private Set<String> supportedMethods;
    @Nullable
    private String allowHeader;
    private boolean requireSession = false;
    @Nullable
    private CacheControl cacheControl;
    private int cacheSeconds = -1;
    @Nullable
    private String[] varyByRequestHeaders;
    private boolean useExpiresHeader = false;
    private boolean useCacheControlHeader = true;
    private boolean useCacheControlNoStore = true;
    private boolean alwaysMustRevalidate = false;

    public WebContentGenerator() {
        this(true);
    }

    public WebContentGenerator(boolean restrictDefaultSupportedMethods) {
        if (restrictDefaultSupportedMethods) {
            this.supportedMethods = new LinkedHashSet<String>(4);
            this.supportedMethods.add(METHOD_GET);
            this.supportedMethods.add(METHOD_HEAD);
            this.supportedMethods.add(METHOD_POST);
        }
        this.initAllowHeader();
    }

    public WebContentGenerator(String ... supportedMethods) {
        this.setSupportedMethods(supportedMethods);
    }

    public final void setSupportedMethods(String ... methods) {
        this.supportedMethods = !ObjectUtils.isEmpty((Object[])methods) ? new LinkedHashSet<String>(Arrays.asList(methods)) : null;
        this.initAllowHeader();
    }

    @Nullable
    public final String[] getSupportedMethods() {
        return this.supportedMethods != null ? StringUtils.toStringArray(this.supportedMethods) : null;
    }

    private void initAllowHeader() {
        Collection<Object> allowedMethods;
        if (this.supportedMethods == null) {
            allowedMethods = new ArrayList(HttpMethod.values().length - 1);
            for (HttpMethod method : HttpMethod.values()) {
                if (method == HttpMethod.TRACE) continue;
                allowedMethods.add(method.name());
            }
        } else if (this.supportedMethods.contains(HttpMethod.OPTIONS.name())) {
            allowedMethods = this.supportedMethods;
        } else {
            allowedMethods = new ArrayList<String>(this.supportedMethods);
            allowedMethods.add(HttpMethod.OPTIONS.name());
        }
        this.allowHeader = StringUtils.collectionToCommaDelimitedString(allowedMethods);
    }

    @Nullable
    protected String getAllowHeader() {
        return this.allowHeader;
    }

    public final void setRequireSession(boolean requireSession) {
        this.requireSession = requireSession;
    }

    public final boolean isRequireSession() {
        return this.requireSession;
    }

    public final void setCacheControl(@Nullable CacheControl cacheControl) {
        this.cacheControl = cacheControl;
    }

    @Nullable
    public final CacheControl getCacheControl() {
        return this.cacheControl;
    }

    public final void setCacheSeconds(int seconds) {
        this.cacheSeconds = seconds;
    }

    public final int getCacheSeconds() {
        return this.cacheSeconds;
    }

    public final void setVaryByRequestHeaders(String ... varyByRequestHeaders) {
        this.varyByRequestHeaders = varyByRequestHeaders;
    }

    @Nullable
    public final String[] getVaryByRequestHeaders() {
        return this.varyByRequestHeaders;
    }

    @Deprecated
    public final void setUseExpiresHeader(boolean useExpiresHeader) {
        this.useExpiresHeader = useExpiresHeader;
    }

    @Deprecated
    public final boolean isUseExpiresHeader() {
        return this.useExpiresHeader;
    }

    @Deprecated
    public final void setUseCacheControlHeader(boolean useCacheControlHeader) {
        this.useCacheControlHeader = useCacheControlHeader;
    }

    @Deprecated
    public final boolean isUseCacheControlHeader() {
        return this.useCacheControlHeader;
    }

    @Deprecated
    public final void setUseCacheControlNoStore(boolean useCacheControlNoStore) {
        this.useCacheControlNoStore = useCacheControlNoStore;
    }

    @Deprecated
    public final boolean isUseCacheControlNoStore() {
        return this.useCacheControlNoStore;
    }

    @Deprecated
    public final void setAlwaysMustRevalidate(boolean mustRevalidate) {
        this.alwaysMustRevalidate = mustRevalidate;
    }

    @Deprecated
    public final boolean isAlwaysMustRevalidate() {
        return this.alwaysMustRevalidate;
    }

    protected final void checkRequest(HttpServletRequest request) throws ServletException {
        String method = request.getMethod();
        if (this.supportedMethods != null && !this.supportedMethods.contains(method)) {
            throw new HttpRequestMethodNotSupportedException(method, this.supportedMethods);
        }
        if (this.requireSession && request.getSession(false) == null) {
            throw new HttpSessionRequiredException("Pre-existing session required but none found");
        }
    }

    protected final void prepareResponse(HttpServletResponse response) {
        if (this.cacheControl != null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("Applying default " + this.getCacheControl()));
            }
            this.applyCacheControl(response, this.cacheControl);
        } else {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("Applying default cacheSeconds=" + this.cacheSeconds));
            }
            this.applyCacheSeconds(response, this.cacheSeconds);
        }
        if (this.varyByRequestHeaders != null) {
            for (String value : this.getVaryRequestHeadersToAdd(response, this.varyByRequestHeaders)) {
                response.addHeader("Vary", value);
            }
        }
    }

    protected final void applyCacheControl(HttpServletResponse response, CacheControl cacheControl) {
        String ccValue = cacheControl.getHeaderValue();
        if (ccValue != null) {
            response.setHeader(HEADER_CACHE_CONTROL, ccValue);
            if (response.containsHeader(HEADER_PRAGMA)) {
                response.setHeader(HEADER_PRAGMA, "");
            }
            if (response.containsHeader(HEADER_EXPIRES)) {
                response.setHeader(HEADER_EXPIRES, "");
            }
        }
    }

    protected final void applyCacheSeconds(HttpServletResponse response, int cacheSeconds) {
        if (this.useExpiresHeader || !this.useCacheControlHeader) {
            if (cacheSeconds > 0) {
                this.cacheForSeconds(response, cacheSeconds);
            } else if (cacheSeconds == 0) {
                this.preventCaching(response);
            }
        } else {
            CacheControl cControl;
            if (cacheSeconds > 0) {
                cControl = CacheControl.maxAge((long)cacheSeconds, (TimeUnit)TimeUnit.SECONDS);
                if (this.alwaysMustRevalidate) {
                    cControl = cControl.mustRevalidate();
                }
            } else {
                cControl = cacheSeconds == 0 ? (this.useCacheControlNoStore ? CacheControl.noStore() : CacheControl.noCache()) : CacheControl.empty();
            }
            this.applyCacheControl(response, cControl);
        }
    }

    @Deprecated
    protected final void checkAndPrepare(HttpServletRequest request, HttpServletResponse response, boolean lastModified) throws ServletException {
        this.checkRequest(request);
        this.prepareResponse(response);
    }

    @Deprecated
    protected final void checkAndPrepare(HttpServletRequest request, HttpServletResponse response, int cacheSeconds, boolean lastModified) throws ServletException {
        this.checkRequest(request);
        this.applyCacheSeconds(response, cacheSeconds);
    }

    @Deprecated
    protected final void applyCacheSeconds(HttpServletResponse response, int cacheSeconds, boolean mustRevalidate) {
        if (cacheSeconds > 0) {
            this.cacheForSeconds(response, cacheSeconds, mustRevalidate);
        } else if (cacheSeconds == 0) {
            this.preventCaching(response);
        }
    }

    @Deprecated
    protected final void cacheForSeconds(HttpServletResponse response, int seconds) {
        this.cacheForSeconds(response, seconds, false);
    }

    @Deprecated
    protected final void cacheForSeconds(HttpServletResponse response, int seconds, boolean mustRevalidate) {
        if (this.useExpiresHeader) {
            response.setDateHeader(HEADER_EXPIRES, System.currentTimeMillis() + (long)seconds * 1000L);
        } else if (response.containsHeader(HEADER_EXPIRES)) {
            response.setHeader(HEADER_EXPIRES, "");
        }
        if (this.useCacheControlHeader) {
            String headerValue = "max-age=" + seconds;
            if (mustRevalidate || this.alwaysMustRevalidate) {
                headerValue = headerValue + ", must-revalidate";
            }
            response.setHeader(HEADER_CACHE_CONTROL, headerValue);
        }
        if (response.containsHeader(HEADER_PRAGMA)) {
            response.setHeader(HEADER_PRAGMA, "");
        }
    }

    @Deprecated
    protected final void preventCaching(HttpServletResponse response) {
        response.setHeader(HEADER_PRAGMA, "no-cache");
        if (this.useExpiresHeader) {
            response.setDateHeader(HEADER_EXPIRES, 1L);
        }
        if (this.useCacheControlHeader) {
            response.setHeader(HEADER_CACHE_CONTROL, "no-cache");
            if (this.useCacheControlNoStore) {
                response.addHeader(HEADER_CACHE_CONTROL, "no-store");
            }
        }
    }

    private Collection<String> getVaryRequestHeadersToAdd(HttpServletResponse response, String[] varyByRequestHeaders) {
        if (!response.containsHeader("Vary")) {
            return Arrays.asList(varyByRequestHeaders);
        }
        ArrayList<String> result = new ArrayList<String>(varyByRequestHeaders.length);
        Collections.addAll(result, varyByRequestHeaders);
        for (String header : response.getHeaders("Vary")) {
            for (String existing : StringUtils.tokenizeToStringArray((String)header, (String)",")) {
                if ("*".equals(existing)) {
                    return Collections.emptyList();
                }
                for (String value : varyByRequestHeaders) {
                    if (!value.equalsIgnoreCase(existing)) continue;
                    result.remove(value);
                }
            }
        }
        return result;
    }
}

