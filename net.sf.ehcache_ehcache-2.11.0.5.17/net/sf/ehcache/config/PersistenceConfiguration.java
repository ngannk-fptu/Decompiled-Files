/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config;

public class PersistenceConfiguration {
    public static final boolean DEFAULT_SYNCHRONOUS_WRITES = false;
    private volatile Strategy strategy;
    private volatile boolean synchronousWrites;

    public Strategy getStrategy() {
        return this.strategy;
    }

    public void setStrategy(String strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("strategy must be non-null");
        }
        this.strategy(Strategy.valueOf(strategy.toUpperCase()));
    }

    public PersistenceConfiguration strategy(Strategy strategy) {
        this.strategy = strategy;
        return this;
    }

    public PersistenceConfiguration strategy(String strategy) {
        this.setStrategy(strategy);
        return this;
    }

    public boolean getSynchronousWrites() {
        return this.synchronousWrites;
    }

    public void setSynchronousWrites(boolean synchronousWrites) {
        this.synchronousWrites = synchronousWrites;
    }

    public PersistenceConfiguration synchronousWrites(boolean synchronousWrites) {
        this.setSynchronousWrites(synchronousWrites);
        return this;
    }

    public static enum Strategy {
        LOCALTEMPSWAP,
        LOCALRESTARTABLE,
        NONE,
        DISTRIBUTED;

    }
}

