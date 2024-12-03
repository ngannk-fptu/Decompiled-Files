/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.servlet.view;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

public abstract class AbstractCachingViewResolver
extends WebApplicationObjectSupport
implements ViewResolver {
    public static final int DEFAULT_CACHE_LIMIT = 1024;
    private static final View UNRESOLVED_VIEW = new View(){

        @Override
        @Nullable
        public String getContentType() {
            return null;
        }

        @Override
        public void render(@Nullable Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {
        }
    };
    private static final CacheFilter DEFAULT_CACHE_FILTER = (view, viewName, locale) -> true;
    private volatile int cacheLimit = 1024;
    private boolean cacheUnresolved = true;
    private CacheFilter cacheFilter = DEFAULT_CACHE_FILTER;
    private final Map<Object, View> viewAccessCache = new ConcurrentHashMap<Object, View>(1024);
    private final Map<Object, View> viewCreationCache = new LinkedHashMap<Object, View>(1024, 0.75f, true){

        @Override
        protected boolean removeEldestEntry(Map.Entry<Object, View> eldest) {
            if (this.size() > AbstractCachingViewResolver.this.getCacheLimit()) {
                AbstractCachingViewResolver.this.viewAccessCache.remove(eldest.getKey());
                return true;
            }
            return false;
        }
    };

    public void setCacheLimit(int cacheLimit) {
        this.cacheLimit = cacheLimit;
    }

    public int getCacheLimit() {
        return this.cacheLimit;
    }

    public void setCache(boolean cache) {
        this.cacheLimit = cache ? 1024 : 0;
    }

    public boolean isCache() {
        return this.cacheLimit > 0;
    }

    public void setCacheUnresolved(boolean cacheUnresolved) {
        this.cacheUnresolved = cacheUnresolved;
    }

    public boolean isCacheUnresolved() {
        return this.cacheUnresolved;
    }

    public void setCacheFilter(CacheFilter cacheFilter) {
        Assert.notNull((Object)cacheFilter, "CacheFilter must not be null");
        this.cacheFilter = cacheFilter;
    }

    public CacheFilter getCacheFilter() {
        return this.cacheFilter;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nullable
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        if (!this.isCache()) {
            return this.createView(viewName, locale);
        }
        Object cacheKey = this.getCacheKey(viewName, locale);
        View view = this.viewAccessCache.get(cacheKey);
        if (view == null) {
            Map<Object, View> map = this.viewCreationCache;
            synchronized (map) {
                view = this.viewCreationCache.get(cacheKey);
                if (view == null) {
                    view = this.createView(viewName, locale);
                    if (view == null && this.cacheUnresolved) {
                        view = UNRESOLVED_VIEW;
                    }
                    if (view != null && this.cacheFilter.filter(view, viewName, locale)) {
                        this.viewAccessCache.put(cacheKey, view);
                        this.viewCreationCache.put(cacheKey, view);
                    }
                }
            }
        } else if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)(AbstractCachingViewResolver.formatKey(cacheKey) + "served from cache"));
        }
        return view != UNRESOLVED_VIEW ? view : null;
    }

    private static String formatKey(Object cacheKey) {
        return "View with key [" + cacheKey + "] ";
    }

    protected Object getCacheKey(String viewName, Locale locale) {
        return viewName + '_' + locale;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeFromCache(String viewName, Locale locale) {
        if (!this.isCache()) {
            this.logger.warn((Object)"Caching is OFF (removal not necessary)");
        } else {
            View cachedView;
            Object cacheKey = this.getCacheKey(viewName, locale);
            Map<Object, View> map = this.viewCreationCache;
            synchronized (map) {
                this.viewAccessCache.remove(cacheKey);
                cachedView = this.viewCreationCache.remove(cacheKey);
            }
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)(AbstractCachingViewResolver.formatKey(cacheKey) + (cachedView != null ? "cleared from cache" : "not found in the cache")));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clearCache() {
        this.logger.debug((Object)"Clearing all views from the cache");
        Map<Object, View> map = this.viewCreationCache;
        synchronized (map) {
            this.viewAccessCache.clear();
            this.viewCreationCache.clear();
        }
    }

    @Nullable
    protected View createView(String viewName, Locale locale) throws Exception {
        return this.loadView(viewName, locale);
    }

    @Nullable
    protected abstract View loadView(String var1, Locale var2) throws Exception;

    @FunctionalInterface
    public static interface CacheFilter {
        public boolean filter(View var1, String var2, Locale var3);
    }
}

