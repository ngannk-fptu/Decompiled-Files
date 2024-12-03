/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.api.model;

import com.atlassian.annotations.ExperimentalApi;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
public class JodaDate {
    @JsonProperty
    private DateTime dateTime;

    public JodaDate(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    @JsonCreator
    public JodaDate(String dateString) {
        try {
            this.dateTime = new DateTime(Long.parseLong(dateString));
        }
        catch (NumberFormatException e) {
            this.dateTime = DateTime.parse((String)dateString);
        }
    }

    public DateTime getDateTime() {
        return this.dateTime;
    }
}

