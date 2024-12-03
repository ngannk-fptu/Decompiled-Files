/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.classmate;

import com.fasterxml.classmate.AnnotationConfiguration;
import com.fasterxml.classmate.AnnotationOverrides;
import com.fasterxml.classmate.Filter;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.HierarchicType;
import com.fasterxml.classmate.members.RawConstructor;
import com.fasterxml.classmate.members.RawField;
import com.fasterxml.classmate.members.RawMethod;
import com.fasterxml.classmate.util.ClassKey;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MemberResolver
implements Serializable {
    protected final TypeResolver _typeResolver;
    protected boolean _cfgIncludeLangObject;
    protected Filter<RawField> _fieldFilter;
    protected Filter<RawMethod> _methodFilter;
    protected Filter<RawConstructor> _constructorFilter;

    public MemberResolver(TypeResolver typeResolver) {
        this._typeResolver = typeResolver;
    }

    public MemberResolver setIncludeLangObject(boolean state) {
        this._cfgIncludeLangObject = state;
        return this;
    }

    public MemberResolver setFieldFilter(Filter<RawField> f) {
        this._fieldFilter = f;
        return this;
    }

    public MemberResolver setMethodFilter(Filter<RawMethod> f) {
        this._methodFilter = f;
        return this;
    }

    public MemberResolver setConstructorFilter(Filter<RawConstructor> f) {
        this._constructorFilter = f;
        return this;
    }

    public ResolvedTypeWithMembers resolve(ResolvedType mainType, AnnotationConfiguration annotationConfig, AnnotationOverrides annotationOverrides) {
        HierarchicType[] htypes;
        ArrayList<ResolvedType> types = new ArrayList<ResolvedType>();
        HashSet<ClassKey> seenTypes = new HashSet<ClassKey>();
        if (!this._cfgIncludeLangObject && mainType.getErasedType() == Object.class) {
            types = new ArrayList(1);
            types.add(mainType);
            seenTypes.add(new ClassKey(Object.class));
        } else {
            types = new ArrayList();
            this._gatherTypes(mainType, seenTypes, types);
        }
        HierarchicType mainHierarchicType = null;
        if (annotationOverrides == null) {
            int len = types.size();
            htypes = new HierarchicType[len];
            for (int i = 0; i < len; ++i) {
                htypes[i] = new HierarchicType((ResolvedType)types.get(i), false, i);
            }
            mainHierarchicType = htypes[0];
        } else {
            ArrayList<HierarchicType> typesWithMixins = new ArrayList<HierarchicType>();
            for (ResolvedType type : types) {
                List<Class<?>> m = annotationOverrides.mixInsFor(type.getErasedType());
                if (m != null) {
                    for (Class<?> mixinClass : m) {
                        this._addOverrides(typesWithMixins, seenTypes, mixinClass);
                    }
                }
                HierarchicType ht = new HierarchicType(type, false, typesWithMixins.size());
                if (mainHierarchicType == null) {
                    mainHierarchicType = ht;
                }
                typesWithMixins.add(ht);
            }
            htypes = typesWithMixins.toArray(new HierarchicType[typesWithMixins.size()]);
        }
        return new ResolvedTypeWithMembers(this._typeResolver, annotationConfig, mainHierarchicType, htypes, this._constructorFilter, this._fieldFilter, this._methodFilter);
    }

    private void _addOverrides(List<HierarchicType> typesWithOverrides, Set<ClassKey> seenTypes, Class<?> override) {
        ClassKey key = new ClassKey(override);
        if (!seenTypes.contains(key)) {
            seenTypes.add(key);
            ResolvedType resolvedOverride = this._typeResolver.resolve(override, new Type[0]);
            typesWithOverrides.add(new HierarchicType(resolvedOverride, true, typesWithOverrides.size()));
            for (ResolvedType r : resolvedOverride.getImplementedInterfaces()) {
                this._addOverrides(typesWithOverrides, seenTypes, r);
            }
            ResolvedType superClass = resolvedOverride.getParentClass();
            this._addOverrides(typesWithOverrides, seenTypes, superClass);
        }
    }

    private void _addOverrides(List<HierarchicType> typesWithOverrides, Set<ClassKey> seenTypes, ResolvedType override) {
        if (override == null) {
            return;
        }
        Class<?> raw = override.getErasedType();
        if (!this._cfgIncludeLangObject && Object.class == raw) {
            return;
        }
        ClassKey key = new ClassKey(raw);
        if (!seenTypes.contains(key)) {
            seenTypes.add(key);
            typesWithOverrides.add(new HierarchicType(override, true, typesWithOverrides.size()));
            for (ResolvedType r : override.getImplementedInterfaces()) {
                this._addOverrides(typesWithOverrides, seenTypes, r);
            }
            ResolvedType superClass = override.getParentClass();
            if (superClass != null) {
                this._addOverrides(typesWithOverrides, seenTypes, superClass);
            }
        }
    }

    protected void _gatherTypes(ResolvedType currentType, Set<ClassKey> seenTypes, List<ResolvedType> types) {
        if (currentType == null) {
            return;
        }
        Class<?> raw = currentType.getErasedType();
        if (!this._cfgIncludeLangObject && raw == Object.class) {
            return;
        }
        ClassKey key = new ClassKey(currentType.getErasedType());
        if (seenTypes.contains(key)) {
            return;
        }
        seenTypes.add(key);
        types.add(currentType);
        for (ResolvedType t : currentType.getImplementedInterfaces()) {
            this._gatherTypes(t, seenTypes, types);
        }
        this._gatherTypes(currentType.getParentClass(), seenTypes, types);
    }
}

