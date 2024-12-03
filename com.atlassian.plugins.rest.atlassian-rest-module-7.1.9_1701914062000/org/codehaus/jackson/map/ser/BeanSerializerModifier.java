/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.ser;

import java.util.List;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.introspect.BasicBeanDescription;
import org.codehaus.jackson.map.ser.BeanPropertyWriter;
import org.codehaus.jackson.map.ser.BeanSerializerBuilder;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class BeanSerializerModifier {
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BasicBeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        return beanProperties;
    }

    public List<BeanPropertyWriter> orderProperties(SerializationConfig config, BasicBeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        return beanProperties;
    }

    public BeanSerializerBuilder updateBuilder(SerializationConfig config, BasicBeanDescription beanDesc, BeanSerializerBuilder builder) {
        return builder;
    }

    public JsonSerializer<?> modifySerializer(SerializationConfig config, BasicBeanDescription beanDesc, JsonSerializer<?> serializer) {
        return serializer;
    }
}

