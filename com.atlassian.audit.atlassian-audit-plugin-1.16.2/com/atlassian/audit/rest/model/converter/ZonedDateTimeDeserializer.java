/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.map.DeserializationContext
 *  org.codehaus.jackson.map.JsonDeserializer
 */
package com.atlassian.audit.rest.model.converter;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

public class ZonedDateTimeDeserializer
extends JsonDeserializer<ZonedDateTime> {
    public ZonedDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return Instant.from(DateTimeFormatter.ISO_INSTANT.parse(jsonParser.getText())).atZone(ZoneId.systemDefault());
    }
}

