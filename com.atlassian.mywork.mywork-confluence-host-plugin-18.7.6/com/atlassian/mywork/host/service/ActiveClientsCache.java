/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.cache.Cache
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Sets
 *  javax.annotation.concurrent.NotThreadSafe
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.mywork.host.service;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.cache.Cache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.concurrent.NotThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NotThreadSafe
class ActiveClientsCache
implements Iterable<ApplicationId> {
    private static final Logger log = LoggerFactory.getLogger(ActiveClientsCache.class);
    private static final String KEY = "ActiveClients";
    private final Cache<String, Set<String>> cache;

    public ActiveClientsCache(Cache<String, Set<String>> cache) {
        this.cache = cache;
    }

    public boolean isInitialized() {
        return this.cache.containsKey((Object)KEY);
    }

    private Set<String> getHostIds() {
        Set hostIds = (Set)this.cache.get((Object)KEY);
        return hostIds == null ? Sets.newHashSet() : Sets.newHashSet((Iterable)hostIds);
    }

    private void setHostIds(Iterable<String> hostIds) {
        this.cache.put((Object)KEY, (Object)ImmutableSet.copyOf(hostIds));
    }

    public void add(ApplicationId applicationId) {
        if (this.isInitialized()) {
            Set<String> hostIds = this.getHostIds();
            hostIds.add(applicationId.get());
            log.debug("Adding {} to ActiveClients, set is now {}", (Object)applicationId, hostIds);
            this.setHostIds(hostIds);
        } else {
            log.debug("ActiveClients cache is not initialized, re-initializing with {}", (Object)applicationId);
            this.setHostIds(Collections.singleton(applicationId.get()));
        }
    }

    public void remove(ApplicationId applicationId) {
        if (this.isInitialized()) {
            HashSet hostIds = Sets.newHashSet(this.getHostIds());
            hostIds.remove(applicationId.get());
            log.debug("Removing {} from ActiveClients, set is now {}", (Object)applicationId, (Object)hostIds);
            this.setHostIds(hostIds);
        } else {
            log.debug("ActiveClients cache is not initialized, skipping removal of {}", (Object)applicationId);
        }
    }

    @Override
    public Iterator<ApplicationId> iterator() {
        if (this.isInitialized()) {
            return Iterators.transform(this.getHostIds().iterator(), input -> new ApplicationId(input));
        }
        return Collections.emptyIterator();
    }
}

