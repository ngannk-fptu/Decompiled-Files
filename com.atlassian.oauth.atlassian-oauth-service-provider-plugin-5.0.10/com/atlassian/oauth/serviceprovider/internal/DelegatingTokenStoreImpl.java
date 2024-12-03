/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.serviceprovider.InvalidTokenException
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken
 *  com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore
 *  com.atlassian.oauth.serviceprovider.StoreException
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.oauth.serviceprovider.internal;

import com.atlassian.oauth.serviceprovider.InvalidTokenException;
import com.atlassian.oauth.serviceprovider.ServiceProviderToken;
import com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore;
import com.atlassian.oauth.serviceprovider.StoreException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component(value="tokenStore")
public class DelegatingTokenStoreImpl
implements ServiceProviderTokenStore {
    private final ServiceProviderTokenStore store;

    public DelegatingTokenStoreImpl(@Qualifier(value="delegateTokenStore") ServiceProviderTokenStore store) {
        this.store = store;
    }

    public ServiceProviderToken get(String token) throws StoreException {
        try {
            return this.store.get(token);
        }
        catch (InvalidTokenException e) {
            this.store.removeAndNotify(token);
            throw e;
        }
    }

    public ServiceProviderToken put(ServiceProviderToken token) throws StoreException {
        return this.store.put(token);
    }

    public void removeAndNotify(String token) throws StoreException {
        this.store.removeAndNotify(token);
    }

    public void removeExpiredTokensAndNotify() throws StoreException {
        this.store.removeExpiredTokensAndNotify();
    }

    public void removeExpiredSessionsAndNotify() throws StoreException {
        this.store.removeExpiredSessionsAndNotify();
    }

    public Iterable<ServiceProviderToken> getAccessTokensForUser(String username) {
        return this.store.getAccessTokensForUser(username);
    }

    public void removeByConsumer(String consumerKey) {
        this.store.removeByConsumer(consumerKey);
    }
}

