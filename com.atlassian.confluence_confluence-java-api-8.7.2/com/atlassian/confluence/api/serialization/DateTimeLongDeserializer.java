/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.map.DeserializationContext
 *  org.codehaus.jackson.map.JsonDeserializer
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.api.serialization;

import java.io.IOException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.joda.time.DateTime;

public class DateTimeLongDeserializer
extends JsonDeserializer<DateTime> {
    public DateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return new DateTime(jp.getLongValue());
    }
}

