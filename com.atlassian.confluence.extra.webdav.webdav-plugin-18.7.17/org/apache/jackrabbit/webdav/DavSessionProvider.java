/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.WebdavRequest;

public interface DavSessionProvider {
    public boolean attachSession(WebdavRequest var1) throws DavException;

    public void releaseSession(WebdavRequest var1);
}

