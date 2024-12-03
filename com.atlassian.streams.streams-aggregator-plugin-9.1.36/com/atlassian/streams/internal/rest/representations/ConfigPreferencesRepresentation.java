/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.streams.internal.rest.representations;

import com.google.common.base.Preconditions;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ConfigPreferencesRepresentation {
    @JsonProperty
    final String dateFormat;
    @JsonProperty
    final String timeFormat;
    @JsonProperty
    final String dateTimeFormat;
    @JsonProperty
    final String timeZone;
    @JsonProperty
    final boolean dateRelativize;

    @JsonCreator
    public ConfigPreferencesRepresentation(@JsonProperty(value="dateFormat") String dateFormat, @JsonProperty(value="timeFormat") String timeFormat, @JsonProperty(value="dateTimeFormat") String dateTimeFormat, @JsonProperty(value="timeZone") String timeZone, @JsonProperty(value="dateRelativize") boolean dateRelativize) {
        this.dateFormat = (String)Preconditions.checkNotNull((Object)dateFormat, (Object)"dateFormat");
        this.timeFormat = (String)Preconditions.checkNotNull((Object)timeFormat, (Object)"timeFormat");
        this.dateTimeFormat = (String)Preconditions.checkNotNull((Object)dateTimeFormat, (Object)"dateTimeFormat");
        this.timeZone = (String)Preconditions.checkNotNull((Object)timeZone, (Object)"timeZone");
        this.dateRelativize = (Boolean)Preconditions.checkNotNull((Object)dateRelativize, (Object)"dateRelativize");
    }

    public String getDateFormat() {
        return this.dateFormat;
    }

    public String getTimeFormat() {
        return this.timeFormat;
    }

    public String getDateTimeFormat() {
        return this.dateTimeFormat;
    }

    public String getTimeZone() {
        return this.timeZone;
    }

    public boolean isDateRelativize() {
        return this.dateRelativize;
    }
}

