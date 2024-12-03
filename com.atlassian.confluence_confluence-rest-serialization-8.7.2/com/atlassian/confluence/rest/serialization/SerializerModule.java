/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.JsonString
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.reference.EnrichableMap
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.fugue.Option
 *  com.google.common.collect.ImmutableMap
 *  org.codehaus.jackson.Version
 *  org.codehaus.jackson.map.BeanDescription
 *  org.codehaus.jackson.map.BeanProperty
 *  org.codehaus.jackson.map.DeserializationConfig
 *  org.codehaus.jackson.map.DeserializerProvider
 *  org.codehaus.jackson.map.Deserializers
 *  org.codehaus.jackson.map.Deserializers$Base
 *  org.codehaus.jackson.map.JsonDeserializer
 *  org.codehaus.jackson.map.JsonMappingException
 *  org.codehaus.jackson.map.JsonSerializer
 *  org.codehaus.jackson.map.KeyDeserializer
 *  org.codehaus.jackson.map.Module
 *  org.codehaus.jackson.map.Module$SetupContext
 *  org.codehaus.jackson.map.Serializers
 *  org.codehaus.jackson.map.TypeDeserializer
 *  org.codehaus.jackson.map.deser.BeanDeserializerModifier
 *  org.codehaus.jackson.map.deser.ValueInstantiators
 *  org.codehaus.jackson.map.introspect.BasicBeanDescription
 *  org.codehaus.jackson.map.module.SimpleSerializers
 *  org.codehaus.jackson.map.type.CollectionLikeType
 *  org.codehaus.jackson.map.type.CollectionType
 *  org.codehaus.jackson.map.type.MapType
 *  org.codehaus.jackson.type.JavaType
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.rest.serialization;

import com.atlassian.confluence.api.model.JsonString;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.reference.EnrichableMap;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.rest.serialization.CollapsedSerializer;
import com.atlassian.confluence.rest.serialization.ContentDeserializer;
import com.atlassian.confluence.rest.serialization.ContentTypeSerializer;
import com.atlassian.confluence.rest.serialization.CustomValueInstantiators;
import com.atlassian.confluence.rest.serialization.DateTimeDeserializer;
import com.atlassian.confluence.rest.serialization.DateTimeSerializer;
import com.atlassian.confluence.rest.serialization.EnrichedMapDeserializer;
import com.atlassian.confluence.rest.serialization.InstantSerializer;
import com.atlassian.confluence.rest.serialization.JsonStringDeserializer;
import com.atlassian.confluence.rest.serialization.JsonStringSerializer;
import com.atlassian.confluence.rest.serialization.OffsetDateTimeDeserializer;
import com.atlassian.confluence.rest.serialization.OffsetDateTimeSerializer;
import com.atlassian.confluence.rest.serialization.OptionDeserializer;
import com.atlassian.confluence.rest.serialization.OptionSerializer;
import com.atlassian.confluence.rest.serialization.OptionalDeserializer;
import com.atlassian.confluence.rest.serialization.OptionalSerializer;
import com.atlassian.confluence.rest.serialization.PageResponseSerializer;
import com.atlassian.confluence.rest.serialization.ReferenceSerializer;
import com.atlassian.confluence.rest.serialization.RestEntitySerializer;
import com.atlassian.confluence.rest.serialization.RestListDeserializer;
import com.atlassian.confluence.rest.serialization.SpaceDeserializer;
import com.atlassian.fugue.Option;
import com.google.common.collect.ImmutableMap;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.BeanDescription;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializerProvider;
import org.codehaus.jackson.map.Deserializers;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.KeyDeserializer;
import org.codehaus.jackson.map.Module;
import org.codehaus.jackson.map.Serializers;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.deser.BeanDeserializerModifier;
import org.codehaus.jackson.map.deser.ValueInstantiators;
import org.codehaus.jackson.map.introspect.BasicBeanDescription;
import org.codehaus.jackson.map.module.SimpleSerializers;
import org.codehaus.jackson.map.type.CollectionLikeType;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.MapType;
import org.codehaus.jackson.type.JavaType;
import org.joda.time.DateTime;

