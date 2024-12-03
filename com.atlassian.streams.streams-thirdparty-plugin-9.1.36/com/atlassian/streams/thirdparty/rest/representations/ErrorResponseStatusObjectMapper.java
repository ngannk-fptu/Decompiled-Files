/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.Status
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.map.JsonSerializer
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.map.SerializerFactory
 *  org.codehaus.jackson.map.SerializerProvider
 *  org.codehaus.jackson.map.ser.CustomSerializerFactory
 */
package com.atlassian.streams.thirdparty.rest.representations;

import com.atlassian.plugins.rest.common.Status;
import com.atlassian.streams.thirdparty.rest.representations.ErrorRepresentation;
import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerFactory;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.CustomSerializerFactory;

public class ErrorResponseStatusObjectMapper
extends ObjectMapper {
    public ErrorResponseStatusObjectMapper() {
        CustomSerializerFactory sf = new CustomSerializerFactory();
        sf.addSpecificMapping(Status.class, (JsonSerializer)new StatusToErrorRepresentationSerializer());
        this.setSerializerFactory((SerializerFactory)sf);
    }

    private final class StatusToErrorRepresentationSerializer
    extends JsonSerializer<Status> {
        private StatusToErrorRepresentationSerializer() {
        }

        public void serialize(Status value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeObject((Object)new ErrorRepresentation(value.getMessage(), "stream.error.unexpected.error"));
        }
    }
}

