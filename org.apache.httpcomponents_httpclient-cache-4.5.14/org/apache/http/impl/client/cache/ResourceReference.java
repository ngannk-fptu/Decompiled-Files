/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.util.Args
 */
package org.apache.http.impl.client.cache;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.Resource;
import org.apache.http.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
class ResourceReference
extends PhantomReference<HttpCacheEntry> {
    private final Resource resource;

    public ResourceReference(HttpCacheEntry entry, ReferenceQueue<HttpCacheEntry> q) {
        super(entry, q);
        Args.notNull((Object)entry.getResource(), (String)"Resource");
        this.resource = entry.getResource();
    }

    public Resource getResource() {
        return this.resource;
    }

    public int hashCode() {
        return this.resource.hashCode();
    }

    public boolean equals(Object obj) {
        return this.resource.equals(obj);
    }
}

