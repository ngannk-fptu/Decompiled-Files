/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.deser.KeyDeserializers;
import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer;
import com.fasterxml.jackson.databind.introspect.AnnotatedAndMetadata;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.EnumResolver;
import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.List;

public class StdKeyDeserializers
implements KeyDeserializers,
Serializable {
    private static final long serialVersionUID = 1L;

    public static KeyDeserializer constructEnumKeyDeserializer(EnumResolver enumResolver) {
        return new StdKeyDeserializer.EnumKD(enumResolver, null);
    }

    public static KeyDeserializer constructEnumKeyDeserializer(EnumResolver enumResolver, AnnotatedMethod factory) {
        return new StdKeyDeserializer.EnumKD(enumResolver, factory);
    }

    public static KeyDeserializer constructDelegatingKeyDeserializer(DeserializationConfig config, JavaType type, JsonDeserializer<?> deser) {
        return new StdKeyDeserializer.DelegatingKD(type.getRawClass(), deser);
    }

    public static KeyDeserializer findStringBasedKeyDeserializer(DeserializationConfig config, JavaType type) throws JsonMappingException {
        BeanDescription beanDesc = config.introspectForCreation(type);
        AnnotatedAndMetadata<AnnotatedConstructor, JsonCreator.Mode> ctorInfo = StdKeyDeserializers._findStringConstructor(beanDesc);
        if (ctorInfo != null && ctorInfo.metadata != null) {
            return StdKeyDeserializers._constructCreatorKeyDeserializer(config, (AnnotatedMember)ctorInfo.annotated);
        }
        List<AnnotatedAndMetadata<AnnotatedMethod, JsonCreator.Mode>> factoryCandidates = beanDesc.getFactoryMethodsWithMode();
        factoryCandidates.removeIf(m -> ((AnnotatedMethod)m.annotated).getParameterCount() != 1 || ((AnnotatedMethod)m.annotated).getRawParameterType(0) != String.class || m.metadata == JsonCreator.Mode.PROPERTIES);
        AnnotatedMethod explicitFactory = StdKeyDeserializers._findExplicitStringFactoryMethod(factoryCandidates);
        if (explicitFactory != null) {
            return StdKeyDeserializers._constructCreatorKeyDeserializer(config, explicitFactory);
        }
        if (ctorInfo != null) {
            return StdKeyDeserializers._constructCreatorKeyDeserializer(config, (AnnotatedMember)ctorInfo.annotated);
        }
        if (!factoryCandidates.isEmpty()) {
            return StdKeyDeserializers._constructCreatorKeyDeserializer(config, (AnnotatedMember)factoryCandidates.get((int)0).annotated);
        }
        return null;
    }

    private static KeyDeserializer _constructCreatorKeyDeserializer(DeserializationConfig config, AnnotatedMember creator) {
        if (creator instanceof AnnotatedConstructor) {
            AnnotatedElement rawCtor = ((AnnotatedConstructor)creator).getAnnotated();
            if (config.canOverrideAccessModifiers()) {
                ClassUtil.checkAndFixAccess((Member)((Object)rawCtor), config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
            }
            return new StdKeyDeserializer.StringCtorKeyDeserializer((Constructor<?>)rawCtor);
        }
        Method m = ((AnnotatedMethod)creator).getAnnotated();
        if (config.canOverrideAccessModifiers()) {
            ClassUtil.checkAndFixAccess(m, config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
        }
        return new StdKeyDeserializer.StringFactoryKeyDeserializer(m);
    }

    private static AnnotatedAndMetadata<AnnotatedConstructor, JsonCreator.Mode> _findStringConstructor(BeanDescription beanDesc) {
        for (AnnotatedAndMetadata<AnnotatedConstructor, JsonCreator.Mode> entry : beanDesc.getConstructorsWithMode()) {
            AnnotatedConstructor ctor = (AnnotatedConstructor)entry.annotated;
            if (ctor.getParameterCount() != 1 || String.class != ctor.getRawParameterType(0)) continue;
            return entry;
        }
        return null;
    }

    private static AnnotatedMethod _findExplicitStringFactoryMethod(List<AnnotatedAndMetadata<AnnotatedMethod, JsonCreator.Mode>> candidates) throws JsonMappingException {
        AnnotatedMethod match = null;
        for (AnnotatedAndMetadata<AnnotatedMethod, JsonCreator.Mode> entry : candidates) {
            if (entry.metadata == null) continue;
            if (match == null) {
                match = (AnnotatedMethod)entry.annotated;
                continue;
            }
            Class<?> rawKeyType = ((AnnotatedMethod)entry.annotated).getDeclaringClass();
            throw new IllegalArgumentException("Multiple suitable annotated Creator factory methods to be used as the Key deserializer for type " + ClassUtil.nameOf(rawKeyType));
        }
        return match;
    }

    @Override
    public KeyDeserializer findKeyDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
        Class<?> raw = type.getRawClass();
        if (raw.isPrimitive()) {
            raw = ClassUtil.wrapperType(raw);
        }
        return StdKeyDeserializer.forType(raw);
    }
}

