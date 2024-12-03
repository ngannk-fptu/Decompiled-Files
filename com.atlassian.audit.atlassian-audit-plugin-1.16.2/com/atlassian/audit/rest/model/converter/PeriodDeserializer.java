/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.map.DeserializationContext
 *  org.codehaus.jackson.map.JsonDeserializer
 */
package com.atlassian.audit.rest.model.converter;

import java.io.IOException;
import java.time.Period;
import java.util.Optional;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

public class PeriodDeserializer
extends JsonDeserializer<Period> {
    public Period deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode root = jsonParser.readValueAsTree();
        int years = Optional.ofNullable(root.get("years")).map(JsonNode::asInt).orElse(0);
        int months = Optional.ofNullable(root.get("months")).map(JsonNode::asInt).orElse(0);
        int days = Optional.ofNullable(root.get("days")).map(JsonNode::asInt).orElse(0);
        return Period.of(years, months, days);
    }
}