public class SerializerModule
extends Module {
    static final SerializerModule INSTANCE = new SerializerModule();

    public String getModuleName() {
        return "Custom Confluence Serializers";
    }

    public Version version() {
        return new Version(1, 0, 0, null);
    }

    public void setupModule(Module.SetupContext setupContext) {
        SimpleSerializers customSerializers = new SimpleSerializers();
        customSerializers.addSerializer((JsonSerializer)new OptionSerializer());
        customSerializers.addSerializer((JsonSerializer)new OptionalSerializer());
        customSerializers.addSerializer((JsonSerializer)new RestEntitySerializer());
        customSerializers.addSerializer((JsonSerializer)new DateTimeSerializer());
        customSerializers.addSerializer((JsonSerializer)OffsetDateTimeSerializer.serializeAsIso());
        customSerializers.addSerializer((JsonSerializer)new InstantSerializer());
        customSerializers.addSerializer((JsonSerializer)new JsonStringSerializer());
        customSerializers.addSerializer((JsonSerializer)new ContentTypeSerializer());
        customSerializers.addSerializer((JsonSerializer)new ReferenceSerializer());
        customSerializers.addSerializer((JsonSerializer)new CollapsedSerializer());
        customSerializers.addSerializer((JsonSerializer)new PageResponseSerializer());
        setupContext.addSerializers((Serializers)customSerializers);
        setupContext.addDeserializers((Deserializers)new ConfluenceJacksonDeserializers());
        setupContext.addBeanDeserializerModifier((BeanDeserializerModifier)new ConfluenceBeanDeserializerModifier());
        setupContext.addValueInstantiators((ValueInstantiators)new CustomValueInstantiators());
    }

    public static class ConfluenceBeanDeserializerModifier
    extends BeanDeserializerModifier {
        public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BasicBeanDescription beanDesc, JsonDeserializer<?> deserializer) {
            if (Content.class.equals((Object)beanDesc.getBeanClass())) {
                return new ContentDeserializer(deserializer);
            }
            if (Space.class.equals((Object)beanDesc.getBeanClass())) {
                return new SpaceDeserializer(deserializer);
            }
            return super.modifyDeserializer(config, beanDesc, deserializer);
        }
    }

    public static class ConfluenceJacksonDeserializers
    extends Deserializers.Base {
        private final Map<String, ? extends JsonDeserializer> beanDeserializer = ImmutableMap.of((Object)DateTime.class.getName(), (Object)((Object)new DateTimeDeserializer()), (Object)OffsetDateTime.class.getName(), (Object)((Object)new OffsetDateTimeDeserializer()), (Object)JsonString.class.getName(), (Object)((Object)new JsonStringDeserializer()));

        public JsonDeserializer<?> findCollectionLikeDeserializer(CollectionLikeType type, DeserializationConfig config, DeserializerProvider provider, BeanDescription beanDesc, BeanProperty property, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
            if (type.getRawClass().equals(Option.class)) {
                return new OptionDeserializer(elementDeserializer, (JavaType)type);
            }
            if (type.getRawClass().equals(Optional.class)) {
                return new OptionalDeserializer(elementDeserializer, (JavaType)type);
            }
            if (PageResponse.class.isAssignableFrom(type.getRawClass())) {
                return new RestListDeserializer(this.findElementDeserializer(provider, config, type.getContentType(), property), type.getContentType(), elementTypeDeserializer);
            }
            return super.findCollectionLikeDeserializer(type, config, provider, beanDesc, property, elementTypeDeserializer, elementDeserializer);
        }

        public JsonDeserializer<?> findMapDeserializer(MapType type, DeserializationConfig config, DeserializerProvider provider, BeanDescription beanDesc, BeanProperty property, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
            if (type.getRawClass().equals(EnrichableMap.class)) {
                return EnrichedMapDeserializer.make(type, config, provider, property, keyDeserializer, elementTypeDeserializer, elementDeserializer);
            }
            return super.findMapDeserializer(type, config, provider, beanDesc, property, keyDeserializer, elementTypeDeserializer, elementDeserializer);
        }

        public JsonDeserializer<?> findCollectionDeserializer(CollectionType type, DeserializationConfig config, DeserializerProvider provider, BeanDescription beanDesc, BeanProperty property, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
            if (PageResponse.class.isAssignableFrom(type.getRawClass())) {
                return new RestListDeserializer(this.findElementDeserializer(provider, config, type.getContentType(), property), type.getContentType(), elementTypeDeserializer);
            }
            return super.findCollectionDeserializer(type, config, provider, beanDesc, property, elementTypeDeserializer, elementDeserializer);
        }

        private JsonDeserializer findElementDeserializer(DeserializerProvider provider, DeserializationConfig config, JavaType type, BeanProperty property) throws JsonMappingException {
            return provider.findValueDeserializer(config, type, property);
        }

        public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, DeserializerProvider provider, BeanDescription beanDesc, BeanProperty property) throws JsonMappingException {
            JavaType restListElementType;
            if (type.getRawClass().equals(Option.class)) {
                JavaType containedType = type.containedType(0);
                return new OptionDeserializer(this.findElementDeserializer(provider, config, containedType, property), containedType);
            }
            if (type.getRawClass().equals(Optional.class)) {
                JavaType containedType = type.containedType(0);
                return new OptionalDeserializer(this.findElementDeserializer(provider, config, containedType, property), containedType);
            }
            if ((PageResponse.class.equals((Object)type.getRawClass()) || PageResponseImpl.class.equals((Object)type.getRawClass()) || RestList.class.equals((Object)type.getRawClass())) && (restListElementType = type.containedType(0)) != null) {
                return new RestListDeserializer(this.findElementDeserializer(provider, config, restListElementType, property), restListElementType, (TypeDeserializer)restListElementType.getTypeHandler());
            }
            return this.beanDeserializer.get(type.getRawClass().getName());
        }
    }
}

