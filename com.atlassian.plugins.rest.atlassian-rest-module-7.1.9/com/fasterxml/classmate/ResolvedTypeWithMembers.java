/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.classmate;

import com.fasterxml.classmate.AnnotationConfiguration;
import com.fasterxml.classmate.AnnotationInclusion;
import com.fasterxml.classmate.Annotations;
import com.fasterxml.classmate.Filter;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeBindings;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.HierarchicType;
import com.fasterxml.classmate.members.RawConstructor;
import com.fasterxml.classmate.members.RawField;
import com.fasterxml.classmate.members.RawMethod;
import com.fasterxml.classmate.members.ResolvedConstructor;
import com.fasterxml.classmate.members.ResolvedField;
import com.fasterxml.classmate.members.ResolvedMethod;
import com.fasterxml.classmate.util.MethodKey;
import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ResolvedTypeWithMembers {
    private static final ResolvedType[] NO_RESOLVED_TYPES = new ResolvedType[0];
    private static final ResolvedMethod[] NO_RESOLVED_METHODS = new ResolvedMethod[0];
    private static final ResolvedField[] NO_RESOLVED_FIELDS = new ResolvedField[0];
    private static final ResolvedConstructor[] NO_RESOLVED_CONSTRUCTORS = new ResolvedConstructor[0];
    protected static final AnnotationConfiguration DEFAULT_ANNOTATION_CONFIG = new AnnotationConfiguration.StdConfiguration(AnnotationInclusion.DONT_INCLUDE);
    protected final TypeResolver _typeResolver;
    protected final AnnotationHandler _annotationHandler;
    protected final HierarchicType _mainType;
    protected final HierarchicType[] _types;
    protected Filter<RawField> _fieldFilter;
    protected Filter<RawConstructor> _constructorFilter;
    protected Filter<RawMethod> _methodFilter;
    protected ResolvedMethod[] _staticMethods = null;
    protected ResolvedField[] _staticFields = null;
    protected ResolvedMethod[] _memberMethods = null;
    protected ResolvedField[] _memberFields = null;
    protected ResolvedConstructor[] _constructors = null;

    public ResolvedTypeWithMembers(TypeResolver typeResolver, AnnotationConfiguration annotationConfig, HierarchicType mainType, HierarchicType[] types, Filter<RawConstructor> constructorFilter, Filter<RawField> fieldFilter, Filter<RawMethod> methodFilter) {
        this._typeResolver = typeResolver;
        this._mainType = mainType;
        this._types = types;
        if (annotationConfig == null) {
            annotationConfig = DEFAULT_ANNOTATION_CONFIG;
        }
        this._annotationHandler = new AnnotationHandler(annotationConfig);
        this._constructorFilter = constructorFilter;
        this._fieldFilter = fieldFilter;
        this._methodFilter = methodFilter;
    }

    public int size() {
        return this._types.length;
    }

    public List<HierarchicType> allTypesAndOverrides() {
        return Arrays.asList(this._types);
    }

    public List<HierarchicType> mainTypeAndOverrides() {
        List<HierarchicType> l = Arrays.asList(this._types);
        int end = this._mainType.getPriority() + 1;
        if (end < l.size()) {
            l = l.subList(0, end);
        }
        return l;
    }

    public List<HierarchicType> overridesOnly() {
        int index = this._mainType.getPriority();
        if (index == 0) {
            return Collections.emptyList();
        }
        List<HierarchicType> l = Arrays.asList(this._types);
        return l.subList(0, index);
    }

    public ResolvedField[] getStaticFields() {
        if (this._staticFields == null) {
            this._staticFields = this.resolveStaticFields();
        }
        return this._staticFields;
    }

    public ResolvedMethod[] getStaticMethods() {
        if (this._staticMethods == null) {
            this._staticMethods = this.resolveStaticMethods();
        }
        return this._staticMethods;
    }

    public ResolvedField[] getMemberFields() {
        if (this._memberFields == null) {
            this._memberFields = this.resolveMemberFields();
        }
        return this._memberFields;
    }

    public ResolvedMethod[] getMemberMethods() {
        if (this._memberMethods == null) {
            this._memberMethods = this.resolveMemberMethods();
        }
        return this._memberMethods;
    }

    public ResolvedConstructor[] getConstructors() {
        if (this._constructors == null) {
            this._constructors = this.resolveConstructors();
        }
        return this._constructors;
    }

    protected ResolvedConstructor[] resolveConstructors() {
        LinkedHashMap<MethodKey, ResolvedConstructor> constructors = new LinkedHashMap<MethodKey, ResolvedConstructor>();
        for (RawConstructor constructor : this._mainType.getType().getConstructors()) {
            if (this._constructorFilter != null && !this._constructorFilter.include(constructor)) continue;
            constructors.put(constructor.createKey(), this.resolveConstructor(constructor));
        }
        for (HierarchicType type : this.overridesOnly()) {
            for (RawConstructor raw : type.getType().getConstructors()) {
                ResolvedConstructor constructor = (ResolvedConstructor)constructors.get(raw.createKey());
                if (constructor == null) continue;
                for (Annotation ann : raw.getAnnotations()) {
                    if (!this._annotationHandler.includeMethodAnnotation(ann)) continue;
                    constructor.applyOverride(ann);
                }
                Annotation[][] params = ((Constructor)raw.getRawMember()).getParameterAnnotations();
                for (int i = 0; i < params.length; ++i) {
                    for (Annotation annotation : params[i]) {
                        if (!this._annotationHandler.includeParameterAnnotation(annotation)) continue;
                        constructor.applyParamOverride(i, annotation);
                    }
                }
            }
        }
        if (constructors.size() == 0) {
            return NO_RESOLVED_CONSTRUCTORS;
        }
        return constructors.values().toArray(new ResolvedConstructor[constructors.size()]);
    }

    protected ResolvedField[] resolveMemberFields() {
        LinkedHashMap<String, ResolvedField> fields = new LinkedHashMap<String, ResolvedField>();
        int typeIndex = this._types.length;
        while (--typeIndex >= 0) {
            HierarchicType thisType = this._types[typeIndex];
            if (thisType.isMixin()) {
                for (RawField raw : thisType.getType().getMemberFields()) {
                    ResolvedField field;
                    if (this._fieldFilter != null && !this._fieldFilter.include(raw) || (field = (ResolvedField)fields.get(raw.getName())) == null) continue;
                    for (Annotation ann : raw.getAnnotations()) {
                        if (!this._annotationHandler.includeMethodAnnotation(ann)) continue;
                        field.applyOverride(ann);
                    }
                }
                continue;
            }
            for (RawField field : thisType.getType().getMemberFields()) {
                if (this._fieldFilter != null && !this._fieldFilter.include(field)) continue;
                fields.put(field.getName(), this.resolveField(field));
            }
        }
        if (fields.size() == 0) {
            return NO_RESOLVED_FIELDS;
        }
        return fields.values().toArray(new ResolvedField[fields.size()]);
    }

    protected ResolvedMethod[] resolveMemberMethods() {
        LinkedHashMap<MethodKey, ResolvedMethod> methods = new LinkedHashMap<MethodKey, ResolvedMethod>();
        LinkedHashMap<MethodKey, Annotations> overrides = new LinkedHashMap<MethodKey, Annotations>();
        LinkedHashMap<MethodKey, Annotations[]> paramOverrides = new LinkedHashMap<MethodKey, Annotations[]>();
        for (HierarchicType type : this.allTypesAndOverrides()) {
            for (RawMethod method : type.getType().getMemberMethods()) {
                if (this._methodFilter != null && !this._methodFilter.include(method)) continue;
                MethodKey key = method.createKey();
                ResolvedMethod old = (ResolvedMethod)methods.get(key);
                if (type.isMixin()) {
                    for (Annotation ann : method.getAnnotations()) {
                        if (old != null) {
                            if (!this.methodCanInherit(ann)) continue;
                            old.applyDefault(ann);
                            continue;
                        }
                        Annotations oldAnn = (Annotations)overrides.get(key);
                        if (oldAnn == null) {
                            oldAnn = new Annotations();
                            oldAnn.add(ann);
                            overrides.put(key, oldAnn);
                            continue;
                        }
                        oldAnn.addAsDefault(ann);
                    }
                    Annotation[][] argAnnotations = method.getRawMember().getParameterAnnotations();
                    if (old == null) {
                        int i;
                        Annotations[] oldParamAnns = (Annotations[])paramOverrides.get(key);
                        if (oldParamAnns == null) {
                            oldParamAnns = new Annotations[argAnnotations.length];
                            for (i = 0; i < argAnnotations.length; ++i) {
                                oldParamAnns[i] = new Annotations();
                                for (Annotation annotation : argAnnotations[i]) {
                                    if (!this.parameterCanInherit(annotation)) continue;
                                    oldParamAnns[i].add(annotation);
                                }
                            }
                            paramOverrides.put(key, oldParamAnns);
                            continue;
                        }
                        for (i = 0; i < argAnnotations.length; ++i) {
                            for (Annotation annotation : argAnnotations[i]) {
                                if (!this.parameterCanInherit(annotation)) continue;
                                oldParamAnns[i].addAsDefault(annotation);
                            }
                        }
                        continue;
                    }
                    for (int i = 0; i < argAnnotations.length; ++i) {
                        for (Annotation annotation : argAnnotations[i]) {
                            if (!this.parameterCanInherit(annotation)) continue;
                            old.applyParamDefault(i, annotation);
                        }
                    }
                    continue;
                }
                if (old == null) {
                    Annotations[] annotations;
                    ResolvedMethod newMethod = this.resolveMethod(method);
                    methods.put(key, newMethod);
                    Annotations overrideAnn = (Annotations)overrides.get(key);
                    if (overrideAnn != null) {
                        newMethod.applyOverrides(overrideAnn);
                    }
                    if ((annotations = (Annotations[])paramOverrides.get(key)) == null) continue;
                    for (int i = 0; i < annotations.length; ++i) {
                        newMethod.applyParamOverrides(i, annotations[i]);
                    }
                    continue;
                }
                for (Annotation ann : method.getAnnotations()) {
                    if (!this.methodCanInherit(ann)) continue;
                    old.applyDefault(ann);
                }
                Annotation[][] parameterAnnotations = method.getRawMember().getParameterAnnotations();
                for (int i = 0; i < parameterAnnotations.length; ++i) {
                    for (Annotation annotation : parameterAnnotations[i]) {
                        if (!this.parameterCanInherit(annotation)) continue;
                        old.applyParamDefault(i, annotation);
                    }
                }
            }
        }
        if (methods.size() == 0) {
            return NO_RESOLVED_METHODS;
        }
        return methods.values().toArray(new ResolvedMethod[methods.size()]);
    }

    protected ResolvedField[] resolveStaticFields() {
        LinkedHashMap<String, ResolvedField> fields = new LinkedHashMap<String, ResolvedField>();
        for (RawField field : this._mainType.getType().getStaticFields()) {
            if (this._fieldFilter != null && !this._fieldFilter.include(field)) continue;
            fields.put(field.getName(), this.resolveField(field));
        }
        for (HierarchicType type : this.overridesOnly()) {
            for (RawField raw : type.getType().getStaticFields()) {
                ResolvedField field = (ResolvedField)fields.get(raw.getName());
                if (field == null) continue;
                for (Annotation ann : raw.getAnnotations()) {
                    if (!this._annotationHandler.includeFieldAnnotation(ann)) continue;
                    field.applyOverride(ann);
                }
            }
        }
        if (fields.isEmpty()) {
            return NO_RESOLVED_FIELDS;
        }
        return fields.values().toArray(new ResolvedField[fields.size()]);
    }

    protected ResolvedMethod[] resolveStaticMethods() {
        LinkedHashMap<MethodKey, ResolvedMethod> methods = new LinkedHashMap<MethodKey, ResolvedMethod>();
        for (RawMethod method : this._mainType.getType().getStaticMethods()) {
            if (this._methodFilter != null && !this._methodFilter.include(method)) continue;
            methods.put(method.createKey(), this.resolveMethod(method));
        }
        for (HierarchicType type : this.overridesOnly()) {
            for (RawMethod raw : type.getType().getStaticMethods()) {
                ResolvedMethod method = (ResolvedMethod)methods.get(raw.createKey());
                if (method == null) continue;
                for (Annotation ann : raw.getAnnotations()) {
                    if (!this._annotationHandler.includeMethodAnnotation(ann)) continue;
                    method.applyOverride(ann);
                }
            }
        }
        if (methods.size() == 0) {
            return NO_RESOLVED_METHODS;
        }
        return methods.values().toArray(new ResolvedMethod[methods.size()]);
    }

    protected ResolvedConstructor resolveConstructor(RawConstructor raw) {
        ResolvedType[] argTypes;
        ResolvedType context = raw.getDeclaringType();
        TypeBindings bindings = context.getTypeBindings();
        Member ctor = raw.getRawMember();
        Type[] rawTypes = ((Constructor)ctor).getGenericParameterTypes();
        if (rawTypes == null || rawTypes.length == 0) {
            argTypes = NO_RESOLVED_TYPES;
        } else {
            argTypes = new ResolvedType[rawTypes.length];
            int len = rawTypes.length;
            for (int i = 0; i < len; ++i) {
                argTypes[i] = this._typeResolver.resolve(bindings, rawTypes[i]);
            }
        }
        Annotations anns = new Annotations();
        for (Annotation ann : ((AccessibleObject)((Object)ctor)).getAnnotations()) {
            if (!this._annotationHandler.includeConstructorAnnotation(ann)) continue;
            anns.add(ann);
        }
        ResolvedConstructor constructor = new ResolvedConstructor(context, anns, (Constructor<?>)ctor, argTypes);
        Annotation[][] annotations = ((Constructor)ctor).getParameterAnnotations();
        for (int i = 0; i < argTypes.length; ++i) {
            for (Annotation ann : annotations[i]) {
                constructor.applyParamOverride(i, ann);
            }
        }
        return constructor;
    }

    protected ResolvedField resolveField(RawField raw) {
        ResolvedType context = raw.getDeclaringType();
        Field field = raw.getRawMember();
        ResolvedType type = this._typeResolver.resolve(context.getTypeBindings(), field.getGenericType());
        Annotations anns = new Annotations();
        for (Annotation ann : field.getAnnotations()) {
            if (!this._annotationHandler.includeFieldAnnotation(ann)) continue;
            anns.add(ann);
        }
        return new ResolvedField(context, anns, field, type);
    }

    protected ResolvedMethod resolveMethod(RawMethod raw) {
        ResolvedType[] argTypes;
        ResolvedType context = raw.getDeclaringType();
        TypeBindings bindings = context.getTypeBindings();
        Method m = raw.getRawMember();
        Type rawType = m.getGenericReturnType();
        ResolvedType rt = rawType == Void.TYPE ? null : this._typeResolver.resolve(bindings, rawType);
        Type[] rawTypes = m.getGenericParameterTypes();
        if (rawTypes == null || rawTypes.length == 0) {
            argTypes = NO_RESOLVED_TYPES;
        } else {
            argTypes = new ResolvedType[rawTypes.length];
            int len = rawTypes.length;
            for (int i = 0; i < len; ++i) {
                argTypes[i] = this._typeResolver.resolve(bindings, rawTypes[i]);
            }
        }
        Annotations anns = new Annotations();
        for (Annotation ann : m.getAnnotations()) {
            if (!this._annotationHandler.includeMethodAnnotation(ann)) continue;
            anns.add(ann);
        }
        ResolvedMethod method = new ResolvedMethod(context, anns, m, rt, argTypes);
        Annotation[][] annotations = m.getParameterAnnotations();
        for (int i = 0; i < argTypes.length; ++i) {
            for (Annotation ann : annotations[i]) {
                method.applyParamOverride(i, ann);
            }
        }
        return method;
    }

    protected boolean methodCanInherit(Annotation annotation) {
        AnnotationInclusion annotationInclusion = this._annotationHandler.methodInclusion(annotation);
        if (annotationInclusion == AnnotationInclusion.INCLUDE_AND_INHERIT_IF_INHERITED) {
            return annotation.annotationType().isAnnotationPresent(Inherited.class);
        }
        return annotationInclusion == AnnotationInclusion.INCLUDE_AND_INHERIT;
    }

    protected boolean parameterCanInherit(Annotation annotation) {
        AnnotationInclusion annotationInclusion = this._annotationHandler.parameterInclusion(annotation);
        if (annotationInclusion == AnnotationInclusion.INCLUDE_AND_INHERIT_IF_INHERITED) {
            return annotation.annotationType().isAnnotationPresent(Inherited.class);
        }
        return annotationInclusion == AnnotationInclusion.INCLUDE_AND_INHERIT;
    }

    private static final class AnnotationHandler {
        private final AnnotationConfiguration _annotationConfig;
        private HashMap<Class<? extends Annotation>, AnnotationInclusion> _fieldInclusions;
        private HashMap<Class<? extends Annotation>, AnnotationInclusion> _constructorInclusions;
        private HashMap<Class<? extends Annotation>, AnnotationInclusion> _methodInclusions;
        private HashMap<Class<? extends Annotation>, AnnotationInclusion> _parameterInclusions;

        public AnnotationHandler(AnnotationConfiguration annotationConfig) {
            this._annotationConfig = annotationConfig;
        }

        public boolean includeConstructorAnnotation(Annotation ann) {
            AnnotationInclusion incl;
            Class<? extends Annotation> annType = ann.annotationType();
            if (this._constructorInclusions == null) {
                this._constructorInclusions = new HashMap();
            } else {
                incl = this._constructorInclusions.get(annType);
                if (incl != null) {
                    return incl != AnnotationInclusion.DONT_INCLUDE;
                }
            }
            incl = this._annotationConfig.getInclusionForConstructor(annType);
            this._constructorInclusions.put(annType, incl);
            return incl != AnnotationInclusion.DONT_INCLUDE;
        }

        public boolean includeFieldAnnotation(Annotation ann) {
            AnnotationInclusion incl;
            Class<? extends Annotation> annType = ann.annotationType();
            if (this._fieldInclusions == null) {
                this._fieldInclusions = new HashMap();
            } else {
                incl = this._fieldInclusions.get(annType);
                if (incl != null) {
                    return incl != AnnotationInclusion.DONT_INCLUDE;
                }
            }
            incl = this._annotationConfig.getInclusionForField(annType);
            this._fieldInclusions.put(annType, incl);
            return incl != AnnotationInclusion.DONT_INCLUDE;
        }

        public boolean includeMethodAnnotation(Annotation ann) {
            return this.methodInclusion(ann) != AnnotationInclusion.DONT_INCLUDE;
        }

        public AnnotationInclusion methodInclusion(Annotation ann) {
            AnnotationInclusion incl;
            Class<? extends Annotation> annType = ann.annotationType();
            if (this._methodInclusions == null) {
                this._methodInclusions = new HashMap();
            } else {
                incl = this._methodInclusions.get(annType);
                if (incl != null) {
                    return incl;
                }
            }
            incl = this._annotationConfig.getInclusionForMethod(annType);
            this._methodInclusions.put(annType, incl);
            return incl;
        }

        public boolean includeParameterAnnotation(Annotation ann) {
            return this.parameterInclusion(ann) != AnnotationInclusion.DONT_INCLUDE;
        }

        public AnnotationInclusion parameterInclusion(Annotation ann) {
            AnnotationInclusion incl;
            Class<? extends Annotation> annType = ann.annotationType();
            if (this._parameterInclusions == null) {
                this._parameterInclusions = new HashMap();
            } else {
                incl = this._parameterInclusions.get(annType);
                if (incl != null) {
                    return incl;
                }
            }
            incl = this._annotationConfig.getInclusionForParameter(annType);
            this._parameterInclusions.put(annType, incl);
            return incl;
        }
    }
}

