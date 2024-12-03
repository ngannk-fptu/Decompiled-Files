/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.DeserializationContext
 *  com.fasterxml.jackson.databind.DeserializationFeature
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 */
package com.fasterxml.jackson.datatype.joda.deser.key;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.joda.deser.key.JodaKeyDeserializer;
import java.io.IOException;
import java.util.TimeZone;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class DateTimeKeyDeserializer
extends JodaKeyDeserializer {
    private static final long serialVersionUID = 1L;

    protected DateTime deserialize(String key, DeserializationContext ctxt) throws IOException {
        if (ctxt.isEnabled(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)) {
            TimeZone tz = ctxt.getTimeZone();
            DateTimeZone dtz = tz == null ? DateTimeZone.UTC : DateTimeZone.forTimeZone((TimeZone)tz);
            return new DateTime((Object)key, dtz);
        }
        return DateTime.parse((String)key);
    }
}

