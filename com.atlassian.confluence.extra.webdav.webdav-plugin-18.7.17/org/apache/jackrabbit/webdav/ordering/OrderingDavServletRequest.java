/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.ordering;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavServletRequest;
import org.apache.jackrabbit.webdav.ordering.OrderPatch;
import org.apache.jackrabbit.webdav.ordering.Position;

public interface OrderingDavServletRequest
extends DavServletRequest {
    public String getOrderingType();

    public Position getPosition();

    public OrderPatch getOrderPatch() throws DavException;
}

