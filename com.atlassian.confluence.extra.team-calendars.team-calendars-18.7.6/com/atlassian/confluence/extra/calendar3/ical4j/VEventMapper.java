/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.fugue.Pair
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Lists
 *  org.apache.commons.collections.map.HashedMap
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.ical4j;

import com.atlassian.confluence.extra.calendar3.ActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.JodaIcal4jDateTimeConverter;
import com.atlassian.confluence.extra.calendar3.JodaIcal4jTimeZoneMapper;
import com.atlassian.confluence.extra.calendar3.calendarstore.CustomEventTypeSupport;
import com.atlassian.confluence.extra.calendar3.calendarstore.generic.KeyStoreToEventTypeMapper;
import com.atlassian.confluence.extra.calendar3.model.ConfluenceUserInvitee;
import com.atlassian.confluence.extra.calendar3.model.ExternalInvitee;
import com.atlassian.confluence.extra.calendar3.model.Invitee;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.persistence.CustomEventTypeEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.EventEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.EventEntityDTO;
import com.atlassian.confluence.extra.calendar3.model.persistence.EventRecurrenceExclusionEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.InviteeEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.extra.calendar3.querydsl.DTO.EventDTO;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.fugue.Pair;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.parameter.XParameter;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Categories;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.ExDate;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.RecurrenceId;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.model.property.XProperty;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="vEventMapper")
public class VEventMapper {
    private static final Logger LOG = LoggerFactory.getLogger(VEventMapper.class);
    private final UserAccessor userAccessor;
    private final SettingsManager settingsManager;
    private final JodaIcal4jDateTimeConverter jodaIcal4jDateTimeConverter;
    private final JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper;
    private final ThreadLocal<Map<String, String>> customEventTypeIdToNameMapper;
    private CustomEventTypeSupport customEventTypeSupport;
    private final ActiveObjectsServiceWrapper activeObjectsServiceWrapper;

    @Autowired
    public VEventMapper(@ComponentImport SettingsManager settingsManager, @ComponentImport UserAccessor userAccessor, JodaIcal4jDateTimeConverter jodaIcal4jDateTimeConverter, JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper, ActiveObjectsServiceWrapper activeObjectsServiceWrapper) {
        this.settingsManager = settingsManager;
        this.userAccessor = userAccessor;
        this.jodaIcal4jDateTimeConverter = jodaIcal4jDateTimeConverter;
        this.jodaIcal4jTimeZoneMapper = jodaIcal4jTimeZoneMapper;
        this.customEventTypeIdToNameMapper = new ThreadLocal();
        this.activeObjectsServiceWrapper = activeObjectsServiceWrapper;
    }

    public void setCustomEventTypeSupport(CustomEventTypeSupport customEventTypeSupport) {
        this.customEventTypeSupport = customEventTypeSupport;
    }

    public Collection<VEvent> toVEvents(EventEntity ... eventEntities) {
        Objects.requireNonNull(eventEntities);
        ArrayList<VEvent> vEventList = new ArrayList<VEvent>(eventEntities.length);
        List<EventEntity> eventEntityList = Arrays.asList(eventEntities);
        Iterators.partition(eventEntityList.iterator(), (int)1000).forEachRemaining(batchEventEntityList -> {
            Map<Integer, Set<InviteeEntity>> batchInvitees = this.activeObjectsServiceWrapper.getInvitees((List<EventEntity>)batchEventEntityList);
            Map<Integer, Set<EventRecurrenceExclusionEntity>> batchRecurrenceExclusions = this.activeObjectsServiceWrapper.getRecurrenceExclusions((List<EventEntity>)batchEventEntityList);
            for (EventEntity eventEntity : batchEventEntityList) {
                Collection recurrenceExclusionEntities;
                int eventId = eventEntity.getID();
                Pair<VEvent, DateTimeZone> pair = this.toVEventInternal(eventEntity);
                VEvent vEvent = (VEvent)pair.left();
                DateTimeZone dateTimeZone = (DateTimeZone)pair.right();
                Collection inviteeEntities = batchInvitees.get(eventId);
                if (inviteeEntities != null) {
                    this.addAttendees(vEvent, inviteeEntities.toArray(new InviteeEntity[0]));
                }
                if ((recurrenceExclusionEntities = (Collection)batchRecurrenceExclusions.get(eventId)) != null) {
                    this.addExclusionDates(vEvent, dateTimeZone, batchRecurrenceExclusions.get(eventId).toArray(new EventRecurrenceExclusionEntity[0]));
                }
                vEventList.add((VEvent)pair.left());
            }
        });
        return vEventList;
    }

