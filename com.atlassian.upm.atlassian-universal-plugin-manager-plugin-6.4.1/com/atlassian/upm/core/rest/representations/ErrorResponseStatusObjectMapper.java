/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.Status
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.map.JsonSerializer
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.map.SerializerFactory
 *  org.codehaus.jackson.map.SerializerProvider
 *  org.codehaus.jackson.map.ser.CustomSerializerFactory
 */
package com.atlassian.upm.core.rest.representations;

import com.atlassian.plugins.rest.common.Status;
import com.atlassian.upm.core.rest.representations.BaseRepresentationFactory;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerFactory;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.CustomSerializerFactory;

public class ErrorResponseStatusObjectMapper
extends ObjectMapper {
    private final BaseRepresentationFactory representationFactory;

    public ErrorResponseStatusObjectMapper(BaseRepresentationFactory representationFactory) {
        this.representationFactory = representationFactory;
        CustomSerializerFactory sf = new CustomSerializerFactory();
        sf.addSpecificMapping(Status.class, (JsonSerializer)new StatusToErrorRepresentationSerializer());
        this.setSerializerFactory((SerializerFactory)sf);
    }

    private final class StatusToErrorRepresentationSerializer
    extends JsonSerializer<Status> {
        private StatusToErrorRepresentationSerializer() {
        }

        public void serialize(Status value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            if (StringUtils.isEmpty((CharSequence)value.getMessage())) {
                jgen.writeObject((Object)ErrorResponseStatusObjectMapper.this.representationFactory.createI18nErrorRepresentation("upm.plugin.error.unexpected.error"));
            } else {
                jgen.writeObject((Object)ErrorResponseStatusObjectMapper.this.representationFactory.createErrorRepresentation(value.getMessage(), "upm.plugin.error.unexpected.error"));
            }
        }
    }
}

