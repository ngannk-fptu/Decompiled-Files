/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.caldav.node;

import com.atlassian.confluence.extra.calendar3.caldav.node.schedule.CalDavNodeSchedulingSupport;
import com.atlassian.confluence.extra.calendar3.caldav.node.schedule.UnsupportedCalDavNodeScheduling;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.namespace.QName;
import net.fortuna.ical4j.model.DateTime;
import org.apache.commons.lang.StringUtils;
import org.bedework.access.AccessPrincipal;
import org.bedework.caldav.server.CalDAVEvent;
import org.bedework.caldav.server.Organizer;
import org.bedework.util.misc.Util;
import org.bedework.util.timezones.DateTimeUtil;
import org.bedework.util.xml.XmlEmit;
import org.bedework.webdav.servlet.shared.WebdavException;

public final class CalendarCalDAVEvent
extends CalDAVEvent<CalendarCalDAVEvent> {
    private final List<SubCalendarEvent> subCalendarEvents;
    private final SubCalendarEvent firstSubCalendarEvent;
    private final CalDavNodeSchedulingSupport schedulingSupport;
    private final boolean existing;

    public CalendarCalDAVEvent(String parentPath, AccessPrincipal owner, boolean existing, Collection<SubCalendarEvent> subCalendarEvents) throws WebdavException {
        this(parentPath, owner, existing, subCalendarEvents.toArray(new SubCalendarEvent[0]));
    }

    public CalendarCalDAVEvent(String parentPath, AccessPrincipal owner, boolean existing, SubCalendarEvent ... subCalendarEvents) throws WebdavException {
        this.subCalendarEvents = Arrays.stream(subCalendarEvents).collect(Collectors.toList());
        this.firstSubCalendarEvent = this.subCalendarEvents.get(0);
        String vEventId = this.firstSubCalendarEvent.getUid();
        if (!this.subCalendarEvents.stream().allMatch(subCalendarEvent -> subCalendarEvent.getUid().equals(vEventId))) {
            throw new IllegalArgumentException("CalendarCalDAVEvent only accept list of events which have same Id");
        }
        this.setDescription(this.firstSubCalendarEvent.getDescription());
        this.setDisplayName(this.firstSubCalendarEvent.getName());
        this.setName(this.firstSubCalendarEvent.getUid());
        this.setParentPath(parentPath);
        this.setPath(Util.buildPath(true, this.getParentPath(), this.firstSubCalendarEvent.getUid()));
        this.setOwner(owner);
        this.existing = existing;
        this.schedulingSupport = new UnsupportedCalDavNodeScheduling();
        this.initLastMofifiedDate();
    }

    private void initLastMofifiedDate() throws WebdavException {
        DateTime maxlastModifiedDate = this.subCalendarEvents.stream().map(subCalendarEvent -> {
            String lastModStr = subCalendarEvent.getLastModifiedDate();
            if (StringUtils.isNumeric(lastModStr)) {
                DateTime lastModifiedDateTime = new DateTime(Long.parseLong(lastModStr));
                return lastModifiedDateTime;
            }
            return null;
        }).filter(Objects::nonNull).max(Date::compareTo).orElse(null);
        if (maxlastModifiedDate == null) {
            String uniqueLastModifiedDateStr = this.subCalendarEvents.stream().map(SubCalendarEvent::getLastModifiedDate).collect(Collectors.joining());
            this.setLastmod(uniqueLastModifiedDateStr);
        } else {
            this.setLastmod(DateTimeUtil.isoDateTimeUTC(maxlastModifiedDate));
        }
    }

    public void addSubCalendarEvent(SubCalendarEvent subCalendarEvent) {
        Objects.nonNull(subCalendarEvent);
        this.subCalendarEvents.add(subCalendarEvent);
    }

    public Set<SubCalendarEvent> getSubCalendarEvents() {
        return Collections.unmodifiableSet(new HashSet<SubCalendarEvent>(this.subCalendarEvents));
    }

    public SubCalendarEvent getSubCalendarEvent() {
        return this.firstSubCalendarEvent;
    }

    @Override
    public String getScheduleTag() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getOrganizerSchedulingObject() throws WebdavException {
        return this.schedulingSupport.getOrganizerSchedulingObject();
    }

    @Override
    public boolean getAttendeeSchedulingObject() throws WebdavException {
        return this.schedulingSupport.getAttendeeSchedulingObject();
    }

    @Override
    public String getPrevScheduleTag() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getSummary() {
        return this.firstSubCalendarEvent.getDescription();
    }

    @Override
    public boolean isNew() {
        return !this.existing;
    }

    @Override
    public boolean getDeleted() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getEntityType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setOrganizer(Organizer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Organizer getOrganizer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setOriginator(String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRecipients(Set<String> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getRecipients() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addRecipient(String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getAttendeeUris() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setScheduleMethod(int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getScheduleMethod() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getUid() {
        return this.firstSubCalendarEvent.getUid();
    }

    @Override
    public boolean generatePropertyValue(QName tag, XmlEmit xml) {
        return false;
    }

    @Override
    public String toIcalString(int methodType, String contentType) {
        throw new UnsupportedOperationException();
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
    public CalendarCalDAVEvent resolveAlias(boolean resolveSubAlias) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setProperty(QName name, String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getProperty(QName name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getEtag() throws WebdavException {
        return this.getLastmod();
    }

    @Override
    public String getPreviousEtag() throws WebdavException {
        return this.getLastmod();
    }
}

