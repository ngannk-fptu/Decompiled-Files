/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.io;

import org.apache.jackrabbit.server.io.DeleteContext;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;

public interface DeleteHandler {
    public boolean delete(DeleteContext var1, DavResource var2) throws DavException;

    public boolean canDelete(DeleteContext var1, DavResource var2);
}

