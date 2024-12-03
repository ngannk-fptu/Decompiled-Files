/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.json;

import java.lang.reflect.Type;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.BeanDescription;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.Module;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.Serializers;
import org.codehaus.jackson.map.introspect.BasicBeanDescription;
import org.codehaus.jackson.map.ser.BeanSerializerFactory;
import org.codehaus.jackson.map.type.TypeBindings;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.map.type.TypeModifier;
import org.codehaus.jackson.type.JavaType;

class GuavaIterableCapableModule
extends Module {
    private static final Class fluentIterableClass;
    private static final TypeModifier guavaIterableTypeModifier;
    private static final Serializers.Base guavaSerializers;
    private static final Version version;

    GuavaIterableCapableModule() {
    }

    @Override
    public String getModuleName() {
        return GuavaIterableCapableModule.class.getSimpleName();
    }

    @Override
    public Version version() {
        return version;
    }

    @Override
    public void setupModule(Module.SetupContext context) {
        if (fluentIterableClass != null) {
            context.addTypeModifier(guavaIterableTypeModifier);
            context.addSerializers(guavaSerializers);
        }
    }

    static {
        Class<?> tmp = null;
        try {
            tmp = Class.forName("com.google.common.collect.FluentIterable");
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        fluentIterableClass = tmp;
        guavaIterableTypeModifier = new TypeModifier(){

            @Override
            public JavaType modifyType(JavaType type, Type jdkType, TypeBindings context, TypeFactory typeFactory) {
                Class<?> raw = type.getRawClass();
                if (fluentIterableClass.isAssignableFrom(raw)) {
                    JavaType[] javaTypeArray = new JavaType[1];
                    javaTypeArray[0] = typeFactory.unknownType();
                    return typeFactory.constructParametricType(Iterable.class, javaTypeArray);
                }
                return type;
            }
        };
        guavaSerializers = new Serializers.Base(){

            @Override
            public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc, BeanProperty property) {
                Class<?> raw = type.getRawClass();
                if (fluentIterableClass.isAssignableFrom(raw)) {
                    BasicBeanDescription basicBeanDescription = (BasicBeanDescription)config.introspect(type);
                    try {
                        return BeanSerializerFactory.instance.findSerializerByAddonType(config, type, basicBeanDescription, property, false);
                    }
                    catch (JsonMappingException jsonMappingException) {
                        // empty catch block
                    }
                }
                return super.findSerializer(config, type, beanDesc, property);
            }
        };
        version = new Version(0, 1, 0, null);
    }
}

