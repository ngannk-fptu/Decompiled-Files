/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.deser;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.deser.BeanDeserializerBuilder;
import org.codehaus.jackson.map.introspect.BasicBeanDescription;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class BeanDeserializerModifier {
    public BeanDeserializerBuilder updateBuilder(DeserializationConfig config, BasicBeanDescription beanDesc, BeanDeserializerBuilder builder) {
        return builder;
    }

    public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BasicBeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        return deserializer;
    }
}

