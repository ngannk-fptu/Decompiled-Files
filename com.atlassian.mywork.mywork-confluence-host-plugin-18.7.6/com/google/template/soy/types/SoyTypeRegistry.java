/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Interner
 *  com.google.common.collect.Interners
 */
package com.google.template.soy.types;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.template.soy.types.SoyType;
import com.google.template.soy.types.SoyTypeProvider;
import com.google.template.soy.types.aggregate.ListType;
import com.google.template.soy.types.aggregate.MapType;
import com.google.template.soy.types.aggregate.RecordType;
import com.google.template.soy.types.aggregate.UnionType;
import com.google.template.soy.types.primitive.AnyType;
import com.google.template.soy.types.primitive.BoolType;
import com.google.template.soy.types.primitive.FloatType;
import com.google.template.soy.types.primitive.IntType;
import com.google.template.soy.types.primitive.NullType;
import com.google.template.soy.types.primitive.SanitizedType;
import com.google.template.soy.types.primitive.StringType;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Singleton
public final class SoyTypeRegistry {
    private static final Map<String, SoyType> BUILTIN_TYPES = ImmutableMap.builder().put((Object)"any", (Object)AnyType.getInstance()).put((Object)"null", (Object)NullType.getInstance()).put((Object)"bool", (Object)BoolType.getInstance()).put((Object)"int", (Object)IntType.getInstance()).put((Object)"float", (Object)FloatType.getInstance()).put((Object)"string", (Object)StringType.getInstance()).put((Object)"number", (Object)UnionType.of(IntType.getInstance(), FloatType.getInstance())).put((Object)"html", (Object)SanitizedType.HtmlType.getInstance()).put((Object)"attributes", (Object)SanitizedType.AttributesType.getInstance()).put((Object)"css", (Object)SanitizedType.CssType.getInstance()).put((Object)"uri", (Object)SanitizedType.UriType.getInstance()).put((Object)"js", (Object)SanitizedType.JsType.getInstance()).build();
    private final ImmutableSet<SoyTypeProvider> typeProviders;
    private final Interner<ListType> listTypes = Interners.newStrongInterner();
    private final Interner<MapType> mapTypes = Interners.newStrongInterner();
    private final Interner<UnionType> unionTypes = Interners.newStrongInterner();
    private final Interner<RecordType> recordTypes = Interners.newStrongInterner();

    @Inject
    public SoyTypeRegistry(Set<SoyTypeProvider> typeProviders) {
        this.typeProviders = ImmutableSet.copyOf(typeProviders);
    }

    @VisibleForTesting
    public SoyTypeRegistry() {
        this.typeProviders = ImmutableSet.of();
    }

    public SoyType getType(String typeName) {
        SoyType result = BUILTIN_TYPES.get(typeName);
        if (result != null) {
            return result;
        }
        for (SoyTypeProvider provider : this.typeProviders) {
            result = provider.getType(typeName, this);
            if (result == null) continue;
            return result;
        }
        return null;
    }

    public ListType getOrCreateListType(SoyType elementType) {
        return (ListType)this.listTypes.intern((Object)ListType.of(elementType));
    }

    public MapType getOrCreateMapType(SoyType keyType, SoyType valueType) {
        return (MapType)this.mapTypes.intern((Object)MapType.of(keyType, valueType));
    }

    public UnionType getOrCreateUnionType(Collection<SoyType> members) {
        return (UnionType)this.unionTypes.intern((Object)UnionType.of(members));
    }

    public UnionType getOrCreateUnionType(SoyType ... members) {
        return (UnionType)this.unionTypes.intern((Object)UnionType.of(members));
    }

    public RecordType getOrCreateRecordType(Map<String, SoyType> fields) {
        return (RecordType)this.recordTypes.intern((Object)RecordType.of(fields));
    }
}

