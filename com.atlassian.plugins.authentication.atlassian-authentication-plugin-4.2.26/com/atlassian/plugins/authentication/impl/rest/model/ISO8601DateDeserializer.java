/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.map.DeserializationContext
 *  org.codehaus.jackson.map.JsonDeserializer
 */
package com.atlassian.plugins.authentication.impl.rest.model;

import java.io.IOException;
import java.time.ZonedDateTime;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

public class ISO8601DateDeserializer
extends JsonDeserializer<ZonedDateTime> {
    public ZonedDateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return ZonedDateTime.parse(jp.getText().trim());
    }
}

