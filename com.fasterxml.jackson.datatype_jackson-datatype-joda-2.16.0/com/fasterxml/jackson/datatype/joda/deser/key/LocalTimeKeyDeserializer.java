/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.DeserializationContext
 *  org.joda.time.LocalTime
 *  org.joda.time.format.DateTimeFormatter
 *  org.joda.time.format.ISODateTimeFormat
 */
package com.fasterxml.jackson.datatype.joda.deser.key;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.joda.deser.key.JodaKeyDeserializer;
import java.io.IOException;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class LocalTimeKeyDeserializer
extends JodaKeyDeserializer {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter parser = ISODateTimeFormat.localTimeParser();

    protected LocalTime deserialize(String key, DeserializationContext ctxt) throws IOException {
        return parser.parseLocalTime(key);
    }
}

