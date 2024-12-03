/*
 * Decompiled with CFR 0.152.
 */
package javax.ws.rs.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.ext.RuntimeDelegate;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CacheControl {
    private boolean _private = false;
    private List<String> privateFields;
    private boolean noCache = false;
    private List<String> noCacheFields;
    private boolean noStore = false;
    private boolean noTransform = true;
    private boolean mustRevalidate = false;
    private boolean proxyRevalidate = false;
    private int maxAge = -1;
    private int sMaxAge = -1;
    private Map<String, String> cacheExtension;
    private static final RuntimeDelegate.HeaderDelegate<CacheControl> delegate = RuntimeDelegate.getInstance().createHeaderDelegate(CacheControl.class);

    public static CacheControl valueOf(String value) throws IllegalArgumentException {
        return delegate.fromString(value);
    }

    public boolean isMustRevalidate() {
        return this.mustRevalidate;
    }

    public void setMustRevalidate(boolean mustRevalidate) {
        this.mustRevalidate = mustRevalidate;
    }

    public boolean isProxyRevalidate() {
        return this.proxyRevalidate;
    }

    public void setProxyRevalidate(boolean proxyRevalidate) {
        this.proxyRevalidate = proxyRevalidate;
    }

    public int getMaxAge() {
        return this.maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public int getSMaxAge() {
        return this.sMaxAge;
    }

    public void setSMaxAge(int sMaxAge) {
        this.sMaxAge = sMaxAge;
    }

    public List<String> getNoCacheFields() {
        if (this.noCacheFields == null) {
            this.noCacheFields = new ArrayList<String>();
        }
        return this.noCacheFields;
    }

    public void setNoCache(boolean noCache) {
        this.noCache = noCache;
    }

    public boolean isNoCache() {
        return this.noCache;
    }

    public boolean isPrivate() {
        return this._private;
    }

    public List<String> getPrivateFields() {
        if (this.privateFields == null) {
            this.privateFields = new ArrayList<String>();
        }
        return this.privateFields;
    }

    public void setPrivate(boolean _private) {
        this._private = _private;
    }

    public boolean isNoTransform() {
        return this.noTransform;
    }

    public void setNoTransform(boolean noTransform) {
        this.noTransform = noTransform;
    }

    public boolean isNoStore() {
        return this.noStore;
    }

    public void setNoStore(boolean noStore) {
        this.noStore = noStore;
    }

    public Map<String, String> getCacheExtension() {
        if (this.cacheExtension == null) {
            this.cacheExtension = new HashMap<String, String>();
        }
        return this.cacheExtension;
    }

    public String toString() {
        return delegate.toString(this);
    }

    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this._private ? 1 : 0);
        hash = 41 * hash + (this.privateFields != null ? ((Object)this.privateFields).hashCode() : 0);
        hash = 41 * hash + (this.noCache ? 1 : 0);
        hash = 41 * hash + (this.noCacheFields != null ? ((Object)this.noCacheFields).hashCode() : 0);
        hash = 41 * hash + (this.noStore ? 1 : 0);
        hash = 41 * hash + (this.noTransform ? 1 : 0);
        hash = 41 * hash + (this.mustRevalidate ? 1 : 0);
        hash = 41 * hash + (this.proxyRevalidate ? 1 : 0);
        hash = 41 * hash + this.maxAge;
        hash = 41 * hash + this.sMaxAge;
        hash = 41 * hash + (this.cacheExtension != null ? ((Object)this.cacheExtension).hashCode() : 0);
        return hash;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        CacheControl other = (CacheControl)obj;
        if (this._private != other._private) {
            return false;
        }
        if (!(this.privateFields == other.privateFields || this.privateFields != null && ((Object)this.privateFields).equals(other.privateFields))) {
            return false;
        }
        if (this.noCache != other.noCache) {
            return false;
        }
        if (!(this.noCacheFields == other.noCacheFields || this.noCacheFields != null && ((Object)this.noCacheFields).equals(other.noCacheFields))) {
            return false;
        }
        if (this.noStore != other.noStore) {
            return false;
        }
        if (this.noTransform != other.noTransform) {
            return false;
        }
        if (this.mustRevalidate != other.mustRevalidate) {
            return false;
        }
        if (this.proxyRevalidate != other.proxyRevalidate) {
            return false;
        }
        if (this.maxAge != other.maxAge) {
            return false;
        }
        if (this.sMaxAge != other.sMaxAge) {
            return false;
        }
        return this.cacheExtension == other.cacheExtension || this.cacheExtension != null && ((Object)this.cacheExtension).equals(other.cacheExtension);
    }
}

