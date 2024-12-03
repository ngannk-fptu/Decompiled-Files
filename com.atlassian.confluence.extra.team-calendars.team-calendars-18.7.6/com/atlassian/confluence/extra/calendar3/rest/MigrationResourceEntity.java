/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Collections2
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.rest;

import com.atlassian.confluence.extra.calendar3.events.migration.ProgressCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.JsonSerializable;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.StatusProvider;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import java.util.Collection;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
public class MigrationResourceEntity
implements JsonSerializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(MigrationResourceEntity.class);
    @XmlElement
    private final StatusProvider.RunningStatus status;
    @XmlElement
    private final Collection<ProgressCalendarEvent> events;

    public MigrationResourceEntity(StatusProvider.RunningStatus status, Collection<ProgressCalendarEvent> events) {
        this.status = status;
        this.events = events;
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObj = new JSONObject();
        try {
            thisObj.put("success", true);
            thisObj.put("status", (Object)this.status.toString());
            if (null == this.events || this.events.isEmpty()) {
                return thisObj;
            }
            Collection messages = Collections2.transform((Collection)Collections2.filter(this.events, (Predicate)Predicates.notNull()), event -> {
                JSONObject message = new JSONObject();
                try {
                    message.put("message", (Object)this.getProperMessage((ProgressCalendarEvent)((Object)event)));
                    message.put("percentage", event.getProgress());
                }
                catch (JSONException e) {
                    LOGGER.error("Exception when creating JSONObject", (Throwable)e);
                }
                return message;
            });
            JSONArray logMessageArray = new JSONArray(messages);
            thisObj.put("messages", (Object)logMessageArray);
        }
        catch (JSONException jsonE) {
            LOGGER.error("Unable to create a JSON object based on this object", (Throwable)jsonE);
        }
        return thisObj;
    }

    private String getProperMessage(ProgressCalendarEvent event) {
        return String.format("Bandana to Active Object migrating - %s", event.toString());
    }
}