    private Pair<VEvent, DateTimeZone> toVEventInternal(EventEntity eventEntity) {
        SubCalendarEntity subCalendar = this.activeObjectsServiceWrapper.getSubCalendarEntity(eventEntity);
        DateTimeZone subCalendarTimeZone = DateTimeZone.forID((String)subCalendar.getTimeZoneId());
        String subCalendarStoreKey = subCalendar.getStoreKey();
        String customEventTypeId = subCalendar.getUsingCustomEventTypeId();
        String eventTypeName = "unknown-event-type";
        if (StringUtils.isNotEmpty(subCalendar.getUsingCustomEventTypeId())) {
            Map<String, String> mapper = this.customEventTypeIdToNameMapper.get();
            if (mapper == null) {
                mapper = new HashMap<String, String>();
                this.customEventTypeIdToNameMapper.set(mapper);
            }
            if (StringUtils.isEmpty(eventTypeName = mapper.get(customEventTypeId)) && StringUtils.isNotEmpty(customEventTypeId)) {
                Optional customEventTypeEntityOptional = this.customEventTypeSupport.getCustomEventTypes(customEventTypeId).stream().findFirst();
                CustomEventTypeEntity customEventTypeEntity = (CustomEventTypeEntity)customEventTypeEntityOptional.get();
                eventTypeName = customEventTypeEntity.getTitle();
                mapper.put(customEventTypeId, eventTypeName);
            }
        } else {
            eventTypeName = (String)KeyStoreToEventTypeMapper.mapper.get((Object)subCalendarStoreKey);
        }
        EventDTO eventDTO = new EventDTO(eventEntity.getID(), subCalendar.getID(), eventEntity.getUtcStart(), eventEntity.getUtcEnd(), 0L, eventEntity.getRecurrenceRule(), eventEntity.getSummary(), eventEntity.getDescription(), eventEntity.getLocation(), eventEntity.getUrl(), eventEntity.getOrganiser(), eventEntity.getRecurrenceIdTimestamp() == null ? 0L : eventEntity.getRecurrenceIdTimestamp(), eventEntity.getCreated(), eventEntity.getLastModified(), eventEntity.getSequence(), subCalendar.getStoreKey(), eventEntity.isAllDay(), eventEntity.getStart(), eventEntity.getEnd(), subCalendar.getTimeZoneId(), eventEntity.getVeventUid(), subCalendar.getName(), subCalendar.getParent() != null ? subCalendar.getParent().getID() : null, customEventTypeId, eventTypeName, subCalendar.getSubscription() != null ? subCalendar.getSubscription().getID() : null);
        VEvent vEvent = this.toVEvent(subCalendarTimeZone, eventDTO);
        return Pair.pair((Object)vEvent, (Object)subCalendarTimeZone);
    }

    public VEvent toVEvent(EventEntity eventEntity) {
        Pair<VEvent, DateTimeZone> result = this.toVEventInternal(eventEntity);
        VEvent vEvent = (VEvent)result.left();
        DateTimeZone subCalendarTimeZone = (DateTimeZone)result.right();
        this.addAttendees(vEvent, this.activeObjectsServiceWrapper.getInvitees(eventEntity));
        this.addExclusionDates(vEvent, subCalendarTimeZone, this.activeObjectsServiceWrapper.getRecurrenceExclusions(eventEntity));
        return vEvent;
    }

    public VEvent toVEvent(PersistedSubCalendar subCalendar, SubCalendarEvent subCalendarEvent) {
        return this.toVEvent(subCalendar, subCalendarEvent, null);
    }

