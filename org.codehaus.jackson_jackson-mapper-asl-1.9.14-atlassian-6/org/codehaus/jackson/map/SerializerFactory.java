/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map;

import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.RuntimeJsonMappingException;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.Serializers;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.ser.BeanSerializerModifier;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class SerializerFactory {
    public abstract Config getConfig();

    public abstract SerializerFactory withConfig(Config var1);

    public final SerializerFactory withAdditionalSerializers(Serializers additional) {
        return this.withConfig(this.getConfig().withAdditionalSerializers(additional));
    }

    public final SerializerFactory withAdditionalKeySerializers(Serializers additional) {
        return this.withConfig(this.getConfig().withAdditionalKeySerializers(additional));
    }

    public final SerializerFactory withSerializerModifier(BeanSerializerModifier modifier) {
        return this.withConfig(this.getConfig().withSerializerModifier(modifier));
    }

    public abstract JsonSerializer<Object> createSerializer(SerializationConfig var1, JavaType var2, BeanProperty var3) throws JsonMappingException;

    public abstract TypeSerializer createTypeSerializer(SerializationConfig var1, JavaType var2, BeanProperty var3) throws JsonMappingException;

    public abstract JsonSerializer<Object> createKeySerializer(SerializationConfig var1, JavaType var2, BeanProperty var3) throws JsonMappingException;

    @Deprecated
    public final JsonSerializer<Object> createSerializer(JavaType type, SerializationConfig config) {
        try {
            return this.createSerializer(config, type, null);
        }
        catch (JsonMappingException e) {
            throw new RuntimeJsonMappingException(e);
        }
    }

    @Deprecated
    public final TypeSerializer createTypeSerializer(JavaType baseType, SerializationConfig config) {
        try {
            return this.createTypeSerializer(config, baseType, null);
        }
        catch (JsonMappingException e) {
            throw new RuntimeException((Throwable)((Object)e));
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class Config {
        public abstract Config withAdditionalSerializers(Serializers var1);

        public abstract Config withAdditionalKeySerializers(Serializers var1);

        public abstract Config withSerializerModifier(BeanSerializerModifier var1);

        public abstract boolean hasSerializers();

        public abstract boolean hasKeySerializers();

        public abstract boolean hasSerializerModifiers();

        public abstract Iterable<Serializers> serializers();

        public abstract Iterable<Serializers> keySerializers();

        public abstract Iterable<BeanSerializerModifier> serializerModifiers();
    }
}

