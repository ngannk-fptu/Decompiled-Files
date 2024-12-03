/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.deser;

import java.util.HashMap;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializerFactory;
import org.codehaus.jackson.map.DeserializerProvider;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.deser.BeanDeserializerFactory;
import org.codehaus.jackson.map.type.ArrayType;
import org.codehaus.jackson.map.type.ClassKey;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Deprecated
public class CustomDeserializerFactory
extends BeanDeserializerFactory {
    protected HashMap<ClassKey, JsonDeserializer<Object>> _directClassMappings = null;
    protected HashMap<ClassKey, Class<?>> _mixInAnnotations;

    public CustomDeserializerFactory() {
        this(null);
    }

    protected CustomDeserializerFactory(DeserializerFactory.Config config) {
        super(config);
    }

    @Override
    public DeserializerFactory withConfig(DeserializerFactory.Config config) {
        if (this.getClass() != CustomDeserializerFactory.class) {
            throw new IllegalStateException("Subtype of CustomDeserializerFactory (" + this.getClass().getName() + ") has not properly overridden method 'withAdditionalDeserializers': can not instantiate subtype with additional deserializer definitions");
        }
        return new CustomDeserializerFactory(config);
    }

    public <T> void addSpecificMapping(Class<T> forClass, JsonDeserializer<? extends T> deser) {
        ClassKey key = new ClassKey(forClass);
        if (this._directClassMappings == null) {
            this._directClassMappings = new HashMap();
        }
        this._directClassMappings.put(key, deser);
    }

    public void addMixInAnnotationMapping(Class<?> destinationClass, Class<?> classWithMixIns) {
        if (this._mixInAnnotations == null) {
            this._mixInAnnotations = new HashMap();
        }
        this._mixInAnnotations.put(new ClassKey(destinationClass), classWithMixIns);
    }

    @Override
    public JsonDeserializer<Object> createBeanDeserializer(DeserializationConfig config, DeserializerProvider p, JavaType type, BeanProperty property) throws JsonMappingException {
        JsonDeserializer<Object> deser;
        Class<?> cls = type.getRawClass();
        ClassKey key = new ClassKey(cls);
        if (this._directClassMappings != null && (deser = this._directClassMappings.get(key)) != null) {
            return deser;
        }
        return super.createBeanDeserializer(config, p, type, property);
    }

    @Override
    public JsonDeserializer<?> createArrayDeserializer(DeserializationConfig config, DeserializerProvider p, ArrayType type, BeanProperty property) throws JsonMappingException {
        JsonDeserializer<Object> deser;
        ClassKey key = new ClassKey(type.getRawClass());
        if (this._directClassMappings != null && (deser = this._directClassMappings.get(key)) != null) {
            return deser;
        }
        return super.createArrayDeserializer(config, p, type, property);
    }

    @Override
    public JsonDeserializer<?> createEnumDeserializer(DeserializationConfig config, DeserializerProvider p, JavaType enumType, BeanProperty property) throws JsonMappingException {
        ClassKey key;
        JsonDeserializer<Object> deser;
        if (this._directClassMappings != null && (deser = this._directClassMappings.get(key = new ClassKey(enumType.getRawClass()))) != null) {
            return deser;
        }
        return super.createEnumDeserializer(config, p, enumType, property);
    }
}

