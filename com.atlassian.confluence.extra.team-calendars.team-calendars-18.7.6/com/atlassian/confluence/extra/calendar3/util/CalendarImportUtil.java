/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTimeZone
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.util;

import com.atlassian.confluence.extra.calendar3.JodaIcal4jDateTimeConverter;
import com.atlassian.confluence.extra.calendar3.JodaIcal4jTimeZoneMapper;
import net.fortuna.ical4j.extensions.property.WrCalDesc;
import net.fortuna.ical4j.extensions.property.WrCalName;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.XProperty;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CalendarImportUtil {
    private static final Logger LOG = LoggerFactory.getLogger(CalendarImportUtil.class);
    private final JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper;
    private final JodaIcal4jDateTimeConverter jodaIcal4jDateTimeConverter;

    @Autowired
    public CalendarImportUtil(JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper, JodaIcal4jDateTimeConverter jodaIcal4jDateTimeConverter) {
        this.jodaIcal4jTimeZoneMapper = jodaIcal4jTimeZoneMapper;
        this.jodaIcal4jDateTimeConverter = jodaIcal4jDateTimeConverter;
    }

    public String getTimeZoneFromCalendar(Calendar calendar) {
        Object xWrTimeZone;
        String timeZoneId = null;
        VTimeZone vTimeZone = (VTimeZone)calendar.getComponent("VTIMEZONE");
        if (vTimeZone != null) {
            timeZoneId = vTimeZone.getTimeZoneId().getValue();
        }
        if (timeZoneId == null && null != (xWrTimeZone = calendar.getProperty("X-WR-TIMEZONE"))) {
            timeZoneId = ((Content)xWrTimeZone).getValue();
        }
        if (timeZoneId == null) {
            timeZoneId = this.jodaIcal4jTimeZoneMapper.getSystemTimeZoneIdJoda(true);
        }
        return timeZoneId;
    }

    public Calendar normalize(Calendar subCalendarData, String timeZoneId, String name, String description) {
        ComponentList<CalendarComponent> subCalendarDataComponents = subCalendarData.getComponents();
        PropertyList<Property> subCalendarDataProperties = subCalendarData.getProperties();
        TimeZone subCalendarTimeZone = this.jodaIcal4jTimeZoneMapper.getIcal4jTimeZone(timeZoneId);
        VTimeZone timeZoneComponent = subCalendarTimeZone.getVTimeZone();
        subCalendarDataComponents.remove(subCalendarDataComponents.getComponent("VTIMEZONE"));
        subCalendarDataComponents.add(timeZoneComponent);
        subCalendarDataProperties.remove((Property)subCalendarDataProperties.getProperty("X-WR-TIMEZONE"));
        subCalendarDataProperties.add(new XProperty("X-WR-TIMEZONE", new ParameterList(), timeZoneComponent.getTimeZoneId().getValue()));
        subCalendarDataProperties.remove((Property)subCalendarDataProperties.getProperty("X-WR-CALNAME"));
        subCalendarDataProperties.add(new WrCalName(new ParameterList(), name));
        subCalendarDataProperties.remove((Property)subCalendarDataProperties.getProperty("X-WR-CALDESC"));
        subCalendarDataProperties.add(new WrCalDesc(new ParameterList(), StringUtils.defaultString(description).replaceAll("((\\r\\n)|\\r|\\n)", "\\n")));
        ComponentList eventComponents = subCalendarDataComponents.getComponents("VEVENT");
        DateTimeZone subCalendarTimeZoneJoda = this.jodaIcal4jTimeZoneMapper.toJodaTimeZone(subCalendarTimeZone.getID());
        for (VEvent eventComponent : eventComponents) {
            DtStart dateStartProperty = eventComponent.getStartDate();
            DtEnd dateEndProperty = eventComponent.getEndDate();
            if (dateEndProperty == null) {
                dateEndProperty = new DtEnd(dateStartProperty.getDate());
            }
            if (!(dateStartProperty.getDate() instanceof DateTime) || !(dateEndProperty.getDate() instanceof DateTime)) continue;
            PropertyList<Property> eventPropertyList = eventComponent.getProperties();
            eventPropertyList.remove(dateStartProperty);
            DateTime startDate = (DateTime)dateStartProperty.getDate();
            this.SanitizeEventTimeZone(subCalendarTimeZoneJoda, eventComponent, startDate);
            eventPropertyList.add(new DtStart(this.jodaIcal4jDateTimeConverter.toIcal4jDateTime(this.jodaIcal4jDateTimeConverter.toJodaTime(startDate, null == startDate.getTimeZone() ? subCalendarTimeZone : startDate.getTimeZone()).withZone(subCalendarTimeZoneJoda))));
            eventPropertyList.remove(dateEndProperty);
            DateTime endDate = (DateTime)dateEndProperty.getDate();
            this.SanitizeEventTimeZone(subCalendarTimeZoneJoda, eventComponent, endDate);
            eventPropertyList.add(new DtEnd(this.jodaIcal4jDateTimeConverter.toIcal4jDateTime(this.jodaIcal4jDateTimeConverter.toJodaTime(endDate, null == endDate.getTimeZone() ? subCalendarTimeZone : endDate.getTimeZone()).withZone(subCalendarTimeZoneJoda))));
        }
        return subCalendarData;
    }

    private void SanitizeEventTimeZone(DateTimeZone subCalendarTimeZoneJoda, VEvent eventComponent, DateTime dateTime) {
        block4: {
            if (dateTime.getTimeZone() == null) {
                dateTime.setTimeZone(this.jodaIcal4jTimeZoneMapper.toIcal4jTimeZone(subCalendarTimeZoneJoda.getID()));
            }
            if (!this.jodaIcal4jTimeZoneMapper.isTimeZoneSupported(dateTime.getTimeZone())) {
                try {
                    TimeZone timeZone = this.jodaIcal4jTimeZoneMapper.getIcal4jTimeZone(dateTime.getTimeZone().getID());
                    dateTime.setTimeZone(timeZone);
                }
                catch (NullPointerException e) {
                    if (subCalendarTimeZoneJoda == null) break block4;
                    LOG.warn("Unknown timezone: " + dateTime.getTimeZone().getID() + " for event " + eventComponent.getSummary() + ". Event timezone set to: " + subCalendarTimeZoneJoda.getID());
                    dateTime.setTimeZone(this.jodaIcal4jTimeZoneMapper.toIcal4jTimeZone(subCalendarTimeZoneJoda.getID()));
                }
            }
        }
    }
}

