/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.pool.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.pool.Size;
import net.sf.ehcache.pool.SizeOfEngine;
import net.sf.ehcache.pool.sizeof.AgentSizeOf;
import net.sf.ehcache.pool.sizeof.MaxDepthExceededException;
import net.sf.ehcache.pool.sizeof.ReflectionSizeOf;
import net.sf.ehcache.pool.sizeof.SizeOf;
import net.sf.ehcache.pool.sizeof.UnsafeSizeOf;
import net.sf.ehcache.pool.sizeof.filter.AnnotationSizeOfFilter;
import net.sf.ehcache.pool.sizeof.filter.CombinationSizeOfFilter;
import net.sf.ehcache.pool.sizeof.filter.ResourceSizeOfFilter;
import net.sf.ehcache.pool.sizeof.filter.SizeOfFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSizeOfEngine
implements SizeOfEngine {
    public static final String USER_FILTER_RESOURCE = "net.sf.ehcache.sizeof.filter";
    private static final Logger LOG = LoggerFactory.getLogger((String)DefaultSizeOfEngine.class.getName());
    private static final String VERBOSE_DEBUG_LOGGING = "net.sf.ehcache.sizeof.verboseDebugLogging";
    private static final SizeOfFilter DEFAULT_FILTER;
    private static final boolean USE_VERBOSE_DEBUG_LOGGING;
    private final SizeOf sizeOf;
    private final int maxDepth;
    private final boolean abortWhenMaxDepthExceeded;

    public DefaultSizeOfEngine(int maxDepth, boolean abortWhenMaxDepthExceeded) {
        this(maxDepth, abortWhenMaxDepthExceeded, false);
    }

    public DefaultSizeOfEngine(int maxDepth, boolean abortWhenMaxDepthExceeded, boolean silent) {
        SizeOf bestSizeOf;
        this.maxDepth = maxDepth;
        this.abortWhenMaxDepthExceeded = abortWhenMaxDepthExceeded;
        try {
            bestSizeOf = new AgentSizeOf(DEFAULT_FILTER);
            if (!silent) {
                LOG.info("using Agent sizeof engine");
            }
        }
        catch (UnsupportedOperationException e) {
            try {
                bestSizeOf = new UnsafeSizeOf(DEFAULT_FILTER);
                if (!silent) {
                    LOG.info("using Unsafe sizeof engine");
                }
            }
            catch (UnsupportedOperationException f) {
                try {
                    bestSizeOf = new ReflectionSizeOf(DEFAULT_FILTER);
                    if (!silent) {
                        LOG.info("using Reflection sizeof engine");
                    }
                }
                catch (UnsupportedOperationException g) {
                    throw new CacheException("A suitable SizeOf engine could not be loaded: " + e + ", " + f + ", " + g);
                }
            }
        }
        this.sizeOf = bestSizeOf;
    }

    private DefaultSizeOfEngine(DefaultSizeOfEngine defaultSizeOfEngine, int maxDepth, boolean abortWhenMaxDepthExceeded) {
        this.sizeOf = defaultSizeOfEngine.sizeOf;
        this.maxDepth = maxDepth;
        this.abortWhenMaxDepthExceeded = abortWhenMaxDepthExceeded;
    }

    @Override
    public SizeOfEngine copyWith(int maxDepth, boolean abortWhenMaxDepthExceeded) {
        return new DefaultSizeOfEngine(this, maxDepth, abortWhenMaxDepthExceeded);
    }

    private static SizeOfFilter getUserFilter() {
        String userFilterProperty = System.getProperty(USER_FILTER_RESOURCE);
        if (userFilterProperty != null) {
            ArrayList<URL> filterUrls = new ArrayList<URL>();
            try {
                filterUrls.add(new URL(userFilterProperty));
            }
            catch (MalformedURLException e) {
                LOG.debug("MalformedURLException using {} as a URL", (Object)userFilterProperty);
            }
            try {
                filterUrls.add(new File(userFilterProperty).toURI().toURL());
            }
            catch (MalformedURLException e) {
                LOG.debug("MalformedURLException using {} as a file URL", (Object)userFilterProperty);
            }
            filterUrls.add(Thread.currentThread().getContextClassLoader().getResource(userFilterProperty));
            for (URL filterUrl : filterUrls) {
                try {
                    ResourceSizeOfFilter filter = new ResourceSizeOfFilter(filterUrl);
                    LOG.info("Using user supplied filter @ {}", (Object)filterUrl);
                    return filter;
                }
                catch (IOException e) {
                    LOG.debug("IOException while loading user size-of filter resource", (Throwable)e);
                }
            }
        }
        return null;
    }

    private static boolean getVerboseSizeOfDebugLogging() {
        String verboseString = System.getProperty(VERBOSE_DEBUG_LOGGING, "false").toLowerCase();
        return verboseString.equals("true");
    }

    @Override
    public Size sizeOf(Object key, Object value, Object container) {
        Size size;
        try {
            size = this.sizeOf.deepSizeOf(this.maxDepth, this.abortWhenMaxDepthExceeded, key, value, container);
        }
        catch (MaxDepthExceededException e) {
            LOG.warn(e.getMessage());
            LOG.warn("key type: {}", (Object)key.getClass().getName());
            LOG.warn("key: {}", key);
            LOG.warn("value type: {}", (Object)value.getClass().getName());
            LOG.warn("value: {}", value);
            LOG.warn("container: {}", container);
            size = new Size(e.getMeasuredSize(), false);
        }
        if (USE_VERBOSE_DEBUG_LOGGING && LOG.isDebugEnabled()) {
            LOG.debug("size of {}/{}/{} -> {}", new Object[]{key, value, container, size.getCalculated()});
        }
        return size;
    }

    static {
        ArrayList<SizeOfFilter> filters = new ArrayList<SizeOfFilter>();
        filters.add(new AnnotationSizeOfFilter());
        try {
            filters.add(new ResourceSizeOfFilter(SizeOfEngine.class.getResource("builtin-sizeof.filter")));
        }
        catch (IOException e) {
            LOG.warn("Built-in sizeof filter could not be loaded: {}", (Throwable)e);
        }
        SizeOfFilter userFilter = DefaultSizeOfEngine.getUserFilter();
        if (userFilter != null) {
            filters.add(userFilter);
        }
        DEFAULT_FILTER = new CombinationSizeOfFilter(filters.toArray(new SizeOfFilter[filters.size()]));
        USE_VERBOSE_DEBUG_LOGGING = DefaultSizeOfEngine.getVerboseSizeOfDebugLogging();
    }
}

