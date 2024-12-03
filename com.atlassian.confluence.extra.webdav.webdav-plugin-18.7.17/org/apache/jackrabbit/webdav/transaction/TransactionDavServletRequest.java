/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.transaction;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavServletRequest;
import org.apache.jackrabbit.webdav.transaction.TransactionInfo;

public interface TransactionDavServletRequest
extends DavServletRequest {
    public TransactionInfo getTransactionInfo() throws DavException;

    public String getTransactionId();
}

