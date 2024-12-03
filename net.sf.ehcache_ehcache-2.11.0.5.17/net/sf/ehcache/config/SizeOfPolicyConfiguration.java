/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;

public class SizeOfPolicyConfiguration
implements Cloneable {
    public static final int DEFAULT_MAX_SIZEOF_DEPTH = 1000;
    public static final MaxDepthExceededBehavior DEFAULT_MAX_DEPTH_EXCEEDED_BEHAVIOR = MaxDepthExceededBehavior.CONTINUE;
    private volatile int maxDepth = 1000;
    private volatile MaxDepthExceededBehavior maxDepthExceededBehavior = DEFAULT_MAX_DEPTH_EXCEEDED_BEHAVIOR;

    public int getMaxDepth() {
        return this.maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public SizeOfPolicyConfiguration maxDepth(int maxDepth) {
        this.setMaxDepth(maxDepth);
        return this;
    }

    public MaxDepthExceededBehavior getMaxDepthExceededBehavior() {
        return this.maxDepthExceededBehavior;
    }

    public void setMaxDepthExceededBehavior(String maxDepthExceededBehavior) {
        if (maxDepthExceededBehavior == null) {
            throw new IllegalArgumentException("maxDepthExceededBehavior must be non-null");
        }
        this.maxDepthExceededBehavior(MaxDepthExceededBehavior.valueOf(MaxDepthExceededBehavior.class, maxDepthExceededBehavior.toUpperCase()));
    }

    public SizeOfPolicyConfiguration maxDepthExceededBehavior(MaxDepthExceededBehavior maxDepthExceededBehavior) {
        this.maxDepthExceededBehavior = maxDepthExceededBehavior;
        return this;
    }

    public SizeOfPolicyConfiguration maxDepthExceededBehavior(String maxDepthExceededBehavior) {
        this.setMaxDepthExceededBehavior(maxDepthExceededBehavior);
        return this;
    }

    public static int resolveMaxDepth(Ehcache cache) {
        if (cache == null) {
            return 1000;
        }
        CacheManager cacheManager = cache.getCacheManager();
        return SizeOfPolicyConfiguration.resolvePolicy(cacheManager == null ? null : cacheManager.getConfiguration(), cache.getCacheConfiguration()).getMaxDepth();
    }

    public static MaxDepthExceededBehavior resolveBehavior(Ehcache cache) {
        if (cache == null) {
            return DEFAULT_MAX_DEPTH_EXCEEDED_BEHAVIOR;
        }
        CacheManager cacheManager = cache.getCacheManager();
        if (cacheManager == null) {
            return SizeOfPolicyConfiguration.resolvePolicy(null, cache.getCacheConfiguration()).getMaxDepthExceededBehavior();
        }
        return SizeOfPolicyConfiguration.resolvePolicy(cacheManager.getConfiguration(), cache.getCacheConfiguration()).getMaxDepthExceededBehavior();
    }

    private static SizeOfPolicyConfiguration resolvePolicy(Configuration configuration, CacheConfiguration cacheConfiguration) {
        SizeOfPolicyConfiguration sizeOfPolicyConfiguration = null;
        if (cacheConfiguration != null) {
            sizeOfPolicyConfiguration = cacheConfiguration.getSizeOfPolicyConfiguration();
        }
        if (sizeOfPolicyConfiguration == null) {
            sizeOfPolicyConfiguration = configuration != null ? configuration.getSizeOfPolicyConfiguration() : new SizeOfPolicyConfiguration();
        }
        return sizeOfPolicyConfiguration;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.maxDepth;
        result = 31 * result + (this.maxDepthExceededBehavior == null ? 0 : this.maxDepthExceededBehavior.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        SizeOfPolicyConfiguration other = (SizeOfPolicyConfiguration)obj;
        return this.maxDepth == other.maxDepth && this.maxDepthExceededBehavior == other.maxDepthExceededBehavior;
    }

    public static enum MaxDepthExceededBehavior {
        ABORT,
        CONTINUE;


        public boolean isAbort() {
            return this == ABORT;
        }

        public boolean isContinue() {
            return this == CONTINUE;
        }
    }
}

