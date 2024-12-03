/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.observation;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.observation.EventDiscovery;
import org.apache.jackrabbit.webdav.observation.ObservationResource;
import org.apache.jackrabbit.webdav.observation.Subscription;
import org.apache.jackrabbit.webdav.observation.SubscriptionDiscovery;
import org.apache.jackrabbit.webdav.observation.SubscriptionInfo;

public interface SubscriptionManager {
    public SubscriptionDiscovery getSubscriptionDiscovery(ObservationResource var1);

    public Subscription subscribe(SubscriptionInfo var1, String var2, ObservationResource var3) throws DavException;

    public void unsubscribe(String var1, ObservationResource var2) throws DavException;

    public EventDiscovery poll(String var1, long var2, ObservationResource var4) throws DavException;
}

