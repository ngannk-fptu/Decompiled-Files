/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavServletRequest;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.DavSession;

public interface DavResourceFactory {
    public DavResource createResource(DavResourceLocator var1, DavServletRequest var2, DavServletResponse var3) throws DavException;

    public DavResource createResource(DavResourceLocator var1, DavSession var2) throws DavException;
}

