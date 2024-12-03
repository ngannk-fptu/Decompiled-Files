/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.observation;

import java.io.IOException;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.observation.EventDiscovery;
import org.apache.jackrabbit.webdav.observation.Subscription;

public interface ObservationDavServletResponse
extends DavServletResponse {
    public void sendSubscriptionResponse(Subscription var1) throws IOException;

    public void sendPollResponse(EventDiscovery var1) throws IOException;
}

