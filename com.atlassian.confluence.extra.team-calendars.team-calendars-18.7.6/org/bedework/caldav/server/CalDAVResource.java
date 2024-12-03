/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.server;

import java.io.InputStream;
import org.bedework.caldav.util.notifications.NotificationType;
import org.bedework.webdav.servlet.shared.WdEntity;
import org.bedework.webdav.servlet.shared.WebdavException;

public abstract class CalDAVResource<T>
extends WdEntity<T> {
    public abstract boolean isNew() throws WebdavException;

    public abstract boolean getDeleted() throws WebdavException;

    public abstract void setBinaryContent(InputStream var1) throws WebdavException;

    public abstract InputStream getBinaryContent() throws WebdavException;

    public abstract long getContentLen() throws WebdavException;

    public abstract long getQuotaSize() throws WebdavException;

    public abstract void setContentType(String var1) throws WebdavException;

    public abstract String getContentType() throws WebdavException;

    public abstract NotificationType.NotificationInfo getNotificationType() throws WebdavException;
}

