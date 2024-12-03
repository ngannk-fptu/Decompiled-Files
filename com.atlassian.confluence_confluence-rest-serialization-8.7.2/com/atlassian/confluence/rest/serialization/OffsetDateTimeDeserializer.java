/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.map.DeserializationContext
 *  org.codehaus.jackson.map.JsonDeserializer
 */
package com.atlassian.confluence.rest.serialization;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

public class OffsetDateTimeDeserializer
extends JsonDeserializer<OffsetDateTime> {
    public OffsetDateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String dateString = jp.getText();
        if (StringUtils.isNumeric((CharSequence)dateString)) {
            return OffsetDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(dateString)), ZoneId.systemDefault());
        }
        return dateString == null ? null : OffsetDateTime.parse(dateString, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}

