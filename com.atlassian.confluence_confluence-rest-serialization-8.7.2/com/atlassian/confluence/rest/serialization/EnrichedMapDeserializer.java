/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.reference.BuilderUtils
 *  com.atlassian.confluence.api.model.reference.EnrichableMap
 *  com.google.common.collect.ImmutableSet
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.map.BeanDescription
 *  org.codehaus.jackson.map.BeanProperty
 *  org.codehaus.jackson.map.DeserializationConfig
 *  org.codehaus.jackson.map.DeserializationContext
 *  org.codehaus.jackson.map.DeserializerProvider
 *  org.codehaus.jackson.map.JsonDeserializer
 *  org.codehaus.jackson.map.JsonMappingException
 *  org.codehaus.jackson.map.KeyDeserializer
 *  org.codehaus.jackson.map.TypeDeserializer
 *  org.codehaus.jackson.map.deser.BeanDeserializerFactory
 *  org.codehaus.jackson.map.deser.ValueInstantiator
 *  org.codehaus.jackson.map.deser.std.StdValueInstantiator
 *  org.codehaus.jackson.map.introspect.BasicBeanDescription
 *  org.codehaus.jackson.map.type.MapType
 *  org.codehaus.jackson.type.JavaType
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.rest.serialization;

import com.atlassian.confluence.api.model.reference.BuilderUtils;
import com.atlassian.confluence.api.model.reference.EnrichableMap;
import com.atlassian.confluence.rest.serialization.MapAndKeyValuePairDeserializer;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.BeanDescription;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.DeserializerProvider;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.KeyDeserializer;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.deser.BeanDeserializerFactory;
import org.codehaus.jackson.map.deser.ValueInstantiator;
import org.codehaus.jackson.map.deser.std.StdValueInstantiator;
import org.codehaus.jackson.map.introspect.BasicBeanDescription;
import org.codehaus.jackson.map.type.MapType;
import org.codehaus.jackson.type.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnrichedMapDeserializer
extends MapAndKeyValuePairDeserializer {
    private static final Logger log = LoggerFactory.getLogger(EnrichedMapDeserializer.class);

    public static JsonDeserializer<?> make(MapType type, DeserializationConfig config, DeserializerProvider provider, BeanProperty property, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        type = (MapType)config.constructSpecializedType((JavaType)type, EnrichableMap.class);
        BeanDescription beanDesc = config.introspectForCreation((JavaType)type);
        if (elementDeserializer == null) {
            elementDeserializer = provider.findValueDeserializer(config, type.getContentType(), property);
        }
        StdValueInstantiator delegateInstantiator = (StdValueInstantiator)BeanDeserializerFactory.instance.findValueInstantiator(config, (BasicBeanDescription)beanDesc);
        StdValueInstantiator inst = new StdValueInstantiator(delegateInstantiator){

            public Object createUsingDefault() throws IOException {
                Map newMap = (Map)super.createUsingDefault();
                return new NullIgnoringMap(newMap);
            }
        };
        KeyDeserializer wrappedKeyDeser = EnrichedMapDeserializer.robustKeyDeserializer(keyDeserializer);
        JsonDeserializer wrappedValueDeser = EnrichedMapDeserializer.robustValueSerializer(elementDeserializer);
        return new EnrichedMapDeserializer((JavaType)type, (ValueInstantiator)inst, wrappedKeyDeser, wrappedValueDeser, elementTypeDeserializer);
    }

    private EnrichedMapDeserializer(JavaType mapType, ValueInstantiator valueInstantiator, KeyDeserializer keyDeser, JsonDeserializer valueDeser, TypeDeserializer valueTypeDeser) {
        super(mapType, valueInstantiator, keyDeser, (JsonDeserializer<Object>)valueDeser, valueTypeDeser);
        this._ignorableProperties = new UnderscorePrefixedStringContainingHashSet(this._ignorableProperties);
    }

    @Override
    public Map<Object, Object> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return BuilderUtils.modelMap((Map)super.deserialize(jp, ctxt));
    }

    @Override
    public Map<Object, Object> deserialize(JsonParser jp, DeserializationContext ctxt, Map<Object, Object> result) throws IOException {
        return BuilderUtils.modelMap(super.deserialize(jp, ctxt, result));
    }

    private static KeyDeserializer robustKeyDeserializer(final KeyDeserializer keyDeser) {
        return new KeyDeserializer(){

            public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
                if (key != null && key.startsWith("_")) {
                    return null;
                }
                if (keyDeser == null) {
                    return key;
                }
                try {
                    return keyDeser.deserializeKey(key, ctxt);
                }
                catch (JsonMappingException e) {
                    log.warn("Unable to deserialize map key from: {}, turn on debug-level logging for more detail.", (Object)key);
                    log.debug("JsonMappingException stacktrace:", (Throwable)e);
                    return null;
                }
            }
        };
    }

    private static JsonDeserializer robustValueSerializer(final JsonDeserializer<?> elementDeserializer) {
        return new JsonDeserializer(){

            public Object deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
                try {
                    return elementDeserializer.deserialize(jp, ctxt);
                }
                catch (JsonMappingException e) {
                    log.warn("Unable to deserialize map value, turn on debug-level logging for more detail.");
                    log.debug("JsonMappingException stacktrace:", (Throwable)e);
                    return null;
                }
            }

            public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
                try {
                    return super.deserializeWithType(jp, ctxt, typeDeserializer);
                }
                catch (JsonMappingException e) {
                    log.warn("Unable to deserializeWithType map value, turn on debug-level logging for more detail.");
                    log.debug("JsonMappingException stacktrace:", (Throwable)e);
                    return null;
                }
            }
        };
    }

    private static class UnderscorePrefixedStringContainingHashSet
    extends HashSet<String> {
        public UnderscorePrefixedStringContainingHashSet(HashSet<String> ignorableProperties) {
            if (ignorableProperties != null) {
                this.addAll(ImmutableSet.copyOf(ignorableProperties));
            }
        }

        @Override
        public boolean contains(Object o) {
            String s;
            if (o instanceof String && (s = (String)o).startsWith("_")) {
                return true;
            }
            return super.contains(o);
        }
    }

    private static class NullIgnoringMap<K, V>
    extends LinkedHashMap<K, V> {
        private NullIgnoringMap(Map m) {
            super(m);
        }

        @Override
        public V put(K key, V value) {
            if (key == null || value == null) {
                return null;
            }
            return super.put(key, value);
        }
    }
}

