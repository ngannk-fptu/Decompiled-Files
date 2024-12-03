/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.ordering;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.ordering.OrderPatch;

public interface OrderingResource
extends DavResource {
    public static final String METHODS = "ORDERPATCH";

    public boolean isOrderable();

    public void orderMembers(OrderPatch var1) throws DavException;
}

