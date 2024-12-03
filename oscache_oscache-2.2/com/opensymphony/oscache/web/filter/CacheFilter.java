/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.oscache.web.filter;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.web.ServletCacheAdministrator;
import com.opensymphony.oscache.web.filter.CacheHttpServletResponseWrapper;
import com.opensymphony.oscache.web.filter.ExpiresRefreshPolicy;
import com.opensymphony.oscache.web.filter.ICacheGroupsProvider;
import com.opensymphony.oscache.web.filter.ICacheKeyProvider;
import com.opensymphony.oscache.web.filter.ResponseContent;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CacheFilter
implements Filter,
ICacheKeyProvider,
ICacheGroupsProvider {
    public static final String HEADER_LAST_MODIFIED = "Last-Modified";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
    public static final String HEADER_EXPIRES = "Expires";
    public static final String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";
    public static final String HEADER_CACHE_CONTROL = "Cache-control";
    public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    public static final int FRAGMENT_AUTODETECT = -1;
    public static final int FRAGMENT_NO = 0;
    public static final int FRAGMENT_YES = 1;
    public static final int NOCACHE_OFF = 0;
    public static final int NOCACHE_SESSION_ID_IN_URL = 1;
    public static final long LAST_MODIFIED_OFF = 0L;
    public static final long LAST_MODIFIED_ON = 1L;
    public static final long LAST_MODIFIED_INITIAL = -1L;
    public static final long EXPIRES_OFF = 0L;
    public static final long EXPIRES_ON = 1L;
    public static final long EXPIRES_TIME = -1L;
    private static final String REQUEST_FILTERED = "__oscache_filtered";
    private ExpiresRefreshPolicy expiresRefreshPolicy;
    private final Log log = LogFactory.getLog(this.getClass());
    private FilterConfig config;
    private ServletCacheAdministrator admin = null;
    private int cacheScope = 4;
    private int fragment = -1;
    private int time = 3600;
    private int nocache = 0;
    private long lastModified = -1L;
    private long expires = 1L;
    private ICacheKeyProvider cacheKeyProvider = this;
    private ICacheGroupsProvider cacheGroupsProvider = this;
    static /* synthetic */ Class class$com$opensymphony$oscache$web$filter$ICacheKeyProvider;
    static /* synthetic */ Class class$com$opensymphony$oscache$web$filter$ICacheGroupsProvider;

    public void destroy() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (this.log.isInfoEnabled()) {
            this.log.info((Object)("<cache>: filter in scope " + this.cacheScope));
        }
        if (this.isFilteredBefore(request) || !this.isCacheable(request)) {
            chain.doFilter(request, response);
            return;
        }
        request.setAttribute(REQUEST_FILTERED, (Object)Boolean.TRUE);
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        boolean fragmentRequest = this.isFragment(httpRequest);
        Cache cache = this.cacheScope == 3 ? this.admin.getSessionScopeCache(httpRequest.getSession(true)) : this.admin.getAppScopeCache(this.config.getServletContext());
        String key = this.cacheKeyProvider.createCacheKey(httpRequest, this.admin, cache);
        try {
            ResponseContent respContent = (ResponseContent)cache.getFromCache(key, this.time);
            if (this.log.isInfoEnabled()) {
                this.log.info((Object)("<cache>: Using cached entry for " + key));
            }
            boolean acceptsGZip = false;
            if (!fragmentRequest && this.lastModified != 0L) {
                long clientLastModified = httpRequest.getDateHeader(HEADER_IF_MODIFIED_SINCE);
                if (clientLastModified != -1L && clientLastModified >= respContent.getLastModified()) {
                    ((HttpServletResponse)response).setStatus(304);
                    return;
                }
                acceptsGZip = respContent.isContentGZiped() && this.acceptsGZipEncoding(httpRequest);
            }
            respContent.writeTo(response, fragmentRequest, acceptsGZip);
        }
        catch (NeedsRefreshException nre) {
            boolean updateSucceeded = false;
            try {
                if (this.log.isInfoEnabled()) {
                    this.log.info((Object)("<cache>: New cache entry, cache stale or cache scope flushed for " + key));
                }
                CacheHttpServletResponseWrapper cacheResponse = new CacheHttpServletResponseWrapper((HttpServletResponse)response, fragmentRequest, (long)this.time * 1000L, this.lastModified, this.expires);
                chain.doFilter(request, (ServletResponse)cacheResponse);
                cacheResponse.flushBuffer();
                if (this.isCacheable(cacheResponse)) {
                    String[] groups = this.cacheGroupsProvider.createCacheGroups(httpRequest, this.admin, cache);
                    cache.putInCache(key, cacheResponse.getContent(), groups, this.expiresRefreshPolicy, null);
                    updateSucceeded = true;
                }
            }
            finally {
                if (!updateSucceeded) {
                    cache.cancelUpdate(key);
                }
            }
        }
    }

    public void init(FilterConfig filterConfig) {
        block52: {
            Class<?> clazz;
            String className;
            block51: {
                this.config = filterConfig;
                this.admin = ServletCacheAdministrator.getInstance(this.config.getServletContext());
                try {
                    this.time = Integer.parseInt(this.config.getInitParameter("time"));
                }
                catch (Exception e) {
                    this.log.info((Object)"Could not get init parameter 'time', defaulting to one hour.");
                }
                this.expiresRefreshPolicy = new ExpiresRefreshPolicy(this.time);
                try {
                    String scopeString = this.config.getInitParameter("scope");
                    if (scopeString.equals("session")) {
                        this.cacheScope = 3;
                    } else if (scopeString.equals("application")) {
                        this.cacheScope = 4;
                    } else if (scopeString.equals("request")) {
                        this.cacheScope = 2;
                    } else if (scopeString.equals("page")) {
                        this.cacheScope = 1;
                    }
                }
                catch (Exception e) {
                    this.log.info((Object)"Could not get init parameter 'scope', defaulting to 'application'.");
                }
                try {
                    String fragmentString = this.config.getInitParameter("fragment");
                    if (fragmentString.equals("no")) {
                        this.fragment = 0;
                    } else if (fragmentString.equals("yes")) {
                        this.fragment = 1;
                    } else if (fragmentString.equalsIgnoreCase("auto")) {
                        this.fragment = -1;
                    }
                }
                catch (Exception e) {
                    this.log.info((Object)"Could not get init parameter 'fragment', defaulting to 'auto detect'.");
                }
                try {
                    String nocacheString = this.config.getInitParameter("nocache");
                    if (nocacheString.equals("off")) {
                        this.nocache = 0;
                    } else if (nocacheString.equalsIgnoreCase("sessionIdInURL")) {
                        this.nocache = 1;
                    }
                }
                catch (Exception e) {
                    this.log.info((Object)"Could not get init parameter 'nocache', defaulting to 'off'.");
                }
                try {
                    String lastModifiedString = this.config.getInitParameter("lastModified");
                    if (lastModifiedString.equals("off")) {
                        this.lastModified = 0L;
                    } else if (lastModifiedString.equals("on")) {
                        this.lastModified = 1L;
                    } else if (lastModifiedString.equalsIgnoreCase("initial")) {
                        this.lastModified = -1L;
                    }
                }
                catch (Exception e) {
                    this.log.info((Object)"Could not get init parameter 'lastModified', defaulting to 'initial'.");
                }
                try {
                    String expiresString = this.config.getInitParameter("expires");
                    if (expiresString.equals("off")) {
                        this.expires = 0L;
                    } else if (expiresString.equals("on")) {
                        this.expires = 1L;
                    } else if (expiresString.equalsIgnoreCase("time")) {
                        this.expires = -1L;
                    }
                }
                catch (Exception e) {
                    this.log.info((Object)"Could not get init parameter 'expires', defaulting to 'on'.");
                }
                try {
                    className = this.config.getInitParameter("ICacheKeyProvider");
                    try {
                        clazz = Class.forName(className);
                        if (!(class$com$opensymphony$oscache$web$filter$ICacheKeyProvider == null ? (class$com$opensymphony$oscache$web$filter$ICacheKeyProvider = CacheFilter.class$("com.opensymphony.oscache.web.filter.ICacheKeyProvider")) : class$com$opensymphony$oscache$web$filter$ICacheKeyProvider).isAssignableFrom(clazz)) {
                            this.log.error((Object)("Specified class '" + className + "' does not implement ICacheKeyProvider. Ignoring this provider."));
                            break block51;
                        }
                        this.cacheKeyProvider = (ICacheKeyProvider)clazz.newInstance();
                    }
                    catch (ClassNotFoundException e) {
                        this.log.error((Object)("Class '" + className + "' not found. Ignoring this cache key provider."), (Throwable)e);
                    }
                    catch (InstantiationException e) {
                        this.log.error((Object)("Class '" + className + "' could not be instantiated because it is not a concrete class. Ignoring this cache key provider."), (Throwable)e);
                    }
                    catch (IllegalAccessException e) {
                        this.log.error((Object)("Class '" + className + "' could not be instantiated because it is not public. Ignoring this cache key provider."), (Throwable)e);
                    }
                }
                catch (Exception e) {
                    this.log.info((Object)("Could not get init parameter 'ICacheKeyProvider', defaulting to " + this.getClass().getName() + "."));
                }
            }
            try {
                className = this.config.getInitParameter("ICacheGroupsProvider");
                try {
                    clazz = Class.forName(className);
                    if (!(class$com$opensymphony$oscache$web$filter$ICacheGroupsProvider == null ? (class$com$opensymphony$oscache$web$filter$ICacheGroupsProvider = CacheFilter.class$("com.opensymphony.oscache.web.filter.ICacheGroupsProvider")) : class$com$opensymphony$oscache$web$filter$ICacheGroupsProvider).isAssignableFrom(clazz)) {
                        this.log.error((Object)("Specified class '" + className + "' does not implement ICacheGroupsProvider. Ignoring this provider."));
                        break block52;
                    }
                    this.cacheGroupsProvider = (ICacheGroupsProvider)clazz.newInstance();
                }
                catch (ClassNotFoundException e) {
                    this.log.error((Object)("Class '" + className + "' not found. Ignoring this cache key provider."), (Throwable)e);
                }
                catch (InstantiationException e) {
                    this.log.error((Object)("Class '" + className + "' could not be instantiated because it is not a concrete class. Ignoring this cache groups provider."), (Throwable)e);
                }
                catch (IllegalAccessException e) {
                    this.log.error((Object)("Class '" + className + "' could not be instantiated because it is not public. Ignoring this cache groups provider."), (Throwable)e);
                }
            }
            catch (Exception e) {
                this.log.info((Object)("Could not get init parameter 'ICacheGroupsProvider', defaulting to " + this.getClass().getName() + "."));
            }
        }
    }

    public String createCacheKey(HttpServletRequest httpRequest, ServletCacheAdministrator scAdmin, Cache cache) {
        return scAdmin.generateEntryKey(null, httpRequest, this.cacheScope);
    }

    public String[] createCacheGroups(HttpServletRequest httpRequest, ServletCacheAdministrator scAdmin, Cache cache) {
        return null;
    }

    protected boolean isFragment(HttpServletRequest request) {
        if (this.fragment == -1) {
            return request.getAttribute("javax.servlet.include.request_uri") != null;
        }
        return this.fragment != 0;
    }

    protected boolean isFilteredBefore(ServletRequest request) {
        return request.getAttribute(REQUEST_FILTERED) != null;
    }

    protected boolean isCacheable(ServletRequest request) {
        boolean cachable = request instanceof HttpServletRequest;
        if (cachable) {
            HttpServletRequest requestHttp = (HttpServletRequest)request;
            if (this.nocache == 1) {
                boolean bl = cachable = !requestHttp.isRequestedSessionIdFromURL();
            }
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("<cache>: the request " + (cachable ? "is" : "is not") + " cachable."));
        }
        return cachable;
    }

    protected boolean isCacheable(CacheHttpServletResponseWrapper cacheResponse) {
        boolean cachable;
        boolean bl = cachable = cacheResponse.getStatus() == 200;
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("<cache>: the response " + (cachable ? "is" : "is not") + " cachable."));
        }
        return cachable;
    }

    protected boolean acceptsGZipEncoding(HttpServletRequest request) {
        String acceptEncoding = request.getHeader(HEADER_ACCEPT_ENCODING);
        return acceptEncoding != null && acceptEncoding.indexOf("gzip") != -1;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

