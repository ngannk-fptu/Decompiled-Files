/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.observation;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.observation.EventDiscovery;
import org.apache.jackrabbit.webdav.observation.Subscription;
import org.apache.jackrabbit.webdav.observation.SubscriptionInfo;
import org.apache.jackrabbit.webdav.observation.SubscriptionManager;

public interface ObservationResource
extends DavResource {
    public static final String METHODS = "SUBSCRIBE, UNSUBSCRIBE, POLL";

    public void init(SubscriptionManager var1);

    public Subscription subscribe(SubscriptionInfo var1, String var2) throws DavException;

    public void unsubscribe(String var1) throws DavException;

    public EventDiscovery poll(String var1, long var2) throws DavException;
}

