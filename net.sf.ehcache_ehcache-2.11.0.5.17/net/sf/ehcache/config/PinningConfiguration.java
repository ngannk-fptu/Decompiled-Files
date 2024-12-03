/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config;

public class PinningConfiguration
implements Cloneable {
    private volatile Store store;

    public void setStore(String store) {
        if (store == null) {
            throw new IllegalArgumentException("Store must be non-null");
        }
        this.store(Store.valueOf(Store.class, store.toUpperCase()));
    }

    public PinningConfiguration store(String store) {
        this.setStore(store);
        return this;
    }

    public PinningConfiguration store(Store store) {
        if (store == null) {
            throw new IllegalArgumentException("Store must be non-null");
        }
        this.store = store;
        return this;
    }

    public Store getStore() {
        return this.store;
    }

    public static enum Store {
        LOCALMEMORY,
        INCACHE;

    }
}

