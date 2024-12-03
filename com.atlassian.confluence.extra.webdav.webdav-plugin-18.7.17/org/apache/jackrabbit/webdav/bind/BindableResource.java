/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.bind;

import java.util.Set;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.bind.ParentElement;

public interface BindableResource {
    public void bind(DavResource var1, DavResource var2) throws DavException;

    public void rebind(DavResource var1, DavResource var2) throws DavException;

    public Set<ParentElement> getParentElements();
}

