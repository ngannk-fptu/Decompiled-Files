/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.tika.metadata.filter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.tika.config.Field;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.filter.MetadataFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateNormalizingMetadataFilter
extends MetadataFilter {
    private static TimeZone UTC = TimeZone.getTimeZone("UTC");
    private static final Logger LOGGER = LoggerFactory.getLogger(DateNormalizingMetadataFilter.class);
    private TimeZone defaultTimeZone = UTC;

    @Override
    public void filter(Metadata metadata) throws TikaException {
        SimpleDateFormat dateFormatter = null;
        DateFormat utcFormatter = null;
        for (String n : metadata.names()) {
            String dateString;
            Property property = Property.get(n);
            if (property == null || !property.getValueType().equals((Object)Property.ValueType.DATE) || (dateString = metadata.get(property)).endsWith("Z")) continue;
            if (dateFormatter == null) {
                dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                dateFormatter.setTimeZone(this.defaultTimeZone);
                utcFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
                utcFormatter.setTimeZone(UTC);
            }
            Date d = null;
            try {
                d = dateFormatter.parse(dateString);
                metadata.set(property, utcFormatter.format(d));
            }
            catch (ParseException e) {
                LOGGER.warn("Couldn't convert date to default time zone: >" + dateString + "<");
            }
        }
    }

    @Field
    public void setDefaultTimeZone(String timeZoneId) {
        this.defaultTimeZone = TimeZone.getTimeZone(ZoneId.of(timeZoneId));
    }
}

