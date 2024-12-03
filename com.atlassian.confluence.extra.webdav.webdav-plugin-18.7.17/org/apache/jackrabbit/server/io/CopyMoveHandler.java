/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.io;

import org.apache.jackrabbit.server.io.CopyMoveContext;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;

public interface CopyMoveHandler {
    public boolean canCopy(CopyMoveContext var1, DavResource var2, DavResource var3);

    public boolean copy(CopyMoveContext var1, DavResource var2, DavResource var3) throws DavException;

    public boolean canMove(CopyMoveContext var1, DavResource var2, DavResource var3);

    public boolean move(CopyMoveContext var1, DavResource var2, DavResource var3) throws DavException;
}

