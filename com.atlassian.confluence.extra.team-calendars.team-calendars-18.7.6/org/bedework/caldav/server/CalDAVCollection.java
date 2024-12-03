/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.server;

import java.util.List;
import org.bedework.webdav.servlet.shared.WdCollection;
import org.bedework.webdav.servlet.shared.WebdavException;

public abstract class CalDAVCollection<T extends CalDAVCollection>
extends WdCollection<T> {
    public static final int calTypeUnknown = -1;
    public static final int calTypeCollection = 0;
    public static final int calTypeCalendarCollection = 1;
    public static final int calTypeInbox = 2;
    public static final int calTypeOutbox = 3;
    public static final int calTypeNotifications = 4;

    @Override
    public abstract T resolveAlias(boolean var1) throws WebdavException;

    public abstract void setCalType(int var1) throws WebdavException;

    public abstract int getCalType() throws WebdavException;

    public abstract boolean freebusyAllowed() throws WebdavException;

    public abstract boolean getDeleted() throws WebdavException;

    public abstract boolean entitiesAllowed() throws WebdavException;

    public abstract void setAffectsFreeBusy(boolean var1) throws WebdavException;

    public abstract boolean getAffectsFreeBusy() throws WebdavException;

    public abstract void setTimezone(String var1) throws WebdavException;

    public abstract String getTimezone() throws WebdavException;

    public abstract void setColor(String var1) throws WebdavException;

    public abstract String getColor() throws WebdavException;

    public abstract void setAliasUri(String var1) throws WebdavException;

    @Override
    public abstract String getAliasUri() throws WebdavException;

    public abstract void setRefreshRate(int var1) throws WebdavException;

    public abstract int getRefreshRate() throws WebdavException;

    public abstract void setRemoteId(String var1) throws WebdavException;

    public abstract String getRemoteId() throws WebdavException;

    public abstract void setRemotePw(String var1) throws WebdavException;

    public abstract String getRemotePw() throws WebdavException;

    public abstract void setSynchDeleteSuppressed(boolean var1) throws WebdavException;

    public abstract boolean getSynchDeleteSuppressed() throws WebdavException;

    public abstract void setSupportedComponents(List<String> var1) throws WebdavException;

    public abstract List<String> getSupportedComponents() throws WebdavException;

    public abstract List<String> getVpollSupportedComponents() throws WebdavException;
}

