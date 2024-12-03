/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.rest;

import com.atlassian.confluence.extra.calendar3.model.JsonSerializable;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.google.common.base.Preconditions;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
public class SubCalendarWatchingStatusEntity
implements JsonSerializable {
    private static final Logger LOG = LoggerFactory.getLogger(SubCalendarWatchingStatusEntity.class);
    @XmlElement
    private String subCalendarId;
    @XmlElement
    private boolean watched;
    @XmlElement
    private boolean watchedViaContent;
    @XmlElement
    private boolean isWatchable;

    public SubCalendarWatchingStatusEntity(PersistedSubCalendar subCalendar, boolean isWatched, boolean isWatchedViaContent) {
        Preconditions.checkArgument((subCalendar != null ? 1 : 0) != 0);
        this.subCalendarId = subCalendar.getId();
        this.isWatchable = subCalendar.isWatchable();
        this.watched = isWatched;
        this.watchedViaContent = isWatchedViaContent;
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObject = new JSONObject();
        try {
            thisObject.put("subCalendarId", (Object)this.subCalendarId);
            thisObject.put("watched", this.watched);
            thisObject.put("watchedViaContent", this.watchedViaContent);
            thisObject.put("isWatchable", this.isWatchable);
        }
        catch (JSONException jsone) {
            LOG.error("Unable to create a JSON object based on this object", (Throwable)jsone);
        }
        return thisObject;
    }
}

