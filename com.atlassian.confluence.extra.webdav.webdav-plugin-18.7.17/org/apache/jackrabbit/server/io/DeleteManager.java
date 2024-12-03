/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.io;

import org.apache.jackrabbit.server.io.DeleteContext;
import org.apache.jackrabbit.server.io.DeleteHandler;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;

public interface DeleteManager {
    public boolean delete(DeleteContext var1, DavResource var2) throws DavException;

    public void addDeleteHandler(DeleteHandler var1);

    public DeleteHandler[] getDeleteHandlers();
}

