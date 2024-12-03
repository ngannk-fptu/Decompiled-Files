/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.webdav.servlet.shared;

import org.bedework.webdav.servlet.shared.UrlHandler;
import org.bedework.webdav.servlet.shared.WdCollection;
import org.bedework.webdav.servlet.shared.WebdavException;

public interface WdSysIntf {
    public UrlHandler getUrlHandler();

    public boolean allowsSyncReport(WdCollection var1) throws WebdavException;

    public String getDefaultContentType();

    public String getNotificationURL() throws WebdavException;
}

