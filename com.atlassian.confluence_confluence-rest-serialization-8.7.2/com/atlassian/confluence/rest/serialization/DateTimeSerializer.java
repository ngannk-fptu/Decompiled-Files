/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonGenerationException
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.map.SerializerProvider
 *  org.codehaus.jackson.map.ser.std.SerializerBase
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.rest.serialization;

import com.atlassian.confluence.rest.serialization.Dates;
import java.io.IOException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.joda.time.DateTime;

public class DateTimeSerializer
extends SerializerBase<DateTime> {
    public DateTimeSerializer() {
        super(DateTime.class);
    }

    public void serialize(DateTime value, JsonGenerator jsonGen, SerializerProvider provider) throws IOException, JsonGenerationException {
        jsonGen.writeString(Dates.asTimeString(value));
    }
}

