/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav;

import org.apache.jackrabbit.webdav.DavServletRequest;
import org.apache.jackrabbit.webdav.bind.BindServletRequest;
import org.apache.jackrabbit.webdav.observation.ObservationDavServletRequest;
import org.apache.jackrabbit.webdav.ordering.OrderingDavServletRequest;
import org.apache.jackrabbit.webdav.transaction.TransactionDavServletRequest;
import org.apache.jackrabbit.webdav.version.DeltaVServletRequest;

public interface WebdavRequest
extends DavServletRequest,
ObservationDavServletRequest,
OrderingDavServletRequest,
TransactionDavServletRequest,
DeltaVServletRequest,
BindServletRequest {
}

