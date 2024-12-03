/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.ser;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.introspect.Annotated;
import org.codehaus.jackson.map.introspect.AnnotatedField;
import org.codehaus.jackson.map.introspect.AnnotatedMember;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;
import org.codehaus.jackson.map.introspect.BasicBeanDescription;
import org.codehaus.jackson.map.ser.BeanPropertyWriter;
import org.codehaus.jackson.map.ser.BeanSerializerFactory;
import org.codehaus.jackson.map.util.Annotations;
import org.codehaus.jackson.map.util.Comparators;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PropertyBuilder {
    protected final SerializationConfig _config;
    protected final BasicBeanDescription _beanDesc;
    protected final JsonSerialize.Inclusion _outputProps;
    protected final AnnotationIntrospector _annotationIntrospector;
    protected Object _defaultBean;

    public PropertyBuilder(SerializationConfig config, BasicBeanDescription beanDesc) {
        this._config = config;
        this._beanDesc = beanDesc;
        this._outputProps = beanDesc.findSerializationInclusion(config.getSerializationInclusion());
        this._annotationIntrospector = this._config.getAnnotationIntrospector();
    }

    public Annotations getClassAnnotations() {
        return this._beanDesc.getClassAnnotations();
    }

    protected BeanPropertyWriter buildWriter(String name, JavaType declaredType, JsonSerializer<Object> ser, TypeSerializer typeSer, TypeSerializer contentTypeSer, AnnotatedMember am, boolean defaultUseStaticTyping) {
        Field f;
        Method m;
        if (am instanceof AnnotatedField) {
            m = null;
            f = ((AnnotatedField)am).getAnnotated();
        } else {
            m = ((AnnotatedMethod)am).getAnnotated();
            f = null;
        }
        JavaType serializationType = this.findSerializationType(am, defaultUseStaticTyping, declaredType);
        if (contentTypeSer != null) {
            JavaType ct;
            if (serializationType == null) {
                serializationType = declaredType;
            }
            if ((ct = serializationType.getContentType()) == null) {
                throw new IllegalStateException("Problem trying to create BeanPropertyWriter for property '" + name + "' (of type " + this._beanDesc.getType() + "); serialization type " + serializationType + " has no content");
            }
            serializationType = serializationType.withContentTypeHandler((Object)contentTypeSer);
            ct = serializationType.getContentType();
        }
        Object valueToSuppress = null;
        boolean suppressNulls = false;
        JsonSerialize.Inclusion methodProps = this._annotationIntrospector.findSerializationInclusion(am, this._outputProps);
        if (methodProps != null) {
            switch (methodProps) {
                case NON_DEFAULT: {
                    valueToSuppress = this.getDefaultValue(name, m, f);
                    if (valueToSuppress == null) {
                        suppressNulls = true;
                        break;
                    }
                    if (!valueToSuppress.getClass().isArray()) break;
                    valueToSuppress = Comparators.getArrayComparator(valueToSuppress);
                    break;
                }
                case NON_EMPTY: {
                    suppressNulls = true;
                    valueToSuppress = this.getEmptyValueChecker(name, declaredType);
                    break;
                }
                case NON_NULL: {
                    suppressNulls = true;
                }
                case ALWAYS: {
                    if (!declaredType.isContainerType()) break;
                    valueToSuppress = this.getContainerValueChecker(name, declaredType);
                }
            }
        }
        BeanPropertyWriter bpw = new BeanPropertyWriter(am, this._beanDesc.getClassAnnotations(), name, declaredType, ser, typeSer, serializationType, m, f, suppressNulls, valueToSuppress);
        Boolean unwrapped = this._annotationIntrospector.shouldUnwrapProperty(am);
        if (unwrapped != null && unwrapped.booleanValue()) {
            bpw = bpw.unwrappingWriter();
        }
        return bpw;
    }

    protected JavaType findSerializationType(Annotated a, boolean useStaticTyping, JavaType declaredType) {
        JsonSerialize.Typing typing;
        JavaType secondary;
        Class<?> serClass = this._annotationIntrospector.findSerializationType(a);
        if (serClass != null) {
            Class rawDeclared = declaredType.getRawClass();
            if (serClass.isAssignableFrom(rawDeclared)) {
                declaredType = declaredType.widenBy(serClass);
            } else {
                if (!rawDeclared.isAssignableFrom(serClass)) {
                    throw new IllegalArgumentException("Illegal concrete-type annotation for method '" + a.getName() + "': class " + serClass.getName() + " not a super-type of (declared) class " + rawDeclared.getName());
                }
                declaredType = this._config.constructSpecializedType(declaredType, serClass);
            }
            useStaticTyping = true;
        }
        if ((secondary = BeanSerializerFactory.modifySecondaryTypesByAnnotation(this._config, a, declaredType)) != declaredType) {
            useStaticTyping = true;
            declaredType = secondary;
        }
        if (!useStaticTyping && (typing = this._annotationIntrospector.findSerializationTyping(a)) != null) {
            useStaticTyping = typing == JsonSerialize.Typing.STATIC;
        }
        return useStaticTyping ? declaredType : null;
    }

    protected Object getDefaultBean() {
        if (this._defaultBean == null) {
            this._defaultBean = this._beanDesc.instantiateBean(this._config.isEnabled(SerializationConfig.Feature.CAN_OVERRIDE_ACCESS_MODIFIERS));
            if (this._defaultBean == null) {
                AnnotatedElement cls = this._beanDesc.getClassInfo().getAnnotated();
                throw new IllegalArgumentException("Class " + ((Class)cls).getName() + " has no default constructor; can not instantiate default bean value to support 'properties=JsonSerialize.Inclusion.NON_DEFAULT' annotation");
            }
        }
        return this._defaultBean;
    }

    protected Object getDefaultValue(String name, Method m, Field f) {
        Object defaultBean = this.getDefaultBean();
        try {
            if (m != null) {
                return m.invoke(defaultBean, new Object[0]);
            }
            return f.get(defaultBean);
        }
        catch (Exception e) {
            return this._throwWrapped(e, name, defaultBean);
        }
    }

    protected Object getContainerValueChecker(String propertyName, JavaType propertyType) {
        if (!this._config.isEnabled(SerializationConfig.Feature.WRITE_EMPTY_JSON_ARRAYS)) {
            if (propertyType.isArrayType()) {
                return new EmptyArrayChecker();
            }
            if (Collection.class.isAssignableFrom(propertyType.getRawClass())) {
                return new EmptyCollectionChecker();
            }
        }
        return null;
    }

    protected Object getEmptyValueChecker(String propertyName, JavaType propertyType) {
        Class rawType = propertyType.getRawClass();
        if (rawType == String.class) {
            return new EmptyStringChecker();
        }
        if (propertyType.isArrayType()) {
            return new EmptyArrayChecker();
        }
        if (Collection.class.isAssignableFrom(rawType)) {
            return new EmptyCollectionChecker();
        }
        if (Map.class.isAssignableFrom(rawType)) {
            return new EmptyMapChecker();
        }
        return null;
    }

    protected Object _throwWrapped(Exception e, String propName, Object defaultBean) {
        Throwable t = e;
        while (t.getCause() != null) {
            t = t.getCause();
        }
        if (t instanceof Error) {
            throw (Error)t;
        }
        if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        throw new IllegalArgumentException("Failed to get property '" + propName + "' of default " + defaultBean.getClass().getName() + " instance");
    }

    public static class EmptyStringChecker {
        public boolean equals(Object other) {
            return other == null || ((String)other).length() == 0;
        }
    }

    public static class EmptyArrayChecker {
        public boolean equals(Object other) {
            return other == null || Array.getLength(other) == 0;
        }
    }

    public static class EmptyMapChecker {
        public boolean equals(Object other) {
            return other == null || ((Map)other).size() == 0;
        }
    }

    public static class EmptyCollectionChecker {
        public boolean equals(Object other) {
            return other == null || ((Collection)other).size() == 0;
        }
    }
}

