/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.map.JsonSerializer
 *  org.codehaus.jackson.map.SerializerProvider
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.api.serialization;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.joda.time.DateTime;

public class DateTimeLongSerializer
extends JsonSerializer<DateTime> {
    public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeNumber(value.getMillis());
    }
}

