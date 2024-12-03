/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.codehaus.jackson.map.BeanPropertyDefinition;
import org.codehaus.jackson.map.introspect.AnnotatedClass;
import org.codehaus.jackson.map.introspect.AnnotatedConstructor;
import org.codehaus.jackson.map.introspect.AnnotatedField;
import org.codehaus.jackson.map.introspect.AnnotatedMember;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;
import org.codehaus.jackson.map.introspect.VisibilityChecker;
import org.codehaus.jackson.map.type.TypeBindings;
import org.codehaus.jackson.map.util.Annotations;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class BeanDescription {
    protected final JavaType _type;

    protected BeanDescription(JavaType type) {
        this._type = type;
    }

    public JavaType getType() {
        return this._type;
    }

    public Class<?> getBeanClass() {
        return this._type.getRawClass();
    }

    public abstract AnnotatedClass getClassInfo();

    public abstract boolean hasKnownClassAnnotations();

    public abstract TypeBindings bindingsForBeanType();

    public abstract JavaType resolveType(Type var1);

    public abstract Annotations getClassAnnotations();

    public abstract List<BeanPropertyDefinition> findProperties();

    public abstract Map<Object, AnnotatedMember> findInjectables();

    public abstract AnnotatedMethod findAnyGetter();

    public abstract AnnotatedMethod findAnySetter();

    public abstract AnnotatedMethod findJsonValueMethod();

    public abstract AnnotatedConstructor findDefaultConstructor();

    public abstract Set<String> getIgnoredPropertyNames();

    @Deprecated
    public abstract LinkedHashMap<String, AnnotatedMethod> findGetters(VisibilityChecker<?> var1, Collection<String> var2);

    @Deprecated
    public abstract LinkedHashMap<String, AnnotatedMethod> findSetters(VisibilityChecker<?> var1);

    @Deprecated
    public abstract LinkedHashMap<String, AnnotatedField> findDeserializableFields(VisibilityChecker<?> var1, Collection<String> var2);

    @Deprecated
    public abstract Map<String, AnnotatedField> findSerializableFields(VisibilityChecker<?> var1, Collection<String> var2);
}

