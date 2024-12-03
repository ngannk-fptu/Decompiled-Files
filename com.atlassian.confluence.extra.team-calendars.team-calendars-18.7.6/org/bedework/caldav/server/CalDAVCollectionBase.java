/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.server;

import org.bedework.caldav.server.CalDAVCollection;
import org.bedework.webdav.servlet.shared.WebdavException;

public abstract class CalDAVCollectionBase<T extends CalDAVCollectionBase>
extends CalDAVCollection<T> {
    private int calType;
    public static final int calTypeResource = 9;
    private final boolean freebusyAllowed;
    private boolean affectsFreeBusy = true;
    private String timezone;
    private String color;
    private String aliasUri;
    private String remoteId;
    private String remotePw;
    private boolean synchDeleteSuppressed;

    public CalDAVCollectionBase(int calType, boolean freebusyAllowed) throws WebdavException {
        this.calType = calType;
        this.freebusyAllowed = freebusyAllowed;
    }

    @Override
    public boolean isAlias() throws WebdavException {
        return false;
    }

    @Override
    public T resolveAlias(boolean resolveSubAlias) throws WebdavException {
        return null;
    }

    @Override
    public void setCalType(int val) throws WebdavException {
        this.calType = val;
    }

    @Override
    public int getCalType() throws WebdavException {
        return this.calType;
    }

    @Override
    public boolean freebusyAllowed() throws WebdavException {
        return this.freebusyAllowed;
    }

    @Override
    public boolean entitiesAllowed() throws WebdavException {
        return this.calType == 1 || this.calType == 2 || this.calType == 3;
    }

    @Override
    public void setAffectsFreeBusy(boolean val) throws WebdavException {
        this.affectsFreeBusy = val;
    }

    @Override
    public boolean getAffectsFreeBusy() throws WebdavException {
        return this.affectsFreeBusy;
    }

    @Override
    public void setTimezone(String val) throws WebdavException {
        this.timezone = val;
    }

    @Override
    public String getTimezone() throws WebdavException {
        return this.timezone;
    }

    @Override
    public void setColor(String val) throws WebdavException {
        this.color = val;
    }

    @Override
    public String getColor() throws WebdavException {
        return this.color;
    }

    @Override
    public void setAliasUri(String val) throws WebdavException {
        this.aliasUri = val;
    }

    @Override
    public String getAliasUri() throws WebdavException {
        return this.aliasUri;
    }

    @Override
    public void setRemoteId(String val) throws WebdavException {
        this.remoteId = val;
    }

    @Override
    public String getRemoteId() throws WebdavException {
        return this.remoteId;
    }

    @Override
    public void setRemotePw(String val) throws WebdavException {
        this.remotePw = val;
    }

    @Override
    public String getRemotePw() throws WebdavException {
        return this.remotePw;
    }

    @Override
    public void setSynchDeleteSuppressed(boolean val) {
        this.synchDeleteSuppressed = val;
    }

    @Override
    public boolean getSynchDeleteSuppressed() {
        return this.synchDeleteSuppressed;
    }
}