    public VEvent toVEvent(PersistedSubCalendar subCalendar, SubCalendarEvent subCalendarEvent, VEvent eventComponentToBeUpdated) {
        DateTimeZone subCalendarTimeZoneJoda = DateTimeZone.forID((String)subCalendar.getTimeZoneId());
        Map<String, BiFunction<SubCalendarEvent, VEvent, PropertyList<Property>>> fieldMap = this.getVEventFieldMapper(subCalendarTimeZoneJoda);
        eventComponentToBeUpdated = eventComponentToBeUpdated == null ? new VEvent() : eventComponentToBeUpdated;
        PropertyList<Property> eventProperties = eventComponentToBeUpdated.getProperties();
        for (Map.Entry<String, BiFunction<SubCalendarEvent, VEvent, PropertyList<Property>>> fieldEntry : fieldMap.entrySet()) {
            String propertyKey = fieldEntry.getKey();
            try {
                LOG.debug("Mapping property {}", (Object)propertyKey);
                BiFunction<SubCalendarEvent, VEvent, PropertyList<Property>> converterFunction = fieldEntry.getValue();
                PropertyList<Property> convertedList = converterFunction.apply(subCalendarEvent, eventComponentToBeUpdated);
                PropertyList existingList = eventProperties.getProperties(propertyKey);
                if (existingList.isEmpty()) {
                    if (convertedList == null) continue;
                    eventProperties.addAll(convertedList);
                    continue;
                }
                if (convertedList == null) {
                    existingList.forEach(eventProperties::remove);
                    continue;
                }
                PropertyList updatingList = new PropertyList();
                existingList.forEach(existingProperty -> convertedList.stream().filter(convertedProperty -> convertedProperty.getName().equals(existingProperty.getName())).forEach(updatingList::add));
                existingList.forEach(eventProperties::remove);
                updatingList.forEach(property -> {
                    if (!eventProperties.contains(property)) {
                        eventProperties.add((Property)property);
                    }
                });
            }
            catch (Exception e) {
                LOG.debug(String.format("Mapping property %s with Exception", propertyKey), (Throwable)e);
                throw e;
            }
        }
        return eventComponentToBeUpdated;
    }

