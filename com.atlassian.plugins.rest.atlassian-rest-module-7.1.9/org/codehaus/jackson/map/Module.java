/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.Versioned;
import org.codehaus.jackson.map.AbstractTypeResolver;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.Deserializers;
import org.codehaus.jackson.map.KeyDeserializers;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.Serializers;
import org.codehaus.jackson.map.deser.BeanDeserializerModifier;
import org.codehaus.jackson.map.deser.ValueInstantiators;
import org.codehaus.jackson.map.ser.BeanSerializerModifier;
import org.codehaus.jackson.map.type.TypeModifier;

public abstract class Module
implements Versioned {
    public abstract String getModuleName();

    public abstract Version version();

    public abstract void setupModule(SetupContext var1);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface SetupContext {
        public Version getMapperVersion();

        public DeserializationConfig getDeserializationConfig();

        public SerializationConfig getSerializationConfig();

        public boolean isEnabled(DeserializationConfig.Feature var1);

        public boolean isEnabled(SerializationConfig.Feature var1);

        public boolean isEnabled(JsonParser.Feature var1);

        public boolean isEnabled(JsonGenerator.Feature var1);

        public void addDeserializers(Deserializers var1);

        public void addKeyDeserializers(KeyDeserializers var1);

        public void addSerializers(Serializers var1);

        public void addKeySerializers(Serializers var1);

        public void addBeanDeserializerModifier(BeanDeserializerModifier var1);

        public void addBeanSerializerModifier(BeanSerializerModifier var1);

        public void addAbstractTypeResolver(AbstractTypeResolver var1);

        public void addTypeModifier(TypeModifier var1);

        public void addValueInstantiators(ValueInstantiators var1);

        public void insertAnnotationIntrospector(AnnotationIntrospector var1);

        public void appendAnnotationIntrospector(AnnotationIntrospector var1);

        public void setMixInAnnotations(Class<?> var1, Class<?> var2);
    }
}

