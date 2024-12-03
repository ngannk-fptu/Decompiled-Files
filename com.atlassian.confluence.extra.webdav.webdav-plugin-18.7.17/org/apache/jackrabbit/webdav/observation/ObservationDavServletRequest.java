/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.observation;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavServletRequest;
import org.apache.jackrabbit.webdav.observation.SubscriptionInfo;

public interface ObservationDavServletRequest
extends DavServletRequest {
    public String getSubscriptionId();

    public long getPollTimeout();

    public SubscriptionInfo getSubscriptionInfo() throws DavException;
}