    public VEvent toVEvent(DateTimeZone subCalendarTimeZone, EventDTO eventDTO) {
        Date endDate;
        Date startDate;
        if (eventDTO.isAllDay()) {
            startDate = new Date(eventDTO.getStart());
            endDate = new Date(eventDTO.getEnd());
        } else {
            startDate = this.jodaIcal4jDateTimeConverter.toIcal4jDateTime(new DateTime(eventDTO.getStart(), subCalendarTimeZone));
            endDate = this.jodaIcal4jDateTimeConverter.toIcal4jDateTime(new DateTime(eventDTO.getEnd(), subCalendarTimeZone));
        }
        VEvent vEvent = new VEvent(startDate, endDate, eventDTO.getSummary());
        PropertyList<Property> vEventProperties = vEvent.getProperties();
        if (StringUtils.isNotEmpty(eventDTO.getCustomEventTypeId())) {
            this.addCustomProperty(vEventProperties, "X-CONFLUENCE-CUSTOM-TYPE-ID", new ParameterList(), eventDTO.getCustomEventTypeId());
        }
        if (StringUtils.isNotEmpty(eventDTO.getEventTypeName())) {
            vEventProperties.add(new Categories(eventDTO.getEventTypeName()));
        }
        if (subCalendarTimeZone.getID().equals(DateTimeZone.UTC.getID())) {
            this.addCustomProperty(vEventProperties, "skipSubCalendarTimezone", new ParameterList(), "true");
        }
        this.addCustomProperty(vEventProperties, "SUBCALENDAR-ID", new ParameterList(), eventDTO.getSubCalendarId());
        this.addCustomProperty(vEventProperties, "PARENT-CALENDAR-ID", new ParameterList(), eventDTO.getParentSubCalendarId());
        this.addCustomProperty(vEventProperties, "PARENT-CALENDAR-NAME", new ParameterList(), eventDTO.getParentCalendarName());
        this.addCustomProperty(vEventProperties, "SUBSCRIPTION-ID", new ParameterList(), eventDTO.getSubscriptionId());
        this.addCustomProperty(vEventProperties, "SUBCALENDAR-TZ-ID", new ParameterList(), eventDTO.getSubCalendarTimeZoneId());
        this.addCustomProperty(vEventProperties, "SUBCALENDAR-NAME", new ParameterList(), eventDTO.getCalendarName());
        this.addCustomProperty(vEventProperties, "EVENT-ID", new ParameterList(), String.valueOf(eventDTO.getEventId()));
        this.addCustomProperty(vEventProperties, "EVENT-ALLDAY", new ParameterList(), String.valueOf(eventDTO.isAllDay()));
        if (eventDTO.getPeriod() > 0L) {
            this.addCustomProperty(vEventProperties, "PERIOD-REMINDER", new ParameterList(), String.valueOf(eventDTO.getPeriod()));
            this.addCustomProperty(vEventProperties, "STORE-KEY-REMINDER", new ParameterList(), String.valueOf(eventDTO.getStoreKey()));
        }
        if (StringUtils.isNotEmpty(eventDTO.getCustomEventTypeId())) {
            this.addCustomProperty(vEventProperties, "CUSTOM-EVENTTYPE-ID", new ParameterList(), String.valueOf(eventDTO.getCustomEventTypeId()));
        }
        vEventProperties.add(new Uid(eventDTO.getVeventUuid()));
        vEventProperties.add(new Description(StringUtils.defaultString(eventDTO.getDescription())));
        if (StringUtils.isNotBlank(eventDTO.getLocation())) {
            vEventProperties.add(new Location(eventDTO.getLocation()));
        }
        if (StringUtils.isNotBlank(eventDTO.getUrl())) {
            try {
                URI uri = new URI(eventDTO.getUrl());
                vEventProperties.add(new Url(uri));
            }
            catch (URISyntaxException e) {
                LOG.error("Unable to construct URI object for {}", (Object)eventDTO.getUrl());
            }
        }
        if (StringUtils.isNotBlank(eventDTO.getOrganiser())) {
            String userKey = eventDTO.getOrganiser();
            ParameterList parameterList = new ParameterList();
            parameterList.add(new XParameter("X-CONFLUENCE-USER-KEY", userKey));
            String userDisplayUrl = String.format("%s/display/~%s", this.settingsManager.getGlobalSettings().getBaseUrl(), HtmlUtil.urlEncode((String)userKey));
            try {
                vEventProperties.add(new Organizer(parameterList, userDisplayUrl));
            }
            catch (URISyntaxException e) {
                LOG.error("Unable to parse user URL {}", (Object)userDisplayUrl);
            }
        }
        if (StringUtils.isNotBlank(eventDTO.getRecurrenceRule())) {
            try {
                vEventProperties.add(new RRule(eventDTO.getRecurrenceRule()));
                this.addExclusionDates(vEvent, subCalendarTimeZone, this.activeObjectsServiceWrapper.getRecurrenceExclusions(new EventEntityDTO(eventDTO.getEventId())));
            }
            catch (ParseException e) {
                LOG.error("Unable to parse recurrence rule {}", (Object)eventDTO.getRecurrenceRule());
            }
        }
        if (eventDTO.getRecurrenceIdTimestamp() > 0L) {
            DateTimeZone timeZone = eventDTO.isAllDay() ? DateTimeZone.UTC : subCalendarTimeZone;
            Date recurrenceDate = eventDTO.isAllDay() ? new Date(eventDTO.getRecurrenceIdTimestamp()) : this.jodaIcal4jDateTimeConverter.toIcal4jDateTime(new DateTime(eventDTO.getRecurrenceIdTimestamp(), timeZone));
            vEventProperties.add(new RecurrenceId(recurrenceDate));
        }
        vEventProperties.add(new Created(new net.fortuna.ical4j.model.DateTime(eventDTO.getCreated())));
        vEventProperties.add(new LastModified(new net.fortuna.ical4j.model.DateTime(eventDTO.getLastModified())));
        vEventProperties.add(new Sequence(eventDTO.getSequence()));
        return vEvent;
    }

