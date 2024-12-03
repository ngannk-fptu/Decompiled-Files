/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.bind;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.bind.BindInfo;
import org.apache.jackrabbit.webdav.bind.RebindInfo;
import org.apache.jackrabbit.webdav.bind.UnbindInfo;

public interface BindServletRequest {
    public RebindInfo getRebindInfo() throws DavException;

    public UnbindInfo getUnbindInfo() throws DavException;

    public BindInfo getBindInfo() throws DavException;

    public DavResourceLocator getHrefLocator(String var1) throws DavException;

    public DavResourceLocator getMemberLocator(String var1);
}

