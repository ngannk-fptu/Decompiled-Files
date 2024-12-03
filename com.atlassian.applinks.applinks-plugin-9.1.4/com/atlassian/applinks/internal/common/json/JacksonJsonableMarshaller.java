/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.json.marshal.JsonableMarshaller
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.common.json;

import com.atlassian.json.marshal.Jsonable;
import com.atlassian.json.marshal.JsonableMarshaller;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import javax.annotation.Nullable;

public class JacksonJsonableMarshaller
implements JsonableMarshaller {
    public static final JsonableMarshaller INSTANCE = new JacksonJsonableMarshaller();
    private static final ObjectWriter OBJECT_WRITER = JacksonJsonableMarshaller.createObjectWriter();

    private static ObjectWriter createObjectWriter() {
        return new ObjectMapper().configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false).writer();
    }

    public Jsonable marshal(@Nullable Object toJsonObj) {
        return writer -> OBJECT_WRITER.writeValue(writer, toJsonObj);
    }
}