    private Map<String, BiFunction<SubCalendarEvent, VEvent, PropertyList<Property>>> getVEventFieldMapper(DateTimeZone subCalendarTimeZoneJoda) {
        DateTime currentTime = new DateTime().withZone(DateTimeZone.forOffsetHours((int)0));
        HashedMap fieldMapper = new HashedMap();
        fieldMapper.put("UID", (subCalendarEvent, vEvent) -> this.toPropertiesList(new Uid(subCalendarEvent.getUid())));
        fieldMapper.put("EXDATE", (subCalendarEvent, vEvent) -> {
            List<ExDate> exDates = subCalendarEvent.getExDates();
            if (exDates == null) {
                return this.toPropertiesList(new Property[0]);
            }
            if (!subCalendarEvent.isAllDay()) {
                exDates.forEach(exDate -> exDate.getDates().setUtc(true));
            }
            return (PropertyList)exDates;
        });
        fieldMapper.put("RECURRENCE-ID", (subCalendarEvent, vEvent) -> {
            DateTime originalStartTime;
            RecurrenceId returnProperty = null;
            if (subCalendarEvent.isEditAllInRecurrenceSeries()) {
                return new PropertyList();
            }
            if (StringUtils.isNotEmpty(subCalendarEvent.getRecurrenceId())) {
                try {
                    TimeZone calendarTimezone = this.jodaIcal4jTimeZoneMapper.toIcal4jTimeZone(subCalendarTimeZoneJoda.getID());
                    Date recurrenceDate = subCalendarEvent.isAllDay() ? new Date(subCalendarEvent.getRecurrenceId()) : new net.fortuna.ical4j.model.DateTime(subCalendarEvent.getRecurrenceId(), calendarTimezone);
                    RecurrenceId recurrenceId = new RecurrenceId(recurrenceDate);
                    return this.toPropertiesList(recurrenceId);
                }
                catch (ParseException e) {
                    LOG.error("Could not parse recurrence id. Fallback to OriginalStartTime to calculate recurrence ID", (Throwable)e);
                }
            }
            if (null != (originalStartTime = subCalendarEvent.getOriginalStartTime())) {
                returnProperty = subCalendarEvent.isAllDay() ? new RecurrenceId(this.jodaIcal4jDateTimeConverter.toIcal4jDate(originalStartTime.withZoneRetainFields(subCalendarTimeZoneJoda))) : new RecurrenceId(this.jodaIcal4jDateTimeConverter.toIcal4jDateTime(originalStartTime.withZoneRetainFields(subCalendarTimeZoneJoda)));
            }
            return this.toPropertiesList(returnProperty);
        });
        fieldMapper.put("CATEGORIES", (subCalendarEvent, vEvent) -> {
            if (StringUtils.isEmpty(subCalendarEvent.getEventTypeName())) {
                return this.toPropertiesList(new Property[0]);
            }
            return this.toPropertiesList(new Categories(subCalendarEvent.getEventTypeName()));
        });
        fieldMapper.put("DTSTAMP", (subCalendarEvent, vEvent) -> this.toPropertiesList(new DtStamp(this.jodaIcal4jDateTimeConverter.toIcal4jDateTime(currentTime))));
        fieldMapper.put("CREATED", (subCalendarEvent, vEvent) -> this.toPropertiesList(new Created(this.jodaIcal4jDateTimeConverter.toIcal4jDateTime(currentTime))));
        fieldMapper.put("DURATION", (subCalendarEvent, vEvent) -> this.toPropertiesList(new Property[0]));
        fieldMapper.put("DTSTART", (subCalendarEvent, vEvent) -> this.toPropertiesList(new DtStart(subCalendarEvent.isAllDay() ? this.jodaIcal4jDateTimeConverter.toIcal4jDate(subCalendarEvent.getStartTime().withZoneRetainFields(subCalendarTimeZoneJoda)) : this.jodaIcal4jDateTimeConverter.toIcal4jDateTime(subCalendarEvent.getStartTime().withZone(subCalendarTimeZoneJoda)))));
        fieldMapper.put("DTEND", (subCalendarEvent, vEvent) -> this.toPropertiesList(new DtEnd(subCalendarEvent.isAllDay() ? this.jodaIcal4jDateTimeConverter.toIcal4jDate(subCalendarEvent.getEndTime().withZoneRetainFields(subCalendarTimeZoneJoda)) : this.jodaIcal4jDateTimeConverter.toIcal4jDateTime(subCalendarEvent.getEndTime().withZone(subCalendarTimeZoneJoda)))));
        fieldMapper.put("SUMMARY", (subCalendarEvent, vEvent) -> this.toPropertiesList(new Summary(subCalendarEvent.getName())));
        fieldMapper.put("ATTENDEE", (subCalendarEvent, vEvent) -> {
            PropertyList<Attendee> propertyList = new PropertyList<Attendee>();
            Set<Invitee> invitees = subCalendarEvent.getInvitees();
            if (null != invitees && !invitees.isEmpty()) {
                for (Invitee invitee : invitees) {
                    if (invitee instanceof ConfluenceUserInvitee) {
                        propertyList.add(this.createAttendeeProperty(this.settingsManager, invitee.getId()));
                        continue;
                    }
                    if (!(invitee instanceof ExternalInvitee)) continue;
                    propertyList.add(new Attendee(URI.create("MAILTO:" + StringUtils.trim(invitee.getEmail()))));
                }
            } else {
                return null;
            }
            return propertyList;
        });
        fieldMapper.put("URL", (subCalendarEvent, vEvent) -> {
            try {
                return this.toPropertiesList(new Url(new URL(subCalendarEvent.getUrl()).toURI()));
            }
            catch (Throwable e) {
                return this.toPropertiesList(new Property[0]);
            }
        });
        fieldMapper.put("LOCATION", (subCalendarEvent, vEvent) -> this.toPropertiesList(new Location(subCalendarEvent.getLocation())));
        fieldMapper.put("DESCRIPTION", (subCalendarEvent, vEvent) -> this.toPropertiesList(new Description(subCalendarEvent.getDescription())));
        fieldMapper.put("SEQUENCE", (subCalendarEvent, vEvent) -> {
            Object oldSequence = vEvent.getProperty("SEQUENCE");
            return this.toPropertiesList(new Sequence(null == oldSequence ? 1 : Integer.parseInt(((Content)oldSequence).getValue()) + 1));
        });
        fieldMapper.put("LAST-MODIFIED", (subCalendarEvent, vEvent) -> this.toPropertiesList(new LastModified(this.jodaIcal4jDateTimeConverter.toIcal4jDateTime(new DateTime().withZone(DateTimeZone.forOffsetHours((int)0))))));
        fieldMapper.put("ORGANIZER", (subCalendarEvent, vEvent) -> {
            try {
                if (AuthenticatedUserThreadLocal.get() == null) {
                    return this.toPropertiesList(new Property[0]);
                }
                return this.toPropertiesList(this.createOrganizerProperty(this.settingsManager, AuthenticatedUserThreadLocal.get()));
            }
            catch (URISyntaxException e) {
                return this.toPropertiesList(new Property[0]);
            }
        });
        fieldMapper.put("RRULE", (subCalendarEvent, vEvent) -> {
            RRule rruleProperty = null;
            try {
                rruleProperty = new RRule(StringUtils.defaultString(subCalendarEvent.getRruleStr(), ""));
            }
            catch (ParseException e) {
                LOG.debug("Unable to parse RruleStr value {} as RRULE", (Object)subCalendarEvent.getRruleStr(), (Object)e);
            }
            catch (IllegalArgumentException e) {
                LOG.debug("Unable to parse RruleStr value {} as RRULE", (Object)subCalendarEvent.getRruleStr(), (Object)e);
                return null;
            }
            return this.toPropertiesList(rruleProperty);
        });
        return fieldMapper;
    }

