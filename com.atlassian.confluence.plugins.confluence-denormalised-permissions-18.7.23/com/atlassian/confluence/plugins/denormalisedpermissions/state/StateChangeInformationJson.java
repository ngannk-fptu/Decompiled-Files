/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.denormalisedpermissions.StateChangeInformation
 *  com.atlassian.confluence.security.denormalisedpermissions.StateChangeInformation$MessageLevel
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize$Inclusion
 */
package com.atlassian.confluence.plugins.denormalisedpermissions.state;

import com.atlassian.confluence.security.denormalisedpermissions.StateChangeInformation;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class StateChangeInformationJson {
    private final long id;
    private final String message;
    private final StateChangeInformation.MessageLevel level;
    private final long eventTimestampMilli;

    @JsonCreator
    public StateChangeInformationJson(@JsonProperty(value="id") long id, @JsonProperty(value="message") String message, @JsonProperty(value="level") StateChangeInformation.MessageLevel level, @JsonProperty(value="eventTimestampMilli") long eventTimestampMilli) {
        this.id = id;
        this.message = message;
        this.level = level;
        this.eventTimestampMilli = eventTimestampMilli;
    }

    public StateChangeInformationJson(StateChangeInformation stateChangeInformation) {
        this(stateChangeInformation.getId(), stateChangeInformation.getMessage(), stateChangeInformation.getLevel(), stateChangeInformation.getEventTimestamp().toEpochMilli());
    }

    @JsonProperty(value="id")
    public long getId() {
        return this.id;
    }

    @JsonProperty(value="message")
    public String getMessage() {
        return this.message;
    }

    @JsonProperty(value="eventTimestampMilli")
    public long getEventTimestampMilli() {
        return this.eventTimestampMilli;
    }

    @JsonProperty(value="level")
    public StateChangeInformation.MessageLevel getLevel() {
        return this.level;
    }
}

