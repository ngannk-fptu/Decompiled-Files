/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.deser.std;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.KeyDeserializer;
import org.codehaus.jackson.map.deser.std.StdKeyDeserializer;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;
import org.codehaus.jackson.map.introspect.BasicBeanDescription;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.map.util.ClassUtil;
import org.codehaus.jackson.map.util.EnumResolver;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class StdKeyDeserializers {
    protected final HashMap<JavaType, KeyDeserializer> _keyDeserializers = new HashMap();

    protected StdKeyDeserializers() {
        this.add(new StdKeyDeserializer.BoolKD());
        this.add(new StdKeyDeserializer.ByteKD());
        this.add(new StdKeyDeserializer.CharKD());
        this.add(new StdKeyDeserializer.ShortKD());
        this.add(new StdKeyDeserializer.IntKD());
        this.add(new StdKeyDeserializer.LongKD());
        this.add(new StdKeyDeserializer.FloatKD());
        this.add(new StdKeyDeserializer.DoubleKD());
        this.add(new StdKeyDeserializer.DateKD());
        this.add(new StdKeyDeserializer.CalendarKD());
        this.add(new StdKeyDeserializer.UuidKD());
    }

    private void add(StdKeyDeserializer kdeser) {
        Class<?> keyClass = kdeser.getKeyClass();
        this._keyDeserializers.put(TypeFactory.defaultInstance().uncheckedSimpleType(keyClass), kdeser);
    }

    public static HashMap<JavaType, KeyDeserializer> constructAll() {
        return new StdKeyDeserializers()._keyDeserializers;
    }

    public static KeyDeserializer constructStringKeyDeserializer(DeserializationConfig config, JavaType type) {
        return StdKeyDeserializer.StringKD.forType(type.getClass());
    }

    public static KeyDeserializer constructEnumKeyDeserializer(EnumResolver<?> enumResolver) {
        return new StdKeyDeserializer.EnumKD(enumResolver, null);
    }

    public static KeyDeserializer constructEnumKeyDeserializer(EnumResolver<?> enumResolver, AnnotatedMethod factory) {
        return new StdKeyDeserializer.EnumKD(enumResolver, factory);
    }

    public static KeyDeserializer findStringBasedKeyDeserializer(DeserializationConfig config, JavaType type) {
        BasicBeanDescription beanDesc = (BasicBeanDescription)config.introspect(type);
        Constructor<?> ctor = beanDesc.findSingleArgConstructor(String.class);
        if (ctor != null) {
            if (config.isEnabled(DeserializationConfig.Feature.CAN_OVERRIDE_ACCESS_MODIFIERS)) {
                ClassUtil.checkAndFixAccess(ctor);
            }
            return new StdKeyDeserializer.StringCtorKeyDeserializer(ctor);
        }
        Method m = beanDesc.findFactoryMethod(String.class);
        if (m != null) {
            if (config.isEnabled(DeserializationConfig.Feature.CAN_OVERRIDE_ACCESS_MODIFIERS)) {
                ClassUtil.checkAndFixAccess(m);
            }
            return new StdKeyDeserializer.StringFactoryKeyDeserializer(m);
        }
        return null;
    }
}

