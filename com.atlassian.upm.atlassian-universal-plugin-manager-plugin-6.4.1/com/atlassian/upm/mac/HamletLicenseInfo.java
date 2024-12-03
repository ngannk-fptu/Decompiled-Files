/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.mac;

import com.atlassian.upm.api.util.Option;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HamletLicenseInfo {
    private static final Logger log = LoggerFactory.getLogger(HamletLicenseInfo.class);
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern((String)"yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(DateTimeZone.UTC);
    @JsonProperty
    private final String key;
    @JsonProperty
    private final String license;
    @JsonProperty
    private final String date;
    @JsonProperty
    private final String purchaser;
    @JsonIgnore
    private final Option<DateTime> parsedDate;

    @JsonCreator
    public HamletLicenseInfo(@JsonProperty(value="key") String key, @JsonProperty(value="license") String license, @JsonProperty(value="date") String date, @JsonProperty(value="purchaser") String purchaser) {
        this.key = key;
        this.license = license;
        this.date = date;
        this.purchaser = purchaser;
        this.parsedDate = HamletLicenseInfo.parseDate(date);
    }

    public String getKey() {
        return this.key;
    }

    public String getLicense() {
        return this.license;
    }

    public String getDate() {
        return this.date;
    }

    public String getPurchaser() {
        return this.purchaser;
    }

    @JsonIgnore
    public Option<DateTime> getParsedDate() {
        return this.parsedDate;
    }

    private static Option<DateTime> parseDate(String date) {
        if (date != null) {
            try {
                return Option.some(DATE_FORMAT.parseDateTime(date));
            }
            catch (IllegalArgumentException e) {
                log.debug("Discarded invalid license date: {}", (Object)date);
            }
        }
        return Option.none();
    }
}

