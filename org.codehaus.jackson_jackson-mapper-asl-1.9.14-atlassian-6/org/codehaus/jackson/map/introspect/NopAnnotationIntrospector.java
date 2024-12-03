/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.introspect;

import java.lang.annotation.Annotation;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.KeyDeserializer;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.introspect.Annotated;
import org.codehaus.jackson.map.introspect.AnnotatedClass;
import org.codehaus.jackson.map.introspect.AnnotatedConstructor;
import org.codehaus.jackson.map.introspect.AnnotatedField;
import org.codehaus.jackson.map.introspect.AnnotatedMember;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;
import org.codehaus.jackson.map.introspect.AnnotatedParameter;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class NopAnnotationIntrospector
extends AnnotationIntrospector {
    public static final NopAnnotationIntrospector instance = new NopAnnotationIntrospector();

    @Override
    public boolean isHandled(Annotation ann) {
        return false;
    }

    @Override
    public String findEnumValue(Enum<?> value) {
        return value.name();
    }

    @Override
    public String findRootName(AnnotatedClass ac) {
        return null;
    }

    @Override
    public String[] findPropertiesToIgnore(AnnotatedClass ac) {
        return null;
    }

    @Override
    public Boolean findIgnoreUnknownProperties(AnnotatedClass ac) {
        return null;
    }

    @Override
    public boolean hasIgnoreMarker(AnnotatedMember member) {
        return false;
    }

    @Override
    public boolean isIgnorableConstructor(AnnotatedConstructor c) {
        return false;
    }

    @Override
    public boolean isIgnorableMethod(AnnotatedMethod m) {
        return false;
    }

    @Override
    public boolean isIgnorableField(AnnotatedField f) {
        return false;
    }

    @Override
    public Object findSerializer(Annotated am) {
        return null;
    }

    @Override
    public Class<?> findSerializationType(Annotated a) {
        return null;
    }

    @Override
    public JsonSerialize.Typing findSerializationTyping(Annotated a) {
        return null;
    }

    @Override
    public Class<?>[] findSerializationViews(Annotated a) {
        return null;
    }

    @Override
    public String[] findSerializationPropertyOrder(AnnotatedClass ac) {
        return null;
    }

    @Override
    public Boolean findSerializationSortAlphabetically(AnnotatedClass ac) {
        return null;
    }

    @Override
    public String findGettablePropertyName(AnnotatedMethod am) {
        return null;
    }

    @Override
    public boolean hasAsValueAnnotation(AnnotatedMethod am) {
        return false;
    }

    @Override
    public String findDeserializablePropertyName(AnnotatedField af) {
        return null;
    }

    @Override
    public Class<?> findDeserializationContentType(Annotated am, JavaType t, String propName) {
        return null;
    }

    @Override
    public Class<?> findDeserializationKeyType(Annotated am, JavaType t, String propName) {
        return null;
    }

    @Override
    public Class<?> findDeserializationType(Annotated am, JavaType t, String propName) {
        return null;
    }

    @Override
    public Object findDeserializer(Annotated am) {
        return null;
    }

    public Class<KeyDeserializer> findKeyDeserializer(Annotated am) {
        return null;
    }

    public Class<JsonDeserializer<?>> findContentDeserializer(Annotated am) {
        return null;
    }

    @Override
    public String findPropertyNameForParam(AnnotatedParameter param) {
        return null;
    }

    @Override
    public String findSerializablePropertyName(AnnotatedField af) {
        return null;
    }

    @Override
    public String findSettablePropertyName(AnnotatedMethod am) {
        return null;
    }
}

