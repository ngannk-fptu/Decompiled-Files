/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.GeneralUtil
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.util;

import com.atlassian.confluence.extra.calendar3.JodaIcal4jTimeZoneMapper;
import com.atlassian.confluence.extra.calendar3.ical4j.transformer.PublishTransformer;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubscribingSubCalendar;
import com.atlassian.confluence.extra.calendar3.util.CalendarHelper;
import com.atlassian.confluence.extra.calendar3.util.SprintEventTransformer;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.GeneralUtil;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Objects;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.CuType;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.parameter.XParameter;
import net.fortuna.ical4j.model.property.ExDate;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Transp;
import net.fortuna.ical4j.model.property.XProperty;
import net.fortuna.ical4j.transform.Transformer;
import net.fortuna.ical4j.util.UidGenerator;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalendarExportTransformer
implements Transformer<Calendar> {
    private static final Logger LOG = LoggerFactory.getLogger(CalendarExportTransformer.class);
    private final SettingsManager settingsManager;
    private final UserAccessor userAccessor;
    private final PersistedSubCalendar subCalendar;
    private final JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper;
    private final UidGenerator uidGenerator;
    private final CalendarHelper calendarHelper;

    public CalendarExportTransformer(SettingsManager settingsManager, UserAccessor userAccessor, PersistedSubCalendar subCalendar, JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper, UidGenerator uidGenerator, CalendarHelper calendarHelper) {
        this.settingsManager = settingsManager;
        this.userAccessor = userAccessor;
        this.subCalendar = subCalendar;
        this.jodaIcal4jTimeZoneMapper = jodaIcal4jTimeZoneMapper;
        this.uidGenerator = uidGenerator;
        this.calendarHelper = calendarHelper;
    }

    @Override
    public Calendar transform(Calendar calendar) {
        if (calendar == null) {
            LOG.warn("Could not transform null SubCalendar, will return the default one");
            return new Calendar();
        }
        Calendar transformedCalendar = new PublishTransformer(this.uidGenerator, true).transform(calendar);
        String subCalendarType = this.getSubCalendarType();
        SprintEventTransformer sprintEventTransformer = new SprintEventTransformer(subCalendarType);
        if (subCalendarType.equals("parent")) {
            this.updateCustomEventTypes(transformedCalendar);
        }
        ComponentList eventComponents = transformedCalendar.getComponents("VEVENT");
        for (VEvent eventComponent : eventComponents) {
            Object statusProperty;
            PropertyList<Property> eventPropertiesList = eventComponent.getProperties();
            eventPropertiesList.add(new XProperty("X-CONFLUENCE-SUBCALENDAR-TYPE", subCalendarType));
            Object transpProperty = eventComponent.getProperty("TRANSP");
            if (null == transpProperty) {
                ConfluenceUser loginUser = AuthenticatedUserThreadLocal.get();
                if (loginUser != null) {
                    if (StringUtils.equals((CharSequence)this.subCalendar.getCreator(), (CharSequence)loginUser.getKey().toString())) {
                        eventPropertiesList.add(eventComponent.getStartDate().getDate() instanceof DateTime && eventComponent.getEndDate().getDate() instanceof DateTime ? Transp.OPAQUE : Transp.TRANSPARENT);
                    }
                } else {
                    eventPropertiesList.add(Transp.TRANSPARENT);
                }
            }
            if (null == (statusProperty = eventComponent.getProperty("STATUS"))) {
                eventPropertiesList.add(Status.VEVENT_CONFIRMED);
            }
            this.updatePersonProperties(eventComponent, "ORGANIZER", "ATTENDEE");
            this.updateExDate(transformedCalendar, subCalendarType, eventComponent);
            if (!StringUtils.equals((CharSequence)"other", (CharSequence)subCalendarType)) {
                this.updatePeopleCalendarSummary(eventComponent);
            }
            sprintEventTransformer.transform(eventComponent);
        }
        return transformedCalendar;
    }

    private void updatePeopleCalendarSummary(VEvent eventComponent) {
        PropertyList attendees = eventComponent.getProperties("ATTENDEE");
        if (!attendees.isEmpty()) {
            String descriptionValue;
            Object summary = eventComponent.getProperty("SUMMARY");
            Object description = eventComponent.getProperty("DESCRIPTION");
            String string = descriptionValue = description != null ? ((Content)description).getValue() : "";
            if (summary != null) {
                Object newSummary = ((Content)summary).getValue();
                try {
                    if (StringUtils.isNotBlank((CharSequence)descriptionValue)) {
                        newSummary = (String)newSummary + ": " + descriptionValue;
                        ((Property)summary).setValue((String)newSummary);
                    }
                }
                catch (Exception e) {
                    LOG.error("Error exporting to iCal", (Throwable)e);
                }
            }
        }
    }

    private void updateExDate(Calendar calendar, String type, VEvent vEvent) {
        if (StringUtils.equals((CharSequence)type, (CharSequence)"local") || StringUtils.equals((CharSequence)type, (CharSequence)"other") || StringUtils.equals((CharSequence)type, (CharSequence)"people")) {
            PropertyList<Property> vEventProperties = vEvent.getProperties();
            PropertyList exDateProperties = vEvent.getProperties("EXDATE");
            TimeZone subCalendarTimeZone = this.getSubCalendarTimeZone(calendar);
            for (ExDate exDate : exDateProperties) {
                boolean isAllDay = !(vEvent.getStartDate().getDate() instanceof DateTime) && !(vEvent.getEndDate().getDate() instanceof DateTime);
                DateList exDateList = exDate.getDates();
                DateList newDateList = isAllDay ? new DateList(Value.DATE) : new DateList(Value.DATE_TIME, subCalendarTimeZone);
                for (Date aDate : exDateList) {
                    if (isAllDay) {
                        long millis = aDate.getTime();
                        newDateList.add(new Date(millis - millis % 86400000L));
                        continue;
                    }
                    try {
                        newDateList.add(new DateTime(aDate.toString(), subCalendarTimeZone));
                    }
                    catch (ParseException notValidDateTime) {
                        long millis = aDate.getTime();
                        newDateList.add(new Date(millis - millis % 86400000L));
                    }
                }
                vEventProperties.remove(exDate);
                ExDate newExDate = new ExDate(newDateList);
                if (!isAllDay) {
                    newExDate.setTimeZone(subCalendarTimeZone);
                }
                vEventProperties.add(newExDate);
            }
        }
    }

    private TimeZone getSubCalendarTimeZone(Calendar subCalendarData) {
        Object xWrTimeZone;
        VTimeZone vTimeZoneComponent = (VTimeZone)subCalendarData.getComponent("VTIMEZONE");
        TimeZone subCalendarTimeZone = null == vTimeZoneComponent || vTimeZoneComponent.getTimeZoneId() == null ? (null != (xWrTimeZone = subCalendarData.getProperty("X-WR-TIMEZONE")) ? this.jodaIcal4jTimeZoneMapper.getIcal4jTimeZone(((Content)xWrTimeZone).getValue()) : this.jodaIcal4jTimeZoneMapper.getIcal4jTimeZone(TimeZone.getDefault().getID())) : this.jodaIcal4jTimeZoneMapper.getIcal4jTimeZone(vTimeZoneComponent.getTimeZoneId().getValue());
        return subCalendarTimeZone;
    }

    private void updatePersonProperties(VEvent vEvent, String ... propertyNames) {
        if (null != propertyNames) {
            PropertyList<Property> vEventProperties = vEvent.getProperties();
            for (String propertyName : propertyNames) {
                PropertyList properties = vEventProperties.getProperties(propertyName);
                for (Property property : properties) {
                    ConfluenceUser confluenceUser;
                    ParameterList propertyParameters = property.getParameters();
                    Object confluenceUserParameter = propertyParameters.getParameter("X-CONFLUENCE-USER-KEY");
                    if (null == confluenceUserParameter || null == (confluenceUser = this.calendarHelper.getUser(property, this.userAccessor))) continue;
                    propertyParameters.remove((Parameter)propertyParameters.getParameter("CN"));
                    propertyParameters.add(new Cn(StringEscapeUtils.unescapeHtml4((String)confluenceUser.getFullName())));
                    propertyParameters.remove((Parameter)propertyParameters.getParameter("CUTYPE"));
                    propertyParameters.add(CuType.INDIVIDUAL);
                    try {
                        if (StringUtils.isNotBlank((CharSequence)confluenceUser.getEmail())) {
                            property.setValue(String.format("mailto:%s", confluenceUser.getEmail()));
                            continue;
                        }
                        property.setValue(String.format("%s/display/~%s", this.settingsManager.getGlobalSettings().getBaseUrl(), GeneralUtil.urlEncode((String)confluenceUser.getName())));
                    }
                    catch (IOException ioError) {
                        LOG.warn(String.format("Unable to update URL for user %s", confluenceUser), (Throwable)ioError);
                    }
                    catch (URISyntaxException invalidUri) {
                        LOG.warn(String.format("URI for user %s seems invalid", confluenceUser), (Throwable)invalidUri);
                    }
                    catch (ParseException invalidUri) {
                        LOG.warn(String.format("URI for user %s rejected by ical4j", confluenceUser), (Throwable)invalidUri);
                    }
                }
            }
        }
    }

    private String getSubCalendarType() {
        return this.subCalendar instanceof SubscribingSubCalendar ? ((SubscribingSubCalendar)this.subCalendar).getSubscriptionType() : this.subCalendar.getType();
    }

    private void updateCustomEventTypes(Calendar calendarToTransform) {
        if (Objects.isNull(this.subCalendar.getCustomEventTypes()) || this.subCalendar.getCustomEventTypes().isEmpty()) {
            calendarToTransform.getProperties().add(new XProperty("X-CONFLUENCE-CUSTOM-EVENT-TYPE", new ParameterList(), Boolean.FALSE.toString()));
            return;
        }
        this.subCalendar.getCustomEventTypes().forEach(customEventType -> {
            ParameterList customEventsParameters = new ParameterList();
            customEventsParameters.add(new XParameter("X-CONFLUENCE-CUSTOM-TYPE-ID", customEventType.getCustomEventTypeId()));
            customEventsParameters.add(new XParameter("X-CONFLUENCE-CUSTOM-TYPE-TITLE", customEventType.getTitle()));
            customEventsParameters.add(new XParameter("X-CONFLUENCE-CUSTOM-TYPE-ICON", customEventType.getIcon()));
            customEventsParameters.add(new XParameter("X-CONFLUENCE-CUSTOM-TYPE-REMINDER-DURATION", Integer.toString(customEventType.getPeriodInMins())));
            calendarToTransform.getProperties().add(new XProperty("X-CONFLUENCE-CUSTOM-EVENT-TYPE", customEventsParameters, Boolean.TRUE.toString()));
        });
    }
}

