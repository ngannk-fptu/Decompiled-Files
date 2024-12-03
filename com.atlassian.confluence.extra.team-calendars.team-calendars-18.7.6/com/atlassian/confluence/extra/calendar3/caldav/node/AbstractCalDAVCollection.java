/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 */
package com.atlassian.confluence.extra.calendar3.caldav.node;

import com.atlassian.confluence.extra.calendar3.caldav.CalendarAccessPrincipal;
import com.atlassian.confluence.extra.calendar3.caldav.node.freebusy.CalDavNodeFreeBusySupport;
import java.util.Collections;
import java.util.List;
import javax.xml.namespace.QName;
import org.bedework.caldav.server.CalDAVCollection;
import org.bedework.util.timezones.DateTimeUtil;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public abstract class AbstractCalDAVCollection<TYPE extends CalDAVCollection>
extends CalDAVCollection<TYPE> {
    private static final String VEVENT_COMPONENT = "VEVENT";
    private int calType;
    private final CalDavNodeFreeBusySupport freeBusySupport;

    AbstractCalDAVCollection(String path, CalendarAccessPrincipal owner, int calType, CalDavNodeFreeBusySupport freeBusySupport) throws WebdavException {
        this.setPath(path);
        this.setOwner(owner);
        this.setCalType(calType);
        this.setLastmod(DateTimeUtil.isoDateTimeUTC(DateTime.now((DateTimeZone)DateTimeZone.UTC).toDate()));
        this.freeBusySupport = freeBusySupport;
    }

    @Override
    public final boolean freebusyAllowed() throws WebdavException {
        return this.freeBusySupport.freeBusyAllowed();
    }

    @Override
    public final void setAffectsFreeBusy(boolean value) throws WebdavException {
        this.freeBusySupport.setAffectsFreeBusy(value);
    }

    @Override
    public final boolean getAffectsFreeBusy() throws WebdavException {
        return this.freeBusySupport.getAffectsFreeBusy();
    }

    @Override
    public boolean getCanShare() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getCanPublish() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAlias() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAliasUri() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setProperty(QName name, String value) {
    }

    @Override
    public String getProperty(QName name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getEtag() throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPreviousEtag() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TYPE resolveAlias(boolean resolveSubAlias) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void setCalType(int value) {
        this.calType = value;
    }

    @Override
    public int getCalType() {
        return this.calType;
    }

    @Override
    public boolean getDeleted() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean entitiesAllowed() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTimezone(String value) {
    }

    @Override
    public String getTimezone() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setColor(String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getColor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSupportedComponents(List<String> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getSupportedComponents() {
        return Collections.singletonList(VEVENT_COMPONENT);
    }

    @Override
    public List<String> getVpollSupportedComponents() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAliasUri(String val) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRemoteId(String val) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRemoteId() throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRemotePw(String val) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRemotePw() throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRefreshRate(int i) throws WebdavException {
    }

    @Override
    public int getRefreshRate() throws WebdavException {
        return 0;
    }

    @Override
    public void setSynchDeleteSuppressed(boolean b) throws WebdavException {
    }

    @Override
    public boolean getSynchDeleteSuppressed() throws WebdavException {
        return false;
    }
}

