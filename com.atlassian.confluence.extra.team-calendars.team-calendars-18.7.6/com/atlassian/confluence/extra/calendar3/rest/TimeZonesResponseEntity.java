/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.joda.time.ReadableInstant
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.rest;

import com.atlassian.confluence.extra.calendar3.model.JsonSerializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
public class TimeZonesResponseEntity
implements JsonSerializable {
    private static final Logger LOG = LoggerFactory.getLogger(TimeZonesResponseEntity.class);
    @XmlElement
    private List<TimeZone> timeZones;

    public TimeZonesResponseEntity() {
    }

    public TimeZonesResponseEntity(Map<String, DateTimeZone> timeZoneMap, Locale locale) {
        this.timeZones = new ArrayList<TimeZone>();
        for (Map.Entry<String, DateTimeZone> entry : timeZoneMap.entrySet()) {
            this.timeZones.add(new TimeZone(entry.getKey(), entry.getValue(), locale));
        }
        this.sortTimeZoneList(this.timeZones);
    }

    @Override
    public JSONObject toJson() {
        JSONObject timeZonesObject = new JSONObject();
        try {
            JSONArray timeZoneArray = new JSONArray();
            for (TimeZone timeZone : this.timeZones) {
                timeZoneArray.put((Object)timeZone.toJson());
            }
            timeZonesObject.put("timeZones", (Object)timeZoneArray);
        }
        catch (JSONException e) {
            LOG.error("Unable to create a JSON object based on this object", (Throwable)e);
        }
        return timeZonesObject;
    }

    private void sortTimeZoneList(List<TimeZone> timeZoneList) {
        Collections.sort(timeZoneList, (aTimeZone, anotherTimeZone) -> aTimeZone.getName().compareTo(anotherTimeZone.getName()));
    }

    @XmlRootElement
    public class TimeZone
    implements JsonSerializable {
        @XmlElement
        private String name;
        @XmlElement
        private String offset;

        public TimeZone(String name, DateTimeZone dateTimeZone, Locale locale) {
            DateTimeFormatter timeZoneOffsetFormatter = DateTimeFormat.forPattern((String)"Z").withLocale(locale);
            this.setName(name);
            this.setOffset(timeZoneOffsetFormatter.print((ReadableInstant)new DateTime().withZoneRetainFields(dateTimeZone)));
        }

        @Override
        public JSONObject toJson() {
            JSONObject timeZoneJsonObject = new JSONObject();
            try {
                timeZoneJsonObject.put("name", (Object)this.getName()).put("offset", (Object)this.getOffset());
            }
            catch (JSONException e) {
                LOG.error("Unable to create a JSON object based on this object", (Throwable)e);
            }
            return timeZoneJsonObject;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOffset() {
            return this.offset;
        }

        public void setOffset(String offset) {
            this.offset = offset;
        }
    }
}

