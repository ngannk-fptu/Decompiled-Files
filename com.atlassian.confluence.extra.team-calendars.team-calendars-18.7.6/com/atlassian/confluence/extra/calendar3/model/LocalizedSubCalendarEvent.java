/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.google.common.base.Function
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Sets
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.apache.commons.lang3.StringUtils
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.joda.time.ReadableInstant
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.ISODateTimeFormat
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.model;

import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.extra.calendar3.CalendarSettingsManager;
import com.atlassian.confluence.extra.calendar3.model.ConfluenceUserInvitee;
import com.atlassian.confluence.extra.calendar3.model.Invitee;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import java.util.Locale;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
public class LocalizedSubCalendarEvent
extends SubCalendarEvent {
    private static final Logger LOG = LoggerFactory.getLogger(LocalizedSubCalendarEvent.class);
    private final DateTimeZone timeZone;
    private final Locale locale;
    private final FormatSettingsManager formatSettingsManager;
    private final boolean expandDates;
    private CalendarSettingsManager calendarSettingsManager;

    public LocalizedSubCalendarEvent() {
        this(null, DateTimeZone.getDefault(), Locale.getDefault(), null, null);
    }

    public LocalizedSubCalendarEvent(SubCalendarEvent toCopy, DateTimeZone userTimeZone, Locale locale, FormatSettingsManager formatSettingsManager, CalendarSettingsManager calendarSettingsManager) {
        this(toCopy, userTimeZone, locale, formatSettingsManager, true);
        this.calendarSettingsManager = calendarSettingsManager;
    }

    private LocalizedSubCalendarEvent(SubCalendarEvent toCopy, DateTimeZone userTimeZone, Locale locale, FormatSettingsManager formatSettingsManager, boolean expandDates) {
        if (null != toCopy) {
            this.setSubCalendar(toCopy.getSubCalendar());
            this.setUid(toCopy.getUid());
            this.setEventType(toCopy.getEventType());
            this.setRecurrenceId(toCopy.getRecurrenceId());
            this.setRruleStr(toCopy.getRruleStr());
            this.setName(toCopy.getName());
            this.setNameCollapseText(toCopy.getNameCollapseText());
            this.setNameCollapseIndex(toCopy.getNameCollapseIndex());
            this.setShortName(toCopy.getShortName());
            this.setUrl(toCopy.getUrl());
            this.setUrlAlias(toCopy.getUrlAlias());
            this.setDescription(toCopy.getDescription());
            this.setDescriptionRendered(toCopy.getDescriptionRendered());
            this.setLocation(toCopy.getLocation());
            this.setOriginalStartTime(toCopy.getOriginalStartTime());
            if (toCopy.isAllDay()) {
                DateTime oldStart = toCopy.getStartTime();
                this.setStartTime(new DateTime(oldStart.getYear(), oldStart.getMonthOfYear(), oldStart.getDayOfMonth(), 0, 0, 0, 0, userTimeZone));
                DateTime oldEnd = toCopy.getEndTime();
                this.setEndTime(new DateTime(oldEnd.getYear(), oldEnd.getMonthOfYear(), oldEnd.getDayOfMonth(), 0, 0, 0, 0, userTimeZone));
                DateTime originalStart = toCopy.getOriginalStartDate();
                this.setOriginalStartDate(new DateTime(originalStart.getYear(), originalStart.getMonthOfYear(), originalStart.getDayOfMonth(), 0, 0, 0, 0, userTimeZone));
                DateTime originalEnd = toCopy.getOriginalEndDate();
                this.setOriginalEndDate(new DateTime(originalEnd.getYear(), originalEnd.getMonthOfYear(), originalEnd.getDayOfMonth(), 0, 0, 0, 0, userTimeZone));
            } else {
                this.setStartTime(toCopy.getStartTime().withZone(userTimeZone));
                this.setEndTime(toCopy.getEndTime().withZone(userTimeZone));
                this.setOriginalStartDate(toCopy.getOriginalStartDate().withZone(userTimeZone));
                this.setOriginalEndDate(toCopy.getOriginalEndDate().withZone(userTimeZone));
            }
            SubCalendarEvent.Repeat repeatToCopy = toCopy.getRepeat();
            if (null != repeatToCopy) {
                this.setRepeat(new LocalizedRepeat(repeatToCopy));
            }
            this.setAllDay(toCopy.isAllDay());
            this.setEditable(toCopy.isEditable());
            this.setCalendarReloadRequiredOnUpdate(toCopy.isCalendarReloadRequiredOnUpdate());
            Set<Invitee> toCopyInvitees = toCopy.getInvitees();
            if (toCopyInvitees != null) {
                this.setInvitees(Sets.newLinkedHashSet((Iterable)Collections2.transform(toCopyInvitees, (Function)new Function<Invitee, Invitee>(){

                    public Invitee apply(Invitee invitee) {
                        if (invitee instanceof ConfluenceUserInvitee) {
                            return new ConfluenceUserInviteeForTeamCal1756((ConfluenceUserInvitee)invitee);
                        }
                        return invitee;
                    }
                })));
            }
            this.setIconUrl(toCopy.getIconUrl());
            this.setMediumIconUrl(toCopy.getMediumIconUrl());
            this.setIconLink(toCopy.getIconLink());
            this.setColorScheme(toCopy.getColorScheme());
            this.setTextColor(toCopy.getTextColor());
            this.setBorderColor(toCopy.getBorderColor());
            this.setSecondaryBorderColor(toCopy.getSecondaryBorderColor());
            this.setBackgroundColor(toCopy.getBackgroundColor());
            this.setClassName(toCopy.getClassName());
            this.setDisableResizing(toCopy.isDisableResizing());
            this.setExtraProperties(toCopy.getExtraProperties());
            this.setExtraPropertiesTemplate(toCopy.getExtraPropertiesTemplate());
            this.setCustomEventTypeId(toCopy.getCustomEventTypeId());
        }
        this.timeZone = userTimeZone;
        this.locale = locale;
        this.formatSettingsManager = formatSettingsManager;
        this.expandDates = expandDates;
    }

    @XmlElement(name="localizedStartDate")
    public String getLocalizedStartDate() {
        return DateTimeFormat.mediumDate().withLocale(this.locale).print((ReadableInstant)this.getStartTime());
    }

    @XmlElement(name="localizedOriginalStartDate")
    public String getLocalizedOriginalStartDate() {
        return DateTimeFormat.mediumDate().withLocale(this.locale).print((ReadableInstant)this.getOriginalStartDate());
    }

    @XmlElement(name="localizedOriginalEndDate")
    public String getLocalizedOriginalEndDate() {
        return DateTimeFormat.mediumDate().withLocale(this.locale).print((ReadableInstant)(this.isAllDay() ? this.getOriginalEndDate().minusDays(1) : this.getOriginalEndDate()));
    }

    @XmlElement(name="localizedStartTime")
    public String getLocalizedStartTime() {
        return DateTimeFormat.forPattern((String)(this.isAllDay() || this.calendarSettingsManager.isTimeFormat24Hour() ? "H:mm" : "h:mm a")).withLocale(this.locale).print((ReadableInstant)this.getStartTime());
    }

    public String getOriginalStartTimeHour() {
        return DateTimeFormat.forPattern((String)(this.calendarSettingsManager.isTimeFormat24Hour() ? "H:mm" : "h:mm a")).withLocale(this.locale).print((ReadableInstant)this.getStartTime());
    }

    @XmlElement(name="localizedEndDate")
    public String getLocalizedEndDate() {
        return DateTimeFormat.mediumDate().withLocale(this.locale).print((ReadableInstant)(this.isAllDay() ? this.getEndTime().minusDays(1) : this.getEndTime()));
    }

    public String getOriginalEndTime() {
        return DateTimeFormat.forPattern((String)(this.calendarSettingsManager.isTimeFormat24Hour() ? "H:mm" : "h:mm a")).withLocale(this.locale).print((ReadableInstant)(this.isAllDay() ? this.getEndTime().minusDays(1) : this.getEndTime()));
    }

    @XmlElement(name="localizedEndTime")
    public String getLocalizedEndTime() {
        if (this.isAllDay()) {
            return DateTimeFormat.forPattern((String)(this.calendarSettingsManager.isTimeFormat24Hour() ? "H:mm" : "h:mm a")).withLocale(this.locale).print((ReadableInstant)this.getEndTime().minusDays(1));
        }
        return DateTimeFormat.forPattern((String)(this.calendarSettingsManager.isTimeFormat24Hour() ? "H:mm" : "h:mm a")).withLocale(this.locale).print((ReadableInstant)this.getEndTime());
    }

    @XmlElement(name="confluenceFormattedStartDate")
    public String getConfluenceFormattedStartDate() {
        return DateTimeFormat.forPattern((String)this.formatSettingsManager.getDateFormat()).withLocale(this.locale).print((ReadableInstant)this.getStartTime());
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObject = super.toJson();
        try {
            thisObject.put("localizedStartDate", (Object)this.getLocalizedStartDate());
            thisObject.put("localizedStartTime", (Object)this.getLocalizedStartTime());
            thisObject.put("localizedEndDate", (Object)this.getLocalizedEndDate());
            thisObject.put("localizedEndTime", (Object)this.getLocalizedEndTime());
            thisObject.put("localizedOriginalStartDate", (Object)this.getLocalizedOriginalStartDate());
            thisObject.put("localizedOriginalEndDate", (Object)this.getLocalizedOriginalEndDate());
            thisObject.put("originalStartTime", (Object)this.getOriginalStartTimeHour());
            thisObject.put("originalEndTime", (Object)this.getOriginalEndTime());
            thisObject.put("customEventTypeId", (Object)this.getCustomEventTypeId());
            if (this.isExpandDates()) {
                thisObject.put("expandDates", this.isExpandDates());
            }
            thisObject.put("confluenceFormattedStartDate", (Object)this.getConfluenceFormattedStartDate());
        }
        catch (JSONException jsonE) {
            LOG.error("Unable to create a JSON object based on this object", (Throwable)jsonE);
        }
        return thisObject;
    }

    @XmlElement
    public boolean isExpandDates() {
        return this.expandDates;
    }

    public static class ConfluenceUserInviteeForTeamCal1756
    extends ConfluenceUserInvitee {
        public ConfluenceUserInviteeForTeamCal1756() {
        }

        public ConfluenceUserInviteeForTeamCal1756(ConfluenceUserInvitee confluenceUserInvitee) {
            super(confluenceUserInvitee.getUser());
            this.setAvatarIconUrl(confluenceUserInvitee.getAvatarIconUrl());
        }

        @Override
        @XmlElement
        public String getId() {
            return super.getId();
        }
    }

    @XmlRootElement
    public class LocalizedRepeat
    extends SubCalendarEvent.Repeat {
        private LocalizedRepeat() {
            this(null);
        }

        private LocalizedRepeat(SubCalendarEvent.Repeat repeat) {
            super(repeat);
        }

        @Override
        public String getUntilAsIsoDateTimeString() {
            String until = this.getUntil();
            if (StringUtils.isNotBlank((CharSequence)until)) {
                try {
                    return ISODateTimeFormat.dateTime().withZone(LocalizedSubCalendarEvent.this.timeZone).print((ReadableInstant)ISODateTimeFormat.basicDateTimeNoMillis().withZone(DateTimeZone.UTC).parseDateTime(until).withZone(LocalizedSubCalendarEvent.this.timeZone));
                }
                catch (IllegalArgumentException notLongUntilFormat) {
                    return ISODateTimeFormat.dateTime().withZone(LocalizedSubCalendarEvent.this.timeZone).print((ReadableInstant)ISODateTimeFormat.basicDate().parseDateTime(until).withZoneRetainFields(LocalizedSubCalendarEvent.this.timeZone));
                }
            }
            return null;
        }

        @XmlElement
        public String getLocalizedUntil() {
            String until = this.getUntil();
            if (StringUtils.isNotBlank((CharSequence)until)) {
                try {
                    return DateTimeFormat.mediumDate().withLocale(LocalizedSubCalendarEvent.this.locale).print((ReadableInstant)ISODateTimeFormat.basicDateTimeNoMillis().parseDateTime(until).withZone(LocalizedSubCalendarEvent.this.timeZone));
                }
                catch (IllegalArgumentException notLongUntilFormat) {
                    return DateTimeFormat.mediumDate().withLocale(LocalizedSubCalendarEvent.this.locale).print((ReadableInstant)ISODateTimeFormat.basicDate().parseDateTime(until).withZoneRetainFields(LocalizedSubCalendarEvent.this.timeZone));
                }
            }
            return null;
        }

        @Override
        public JSONObject toJson() {
            JSONObject thisObject = super.toJson();
            try {
                if (StringUtils.isNotBlank((CharSequence)this.getUntil())) {
                    thisObject.put("localizedUntil", (Object)this.getLocalizedUntil());
                }
            }
            catch (JSONException jsonE) {
                LOG.error("Unable to create a JSON object based on this object", (Throwable)jsonE);
            }
            return thisObject;
        }
    }
}

