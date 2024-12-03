/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.apache.jackrabbit.webdav;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.lock.LockInfo;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.PropEntry;
import org.w3c.dom.Document;

public interface DavServletRequest
extends HttpServletRequest {
    public void setDavSession(DavSession var1);

    public DavSession getDavSession();

    public DavResourceLocator getRequestLocator();

    public DavResourceLocator getDestinationLocator() throws DavException;

    public boolean isOverwrite();

    public int getDepth();

    public int getDepth(int var1);

    public String getLockToken();

    public long getTimeout();

    public Document getRequestDocument() throws DavException;

    public int getPropFindType() throws DavException;

    public DavPropertyNameSet getPropFindProperties() throws DavException;

    public List<? extends PropEntry> getPropPatchChangeList() throws DavException;

    public LockInfo getLockInfo() throws DavException;

    public boolean matchesIfHeader(DavResource var1);

    public boolean matchesIfHeader(String var1, String var2, String var3);
}

