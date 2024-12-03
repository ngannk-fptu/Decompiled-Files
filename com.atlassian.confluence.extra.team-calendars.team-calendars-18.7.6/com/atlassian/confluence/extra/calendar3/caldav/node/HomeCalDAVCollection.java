/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.caldav.node;

import com.atlassian.confluence.extra.calendar3.caldav.CalendarAccessPrincipal;
import com.atlassian.confluence.extra.calendar3.caldav.node.AbstractCalDAVCollection;
import com.atlassian.confluence.extra.calendar3.caldav.node.freebusy.UnsupportedCalDavNodeFreeBusy;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import java.util.Collection;
import javax.xml.namespace.QName;
import org.bedework.webdav.servlet.shared.WebdavException;

public class HomeCalDAVCollection
extends AbstractCalDAVCollection<HomeCalDAVCollection> {
    private final Collection<PersistedSubCalendar> calendars;

    public HomeCalDAVCollection(String path, CalendarAccessPrincipal owner, Collection<PersistedSubCalendar> calendars) throws WebdavException {
        super(path, owner, 0, new UnsupportedCalDavNodeFreeBusy());
        this.calendars = calendars;
    }

    @Override
    public HomeCalDAVCollection resolveAlias(boolean resolveSubAlias) {
        return this;
    }

    @Override
    public boolean getCanShare() {
        return false;
    }

    @Override
    public String getColor() {
        return null;
    }

    @Override
    public String getTimezone() {
        return null;
    }

    @Override
    public String getProperty(QName name) {
        return null;
    }

    @Override
    public String getEtag() throws WebdavException {
        return this.getLastmod();
    }

    @Override
    public boolean entitiesAllowed() {
        return false;
    }

    public Collection<PersistedSubCalendar> getCalendars() {
        return this.calendars;
    }
}