    private PropertyList<Property> toPropertiesList(Property ... properties) {
        PropertyList<Property> propertyList = new PropertyList<Property>();
        propertyList.addAll(Lists.newArrayList((Object[])properties).stream().filter(Objects::nonNull).collect(Collectors.toList()));
        return propertyList;
    }

    private void addAttendees(VEvent vEvent, InviteeEntity[] inviteeEntities) {
        if (inviteeEntities != null && inviteeEntities.length > 0) {
            PropertyList<Property> vEventProperties = vEvent.getProperties();
            for (InviteeEntity inviteeEntity : inviteeEntities) {
                ConfluenceUser invitee = this.userAccessor.getUserByKey(new UserKey(inviteeEntity.getInviteeId()));
                if (invitee == null) continue;
                String inviteeId = inviteeEntity.getInviteeId();
                ParameterList parameterList = new ParameterList();
                parameterList.add(new XParameter("X-CONFLUENCE-USER-KEY", inviteeId));
                String inviteeDisplayUrl = String.format("%s/display/~%s", this.settingsManager.getGlobalSettings().getBaseUrl(), HtmlUtil.urlEncode((String)inviteeId));
                try {
                    vEventProperties.add(new Attendee(parameterList, inviteeDisplayUrl));
                }
                catch (URISyntaxException e) {
                    LOG.error("Unable to parse invitee display url {}", (Object)inviteeId);
                }
            }
        }
    }

