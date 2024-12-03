/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth.serviceprovider;

import com.atlassian.oauth.serviceprovider.ServiceProviderToken;
import com.atlassian.oauth.serviceprovider.StoreException;

public interface ServiceProviderTokenStore {
    public ServiceProviderToken get(String var1) throws StoreException;

    public Iterable<ServiceProviderToken> getAccessTokensForUser(String var1);

    public ServiceProviderToken put(ServiceProviderToken var1) throws StoreException;

    public void removeAndNotify(String var1) throws StoreException;

    public void removeExpiredTokensAndNotify() throws StoreException;

    public void removeExpiredSessionsAndNotify() throws StoreException;

    public void removeByConsumer(String var1);
}

