/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.jsontype.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.MapperConfig;
import org.codehaus.jackson.map.introspect.AnnotatedClass;
import org.codehaus.jackson.map.introspect.AnnotatedMember;
import org.codehaus.jackson.map.jsontype.NamedType;
import org.codehaus.jackson.map.jsontype.SubtypeResolver;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class StdSubtypeResolver
extends SubtypeResolver {
    protected LinkedHashSet<NamedType> _registeredSubtypes;

    @Override
    public void registerSubtypes(NamedType ... types) {
        if (this._registeredSubtypes == null) {
            this._registeredSubtypes = new LinkedHashSet();
        }
        for (NamedType type : types) {
            this._registeredSubtypes.add(type);
        }
    }

    @Override
    public void registerSubtypes(Class<?> ... classes) {
        NamedType[] types = new NamedType[classes.length];
        int len = classes.length;
        for (int i = 0; i < len; ++i) {
            types[i] = new NamedType(classes[i]);
        }
        this.registerSubtypes(types);
    }

    @Override
    public Collection<NamedType> collectAndResolveSubtypes(AnnotatedMember property, MapperConfig<?> config, AnnotationIntrospector ai) {
        List<NamedType> st;
        HashMap<NamedType, NamedType> collected = new HashMap<NamedType, NamedType>();
        if (this._registeredSubtypes != null) {
            Class<?> rawBase = property.getRawType();
            for (NamedType subtype : this._registeredSubtypes) {
                if (!rawBase.isAssignableFrom(subtype.getType())) continue;
                AnnotatedClass curr = AnnotatedClass.constructWithoutSuperTypes(subtype.getType(), ai, config);
                this._collectAndResolve(curr, subtype, config, ai, collected);
            }
        }
        if ((st = ai.findSubtypes(property)) != null) {
            for (NamedType nt : st) {
                AnnotatedClass ac = AnnotatedClass.constructWithoutSuperTypes(nt.getType(), ai, config);
                this._collectAndResolve(ac, nt, config, ai, collected);
            }
        }
        NamedType rootType = new NamedType(property.getRawType(), null);
        AnnotatedClass ac = AnnotatedClass.constructWithoutSuperTypes(property.getRawType(), ai, config);
        this._collectAndResolve(ac, rootType, config, ai, collected);
        return new ArrayList<NamedType>(collected.values());
    }

    @Override
    public Collection<NamedType> collectAndResolveSubtypes(AnnotatedClass type, MapperConfig<?> config, AnnotationIntrospector ai) {
        HashMap<NamedType, NamedType> subtypes = new HashMap<NamedType, NamedType>();
        if (this._registeredSubtypes != null) {
            Class<?> rawBase = type.getRawType();
            for (NamedType subtype : this._registeredSubtypes) {
                if (!rawBase.isAssignableFrom(subtype.getType())) continue;
                AnnotatedClass curr = AnnotatedClass.constructWithoutSuperTypes(subtype.getType(), ai, config);
                this._collectAndResolve(curr, subtype, config, ai, subtypes);
            }
        }
        NamedType rootType = new NamedType(type.getRawType(), null);
        this._collectAndResolve(type, rootType, config, ai, subtypes);
        return new ArrayList<NamedType>(subtypes.values());
    }

    protected void _collectAndResolve(AnnotatedClass annotatedType, NamedType namedType, MapperConfig<?> config, AnnotationIntrospector ai, HashMap<NamedType, NamedType> collectedSubtypes) {
        String name;
        if (!namedType.hasName() && (name = ai.findTypeName(annotatedType)) != null) {
            namedType = new NamedType(namedType.getType(), name);
        }
        if (collectedSubtypes.containsKey(namedType)) {
            NamedType prev;
            if (namedType.hasName() && !(prev = collectedSubtypes.get(namedType)).hasName()) {
                collectedSubtypes.put(namedType, namedType);
            }
            return;
        }
        collectedSubtypes.put(namedType, namedType);
        List<NamedType> st = ai.findSubtypes(annotatedType);
        if (st != null && !st.isEmpty()) {
            for (NamedType subtype : st) {
                AnnotatedClass subtypeClass = AnnotatedClass.constructWithoutSuperTypes(subtype.getType(), ai, config);
                if (!subtype.hasName()) {
                    subtype = new NamedType(subtype.getType(), ai.findTypeName(subtypeClass));
                }
                this._collectAndResolve(subtypeClass, subtype, config, ai, collectedSubtypes);
            }
        }
    }
}

