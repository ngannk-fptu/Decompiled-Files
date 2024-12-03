/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.DefaultElementValueComparator;
import net.sf.ehcache.store.ElementValueComparator;
import net.sf.ehcache.util.ClassLoaderUtil;

public class ElementValueComparatorConfiguration {
    private static final String DEFAULT_IMPL = DefaultElementValueComparator.class.getName();
    private volatile String className = DEFAULT_IMPL;

    public String getClassName() {
        return this.className;
    }

    public void setClass(String className) {
        this.className = className;
    }

    public ElementValueComparator createElementComparatorInstance(CacheConfiguration cacheConfiguration, ClassLoader loader) {
        try {
            if (DEFAULT_IMPL.equals(this.className)) {
                loader = this.getClass().getClassLoader();
            }
            return (ElementValueComparator)ClassLoaderUtil.createNewInstance(loader, this.className, new Class[]{CacheConfiguration.class}, new Object[]{cacheConfiguration});
        }
        catch (ClassCastException cce) {
            throw new CacheException(this.className + " must implement " + ElementValueComparator.class.getName(), cce);
        }
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.className == null ? 0 : this.className.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ElementValueComparatorConfiguration other = (ElementValueComparatorConfiguration)obj;
        return !(this.className == null ? other.className != null : !this.className.equals(other.className));
    }
}

