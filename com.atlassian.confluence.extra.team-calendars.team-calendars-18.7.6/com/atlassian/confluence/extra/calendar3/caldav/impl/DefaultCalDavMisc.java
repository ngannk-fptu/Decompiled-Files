/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.base.Function
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.caldav.impl;

import com.atlassian.confluence.extra.calendar3.SubCalendarEventConverter;
import com.atlassian.confluence.extra.calendar3.caldav.CalDavCalendarManager;
import com.atlassian.confluence.extra.calendar3.caldav.CalDavEventManager;
import com.atlassian.confluence.extra.calendar3.caldav.CalDavMisc;
import com.atlassian.confluence.extra.calendar3.caldav.CalendarSysiIcalendar;
import com.atlassian.confluence.extra.calendar3.caldav.node.CalendarCalDAVCollection;
import com.atlassian.confluence.extra.calendar3.caldav.node.CalendarCalDAVEvent;
import com.atlassian.confluence.extra.calendar3.ical4j.VEventMapper;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.service.UserSearchService;
import com.atlassian.confluence.extra.calendar3.util.ICalPersonToConfluenceUserTransformer;
import com.atlassian.confluence.extra.calendar3.util.TextLimitICalTransformer;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.base.Function;
import ietf.params.xml.ns.icalendar_2.IcalendarType;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.data.UnfoldingReader;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.TzId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.validate.ValidationException;
import org.bedework.caldav.server.CalDAVCollection;
import org.bedework.caldav.server.CalDAVEvent;
import org.bedework.caldav.server.SysiIcalendar;
import org.bedework.caldav.server.sysinterface.SysIntf;
import org.bedework.util.xml.XmlEmit;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="calDavMisc")
public final class DefaultCalDavMisc
implements CalDavMisc {
    private static final Logger logger = LoggerFactory.getLogger(DefaultCalDavMisc.class);
    private static final int NUMBER_OF_WORD_LIMIT = 250;
    private static final VTimeZone DEFAULT_TIMEZONE;
    private final CalDavCalendarManager calDavCalendarManager;
    private final CalDavEventManager calDavEventManager;
    private final SubCalendarEventConverter<PersistedSubCalendar> eventConverter;
    private final VEventMapper vEventMapper;
    private final SettingsManager settingsManager;
    private final UserSearchService userSearchService;

    @Autowired
    public DefaultCalDavMisc(CalDavCalendarManager calDavCalendarManager, CalDavEventManager calDavEventManager, SubCalendarEventConverter<PersistedSubCalendar> eventConverter, VEventMapper vEventMapper, @ComponentImport SettingsManager settingsManager, UserSearchService userSearchService) {
        this.calDavCalendarManager = calDavCalendarManager;
        this.calDavEventManager = calDavEventManager;
        this.eventConverter = eventConverter;
        this.vEventMapper = vEventMapper;
        this.settingsManager = settingsManager;
        this.userSearchService = userSearchService;
    }

    @Override
    public Calendar toCalendar(CalDAVEvent calDavEvent, boolean incSchedMethod) throws WebdavException {
        CalendarCalDAVEvent calendarCalDAVEvent = (CalendarCalDAVEvent)calDavEvent;
        PersistedSubCalendar subCalendar = calendarCalDAVEvent.getSubCalendarEvent().getSubCalendar();
        Set<SubCalendarEvent> subCalendarEvents = calendarCalDAVEvent.getSubCalendarEvents();
        PropertyList<Property> calendarProperties = new PropertyList<Property>();
        ComponentList<CalendarComponent> calendarComponents = new ComponentList<CalendarComponent>();
        try {
            subCalendarEvents.stream().forEach(subCalendarEvent -> {
                VEvent event = this.vEventMapper.toVEvent(subCalendarEvent.getSubCalendar(), (SubCalendarEvent)subCalendarEvent);
                calendarComponents.add(event);
            });
            Calendar calendar = new Calendar(calendarProperties, calendarComponents);
            calendar = this.calDavCalendarManager.transform(subCalendar, calendar);
            if (logger.isDebugEnabled()) {
                logger.debug("Response calendar data: \n {}", (Object)calendar.toString());
            }
            return calendar;
        }
        catch (Exception e) {
            logger.error("Exception while try to convert Calendar", (Throwable)e);
            throw new WebdavException(e);
        }
    }

    @Override
    public IcalendarType toIcalendar(CalDAVEvent event, boolean incSchedMethod, IcalendarType pattern) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toJcal(CalDAVEvent event, boolean incSchedMethod, IcalendarType pattern) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toIcalString(Calendar calendar, String contentType) throws WebdavException {
        try {
            StringWriter writer = new StringWriter();
            CalendarOutputter result = new CalendarOutputter(false);
            result.output(calendar, writer);
            String calendarStr = writer.toString();
            if (logger.isDebugEnabled()) {
                logger.debug("Response calendar data: \n {}", (Object)calendarStr);
            }
            return calendarStr;
        }
        catch (Exception exception) {
            throw new WebdavException(exception);
        }
    }

    @Override
    public String writeCalendar(Collection<CalDAVEvent> events, SysIntf.MethodEmitted method, XmlEmit xml, Writer writer, String contentType) throws WebdavException {
        try {
            if (!contentType.equals("text/calendar")) {
                throw new WebdavException("Unsupported content type");
            }
            if (events.isEmpty()) {
                return contentType;
            }
            List calendarCalDAVEvents = events.stream().map(CalendarCalDAVEvent.class::cast).collect(Collectors.toList());
            CalendarCalDAVEvent firstEvent = (CalendarCalDAVEvent)calendarCalDAVEvents.get(0);
            PersistedSubCalendar parentCalendar = firstEvent.getSubCalendarEvent().getSubCalendar();
            Collection subCalendarEvents = calendarCalDAVEvents.stream().flatMap(calendarCalDAVEvent -> calendarCalDAVEvent.getSubCalendarEvents().stream()).collect(Collectors.toList());
            Calendar calendar = this.calDavCalendarManager.toCalendar(parentCalendar, subCalendarEvents);
            calendar = this.calDavCalendarManager.transform(parentCalendar, calendar);
            if (xml == null) {
                CalendarOutputter calendarOutputter = new CalendarOutputter(false);
                calendarOutputter.output(calendar, writer);
            } else {
                xml.cdataValue(this.toIcalString(calendar, "text/calendar"));
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Response calendar data: \n {}", (Object)calendar.toString());
            }
            return contentType;
        }
        catch (Exception exception) {
            throw new WebdavException(exception);
        }
    }

    @Override
    public SysiIcalendar fromIcal(CalDAVCollection collection, Reader reader, String contentType, SysIntf.IcalResultType rtype, boolean mergeAttendees) throws WebdavException {
        try {
            if (!contentType.matches("text/calendar")) {
                throw new UnsupportedOperationException("unsupported content type: " + contentType);
            }
            CalendarBuilder calendarBuilder = new CalendarBuilder();
            Calendar calendar = calendarBuilder.build(new UnfoldingReader(reader));
            if (logger.isDebugEnabled()) {
                logger.debug("Request calendar data: \n {}", (Object)calendar.toString());
            }
            ICalPersonToConfluenceUserTransformer transformer = new ICalPersonToConfluenceUserTransformer(this.settingsManager.getGlobalSettings().getBaseUrl(), this.userSearchService);
            TextLimitICalTransformer textLimitICalTransformer = new TextLimitICalTransformer(250);
            calendar = transformer.transform(calendar);
            calendar = textLimitICalTransformer.transform(calendar);
            CalendarCalDAVCollection calDAVCollection = (CalendarCalDAVCollection)collection;
            PropertyList<Property> calendarProperties = calendar.getProperties();
            ComponentList componentList = calendar.getComponents("VEVENT");
            VTimeZone timezoneComponent = (VTimeZone)calendar.getComponent("VTIMEZONE");
            net.fortuna.ical4j.model.TimeZone timeZone = timezoneComponent != null ? new net.fortuna.ical4j.model.TimeZone(timezoneComponent) : new net.fortuna.ical4j.model.TimeZone(DEFAULT_TIMEZONE);
            ArrayList<CalendarCalDAVEvent> events = new ArrayList<CalendarCalDAVEvent>();
            Stream eventStream = componentList.stream();
            Map<String, List<VEvent>> groupByUID = eventStream.collect(Collectors.groupingBy(event -> event.getUid().getValue()));
            for (Map.Entry<String, List<VEvent>> entry : groupByUID.entrySet()) {
                String vEventId = entry.getKey();
                List<VEvent> eventList = entry.getValue();
                List sortedEventList = eventList.stream().sorted((o1, o2) -> {
                    if (o1.getRecurrenceId() == null) {
                        return -1;
                    }
                    if (o2.getRecurrenceId() == null) {
                        return 1;
                    }
                    return 0;
                }).collect(Collectors.toList());
                List<SubCalendarEvent> subCalendarEventList = sortedEventList.stream().map(event -> this.eventConverter.toSubCalendarEvent((VEvent)event, calDAVCollection.getPersistedSubCalendar(), timeZone, (Function<Void, Boolean>)((Function)input -> true))).collect(Collectors.toList());
                CalendarCalDAVEvent existingEvent = (CalendarCalDAVEvent)this.calDavEventManager.getEvent(calDAVCollection, vEventId);
                existingEvent = existingEvent == null ? new CalendarCalDAVEvent(calDAVCollection.getPath(), calDAVCollection.getOwner(), false, subCalendarEventList) : new CalendarCalDAVEvent(calDAVCollection.getPath(), calDAVCollection.getOwner(), true, subCalendarEventList);
                events.add(existingEvent);
            }
            return new CalendarSysiIcalendar(calendarProperties, (CalendarCalDAVCollection)collection, events);
        }
        catch (Exception exception) {
            throw new WebdavException(exception);
        }
    }

    @Override
    public SysiIcalendar fromIcal(CalDAVCollection collection, IcalendarType ical, SysIntf.IcalResultType rtype) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toStringTzCalendar(String tzid) throws WebdavException {
        try {
            CalendarBuilder calendarBuilder = new CalendarBuilder();
            Calendar calendar = new Calendar();
            net.fortuna.ical4j.model.TimeZone tz = calendarBuilder.getRegistry().getTimeZone(tzid);
            calendar.getComponents().add(tz.getVTimeZone());
            PropertyList<Property> pl = calendar.getProperties();
            pl.add(new ProdId("team-calendars"));
            pl.add(Version.VERSION_2_0);
            StringWriter writer = new StringWriter();
            CalendarOutputter output = new CalendarOutputter(true);
            output.output(calendar, writer);
            return writer.toString();
        }
        catch (IOException | ValidationException exception) {
            throw new WebdavException(exception);
        }
    }

    @Override
    public String tzidFromTzdef(String value) throws WebdavException {
        try {
            CalendarBuilder calendarBuilder = new CalendarBuilder();
            Calendar calendar = calendarBuilder.build(new StringReader(value));
            VTimeZone timeZone = (VTimeZone)calendar.getComponent("VTIMEZONE");
            return timeZone.getTimeZoneId().getValue();
        }
        catch (IOException | ParserException exception) {
            throw new WebdavException(exception);
        }
    }

    @Override
    public boolean validateAlarm(String value) throws WebdavException {
        try {
            String alarmPrefix = "BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:team-calendars\n";
            String alarmSuffix = "END:VCALENDAR\n";
            StringReader reader = new StringReader(String.format("%s%s%s", "BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:team-calendars\n", value, "END:VCALENDAR\n"));
            CalendarBuilder calendarBuilder = new CalendarBuilder();
            Calendar calendar = calendarBuilder.build(reader);
            ComponentList alarms = calendar.getComponents("VALARM");
            return alarms != null && alarms.size() > 0;
        }
        catch (IOException | ParserException exception) {
            throw new WebdavException(exception);
        }
    }

    @Override
    public void rollback() {
    }

    @Override
    public void close() throws WebdavException {
    }

    static {
        PropertyList<TzId> timeZoneProperties = new PropertyList<TzId>();
        timeZoneProperties.add(new TzId(TimeZone.getDefault().getID()));
        DEFAULT_TIMEZONE = new VTimeZone(timeZoneProperties);
    }
}

