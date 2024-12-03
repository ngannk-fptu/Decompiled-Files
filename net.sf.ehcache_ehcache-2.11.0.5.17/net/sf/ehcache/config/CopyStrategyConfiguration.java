/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config;

import net.sf.ehcache.Element;
import net.sf.ehcache.store.compound.CopyStrategy;
import net.sf.ehcache.store.compound.LegacyCopyStrategyAdapter;
import net.sf.ehcache.store.compound.ReadWriteCopyStrategy;

public class CopyStrategyConfiguration {
    private static final String DEFAULT_IMPL = "net.sf.ehcache.store.compound.ReadWriteSerializationCopyStrategy";
    private volatile String className = "net.sf.ehcache.store.compound.ReadWriteSerializationCopyStrategy";
    private ReadWriteCopyStrategy<Element> strategy;

    public String getClassName() {
        return this.className;
    }

    public void setClass(String className) {
        this.className = className;
    }

    public synchronized void setCopyStrategyInstance(ReadWriteCopyStrategy<Element> copyStrategy) {
        this.strategy = copyStrategy;
    }

    public synchronized ReadWriteCopyStrategy<Element> getCopyStrategyInstance(ClassLoader loader) {
        if (this.strategy == null) {
            Class<?> copyStrategy = null;
            try {
                Object strategyObject;
                if (DEFAULT_IMPL.equals(this.className)) {
                    loader = this.getClass().getClassLoader();
                }
                this.strategy = (strategyObject = (copyStrategy = loader.loadClass(this.className)).newInstance()) instanceof CopyStrategy ? new LegacyCopyStrategyAdapter((CopyStrategy)strategyObject) : (ReadWriteCopyStrategy)strategyObject;
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException("Couldn't find the CopyStrategy class!", e);
            }
            catch (InstantiationException e) {
                throw new RuntimeException("Couldn't instantiate the CopyStrategy instance!", e);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException("Couldn't instantiate the CopyStrategy instance!", e);
            }
            catch (ClassCastException e) {
                throw new RuntimeException((String)(copyStrategy != null ? copyStrategy.getSimpleName() + " doesn't implement net.sf.ehcache.store.compound.CopyStrategy" : "Error with CopyStrategy"), e);
            }
        }
        return this.strategy;
    }

    protected CopyStrategyConfiguration copy() {
        CopyStrategyConfiguration clone = new CopyStrategyConfiguration();
        clone.setClass(this.getClassName());
        return clone;
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
        CopyStrategyConfiguration other = (CopyStrategyConfiguration)obj;
        return !(this.className == null ? other.className != null : !this.className.equals(other.className));
    }
}

