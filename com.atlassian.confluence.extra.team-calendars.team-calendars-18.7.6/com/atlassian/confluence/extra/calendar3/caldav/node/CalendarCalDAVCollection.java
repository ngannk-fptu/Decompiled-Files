/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.caldav.node;

import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.caldav.CalendarAccessPrincipal;
import com.atlassian.confluence.extra.calendar3.caldav.node.AbstractCalDAVCollection;
import com.atlassian.confluence.extra.calendar3.caldav.node.freebusy.UnsupportedCalDavNodeFreeBusy;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import javax.xml.namespace.QName;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CalendarCalDAVCollection
extends AbstractCalDAVCollection<CalendarCalDAVCollection> {
    private static Logger logger = LoggerFactory.getLogger(CalendarCalDAVCollection.class);
    private final PersistedSubCalendar parentSubCalendar;
    private final PersistedSubCalendar persistedSubCalendar;
    private final CalendarManager calendarManager;
    private String timezone;
    private String color;

    public CalendarCalDAVCollection(String path, PersistedSubCalendar persistedSubCalendar, CalendarAccessPrincipal owner, CalendarManager calendarManager) throws WebdavException {
        super(path, owner, 1, new UnsupportedCalDavNodeFreeBusy());
        this.persistedSubCalendar = persistedSubCalendar;
        this.parentSubCalendar = persistedSubCalendar.getParent();
        this.calendarManager = calendarManager;
        this.setCreated(String.valueOf(persistedSubCalendar.getCreatedDate()));
        this.setDescription(persistedSubCalendar.getDescription());
        this.setDisplayName(persistedSubCalendar.getName());
        this.setName(persistedSubCalendar.getName());
        this.setTimezone(persistedSubCalendar.getTimeZoneId());
    }

    public PersistedSubCalendar getPersistedSubCalendar() {
        return this.persistedSubCalendar;
    }

    @Override
    public CalendarCalDAVCollection resolveAlias(boolean defer) {
        return this;
    }

    @Override
    public String getEtag() {
        long lastUpdateTime = this.parentSubCalendar != null ? this.parentSubCalendar.getLastUpdateDate() : this.persistedSubCalendar.getLastUpdateDate();
        return String.valueOf(lastUpdateTime);
    }

    @Override
    public String getPreviousEtag() {
        return this.getEtag();
    }

    @Override
    public String getProperty(QName name) {
        return null;
    }

    @Override
    public boolean entitiesAllowed() {
        return true;
    }

    @Override
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    @Override
    public String getTimezone() {
        return this.timezone;
    }

    @Override
    public void setColor(String color) {
        this.color = color;
        this.persistedSubCalendar.setColor(color);
        try {
            this.calendarManager.save(this.persistedSubCalendar);
        }
        catch (Exception e) {
            logger.error("Could not set color for calendar {}", (Object)this.parentSubCalendar.getName(), (Object)e);
        }
    }

    @Override
    public String getColor() {
        return this.persistedSubCalendar.getColor();
    }

    @Override
    public void setAliasUri(String val) throws WebdavException {
    }

    @Override
    public void setRemoteId(String val) throws WebdavException {
    }

    @Override
    public String getRemoteId() throws WebdavException {
        return null;
    }

    @Override
    public void setRemotePw(String val) throws WebdavException {
    }

    @Override
    public String getRemotePw() throws WebdavException {
        return null;
    }

    @Override
    public boolean getCanShare() {
        return false;
    }
}

