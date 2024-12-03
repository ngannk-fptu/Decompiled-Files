/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.introspect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.BeanDescription;
import org.codehaus.jackson.map.BeanPropertyDefinition;
import org.codehaus.jackson.map.MapperConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.introspect.AnnotatedClass;
import org.codehaus.jackson.map.introspect.AnnotatedConstructor;
import org.codehaus.jackson.map.introspect.AnnotatedField;
import org.codehaus.jackson.map.introspect.AnnotatedMember;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;
import org.codehaus.jackson.map.introspect.AnnotatedWithParams;
import org.codehaus.jackson.map.introspect.POJOPropertiesCollector;
import org.codehaus.jackson.map.introspect.VisibilityChecker;
import org.codehaus.jackson.map.type.TypeBindings;
import org.codehaus.jackson.map.util.Annotations;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class BasicBeanDescription
extends BeanDescription {
    protected final MapperConfig<?> _config;
    protected final AnnotationIntrospector _annotationIntrospector;
    protected final AnnotatedClass _classInfo;
    protected TypeBindings _bindings;
    protected final List<BeanPropertyDefinition> _properties;
    protected AnnotatedMethod _anySetterMethod;
    protected Map<Object, AnnotatedMember> _injectables;
    protected Set<String> _ignoredPropertyNames;
    protected Set<String> _ignoredPropertyNamesForDeser;
    protected AnnotatedMethod _jsonValueMethod;
    protected AnnotatedMethod _anyGetterMethod;

    @Deprecated
    public BasicBeanDescription(MapperConfig<?> config, JavaType type, AnnotatedClass ac) {
        this(config, type, ac, Collections.emptyList());
    }

    protected BasicBeanDescription(MapperConfig<?> config, JavaType type, AnnotatedClass ac, List<BeanPropertyDefinition> properties) {
        super(type);
        this._config = config;
        this._annotationIntrospector = config == null ? null : config.getAnnotationIntrospector();
        this._classInfo = ac;
        this._properties = properties;
    }

    public static BasicBeanDescription forDeserialization(POJOPropertiesCollector coll) {
        BasicBeanDescription desc = new BasicBeanDescription(coll.getConfig(), coll.getType(), coll.getClassDef(), coll.getProperties());
        desc._anySetterMethod = coll.getAnySetterMethod();
        desc._ignoredPropertyNames = coll.getIgnoredPropertyNames();
        desc._ignoredPropertyNamesForDeser = coll.getIgnoredPropertyNamesForDeser();
        desc._injectables = coll.getInjectables();
        return desc;
    }

    public static BasicBeanDescription forSerialization(POJOPropertiesCollector coll) {
        BasicBeanDescription desc = new BasicBeanDescription(coll.getConfig(), coll.getType(), coll.getClassDef(), coll.getProperties());
        desc._jsonValueMethod = coll.getJsonValueMethod();
        desc._anyGetterMethod = coll.getAnyGetterMethod();
        return desc;
    }

    public static BasicBeanDescription forOtherUse(MapperConfig<?> config, JavaType type, AnnotatedClass ac) {
        return new BasicBeanDescription(config, type, ac, Collections.<BeanPropertyDefinition>emptyList());
    }

    @Override
    public AnnotatedClass getClassInfo() {
        return this._classInfo;
    }

    @Override
    public List<BeanPropertyDefinition> findProperties() {
        return this._properties;
    }

    @Override
    public AnnotatedMethod findJsonValueMethod() {
        return this._jsonValueMethod;
    }

    @Override
    public Set<String> getIgnoredPropertyNames() {
        if (this._ignoredPropertyNames == null) {
            return Collections.emptySet();
        }
        return this._ignoredPropertyNames;
    }

    public Set<String> getIgnoredPropertyNamesForDeser() {
        return this._ignoredPropertyNamesForDeser;
    }

    @Override
    public boolean hasKnownClassAnnotations() {
        return this._classInfo.hasAnnotations();
    }

    @Override
    public Annotations getClassAnnotations() {
        return this._classInfo.getAnnotations();
    }

    @Override
    public TypeBindings bindingsForBeanType() {
        if (this._bindings == null) {
            this._bindings = new TypeBindings(this._config.getTypeFactory(), this._type);
        }
        return this._bindings;
    }

    @Override
    public JavaType resolveType(Type jdkType) {
        if (jdkType == null) {
            return null;
        }
        return this.bindingsForBeanType().resolveType(jdkType);
    }

    @Override
    public AnnotatedConstructor findDefaultConstructor() {
        return this._classInfo.getDefaultConstructor();
    }

    @Override
    public AnnotatedMethod findAnySetter() throws IllegalArgumentException {
        Class<?> type;
        if (this._anySetterMethod != null && (type = this._anySetterMethod.getParameterClass(0)) != String.class && type != Object.class) {
            throw new IllegalArgumentException("Invalid 'any-setter' annotation on method " + this._anySetterMethod.getName() + "(): first argument not of type String or Object, but " + type.getName());
        }
        return this._anySetterMethod;
    }

    @Override
    public Map<Object, AnnotatedMember> findInjectables() {
        return this._injectables;
    }

    public List<AnnotatedConstructor> getConstructors() {
        return this._classInfo.getConstructors();
    }

    public AnnotatedMethod findMethod(String name, Class<?>[] paramTypes) {
        return this._classInfo.findMethod(name, paramTypes);
    }

    public Object instantiateBean(boolean fixAccess) {
        AnnotatedConstructor ac = this._classInfo.getDefaultConstructor();
        if (ac == null) {
            return null;
        }
        if (fixAccess) {
            ac.fixAccess();
        }
        try {
            return ((Constructor)ac.getAnnotated()).newInstance(new Object[0]);
        }
        catch (Exception e) {
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
            throw new IllegalArgumentException("Failed to instantiate bean of type " + ((Class)this._classInfo.getAnnotated()).getName() + ": (" + t.getClass().getName() + ") " + t.getMessage(), t);
        }
    }

    public List<AnnotatedMethod> getFactoryMethods() {
        List<AnnotatedMethod> candidates = this._classInfo.getStaticMethods();
        if (candidates.isEmpty()) {
            return candidates;
        }
        ArrayList<AnnotatedMethod> result = new ArrayList<AnnotatedMethod>();
        for (AnnotatedMethod am : candidates) {
            if (!this.isFactoryMethod(am)) continue;
            result.add(am);
        }
        return result;
    }

    public Constructor<?> findSingleArgConstructor(Class<?> ... argTypes) {
        for (AnnotatedConstructor ac : this._classInfo.getConstructors()) {
            if (ac.getParameterCount() != 1) continue;
            Class<?> actArg = ac.getParameterClass(0);
            for (Class<?> expArg : argTypes) {
                if (expArg != actArg) continue;
                return ac.getAnnotated();
            }
        }
        return null;
    }

    public Method findFactoryMethod(Class<?> ... expArgTypes) {
        for (AnnotatedMethod am : this._classInfo.getStaticMethods()) {
            if (!this.isFactoryMethod(am)) continue;
            Class<?> actualArgType = am.getParameterClass(0);
            for (Class<?> expArgType : expArgTypes) {
                if (!actualArgType.isAssignableFrom(expArgType)) continue;
                return am.getAnnotated();
            }
        }
        return null;
    }

    protected boolean isFactoryMethod(AnnotatedMethod am) {
        Class<?> rt = am.getRawType();
        if (!this.getBeanClass().isAssignableFrom(rt)) {
            return false;
        }
        if (this._annotationIntrospector.hasCreatorAnnotation(am)) {
            return true;
        }
        return "valueOf".equals(am.getName());
    }

    public List<String> findCreatorPropertyNames() {
        ArrayList<String> names = null;
        for (int i = 0; i < 2; ++i) {
            List<AnnotatedWithParams> l = i == 0 ? this.getConstructors() : this.getFactoryMethods();
            for (AnnotatedWithParams creator : l) {
                String name;
                int argCount = creator.getParameterCount();
                if (argCount < 1 || (name = this._annotationIntrospector.findPropertyNameForParam(creator.getParameter(0))) == null) continue;
                if (names == null) {
                    names = new ArrayList<String>();
                }
                names.add(name);
                for (int p = 1; p < argCount; ++p) {
                    names.add(this._annotationIntrospector.findPropertyNameForParam(creator.getParameter(p)));
                }
            }
        }
        if (names == null) {
            return Collections.emptyList();
        }
        return names;
    }

    public JsonSerialize.Inclusion findSerializationInclusion(JsonSerialize.Inclusion defValue) {
        if (this._annotationIntrospector == null) {
            return defValue;
        }
        return this._annotationIntrospector.findSerializationInclusion(this._classInfo, defValue);
    }

    @Override
    public AnnotatedMethod findAnyGetter() throws IllegalArgumentException {
        Class<?> type;
        if (this._anyGetterMethod != null && !Map.class.isAssignableFrom(type = this._anyGetterMethod.getRawType())) {
            throw new IllegalArgumentException("Invalid 'any-getter' annotation on method " + this._anyGetterMethod.getName() + "(): return type is not instance of java.util.Map");
        }
        return this._anyGetterMethod;
    }

    public Map<String, AnnotatedMember> findBackReferenceProperties() {
        HashMap<String, AnnotatedMember> result = null;
        for (BeanPropertyDefinition property : this._properties) {
            String refName;
            AnnotationIntrospector.ReferenceProperty refDef;
            AnnotatedMember am = property.getMutator();
            if (am == null || (refDef = this._annotationIntrospector.findReferenceType(am)) == null || !refDef.isBackReference()) continue;
            if (result == null) {
                result = new HashMap<String, AnnotatedMember>();
            }
            if (result.put(refName = refDef.getName(), am) == null) continue;
            throw new IllegalArgumentException("Multiple back-reference properties with name '" + refName + "'");
        }
        return result;
    }

    public LinkedHashMap<String, AnnotatedField> _findPropertyFields(Collection<String> ignoredProperties, boolean forSerialization) {
        LinkedHashMap<String, AnnotatedField> results = new LinkedHashMap<String, AnnotatedField>();
        for (BeanPropertyDefinition property : this._properties) {
            AnnotatedField f = property.getField();
            if (f == null) continue;
            String name = property.getName();
            if (ignoredProperties != null && ignoredProperties.contains(name)) continue;
            results.put(name, f);
        }
        return results;
    }

    @Override
    public LinkedHashMap<String, AnnotatedMethod> findGetters(VisibilityChecker<?> visibilityChecker, Collection<String> ignoredProperties) {
        LinkedHashMap<String, AnnotatedMethod> results = new LinkedHashMap<String, AnnotatedMethod>();
        for (BeanPropertyDefinition property : this._properties) {
            AnnotatedMethod m = property.getGetter();
            if (m == null) continue;
            String name = property.getName();
            if (ignoredProperties != null && ignoredProperties.contains(name)) continue;
            results.put(name, m);
        }
        return results;
    }

    @Override
    public LinkedHashMap<String, AnnotatedMethod> findSetters(VisibilityChecker<?> visibilityChecker) {
        LinkedHashMap<String, AnnotatedMethod> results = new LinkedHashMap<String, AnnotatedMethod>();
        for (BeanPropertyDefinition property : this._properties) {
            AnnotatedMethod m = property.getSetter();
            if (m == null) continue;
            results.put(property.getName(), m);
        }
        return results;
    }

    public LinkedHashMap<String, AnnotatedField> findSerializableFields(VisibilityChecker<?> visibilityChecker, Collection<String> ignoredProperties) {
        return this._findPropertyFields(ignoredProperties, true);
    }

    @Override
    public LinkedHashMap<String, AnnotatedField> findDeserializableFields(VisibilityChecker<?> visibilityChecker, Collection<String> ignoredProperties) {
        return this._findPropertyFields(ignoredProperties, false);
    }
}

