/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.config.TimeoutBehaviorConfiguration;

public class NonstopConfiguration
implements Cloneable {
    public static final String BULK_OPS_TIMEOUT_MULTIPLY_FACTOR = "net.sf.ehcache.nonstop.bulkOpsTimeoutMultiplyFactor";
    public static final boolean DEFAULT_ENABLED = true;
    public static final boolean DEFAULT_IMMEDIATE_TIMEOUT = false;
    public static final int DEFAULT_TIMEOUT_MILLIS = 30000;
    public static final int DEFAULT_SEARCH_TIMEOUT_MILLIS = 30000;
    public static final int DEFAULT_BULK_OP_TIMEOUT_FACTOR = Integer.getInteger("net.sf.ehcache.nonstop.bulkOpsTimeoutMultiplyFactor", 10);
    public static final TimeoutBehaviorConfiguration DEFAULT_TIMEOUT_BEHAVIOR = new TimeoutBehaviorConfiguration();
    private volatile boolean enabled = true;
    private volatile boolean immediateTimeout = false;
    private volatile long timeoutMillis = 30000L;
    private volatile long searchTimeoutMillis = 30000L;
    private volatile int bulkOpsTimeoutMultiplyFactor = DEFAULT_BULK_OP_TIMEOUT_FACTOR;
    private TimeoutBehaviorConfiguration timeoutBehavior = new TimeoutBehaviorConfiguration(DEFAULT_TIMEOUT_BEHAVIOR);
    private volatile boolean configFrozen;

    public void freezeConfig() {
        this.configFrozen = true;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.configFrozen) {
            throw new CacheException("NonstopConfiguration cannot be enabled or disabled after Cache has been initialized.");
        }
        this.enabled = enabled;
    }

    public NonstopConfiguration enabled(boolean nonstop) {
        this.setEnabled(nonstop);
        return this;
    }

    public boolean isImmediateTimeout() {
        return this.immediateTimeout;
    }

    public void setImmediateTimeout(boolean immediateTimeout) {
        this.immediateTimeout = immediateTimeout;
    }

    public NonstopConfiguration immediateTimeout(boolean immediateTimeout) {
        this.setImmediateTimeout(immediateTimeout);
        return this;
    }

    public long getTimeoutMillis() {
        return this.timeoutMillis;
    }

    public void setTimeoutMillis(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    public long getSearchTimeoutMillis() {
        return this.searchTimeoutMillis;
    }

    public void setSearchTimeoutMillis(long searchTimeoutMillis) {
        this.searchTimeoutMillis = searchTimeoutMillis;
    }

    public int getBulkOpsTimeoutMultiplyFactor() {
        return this.bulkOpsTimeoutMultiplyFactor;
    }

    public void setBulkOpsTimeoutMultiplyFactor(int bulkOpsTimeoutMultiplyFactor) {
        this.bulkOpsTimeoutMultiplyFactor = bulkOpsTimeoutMultiplyFactor;
    }

    public NonstopConfiguration timeoutMillis(long timeoutMillis) {
        this.setTimeoutMillis(timeoutMillis);
        return this;
    }

    public NonstopConfiguration searchTimeoutMillis(long searchTimeoutMillis) {
        this.setSearchTimeoutMillis(searchTimeoutMillis);
        return this;
    }

    public TimeoutBehaviorConfiguration getTimeoutBehavior() {
        return this.timeoutBehavior;
    }

    public void addTimeoutBehavior(TimeoutBehaviorConfiguration timeoutBehavior) {
        this.timeoutBehavior = timeoutBehavior;
    }

    public NonstopConfiguration timeoutBehavior(TimeoutBehaviorConfiguration timeoutBehavior) {
        this.addTimeoutBehavior(timeoutBehavior);
        return this;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.bulkOpsTimeoutMultiplyFactor;
        result = 31 * result + (this.configFrozen ? 1231 : 1237);
        result = 31 * result + (this.enabled ? 1231 : 1237);
        result = 31 * result + (this.immediateTimeout ? 1231 : 1237);
        result = 31 * result + (this.timeoutBehavior == null ? 0 : this.timeoutBehavior.hashCode());
        result = 31 * result + (int)(this.timeoutMillis ^ this.timeoutMillis >>> 32);
        result = 31 * result + (int)(this.searchTimeoutMillis ^ this.searchTimeoutMillis >>> 32);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        NonstopConfiguration other = (NonstopConfiguration)obj;
        if (this.bulkOpsTimeoutMultiplyFactor != other.bulkOpsTimeoutMultiplyFactor || this.configFrozen != other.configFrozen || this.enabled != other.enabled || this.immediateTimeout != other.immediateTimeout || this.searchTimeoutMillis != other.searchTimeoutMillis || this.timeoutMillis != other.timeoutMillis) {
            return false;
        }
        return !(this.timeoutBehavior == null ? other.timeoutBehavior != null : !this.timeoutBehavior.equals(other.timeoutBehavior));
    }

    public NonstopConfiguration clone() throws CloneNotSupportedException {
        try {
            NonstopConfiguration clone = (NonstopConfiguration)super.clone();
            clone.addTimeoutBehavior((TimeoutBehaviorConfiguration)this.timeoutBehavior.clone());
            return clone;
        }
        catch (CloneNotSupportedException e) {
            throw new CacheException(e);
        }
    }
}

