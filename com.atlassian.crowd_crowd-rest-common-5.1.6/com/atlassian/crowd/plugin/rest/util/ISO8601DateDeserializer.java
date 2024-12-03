/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.map.DeserializationContext
 *  org.codehaus.jackson.map.JsonDeserializer
 */
package com.atlassian.crowd.plugin.rest.util;

import com.atlassian.crowd.plugin.rest.util.SearchRestrictionEntityTranslator;
import java.io.IOException;
import java.util.Date;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

public class ISO8601DateDeserializer
extends JsonDeserializer<Date> {
    public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return SearchRestrictionEntityTranslator.fromTimeString(jp.getText().trim());
    }
}

