/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.google.common.base.Enums
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.apache.commons.lang3.StringUtils
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.joda.time.Days
 *  org.joda.time.ReadableInstant
 *  org.joda.time.format.ISODateTimeFormat
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.model;

import com.atlassian.confluence.extra.calendar3.model.Invitee;
import com.atlassian.confluence.extra.calendar3.model.JsonSerializable;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.google.common.base.Enums;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.fortuna.ical4j.model.property.ExDate;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.ReadableInstant;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
@XmlAccessorType(value=XmlAccessType.NONE)
public class SubCalendarEvent
implements Comparable<SubCalendarEvent>,
JsonSerializable {
    private static final Logger LOG = LoggerFactory.getLogger(SubCalendarEvent.class);
    private PersistedSubCalendar subCalendar;
    private String eventType;
    private String eventTypeName;
    private String uid;
    private String recurrenceId;
    private String name;
    private int nameCollapseIndex = -1;
    private String nameCollapseText;
    private String shortName;
    private String url;
    private String urlAlias;
    private String description;
    private String descriptionRendered;
    private String location;
    private DateTime originalStartTime;
    private DateTime startTime;
    private DateTime endTime;
    private DateTime originalStartDate;
    private DateTime originalEndDate;
    private String rruleStr;
    private List<ExDate> exDates;
    private Repeat repeat;
    private boolean allDay;
    private boolean editable;
    private boolean calendarReloadRequiredOnUpdate;
    private boolean editAllInRecurrenceSeries;
    private Set<Invitee> invitees;
    private String iconUrl;
    private String mediumIconUrl;
    private String iconLink;
    private String backgroundColor;
    private String borderColor;
    private String secondaryBorderColor;
    private String textColor;
    private String className;
    private Map<String, String> extraProperties;
    private String extraPropertiesTemplate;
    private String colorScheme;
    private boolean disableResizing;
    private String customEventTypeId;
    private String originalCustomEventTypeId;
    private String lastModifiedDate;
    private String status;

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ExDate> getExDates() {
        return this.exDates;
    }

    public void setExDates(List<ExDate> exDates) {
        this.exDates = exDates;
    }

    @XmlElement
    public String getRruleStr() {
        return this.rruleStr;
    }

    public void setRruleStr(String rruleStr) {
        this.rruleStr = rruleStr;
    }

    public String getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public PersistedSubCalendar getSubCalendar() {
        return this.subCalendar;
    }

    public void setSubCalendar(PersistedSubCalendar subCalendar) {
        this.subCalendar = subCalendar;
    }

    public boolean isEditAllInRecurrenceSeries() {
        return this.editAllInRecurrenceSeries;
    }

    public void setEditAllInRecurrenceSeries(boolean editAllInRecurrenceSeries) {
        this.editAllInRecurrenceSeries = editAllInRecurrenceSeries;
    }

    public String getEventTypeName() {
        return this.eventTypeName;
    }

    public void setEventTypeName(String eventTypeName) {
        this.eventTypeName = eventTypeName;
    }

    @XmlElement
    public String getEventType() {
        return this.eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @XmlElement(name="id")
    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @XmlElement(name="recurId")
    public String getRecurrenceId() {
        return this.recurrenceId;
    }

    public void setRecurrenceId(String recurrenceId) {
        this.recurrenceId = recurrenceId;
    }

    @XmlElement(name="title")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNameCollapseIndex() {
        return this.nameCollapseIndex;
    }

    public void setNameCollapseIndex(int nameCollapseIndex) {
        this.nameCollapseIndex = nameCollapseIndex;
    }

    public String getNameCollapseText() {
        return this.nameCollapseText;
    }

    public void setNameCollapseText(String nameCollapseText) {
        this.nameCollapseText = nameCollapseText;
    }

    @XmlElement(name="shortTitle")
    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @XmlElement(name="workingUrl")
    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @XmlElement(name="urlAlias")
    public String getUrlAlias() {
        return this.urlAlias;
    }

    public void setUrlAlias(String urlAlias) {
        this.urlAlias = urlAlias;
    }

    @XmlElement(name="description")
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @HtmlSafe
    @XmlElement(name="descriptionRendered")
    public String getDescriptionRendered() {
        return this.descriptionRendered;
    }

    public void setDescriptionRendered(String descriptionRendered) {
        this.descriptionRendered = descriptionRendered;
    }

    @XmlElement(name="where")
    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @XmlElement(name="customEventTypeId")
    public String getCustomEventTypeId() {
        return this.customEventTypeId;
    }

    public void setCustomEventTypeId(String customEventTypeId) {
        this.customEventTypeId = customEventTypeId;
    }

    public String getOriginalCustomEventTypeId() {
        return this.originalCustomEventTypeId;
    }

    public void setOriginalCustomEventTypeId(String originalCustomEventTypeId) {
        this.originalCustomEventTypeId = originalCustomEventTypeId;
    }

    private String getDateTimeAsString(DateTime dateTime) {
        return null == dateTime ? null : ISODateTimeFormat.dateTime().print((ReadableInstant)dateTime);
    }

    @XmlElement(name="originalStart")
    public String getOriginalStart() {
        return this.getDateTimeAsString(this.getOriginalStartTime());
    }

    public void setOriginalStart(String originalStart) {
        this.setOriginalStartTime(ISODateTimeFormat.dateTime().withOffsetParsed().parseDateTime(originalStart));
    }

    public DateTime getOriginalStartTime() {
        return this.originalStartTime;
    }

    public void setOriginalStartTime(DateTime originalStartTime) {
        this.originalStartTime = originalStartTime;
    }

    @XmlElement(name="start")
    public String getStart() {
        return this.getDateTimeAsString(this.getStartTime());
    }

    public void setStart(String start) {
        this.setStartTime(ISODateTimeFormat.dateTime().parseDateTime(start));
    }

    public DateTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    @XmlElement(name="end")
    public String getEnd() {
        return this.getDateTimeAsString(this.isAllDay() ? this.getEndTime().minusDays(1) : this.getEndTime());
    }

    public void setEnd(String end) {
        this.setEndTime(ISODateTimeFormat.dateTime().parseDateTime(end));
    }

    public DateTime getEndTime() {
        return this.endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    public DateTime getOriginalStartDate() {
        return this.originalStartDate;
    }

    public void setOriginalStartDate(DateTime originalStartDate) {
        this.originalStartDate = originalStartDate;
    }

    public DateTime getOriginalEndDate() {
        return this.originalEndDate;
    }

    public void setOriginalEndDate(DateTime originalEndDate) {
        this.originalEndDate = originalEndDate;
    }

    public String getOriginalStartDateTime() {
        return this.getDateTimeAsString(this.getOriginalStartDate());
    }

    public String getOriginalEndDateTime() {
        return this.getDateTimeAsString(this.isAllDay() ? this.getOriginalEndDate().minusDays(1) : this.getOriginalEndDate());
    }

    @XmlElement(name="recur")
    public Repeat getRepeat() {
        return this.repeat;
    }

    public void setRepeat(Repeat repeat) {
        this.repeat = repeat;
    }

    @XmlElement(name="allDay")
    public boolean isAllDay() {
        return this.allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    @XmlElement(name="editable")
    public boolean isEditable() {
        return this.editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @XmlElement(name="calendarReloadRequiredOnUpdate")
    public boolean isCalendarReloadRequiredOnUpdate() {
        return this.calendarReloadRequiredOnUpdate;
    }

    public void setCalendarReloadRequiredOnUpdate(boolean calendarReloadRequiredOnUpdate) {
        this.calendarReloadRequiredOnUpdate = calendarReloadRequiredOnUpdate;
    }

    @XmlElement
    public Set<Invitee> getInvitees() {
        return this.invitees;
    }

    public void setInvitees(Set<Invitee> invitees) {
        this.invitees = invitees;
    }

    @XmlElement
    public String getIconUrl() {
        return this.iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    @XmlElement
    public String getMediumIconUrl() {
        return this.mediumIconUrl;
    }

    public void setMediumIconUrl(String mediumIconUrl) {
        this.mediumIconUrl = mediumIconUrl;
    }

    @XmlElement
    public String getIconLink() {
        return this.iconLink;
    }

    public void setIconLink(String iconLink) {
        this.iconLink = iconLink;
    }

    @XmlElement(name="subCalendarId")
    public String getSubCalendarId() {
        return null != this.getSubCalendar() ? this.getSubCalendar().getId() : null;
    }

    @XmlElement
    public String getTextColor() {
        return this.textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    @XmlElement
    public String getBorderColor() {
        return this.borderColor;
    }

    @XmlElement
    public String getSecondaryBorderColor() {
        return this.secondaryBorderColor;
    }

    public void setSecondaryBorderColor(String secondaryBorderColor) {
        this.secondaryBorderColor = secondaryBorderColor;
    }

    @XmlElement
    public String getBackgroundColor() {
        return this.backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @XmlElement
    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @XmlElement
    public Map<String, String> getExtraProperties() {
        return this.extraProperties;
    }

    public void setExtraProperties(Map<String, String> extraProperties) {
        this.extraProperties = extraProperties;
    }

    @XmlElement
    public String getExtraPropertiesTemplate() {
        return this.extraPropertiesTemplate;
    }

    public void setExtraPropertiesTemplate(String extraPropertiesTemplate) {
        this.extraPropertiesTemplate = extraPropertiesTemplate;
    }

    @XmlElement
    public String getColorScheme() {
        return this.colorScheme;
    }

    public void setColorScheme(String colorScheme) {
        this.colorScheme = colorScheme;
    }

    @XmlElement
    public boolean isDisableResizing() {
        return this.disableResizing;
    }

    public void setDisableResizing(boolean disableResizing) {
        this.disableResizing = disableResizing;
    }

    public boolean compareWithDayRange(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SubCalendarEvent that = (SubCalendarEvent)o;
        if (this.originalStartTime != null && that.originalStartTime != null) {
            LOG.debug("Comparing SubCalendarEvent with this [{}] and that [{}]", (Object)this.originalStartTime, (Object)that.originalStartTime);
            if (!this.originalStartTime.equals((Object)that.originalStartTime.withZone(this.originalStartTime.getZone()))) {
                int periodInDays = Math.abs(Days.daysBetween((ReadableInstant)this.originalStartTime, (ReadableInstant)that.originalStartTime).getDays());
                String rrrule = StringUtils.defaultString((String)this.getRruleStr()).toLowerCase();
                if ((rrrule.contains("daily") || periodInDays != 1) && periodInDays != 0) {
                    return false;
                }
            }
        }
        return Objects.equals(this.uid, that.uid);
    }

    public boolean compareWithDateOnly(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SubCalendarEvent that = (SubCalendarEvent)o;
        if (this.originalStartTime != null && that.originalStartTime != null) {
            LOG.debug("Comparing SubCalendarEvent original date (for allday event) with this [{}] and that [{}]", (Object)this.originalStartTime.toLocalDate(), (Object)that.originalStartTime.toLocalDate());
            if (!this.originalStartTime.toLocalDate().equals((Object)that.originalStartTime.toLocalDate())) {
                return false;
            }
        }
        return Objects.equals(this.uid, that.uid);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SubCalendarEvent that = (SubCalendarEvent)o;
        if (this.originalStartTime != null && that.originalStartTime != null) {
            LOG.debug("Comparing SubCalendarEvent with this [{}] and that [{}]", (Object)this.originalStartTime, (Object)that.originalStartTime);
            if (!this.originalStartTime.equals((Object)that.originalStartTime.withZone(this.originalStartTime.getZone()))) {
                return false;
            }
        }
        return Objects.equals(this.uid, that.uid);
    }

    public int hashCode() {
        int result = this.uid != null ? this.uid.hashCode() : 0;
        result = 31 * result + (this.originalStartTime != null ? this.originalStartTime.withZone(DateTimeZone.UTC).hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(SubCalendarEvent subCalendarEvent) {
        int startDateCompareResult = this.compare(this.startTime, subCalendarEvent.startTime);
        if (0 != startDateCompareResult) {
            return startDateCompareResult;
        }
        return this.name.compareTo(subCalendarEvent.name);
    }

    private int compare(DateTime t1, DateTime t2) {
        if (t1 == t2) {
            return 0;
        }
        long l1 = t2.getMillis();
        long l2 = t1.getMillis();
        if (l2 == l1) {
            return 0;
        }
        if (l2 < l1) {
            return -1;
        }
        return 1;
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObject = new JSONObject();
        try {
            Map<String, String> extraProperties;
            thisObject.put("id", (Object)this.getUid());
            thisObject.putOpt("recurId", (Object)this.getRecurrenceId());
            thisObject.putOpt("eventType", (Object)this.getEventType());
            thisObject.put("title", (Object)this.getName());
            this.putIfNotBlank(thisObject, "shortTitle", this.getShortName());
            thisObject.putOpt("workingUrl", (Object)this.getUrl());
            thisObject.putOpt("urlAlias", (Object)this.getUrlAlias());
            thisObject.putOpt("description", (Object)this.getDescription());
            thisObject.putOpt("descriptionRendered", (Object)this.getDescriptionRendered());
            thisObject.putOpt("where", (Object)this.getLocation());
            thisObject.putOpt("originalStart", (Object)this.getOriginalStart());
            thisObject.put("start", (Object)this.getStart());
            thisObject.put("end", (Object)this.getEnd());
            thisObject.put("originalStartDateTime", (Object)this.getOriginalStartDateTime());
            thisObject.put("originalEndDateTime", (Object)this.getOriginalEndDateTime());
            thisObject.put("recurOriginalStartTime", (Object)this.getOriginalStartTime());
            thisObject.put("rruleStr", (Object)this.getRruleStr());
            if (null != this.getRepeat()) {
                thisObject.put("recur", (Object)this.getRepeat().toJson());
            }
            thisObject.put("allDay", this.isAllDay());
            thisObject.put("editable", this.isEditable());
            thisObject.put("calendarReloadRequiredOnUpdate", this.isCalendarReloadRequiredOnUpdate());
            Set<Invitee> invitees = this.getInvitees();
            if (null != invitees && !invitees.isEmpty()) {
                JSONArray inviteesArray = new JSONArray();
                for (Invitee invitee : invitees) {
                    inviteesArray.put((Object)invitee.toJson());
                }
                thisObject.put("invitees", (Object)inviteesArray);
            }
            if (StringUtils.isNotBlank((CharSequence)this.getIconUrl())) {
                thisObject.put("iconUrl", (Object)this.getIconUrl());
                this.putIfNotBlank(thisObject, "iconLink", this.getIconLink());
            }
            this.putIfNotBlank(thisObject, "mediumIconUrl", this.getMediumIconUrl());
            thisObject.put("subCalendarId", (Object)this.getSubCalendarId());
            this.putIfNotBlank(thisObject, "textColor", this.getTextColor());
            this.putIfNotBlank(thisObject, "borderColor", this.getBorderColor());
            this.putIfNotBlank(thisObject, "secondaryBorderColor", this.getSecondaryBorderColor());
            this.putIfNotBlank(thisObject, "backgroundColor", this.getBackgroundColor());
            this.putIfNotBlank(thisObject, "colorScheme", this.getColorScheme());
            this.putIfNotBlank(thisObject, "className", this.getClassName());
            if (this.isDisableResizing()) {
                thisObject.put("disableResizing", this.isDisableResizing());
            }
            if (null != (extraProperties = this.getExtraProperties()) && !extraProperties.isEmpty()) {
                JSONObject extraPropertiesObj = new JSONObject();
                for (Map.Entry<String, String> propertyPair : extraProperties.entrySet()) {
                    extraPropertiesObj.put(propertyPair.getKey(), (Object)propertyPair.getValue());
                }
                thisObject.put("extraProperties", (Object)extraPropertiesObj);
            }
            this.putIfNotBlank(thisObject, "extraPropertiesTemplate", this.getExtraPropertiesTemplate());
        }
        catch (Exception ex) {
            LOG.error("Unable to create a JSON object based on this object", (Throwable)ex);
        }
        return thisObject;
    }

    private void putIfNotBlank(JSONObject jsonObject, String key, String value) throws JSONException {
        if (StringUtils.isNotBlank((CharSequence)value)) {
            jsonObject.put(key, (Object)value);
        }
    }

    @XmlRootElement
    @XmlAccessorType(value=XmlAccessType.NONE)
    @Deprecated
    public static class Repeat
    implements JsonSerializable {
        private static final Logger LOG = LoggerFactory.getLogger(Repeat.class);
        private final Map<Param, String> fields = new HashMap<Param, String>();

        public Repeat() {
            this((String)null);
        }

        public Repeat(String value) {
            this.initFieldsMap(value);
        }

        public Repeat(Repeat toCopy) {
            this.fields.putAll(toCopy.fields);
        }

        private void initFieldsMap(String value) {
            String[] rruleParams = StringUtils.split((String)value, (String)"; ");
            if (null != rruleParams) {
                for (String rruleParamNameValuePair : rruleParams) {
                    Param rruleParam;
                    String[] rruleParamTokens = StringUtils.split((String)rruleParamNameValuePair, (String)"=");
                    if (rruleParamTokens.length != 2 || (rruleParam = (Param)((Object)Enums.getIfPresent(Param.class, (String)rruleParamTokens[0]).orNull())) == null || rruleParamTokens.length != 2) continue;
                    this.fields.put(rruleParam, rruleParamTokens[1]);
                }
            }
        }

        public Repeat(String freq, String byDay, String interval, String until) {
            StringBuilder valueBuilder = new StringBuilder();
            this.appendToValueBuilder(valueBuilder, Param.FREQ, freq);
            this.appendToValueBuilder(valueBuilder, Param.BYDAY, byDay);
            this.appendToValueBuilder(valueBuilder, Param.INTERVAL, interval);
            this.appendToValueBuilder(valueBuilder, Param.UNTIL, until);
            this.initFieldsMap(valueBuilder.toString());
        }

        private void appendToValueBuilder(StringBuilder valueBuilder, Param param, String paramValue) {
            if (null != param && StringUtils.isNotBlank((CharSequence)paramValue)) {
                if (valueBuilder.length() > 0) {
                    valueBuilder.append(';');
                }
                valueBuilder.append(param.toString()).append('=').append(paramValue);
            }
        }

        @XmlElement
        public String getFreq() {
            return this.fields.get((Object)Param.FREQ);
        }

        @XmlElement
        public String getByDay() {
            return this.fields.get((Object)Param.BYDAY);
        }

        @XmlElement
        public String getInterval() {
            return this.fields.get((Object)Param.INTERVAL);
        }

        public String getUntil() {
            return this.fields.get((Object)Param.UNTIL);
        }

        public boolean equals(Repeat repeat, Param ... rruleParams) {
            if (this == repeat) {
                return true;
            }
            if (null == repeat) {
                return false;
            }
            for (Param paramToCompare : rruleParams) {
                if (StringUtils.equals((CharSequence)this.fields.get((Object)paramToCompare), (CharSequence)repeat.fields.get((Object)paramToCompare))) continue;
                return false;
            }
            return true;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Repeat repeat = (Repeat)o;
            return this.fields != null ? this.fields.equals(repeat.fields) : repeat.fields == null;
        }

        public int hashCode() {
            return this.fields != null ? this.fields.hashCode() : 0;
        }

        @Override
        public JSONObject toJson() {
            JSONObject thisObject = new JSONObject();
            try {
                thisObject.put("freq", (Object)this.getFreq());
                thisObject.putOpt("byDay", (Object)this.getByDay());
                if (null != this.getInterval()) {
                    thisObject.put("interval", (Object)this.getInterval());
                } else if (null != this.getFreq()) {
                    thisObject.put("interval", (Object)"1");
                }
                thisObject.putOpt("until", (Object)this.getUntilAsIsoDateTimeString());
            }
            catch (JSONException jsonE) {
                LOG.error("Unable to create a JSON object based on this object", (Throwable)jsonE);
            }
            return thisObject;
        }

        @XmlElement(name="until")
        public String getUntilAsIsoDateTimeString() {
            String untilStr = this.getUntil();
            if (StringUtils.isBlank((CharSequence)untilStr)) {
                return null;
            }
            try {
                return ISODateTimeFormat.dateTime().print((ReadableInstant)ISODateTimeFormat.basicDateTimeNoMillis().parseDateTime(this.getUntil()));
            }
            catch (Exception e) {
                return ISODateTimeFormat.dateTime().print((ReadableInstant)ISODateTimeFormat.basicDate().parseDateTime(this.getUntil()));
            }
        }

        public static enum Param {
            FREQ,
            BYDAY,
            INTERVAL,
            UNTIL;

        }
    }
}

