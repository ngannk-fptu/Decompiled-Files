/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.Consumer
 */
package com.atlassian.oauth.serviceprovider;

import com.atlassian.oauth.Consumer;
import com.atlassian.oauth.serviceprovider.StoreException;

public interface ServiceProviderConsumerStore {
    public void put(Consumer var1) throws StoreException;

    public Consumer get(String var1) throws StoreException;

    public void remove(String var1) throws StoreException;

    public Iterable<Consumer> getAll() throws StoreException;
}

