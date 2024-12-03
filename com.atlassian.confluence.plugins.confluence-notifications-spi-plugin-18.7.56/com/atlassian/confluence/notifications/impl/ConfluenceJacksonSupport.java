/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.sal.api.user.UserKey
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.Version
 *  org.codehaus.jackson.map.BeanDescription
 *  org.codehaus.jackson.map.BeanProperty
 *  org.codehaus.jackson.map.DeserializationConfig
 *  org.codehaus.jackson.map.DeserializationContext
 *  org.codehaus.jackson.map.DeserializerProvider
 *  org.codehaus.jackson.map.Deserializers
 *  org.codehaus.jackson.map.Deserializers$Base
 *  org.codehaus.jackson.map.JsonDeserializer
 *  org.codehaus.jackson.map.JsonMappingException
 *  org.codehaus.jackson.map.JsonSerializer
 *  org.codehaus.jackson.map.Module
 *  org.codehaus.jackson.map.Module$SetupContext
 *  org.codehaus.jackson.map.SerializationConfig
 *  org.codehaus.jackson.map.SerializerProvider
 *  org.codehaus.jackson.map.Serializers
 *  org.codehaus.jackson.map.Serializers$Base
 *  org.codehaus.jackson.type.JavaType
 */
package com.atlassian.confluence.notifications.impl;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.sal.api.user.UserKey;
import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.BeanDescription;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.DeserializerProvider;
import org.codehaus.jackson.map.Deserializers;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.Module;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.Serializers;
import org.codehaus.jackson.type.JavaType;

public class ConfluenceJacksonSupport
extends Module {
    public String getModuleName() {
        return "Confluence Notifications Support";
    }

    public Version version() {
        return new Version(1, 0, 0, "");
    }

    public void setupModule(Module.SetupContext context) {
        context.addDeserializers((Deserializers)new ConfluenceDeserializers());
        context.addSerializers((Serializers)new ConfluenceSerializers());
    }

    private class ContentIdSerializer
    extends JsonSerializer<ContentId> {
        private ContentIdSerializer() {
        }

        public void serialize(ContentId value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeString(value.serialise());
        }
    }

    private class ContentIdDeserializer
    extends JsonDeserializer<ContentId> {
        private ContentIdDeserializer() {
        }

        public ContentId deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            return ContentId.deserialise((String)jp.getText());
        }
    }

    private class UserKeySerializer
    extends JsonSerializer<UserKey> {
        private UserKeySerializer() {
        }

        public void serialize(UserKey value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeString(value.getStringValue());
        }
    }

    private class UserKeyDeserializer
    extends JsonDeserializer<UserKey> {
        private UserKeyDeserializer() {
        }

        public UserKey deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            return new UserKey(jp.getText());
        }
    }

    private class ConfluenceSerializers
    extends Serializers.Base {
        private ConfluenceSerializers() {
        }

        public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc, BeanProperty property) {
            Class raw = type.getRawClass();
            if (UserKey.class.isAssignableFrom(raw)) {
                return new UserKeySerializer();
            }
            if (ContentId.class.isAssignableFrom(raw)) {
                return new ContentIdSerializer();
            }
            return null;
        }
    }

    private class ConfluenceDeserializers
    extends Deserializers.Base {
        private ConfluenceDeserializers() {
        }

        public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, DeserializerProvider provider, BeanDescription beanDesc, BeanProperty property) throws JsonMappingException {
            Class raw = type.getRawClass();
            if (UserKey.class.isAssignableFrom(raw)) {
                return new UserKeyDeserializer();
            }
            if (ContentId.class.isAssignableFrom(raw)) {
                return new ContentIdDeserializer();
            }
            return null;
        }
    }
}