    private void addExclusionDates(VEvent vEvent, DateTimeZone subCalendarTimeZone, EventRecurrenceExclusionEntity[] eventRecurrenceExclusionEntities) {
        if (eventRecurrenceExclusionEntities != null && eventRecurrenceExclusionEntities.length > 0) {
            PropertyList<Property> vEventProperties = vEvent.getProperties();
            for (EventRecurrenceExclusionEntity eventRecurrenceExclusionEntity : eventRecurrenceExclusionEntities) {
                DateList excludedDates;
                if (eventRecurrenceExclusionEntity.isAllDay()) {
                    excludedDates = new DateList(Value.DATE);
                    excludedDates.add(new Date(eventRecurrenceExclusionEntity.getExclusion()));
                } else {
                    excludedDates = new DateList(Value.DATE_TIME);
                    excludedDates.add(this.jodaIcal4jDateTimeConverter.toIcal4jDateTime(new DateTime(eventRecurrenceExclusionEntity.getExclusion(), subCalendarTimeZone)));
                }
                vEventProperties.add(new ExDate(excludedDates));
            }
        }
    }

    private void addCustomUserProperty(SettingsManager settingsManager, PropertyList<Property> propertyList, String propertyName, String userKey) {
        ParameterList parameterList = new ParameterList();
        parameterList.add(new XParameter("X-CONFLUENCE-USER-KEY", userKey));
        this.addCustomProperty(propertyList, propertyName, parameterList, String.format("%s/display/~%s", settingsManager.getGlobalSettings().getBaseUrl(), HtmlUtil.urlEncode((String)userKey)));
    }

    private void addCustomProperty(PropertyList<Property> propertyList, String propertyName, ParameterList parameterList, String value) {
        try {
            propertyList.add(new XProperty(propertyName, parameterList, value));
        }
        catch (Exception errorCreatingProperty) {
            LOG.error(String.format("Unable to create property %s with value %s", propertyName, value), (Throwable)errorCreatingProperty);
        }
    }

    private Attendee createAttendeeProperty(SettingsManager settingsManager, String inviteeId) {
        ParameterList parameterList = new ParameterList();
        parameterList.add(new XParameter("X-CONFLUENCE-USER-KEY", inviteeId));
        return new Attendee(parameterList, URI.create(String.format("%s/display/~%s", settingsManager.getGlobalSettings().getBaseUrl(), HtmlUtil.urlEncode((String)inviteeId))));
    }

    private Organizer createOrganizerProperty(SettingsManager settingsManager, ConfluenceUser currentUser) throws URISyntaxException {
        ParameterList parameterList = new ParameterList();
        parameterList.add(new XParameter("X-CONFLUENCE-USER-KEY", currentUser.getKey().toString()));
        return new Organizer(parameterList, URI.create(String.format("%s/display/~%s", settingsManager.getGlobalSettings().getBaseUrl(), HtmlUtil.urlEncode((String)currentUser.getName()))));
    }

    private RRule getRruleFromSubCalendarEvent(SubCalendarEvent subCalendarEvent) {
        SubCalendarEvent.Repeat r = subCalendarEvent.getRepeat();
        if (null != r && StringUtils.isNotBlank(r.getFreq())) {
            HashMap<String, String> rruleParams = new HashMap<String, String>();
            StringBuilder rruleValueBuilder = new StringBuilder();
            rruleParams.put("FREQ", r.getFreq());
            rruleParams.put("WKST", "SU");
            if (StringUtils.isNotBlank(r.getByDay())) {
                rruleParams.put("BYDAY", r.getByDay());
            }
            if (StringUtils.isNotBlank(r.getInterval())) {
                rruleParams.put("INTERVAL", r.getInterval());
            }
            if (StringUtils.isNotBlank(r.getUntil())) {
                rruleParams.put("UNTIL", r.getUntil());
            }
            try {
                for (Map.Entry e : rruleParams.entrySet()) {
                    if (rruleValueBuilder.length() > 0) {
                        rruleValueBuilder.append(';');
                    }
                    rruleValueBuilder.append((String)e.getKey()).append('=').append((String)e.getValue());
                }
                return new RRule(rruleValueBuilder.toString());
            }
            catch (ParseException pe) {
                LOG.error("Unable to parse repeat value " + rruleParams + " as RRULE", (Throwable)pe);
            }
        }
        return null;
    }
}

