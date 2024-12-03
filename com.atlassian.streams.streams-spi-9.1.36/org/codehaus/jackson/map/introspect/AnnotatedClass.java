/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.introspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ClassIntrospector;
import org.codehaus.jackson.map.introspect.Annotated;
import org.codehaus.jackson.map.introspect.AnnotatedConstructor;
import org.codehaus.jackson.map.introspect.AnnotatedField;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;
import org.codehaus.jackson.map.introspect.AnnotatedMethodMap;
import org.codehaus.jackson.map.introspect.AnnotationMap;
import org.codehaus.jackson.map.introspect.MemberKey;
import org.codehaus.jackson.map.introspect.MethodFilter;
import org.codehaus.jackson.map.util.Annotations;
import org.codehaus.jackson.map.util.ClassUtil;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class AnnotatedClass
extends Annotated {
    private static final AnnotationMap[] NO_ANNOTATION_MAPS = new AnnotationMap[0];
    protected final Class<?> _class;
    protected final List<Class<?>> _superTypes;
    protected final AnnotationIntrospector _annotationIntrospector;
    protected final ClassIntrospector.MixInResolver _mixInResolver;
    protected final Class<?> _primaryMixIn;
    protected AnnotationMap _classAnnotations;
    protected AnnotatedConstructor _defaultConstructor;
    protected List<AnnotatedConstructor> _constructors;
    protected List<AnnotatedMethod> _creatorMethods;
    protected AnnotatedMethodMap _memberMethods;
    protected List<AnnotatedField> _fields;

    private AnnotatedClass(Class<?> cls, List<Class<?>> superTypes, AnnotationIntrospector aintr, ClassIntrospector.MixInResolver mir, AnnotationMap classAnnotations) {
        this._class = cls;
        this._superTypes = superTypes;
        this._annotationIntrospector = aintr;
        this._mixInResolver = mir;
        this._primaryMixIn = this._mixInResolver == null ? null : this._mixInResolver.findMixInClassFor(this._class);
        this._classAnnotations = classAnnotations;
    }

    @Override
    public AnnotatedClass withAnnotations(AnnotationMap ann) {
        return new AnnotatedClass(this._class, this._superTypes, this._annotationIntrospector, this._mixInResolver, ann);
    }

    public static AnnotatedClass construct(Class<?> cls, AnnotationIntrospector aintr, ClassIntrospector.MixInResolver mir) {
        List<Class<?>> st = ClassUtil.findSuperTypes(cls, null);
        AnnotatedClass ac = new AnnotatedClass(cls, st, aintr, mir, null);
        ac.resolveClassAnnotations();
        return ac;
    }

    public static AnnotatedClass constructWithoutSuperTypes(Class<?> cls, AnnotationIntrospector aintr, ClassIntrospector.MixInResolver mir) {
        List<Class<?>> empty = Collections.emptyList();
        AnnotatedClass ac = new AnnotatedClass(cls, empty, aintr, mir, null);
        ac.resolveClassAnnotations();
        return ac;
    }

    @Override
    public Class<?> getAnnotated() {
        return this._class;
    }

    @Override
    public int getModifiers() {
        return this._class.getModifiers();
    }

    @Override
    public String getName() {
        return this._class.getName();
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> acls) {
        if (this._classAnnotations == null) {
            return null;
        }
        return this._classAnnotations.get(acls);
    }

    @Override
    public Type getGenericType() {
        return this._class;
    }

    @Override
    public Class<?> getRawType() {
        return this._class;
    }

    @Override
    protected AnnotationMap getAllAnnotations() {
        return this._classAnnotations;
    }

    public Annotations getAnnotations() {
        return this._classAnnotations;
    }

    public boolean hasAnnotations() {
        return this._classAnnotations.size() > 0;
    }

    public AnnotatedConstructor getDefaultConstructor() {
        return this._defaultConstructor;
    }

    public List<AnnotatedConstructor> getConstructors() {
        if (this._constructors == null) {
            return Collections.emptyList();
        }
        return this._constructors;
    }

    public List<AnnotatedMethod> getStaticMethods() {
        if (this._creatorMethods == null) {
            return Collections.emptyList();
        }
        return this._creatorMethods;
    }

    public Iterable<AnnotatedMethod> memberMethods() {
        return this._memberMethods;
    }

    public int getMemberMethodCount() {
        return this._memberMethods.size();
    }

    public AnnotatedMethod findMethod(String name, Class<?>[] paramTypes) {
        return this._memberMethods.find(name, paramTypes);
    }

    public int getFieldCount() {
        return this._fields == null ? 0 : this._fields.size();
    }

    public Iterable<AnnotatedField> fields() {
        if (this._fields == null) {
            return Collections.emptyList();
        }
        return this._fields;
    }

    public void resolveClassAnnotations() {
        this._classAnnotations = new AnnotationMap();
        if (this._annotationIntrospector == null) {
            return;
        }
        if (this._primaryMixIn != null) {
            this._addClassMixIns(this._classAnnotations, this._class, this._primaryMixIn);
        }
        for (Annotation a : this._class.getDeclaredAnnotations()) {
            if (!this._annotationIntrospector.isHandled(a)) continue;
            this._classAnnotations.addIfNotPresent(a);
        }
        for (Class clazz : this._superTypes) {
            this._addClassMixIns(this._classAnnotations, clazz);
            for (Annotation a : clazz.getDeclaredAnnotations()) {
                if (!this._annotationIntrospector.isHandled(a)) continue;
                this._classAnnotations.addIfNotPresent(a);
            }
        }
        this._addClassMixIns(this._classAnnotations, Object.class);
    }

    public void resolveCreators(boolean includeAll) {
        Constructor<?>[] declaredCtors;
        this._constructors = null;
        for (Constructor<?> ctor : declaredCtors = this._class.getDeclaredConstructors()) {
            if (ctor.getParameterTypes().length == 0) {
                this._defaultConstructor = this._constructConstructor(ctor, true);
                continue;
            }
            if (!includeAll) continue;
            if (this._constructors == null) {
                this._constructors = new ArrayList<AnnotatedConstructor>(Math.max(10, declaredCtors.length));
            }
            this._constructors.add(this._constructConstructor(ctor, false));
        }
        if (this._primaryMixIn != null && (this._defaultConstructor != null || this._constructors != null)) {
            this._addConstructorMixIns(this._primaryMixIn);
        }
        if (this._annotationIntrospector != null) {
            if (this._defaultConstructor != null && this._annotationIntrospector.isIgnorableConstructor(this._defaultConstructor)) {
                this._defaultConstructor = null;
            }
            if (this._constructors != null) {
                int i = this._constructors.size();
                while (--i >= 0) {
                    if (!this._annotationIntrospector.isIgnorableConstructor(this._constructors.get(i))) continue;
                    this._constructors.remove(i);
                }
            }
        }
        this._creatorMethods = null;
        if (includeAll) {
            for (Method m : this._class.getDeclaredMethods()) {
                int argCount;
                if (!Modifier.isStatic(m.getModifiers()) || (argCount = m.getParameterTypes().length) < 1) continue;
                if (this._creatorMethods == null) {
                    this._creatorMethods = new ArrayList<AnnotatedMethod>(8);
                }
                this._creatorMethods.add(this._constructCreatorMethod(m));
            }
            if (this._primaryMixIn != null && this._creatorMethods != null) {
                this._addFactoryMixIns(this._primaryMixIn);
            }
            if (this._annotationIntrospector != null && this._creatorMethods != null) {
                int i = this._creatorMethods.size();
                while (--i >= 0) {
                    if (!this._annotationIntrospector.isIgnorableMethod(this._creatorMethods.get(i))) continue;
                    this._creatorMethods.remove(i);
                }
            }
        }
    }

    public void resolveMemberMethods(MethodFilter methodFilter) {
        Class<?> mixin;
        this._memberMethods = new AnnotatedMethodMap();
        AnnotatedMethodMap mixins = new AnnotatedMethodMap();
        this._addMemberMethods(this._class, methodFilter, this._memberMethods, this._primaryMixIn, mixins);
        for (Class<?> cls : this._superTypes) {
            Class<?> mixin2 = this._mixInResolver == null ? null : this._mixInResolver.findMixInClassFor(cls);
            this._addMemberMethods(cls, methodFilter, this._memberMethods, mixin2, mixins);
        }
        if (this._mixInResolver != null && (mixin = this._mixInResolver.findMixInClassFor(Object.class)) != null) {
            this._addMethodMixIns(this._class, methodFilter, this._memberMethods, mixin, mixins);
        }
        if (this._annotationIntrospector != null && !mixins.isEmpty()) {
            for (AnnotatedMethod mixIn : mixins) {
                try {
                    Method m = Object.class.getDeclaredMethod(mixIn.getName(), mixIn.getParameterClasses());
                    if (m == null) continue;
                    AnnotatedMethod am = this._constructMethod(m);
                    this._addMixOvers(mixIn.getAnnotated(), am, false);
                    this._memberMethods.add(am);
                }
                catch (Exception exception) {}
            }
        }
    }

    public void resolveFields() {
        LinkedHashMap<String, AnnotatedField> foundFields = new LinkedHashMap<String, AnnotatedField>();
        this._addFields(foundFields, this._class);
        if (foundFields.isEmpty()) {
            this._fields = Collections.emptyList();
        } else {
            this._fields = new ArrayList<AnnotatedField>(foundFields.size());
            this._fields.addAll(foundFields.values());
        }
    }

    @Deprecated
    public void resolveMemberMethods(MethodFilter methodFilter, boolean collectIgnored) {
        this.resolveMemberMethods(methodFilter);
    }

    @Deprecated
    public void resolveFields(boolean collectIgnored) {
        this.resolveFields();
    }

    protected void _addClassMixIns(AnnotationMap annotations, Class<?> toMask) {
        if (this._mixInResolver != null) {
            this._addClassMixIns(annotations, toMask, this._mixInResolver.findMixInClassFor(toMask));
        }
    }

    protected void _addClassMixIns(AnnotationMap annotations, Class<?> toMask, Class<?> mixin) {
        if (mixin == null) {
            return;
        }
        for (Annotation a : mixin.getDeclaredAnnotations()) {
            if (!this._annotationIntrospector.isHandled(a)) continue;
            annotations.addIfNotPresent(a);
        }
        for (Class clazz : ClassUtil.findSuperTypes(mixin, toMask)) {
            for (Annotation a : clazz.getDeclaredAnnotations()) {
                if (!this._annotationIntrospector.isHandled(a)) continue;
                annotations.addIfNotPresent(a);
            }
        }
    }

    protected void _addConstructorMixIns(Class<?> mixin) {
        MemberKey[] ctorKeys = null;
        int ctorCount = this._constructors == null ? 0 : this._constructors.size();
        block0: for (Constructor<?> ctor : mixin.getDeclaredConstructors()) {
            if (ctor.getParameterTypes().length == 0) {
                if (this._defaultConstructor == null) continue;
                this._addMixOvers(ctor, this._defaultConstructor, false);
                continue;
            }
            if (ctorKeys == null) {
                ctorKeys = new MemberKey[ctorCount];
                for (int i = 0; i < ctorCount; ++i) {
                    ctorKeys[i] = new MemberKey((Constructor<?>)this._constructors.get(i).getAnnotated());
                }
            }
            MemberKey key = new MemberKey(ctor);
            for (int i = 0; i < ctorCount; ++i) {
                if (!key.equals(ctorKeys[i])) continue;
                this._addMixOvers(ctor, this._constructors.get(i), true);
                continue block0;
            }
        }
    }

    protected void _addFactoryMixIns(Class<?> mixin) {
        MemberKey[] methodKeys = null;
        int methodCount = this._creatorMethods.size();
        block0: for (Method m : mixin.getDeclaredMethods()) {
            if (!Modifier.isStatic(m.getModifiers()) || m.getParameterTypes().length == 0) continue;
            if (methodKeys == null) {
                methodKeys = new MemberKey[methodCount];
                for (int i = 0; i < methodCount; ++i) {
                    methodKeys[i] = new MemberKey(this._creatorMethods.get(i).getAnnotated());
                }
            }
            MemberKey key = new MemberKey(m);
            for (int i = 0; i < methodCount; ++i) {
                if (!key.equals(methodKeys[i])) continue;
                this._addMixOvers(m, this._creatorMethods.get(i), true);
                continue block0;
            }
        }
    }

    protected void _addMemberMethods(Class<?> cls, MethodFilter methodFilter, AnnotatedMethodMap methods, Class<?> mixInCls, AnnotatedMethodMap mixIns) {
        if (mixInCls != null) {
            this._addMethodMixIns(cls, methodFilter, methods, mixInCls, mixIns);
        }
        if (cls == null) {
            return;
        }
        for (Method m : cls.getDeclaredMethods()) {
            if (!this._isIncludableMethod(m, methodFilter)) continue;
            AnnotatedMethod old = methods.find(m);
            if (old == null) {
                AnnotatedMethod newM = this._constructMethod(m);
                methods.add(newM);
                old = mixIns.remove(m);
                if (old == null) continue;
                this._addMixOvers(old.getAnnotated(), newM, false);
                continue;
            }
            this._addMixUnders(m, old);
            if (!old.getDeclaringClass().isInterface() || m.getDeclaringClass().isInterface()) continue;
            methods.add(old.withMethod(m));
        }
    }

    protected void _addMethodMixIns(Class<?> targetClass, MethodFilter methodFilter, AnnotatedMethodMap methods, Class<?> mixInCls, AnnotatedMethodMap mixIns) {
        ArrayList parents = new ArrayList();
        parents.add(mixInCls);
        ClassUtil.findSuperTypes(mixInCls, targetClass, parents);
        for (Class clazz : parents) {
            for (Method m : clazz.getDeclaredMethods()) {
                if (!this._isIncludableMethod(m, methodFilter)) continue;
                AnnotatedMethod am = methods.find(m);
                if (am != null) {
                    this._addMixUnders(m, am);
                    continue;
                }
                mixIns.add(this._constructMethod(m));
            }
        }
    }

    protected void _addFields(Map<String, AnnotatedField> fields, Class<?> c) {
        Class<?> parent = c.getSuperclass();
        if (parent != null) {
            Class<?> mixin;
            this._addFields(fields, parent);
            for (Field f : c.getDeclaredFields()) {
                if (!this._isIncludableField(f)) continue;
                fields.put(f.getName(), this._constructField(f));
            }
            if (this._mixInResolver != null && (mixin = this._mixInResolver.findMixInClassFor(c)) != null) {
                this._addFieldMixIns(parent, mixin, fields);
            }
        }
    }

    protected void _addFieldMixIns(Class<?> targetClass, Class<?> mixInCls, Map<String, AnnotatedField> fields) {
        ArrayList parents = new ArrayList();
        parents.add(mixInCls);
        ClassUtil.findSuperTypes(mixInCls, targetClass, parents);
        for (Class clazz : parents) {
            for (Field mixinField : clazz.getDeclaredFields()) {
                String name;
                AnnotatedField maskedField;
                if (!this._isIncludableField(mixinField) || (maskedField = fields.get(name = mixinField.getName())) == null) continue;
                for (Annotation a : mixinField.getDeclaredAnnotations()) {
                    if (!this._annotationIntrospector.isHandled(a)) continue;
                    maskedField.addOrOverride(a);
                }
            }
        }
    }

    protected AnnotatedMethod _constructMethod(Method m) {
        if (this._annotationIntrospector == null) {
            return new AnnotatedMethod(m, this._emptyAnnotationMap(), null);
        }
        return new AnnotatedMethod(m, this._collectRelevantAnnotations(m.getDeclaredAnnotations()), null);
    }

    protected AnnotatedConstructor _constructConstructor(Constructor<?> ctor, boolean defaultCtor) {
        if (this._annotationIntrospector == null) {
            return new AnnotatedConstructor(ctor, this._emptyAnnotationMap(), this._emptyAnnotationMaps(ctor.getParameterTypes().length));
        }
        if (defaultCtor) {
            return new AnnotatedConstructor(ctor, this._collectRelevantAnnotations(ctor.getDeclaredAnnotations()), null);
        }
        Annotation[][] paramAnns = ctor.getParameterAnnotations();
        int paramCount = ctor.getParameterTypes().length;
        AnnotationMap[] resolvedAnnotations = null;
        if (paramCount != paramAnns.length) {
            Class<?> dc = ctor.getDeclaringClass();
            if (dc.isEnum() && paramCount == paramAnns.length + 2) {
                Annotation[][] old = paramAnns;
                paramAnns = new Annotation[old.length + 2][];
                System.arraycopy(old, 0, paramAnns, 2, old.length);
                resolvedAnnotations = this._collectRelevantAnnotations(paramAnns);
            } else if (dc.isMemberClass() && paramCount == paramAnns.length + 1) {
                Annotation[][] old = paramAnns;
                paramAnns = new Annotation[old.length + 1][];
                System.arraycopy(old, 0, paramAnns, 1, old.length);
                resolvedAnnotations = this._collectRelevantAnnotations(paramAnns);
            }
            if (resolvedAnnotations == null) {
                throw new IllegalStateException("Internal error: constructor for " + ctor.getDeclaringClass().getName() + " has mismatch: " + paramCount + " parameters; " + paramAnns.length + " sets of annotations");
            }
        } else {
            resolvedAnnotations = this._collectRelevantAnnotations(paramAnns);
        }
        return new AnnotatedConstructor(ctor, this._collectRelevantAnnotations(ctor.getDeclaredAnnotations()), resolvedAnnotations);
    }

    protected AnnotatedMethod _constructCreatorMethod(Method m) {
        if (this._annotationIntrospector == null) {
            return new AnnotatedMethod(m, this._emptyAnnotationMap(), this._emptyAnnotationMaps(m.getParameterTypes().length));
        }
        return new AnnotatedMethod(m, this._collectRelevantAnnotations(m.getDeclaredAnnotations()), this._collectRelevantAnnotations(m.getParameterAnnotations()));
    }

    protected AnnotatedField _constructField(Field f) {
        if (this._annotationIntrospector == null) {
            return new AnnotatedField(f, this._emptyAnnotationMap());
        }
        return new AnnotatedField(f, this._collectRelevantAnnotations(f.getDeclaredAnnotations()));
    }

    protected AnnotationMap[] _collectRelevantAnnotations(Annotation[][] anns) {
        int len = anns.length;
        AnnotationMap[] result = new AnnotationMap[len];
        for (int i = 0; i < len; ++i) {
            result[i] = this._collectRelevantAnnotations(anns[i]);
        }
        return result;
    }

    protected AnnotationMap _collectRelevantAnnotations(Annotation[] anns) {
        AnnotationMap annMap = new AnnotationMap();
        if (anns != null) {
            for (Annotation a : anns) {
                if (!this._annotationIntrospector.isHandled(a)) continue;
                annMap.add(a);
            }
        }
        return annMap;
    }

    private AnnotationMap _emptyAnnotationMap() {
        return new AnnotationMap();
    }

    private AnnotationMap[] _emptyAnnotationMaps(int count) {
        if (count == 0) {
            return NO_ANNOTATION_MAPS;
        }
        AnnotationMap[] maps = new AnnotationMap[count];
        for (int i = 0; i < count; ++i) {
            maps[i] = this._emptyAnnotationMap();
        }
        return maps;
    }

    protected boolean _isIncludableMethod(Method m, MethodFilter filter) {
        if (filter != null && !filter.includeMethod(m)) {
            return false;
        }
        return !m.isSynthetic() && !m.isBridge();
    }

    private boolean _isIncludableField(Field f) {
        if (f.isSynthetic()) {
            return false;
        }
        int mods = f.getModifiers();
        return !Modifier.isStatic(mods) && !Modifier.isTransient(mods);
    }

    protected void _addMixOvers(Constructor<?> mixin, AnnotatedConstructor target, boolean addParamAnnotations) {
        for (Annotation a : mixin.getDeclaredAnnotations()) {
            if (!this._annotationIntrospector.isHandled(a)) continue;
            target.addOrOverride(a);
        }
        if (addParamAnnotations) {
            Annotation[][] pa = mixin.getParameterAnnotations();
            int len = pa.length;
            for (int i = 0; i < len; ++i) {
                for (Annotation a : pa[i]) {
                    target.addOrOverrideParam(i, a);
                }
            }
        }
    }

    protected void _addMixOvers(Method mixin, AnnotatedMethod target, boolean addParamAnnotations) {
        for (Annotation a : mixin.getDeclaredAnnotations()) {
            if (!this._annotationIntrospector.isHandled(a)) continue;
            target.addOrOverride(a);
        }
        if (addParamAnnotations) {
            Annotation[][] pa = mixin.getParameterAnnotations();
            int len = pa.length;
            for (int i = 0; i < len; ++i) {
                for (Annotation a : pa[i]) {
                    target.addOrOverrideParam(i, a);
                }
            }
        }
    }

    protected void _addMixUnders(Method src, AnnotatedMethod target) {
        for (Annotation a : src.getDeclaredAnnotations()) {
            if (!this._annotationIntrospector.isHandled(a)) continue;
            target.addIfNotPresent(a);
        }
    }

    public String toString() {
        return "[AnnotedClass " + this._class.getName() + "]";
    }
}

