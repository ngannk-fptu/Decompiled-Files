/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Option
 *  com.atlassian.xwork.FileUploadUtils
 *  com.atlassian.xwork.FileUploadUtils$FileUploadException
 *  com.atlassian.xwork.FileUploadUtils$UploadedFile
 *  com.google.common.base.Optional
 *  com.google.common.collect.Sets
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.xwork;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.CalendarPermissionManager;
import com.atlassian.confluence.extra.calendar3.JodaIcal4jTimeZoneMapper;
import com.atlassian.confluence.extra.calendar3.calendarstore.generic.KeyStoreToEventTypeMapper;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarImported;
import com.atlassian.confluence.extra.calendar3.model.CustomEventType;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendar;
import com.atlassian.confluence.extra.calendar3.util.CalendarImportUtil;
import com.atlassian.confluence.extra.calendar3.util.Ical4jIoUtil;
import com.atlassian.confluence.extra.calendar3.util.UserKeyMigratorTransformer;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Option;
import com.atlassian.xwork.FileUploadUtils;
import com.google.common.collect.Sets;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.XComponent;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImportSubCalendarAction
extends ConfluenceActionSupport {
    private static final Logger LOG = LoggerFactory.getLogger(ImportSubCalendarAction.class);
    private static final Set<String> SUBCALENDAR_TYPES_TO_REINTERPRET_AS_OTHER = Collections.unmodifiableSet(Sets.newHashSet((Object[])new String[]{"people", "local", "subscription"}));
    private static final String TIME_ZONE_REQUIRED = "timeZoneRequired";
    private boolean needsUserSpecifiedTimeZone = false;
    private String subCalendarId;
    private String calendarId;
    private String name;
    private String description;
    private String spaceKey;
    private String timeZoneId;
    private UserAccessor userAccessor;
    private CalendarManager calendarManager;
    private CalendarPermissionManager calendarPermissionManager;
    private JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper;
    private Calendar calendarToImport;
    private CalendarImportUtil calendarImportUtil;
    private EventPublisher eventPublisher;
    private List<String> eventSeriesErrors = new ArrayList<String>();
    private List<String> eventsOfTypeErrors = new ArrayList<String>();
    private List<String> customEventTypeErrors = new ArrayList<String>();

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public void setCalendarManager(CalendarManager calendarManager) {
        this.calendarManager = calendarManager;
    }

    public void setCalendarPermissionManager(CalendarPermissionManager calendarPermissionManager) {
        this.calendarPermissionManager = calendarPermissionManager;
    }

    public void setJodaIcal4jTimeZoneMapper(JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper) {
        this.jodaIcal4jTimeZoneMapper = jodaIcal4jTimeZoneMapper;
    }

    public String getSubCalendarId() {
        return this.subCalendarId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public void setTimeZoneId(String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public void setCalendarImportUtil(CalendarImportUtil calendarImportUtil) {
        this.calendarImportUtil = calendarImportUtil;
    }

    public boolean isPermitted() {
        return this.calendarPermissionManager.hasEditSubCalendarPrivilege(this.getAuthenticatedUser());
    }

    public void validate() {
        InputStream icalendarInput;
        if (!this.calendarPermissionManager.hasEditSubCalendarPrivilege(this.getAuthenticatedUser())) {
            this.addActionError(this.getText("calendar3.error.notpermitted"));
        }
        if (null == (icalendarInput = this.getIcalendarInput())) {
            this.addFieldError("file_0", this.getText("calendar3.error.import.uploadedfilenotfound"));
        } else {
            try {
                this.calendarToImport = Ical4jIoUtil.newCalendarBuilder().build(icalendarInput);
                String subCalendarName = this.getCalendarName();
                if (StringUtils.isBlank(subCalendarName)) {
                    this.addFieldError("name", this.getText("calendar3.error.blank"));
                }
                SubCalendar subCalendar = new SubCalendar();
                subCalendar.setType("parent");
                subCalendar.setName(subCalendarName);
                subCalendar.setDescription(this.description);
                subCalendar.setColor(this.calendarManager.getRandomCalendarColor(new String[0]));
                subCalendar.setSpaceKey(this.spaceKey);
                subCalendar.setTimeZoneId(this.getTimeZoneId());
                HashMap<String, List<String>> fieldErrors = new HashMap<String, List<String>>();
                this.calendarManager.validateSubCalendar(subCalendar, fieldErrors);
                for (Map.Entry fieldErrorsEntry : fieldErrors.entrySet()) {
                    if (StringUtils.equals("timeZoneId", (String)fieldErrorsEntry.getKey())) {
                        this.needsUserSpecifiedTimeZone = true;
                        continue;
                    }
                    for (String errorMessage : (List)fieldErrorsEntry.getValue()) {
                        this.addFieldError((String)fieldErrorsEntry.getKey(), errorMessage);
                    }
                }
            }
            catch (IOException ioe) {
                this.addFieldError("file_0", this.getText("calendar3.error.import.uploadeddatanotical"));
            }
            catch (ParserException notIcalendar) {
                LOG.debug("Unable to read uploaded file as iCalendar", (Throwable)notIcalendar);
                this.addFieldError("file_0", this.getText("calendar3.error.import.uploadeddatanotical"));
            }
        }
    }

    private String getTimeZoneId() {
        if (this.timeZoneId != null) {
            return this.timeZoneId;
        }
        String timeZoneId = this.calendarImportUtil.getTimeZoneFromCalendar(this.calendarToImport);
        return null == timeZoneId ? null : this.jodaIcal4jTimeZoneMapper.getTimeZoneIdForAlias(timeZoneId);
    }

    private String getCalendarName() {
        Object calendarNameProperty = this.calendarToImport.getProperty("X-WR-CALNAME");
        return StringUtils.isNotBlank(this.name) ? this.name : (null == calendarNameProperty ? this.name : StringUtils.defaultIfEmpty(((Content)calendarNameProperty).getValue(), this.name));
    }

    InputStream getIcalendarInput() {
        try {
            FileUploadUtils.UploadedFile uploadedFile = FileUploadUtils.getSingleUploadedFile();
            if (null != uploadedFile) {
                return new BufferedInputStream(new FileInputStream(uploadedFile.getFile()));
            }
        }
        catch (FileUploadUtils.FileUploadException uploadException) {
            LOG.error("Unable to process uploaded iCalendar file", (Throwable)uploadException);
            for (String error : uploadException.getErrors()) {
                this.addActionError(error);
            }
        }
        catch (FileNotFoundException fnfe) {
            LOG.error("Unable to find the File object of the uploaded file", (Throwable)fnfe);
        }
        return null;
    }

    public String execute() throws Exception {
        if (this.needsUserSpecifiedTimeZone) {
            return TIME_ZONE_REQUIRED;
        }
        if (StringUtils.isNotBlank(this.calendarId)) {
            return this.importIntoExistingCalendar();
        }
        PersistedSubCalendar parentSubCalendar = this.createParentSubCalendar();
        Calendar normalisedCalendar = this.calendarImportUtil.normalize(this.calendarToImport, parentSubCalendar.getTimeZoneId(), parentSubCalendar.getName(), parentSubCalendar.getDescription());
        normalisedCalendar = new UserKeyMigratorTransformer(this.settingsManager.getGlobalSettings().getBaseUrl(), this.userAccessor).transform(normalisedCalendar);
        ComponentList vEventComponents = normalisedCalendar.getComponents("VEVENT");
        PropertyList<Property> customEventProperties = normalisedCalendar.getProperties("X-CONFLUENCE-CUSTOM-EVENT-TYPE");
        HashMap<String, String> customEventTypesOldIdToNewIdMap = new HashMap<String, String>();
        boolean someContentGotCreated = false;
        if (!customEventProperties.isEmpty() && !((Property)customEventProperties.get(0)).getValue().equals(Boolean.FALSE.toString())) {
            customEventProperties.forEach(customEventProperty -> {
                String oldId = ((Content)customEventProperty.getParameter("X-CONFLUENCE-CUSTOM-TYPE-ID")).getValue().replace("\\n", "");
                String title = ((Content)customEventProperty.getParameter("X-CONFLUENCE-CUSTOM-TYPE-TITLE")).getValue();
                String icon = ((Content)customEventProperty.getParameter("X-CONFLUENCE-CUSTOM-TYPE-ICON")).getValue();
                int periodInMins = Integer.parseInt(((Content)customEventProperty.getParameter("X-CONFLUENCE-CUSTOM-TYPE-REMINDER-DURATION")).getValue());
                try {
                    CustomEventType customEventType = this.calendarManager.updateCustomEventType(parentSubCalendar, null, title, icon, periodInMins);
                    customEventTypesOldIdToNewIdMap.put(oldId, customEventType.getCustomEventTypeId());
                }
                catch (Exception errorCreatingCustomEvent) {
                    LOG.error(String.format("Unable to create custom events %s", customEventProperty.getParameters().toString()), (Throwable)errorCreatingCustomEvent);
                    this.addActionError(this.getText("calendar3.error.import.someeventsnotimported", new Object[]{this.getExceptionMessageRecursively(errorCreatingCustomEvent)}));
                    this.eventsOfTypeErrors.add(customEventProperty.getParameters().toString());
                }
            });
        }
        if (!vEventComponents.isEmpty()) {
            Map<String, List<VEvent>> eventsByType = this.getSubCalendarEventsByType(normalisedCalendar);
            for (Map.Entry<String, List<VEvent>> eventsOfType : eventsByType.entrySet()) {
                String eventType = eventsOfType.getKey();
                try {
                    if (eventType.equals("custom")) {
                        Map<String, List<VEvent>> eventsByCustomEventId = this.groupCustomEventsById(eventsOfType.getValue());
                        for (Map.Entry<String, List<VEvent>> eventsWithCustomId : eventsByCustomEventId.entrySet()) {
                            String oldCustomId = eventsWithCustomId.getKey();
                            List<VEvent> vEventsForCustomId = eventsWithCustomId.getValue();
                            String customEventTypeId = (String)customEventTypesOldIdToNewIdMap.get(oldCustomId);
                            if (Objects.isNull(customEventTypeId)) {
                                VEvent firstVEvent = vEventsForCustomId.get(0);
                                String customEventTitle = ((Content)firstVEvent.getProperty("CATEGORIES")).getValue();
                                CustomEventType customEventType = this.calendarManager.updateCustomEventType(parentSubCalendar, null, customEventTitle, "events", 0);
                                customEventTypeId = customEventType.getCustomEventTypeId();
                                customEventTypesOldIdToNewIdMap.put(oldCustomId, customEventTypeId);
                            }
                            PersistedSubCalendar customChildSubCalendar = this.createCustomChildSubCalendar(parentSubCalendar, customEventTypeId);
                            Calendar childSubCalendarContent = this.calendarManager.getSubCalendarContent(customChildSubCalendar);
                            ComponentList<CalendarComponent> childSubCalendarComponents = childSubCalendarContent.getComponents();
                            childSubCalendarComponents.addAll(vEventsForCustomId);
                            this.calendarManager.setSubCalendarContent(customChildSubCalendar, childSubCalendarContent);
                        }
                    } else {
                        PersistedSubCalendar childSubCalendar = this.createLocallyManagedChildSubCalendar(parentSubCalendar, eventType);
                        Calendar childSubCalendarContent = this.calendarManager.getSubCalendarContent(childSubCalendar);
                        ComponentList<CalendarComponent> childSubCalendarComponents = childSubCalendarContent.getComponents();
                        childSubCalendarComponents.addAll(eventsOfType.getValue());
                        this.calendarManager.setSubCalendarContent(childSubCalendar, childSubCalendarContent);
                    }
                    someContentGotCreated = true;
                }
                catch (Exception errorImportingSubCalendar) {
                    LOG.error(String.format("Unable to import events of type %s", eventType), (Throwable)errorImportingSubCalendar);
                    this.addActionError(this.getText("calendar3.error.import.someeventsnotimported", new Object[]{this.getExceptionMessageRecursively(errorImportingSubCalendar)}));
                    this.eventsOfTypeErrors.add(eventType);
                }
            }
        }
        someContentGotCreated = this.importEventSeries(parentSubCalendar, normalisedCalendar.getComponents("X-EVENT-SERIES")) || someContentGotCreated;
        this.subCalendarId = parentSubCalendar.getId();
        this.calendarToImport = null;
        if (someContentGotCreated) {
            this.eventPublisher.publish((Object)new SubCalendarImported((Object)this, AuthenticatedUserThreadLocal.get(), StringUtils.isNotBlank(this.spaceKey)));
        }
        if (this.getActionErrors().isEmpty()) {
            return "success";
        }
        return "error";
    }

    public String importIntoExistingCalendar() throws Exception {
        boolean someContentGotCreated = false;
        com.google.common.base.Optional<PersistedSubCalendar> optionalParentSubCalendar = this.calendarManager.getPersistedSubCalendar(this.calendarId);
        if (!optionalParentSubCalendar.isPresent()) {
            return "error";
        }
        PersistedSubCalendar parentSubCalendar = (PersistedSubCalendar)optionalParentSubCalendar.get();
        Set<CustomEventType> existingCustomEventTypes = parentSubCalendar.getCustomEventTypes();
        Calendar normalisedCalendar = this.calendarImportUtil.normalize(this.calendarToImport, parentSubCalendar.getTimeZoneId(), parentSubCalendar.getName(), parentSubCalendar.getDescription());
        normalisedCalendar = new UserKeyMigratorTransformer(this.settingsManager.getGlobalSettings().getBaseUrl(), this.userAccessor).transform(normalisedCalendar);
        ComponentList vEventComponents = normalisedCalendar.getComponents("VEVENT");
        PropertyList<Property> newCustomEventProperties = normalisedCalendar.getProperties("X-CONFLUENCE-CUSTOM-EVENT-TYPE");
        Map<Object, Object> newCustomEventTypesMap = new HashMap();
        if (!newCustomEventProperties.isEmpty() && !((Property)newCustomEventProperties.get(0)).getValue().equals(Boolean.FALSE.toString())) {
            newCustomEventTypesMap = newCustomEventProperties.stream().map(customEventProperty -> new CustomEventType(((Content)customEventProperty.getParameter("X-CONFLUENCE-CUSTOM-TYPE-ID")).getValue().replace("\\n", ""), ((Content)customEventProperty.getParameter("X-CONFLUENCE-CUSTOM-TYPE-TITLE")).getValue(), ((Content)customEventProperty.getParameter("X-CONFLUENCE-CUSTOM-TYPE-ICON")).getValue(), null, null, Integer.parseInt(((Content)customEventProperty.getParameter("X-CONFLUENCE-CUSTOM-TYPE-REMINDER-DURATION")).getValue()))).collect(Collectors.toMap(CustomEventType::getTitle, customEventType -> customEventType));
        }
        if (!vEventComponents.isEmpty()) {
            Map<String, List<VEvent>> eventsByType = this.getSubCalendarEventsByType(normalisedCalendar);
            for (Map.Entry<String, List<VEvent>> eventsOfType : eventsByType.entrySet()) {
                String eventType = eventsOfType.getKey();
                try {
                    if (eventType.equals("custom")) {
                        Map<String, List<VEvent>> eventsByCustomEventName = this.groupCustomEventsByName(eventsOfType.getValue());
                        for (Map.Entry<String, List<VEvent>> eventsWithCustomName : eventsByCustomEventName.entrySet()) {
                            PersistedSubCalendar subCalendarForCustomEvent;
                            CustomEventType customEventType2;
                            String customName = eventsWithCustomName.getKey();
                            List<VEvent> vEventsForCustomName = eventsWithCustomName.getValue();
                            Optional<CustomEventType> matchingExistingCustomEvent = existingCustomEventTypes.stream().filter(customEventType -> customEventType.getTitle().equals(customName)).findFirst();
                            if (matchingExistingCustomEvent.isPresent()) {
                                customEventType2 = matchingExistingCustomEvent.get();
                                Option<PersistedSubCalendar> subCalendarForCustomEventOptional = this.calendarManager.getChildSubCalendarByCustomEventTypeId(parentSubCalendar, customEventType2.getCustomEventTypeId());
                                subCalendarForCustomEvent = (PersistedSubCalendar)subCalendarForCustomEventOptional.get();
                            } else if (newCustomEventTypesMap.containsKey(customName)) {
                                CustomEventType customEventFromImport = (CustomEventType)newCustomEventTypesMap.get(customName);
                                customEventType2 = this.calendarManager.updateCustomEventType(parentSubCalendar, null, customName, customEventFromImport.getIcon(), customEventFromImport.getPeriodInMins());
                                subCalendarForCustomEvent = this.createCustomChildSubCalendar(parentSubCalendar, customEventType2.getCustomEventTypeId());
                            } else {
                                customEventType2 = this.calendarManager.updateCustomEventType(parentSubCalendar, null, customName, "other", 0);
                                newCustomEventTypesMap.put(customName, customEventType2);
                                subCalendarForCustomEvent = this.createCustomChildSubCalendar(parentSubCalendar, customEventType2.getCustomEventTypeId());
                            }
                            Calendar subCalendarContent = this.calendarManager.getSubCalendarContent(subCalendarForCustomEvent);
                            ComponentList<CalendarComponent> childSubCalendarComponents = subCalendarContent.getComponents();
                            childSubCalendarComponents.addAll(vEventsForCustomName);
                            this.calendarManager.setSubCalendarContent(subCalendarForCustomEvent, subCalendarContent);
                        }
                    } else {
                        Option<PersistedSubCalendar> existingChildSubCalendarOptional = this.calendarManager.getChildSubCalendarByStoreKey(parentSubCalendar, (String)KeyStoreToEventTypeMapper.mapper.inverse().get((Object)eventType));
                        PersistedSubCalendar childSubCalendar = existingChildSubCalendarOptional.isEmpty() ? this.createLocallyManagedChildSubCalendar(parentSubCalendar, eventType) : (PersistedSubCalendar)existingChildSubCalendarOptional.get();
                        Calendar childSubCalendarContent = this.calendarManager.getSubCalendarContent(childSubCalendar);
                        ComponentList<CalendarComponent> childSubCalendarComponents = childSubCalendarContent.getComponents();
                        childSubCalendarComponents.addAll(eventsOfType.getValue());
                        this.calendarManager.setSubCalendarContent(childSubCalendar, childSubCalendarContent);
                    }
                    someContentGotCreated = true;
                }
                catch (Exception errorImportingSubCalendar) {
                    LOG.error(String.format("Unable to import events of type %s", eventType), (Throwable)errorImportingSubCalendar);
                    this.addActionError(this.getText("calendar3.error.import.someeventsnotimported", new Object[]{this.getExceptionMessageRecursively(errorImportingSubCalendar)}));
                    this.eventsOfTypeErrors.add(eventType);
                }
            }
        }
        someContentGotCreated = this.importEventSeries(parentSubCalendar, normalisedCalendar.getComponents("X-EVENT-SERIES")) || someContentGotCreated;
        this.subCalendarId = parentSubCalendar.getId();
        this.calendarToImport = null;
        if (someContentGotCreated) {
            this.eventPublisher.publish((Object)new SubCalendarImported((Object)this, AuthenticatedUserThreadLocal.get(), StringUtils.isNotBlank(this.spaceKey)));
        }
        if (this.getActionErrors().isEmpty()) {
            return "success";
        }
        return "error";
    }

    private boolean importEventSeries(PersistedSubCalendar parentSubCalendar, List<XComponent> eventSerieses) throws Exception {
        Iterator<XComponent> iterator;
        if (!eventSerieses.isEmpty() && (iterator = eventSerieses.iterator()).hasNext()) {
            XComponent eventSeries = iterator.next();
            try {
                this.createSubCalendar(parentSubCalendar, ((Content)eventSeries.getProperty("X-CONFLUENCE-SUBCALENDAR-TYPE")).getValue(), ((Content)eventSeries.getProperty("SUMMARY")).getValue(), StringUtils.defaultString(((Content)eventSeries.getProperty("DESCRIPTION")).getValue()), "", ((Content)eventSeries.getProperty("URL")).getValue(), parentSubCalendar.getTimeZoneId(), null);
                return true;
            }
            catch (Exception ex) {
                LOG.error(String.format("Unable to import eventSeries %s", eventSeries.getName()), (Throwable)ex);
                this.addActionError(this.getText("calendar3.error.import.someeventsnotimported", new Object[]{this.getExceptionMessageRecursively(ex)}));
                this.eventSeriesErrors.add(eventSeries.getName());
                throw ex;
            }
        }
        return false;
    }

    public Collection<String> getEventSeriesErrors() {
        return this.eventSeriesErrors;
    }

    public Collection<String> getEventsOfTypeErrors() {
        return this.eventsOfTypeErrors;
    }

    public List<String> getCustomEventTypeErrors() {
        return this.customEventTypeErrors;
    }

    private String getExceptionMessageRecursively(Throwable anError) {
        String errorMessage = anError.getMessage();
        if (StringUtils.isNotBlank(errorMessage)) {
            return errorMessage;
        }
        Throwable cause = anError.getCause();
        if (cause != null) {
            this.getExceptionMessageRecursively(cause);
        }
        return anError.getClass().getName();
    }

    protected Map<String, List<VEvent>> getSubCalendarEventsByType(Calendar normalisedCalendar) {
        HashMap<String, List<VEvent>> eventsByType = new HashMap<String, List<VEvent>>();
        Object calendarTypeProperty = normalisedCalendar.getProperty("X-CONFLUENCE-SUBCALENDAR-TYPE");
        if (calendarTypeProperty != null) {
            eventsByType.put(this.reinterpretEventType(((Content)calendarTypeProperty).getValue()), normalisedCalendar.getComponents("VEVENT"));
            return eventsByType;
        }
        ComponentList allVEventComponents = normalisedCalendar.getComponents("VEVENT");
        for (VEvent vEvent : allVEventComponents) {
            ArrayList<VEvent> eventsOfParticularType;
            String eventType = "other";
            Object eventTypeProperty = vEvent.getProperty("X-CONFLUENCE-SUBCALENDAR-TYPE");
            Object customEventIdProperty = vEvent.getProperty("X-CONFLUENCE-CUSTOM-TYPE-ID");
            Object categoriesProperty = vEvent.getProperty("CATEGORIES");
            if (eventTypeProperty != null) {
                eventType = this.reinterpretEventType(((Content)eventTypeProperty).getValue());
            }
            if (eventType.equals("custom") && (Objects.isNull(customEventIdProperty) || Objects.isNull(categoriesProperty))) {
                eventType = "other";
            }
            if (!eventsByType.containsKey(eventType)) {
                eventsOfParticularType = new ArrayList<VEvent>();
                eventsByType.put(eventType, eventsOfParticularType);
            } else {
                eventsOfParticularType = (ArrayList<VEvent>)eventsByType.get(eventType);
            }
            eventsOfParticularType.add(vEvent);
        }
        return eventsByType;
    }

    protected Map<String, List<VEvent>> groupCustomEventsById(List<VEvent> customTypeVEvents) {
        HashMap<String, List<VEvent>> customIdToVEventHashMap = new HashMap<String, List<VEvent>>();
        customTypeVEvents.forEach(vEvent -> {
            String customEventTypeId = ((Content)vEvent.getProperty("X-CONFLUENCE-CUSTOM-TYPE-ID")).getValue();
            List vEventListForCustomId = customIdToVEventHashMap.getOrDefault(customEventTypeId, new ArrayList());
            vEventListForCustomId.add(vEvent);
            customIdToVEventHashMap.put(customEventTypeId, vEventListForCustomId);
        });
        return customIdToVEventHashMap;
    }

    protected Map<String, List<VEvent>> groupCustomEventsByName(List<VEvent> customTypeVEvents) {
        HashMap<String, List<VEvent>> customEventNameToVEventHashMap = new HashMap<String, List<VEvent>>();
        customTypeVEvents.forEach(vEvent -> {
            String customEventTypeId = ((Content)vEvent.getProperty("CATEGORIES")).getValue();
            List vEventListForCustomId = customEventNameToVEventHashMap.getOrDefault(customEventTypeId, new ArrayList());
            vEventListForCustomId.add(vEvent);
            customEventNameToVEventHashMap.put(customEventTypeId, vEventListForCustomId);
        });
        return customEventNameToVEventHashMap;
    }

    private String reinterpretEventType(String originalType) {
        return SUBCALENDAR_TYPES_TO_REINTERPRET_AS_OTHER.contains(originalType) ? "other" : originalType;
    }

    private PersistedSubCalendar createLocallyManagedChildSubCalendar(PersistedSubCalendar parent, String type) throws Exception {
        return this.createSubCalendar(parent, type, parent.getName(), parent.getDescription(), parent.getSpaceKey(), null, parent.getTimeZoneId(), null);
    }

    private PersistedSubCalendar createCustomChildSubCalendar(PersistedSubCalendar parent, String customEventTypeId) throws Exception {
        return this.createSubCalendar(parent, "custom", parent.getName(), parent.getDescription(), parent.getSpaceKey(), null, parent.getTimeZoneId(), customEventTypeId);
    }

    private PersistedSubCalendar createParentSubCalendar() throws Exception {
        return this.createSubCalendar(null, "parent", this.getCalendarName(), this.description, this.spaceKey, null, this.getTimeZoneId(), null);
    }

    private PersistedSubCalendar createSubCalendar(PersistedSubCalendar parent, String type, String calendarName, String description, String spaceKey, String sourceLocation, String timeZoneId, String customEventTypeId) throws Exception {
        SubCalendar subCalendar = new SubCalendar();
        if (parent != null) {
            subCalendar.setParent(parent);
        }
        subCalendar.setType(type);
        subCalendar.setName(calendarName);
        subCalendar.setDescription(description);
        subCalendar.setColor(this.calendarManager.getRandomCalendarColor(parent != null ? parent.getColor() : null));
        subCalendar.setSpaceKey(spaceKey);
        subCalendar.setSourceLocation(sourceLocation);
        subCalendar.setTimeZoneId(timeZoneId);
        if (Objects.nonNull(customEventTypeId)) {
            subCalendar.setCustomEventTypeId(customEventTypeId);
        }
        return this.calendarManager.save(subCalendar);
    }
}

