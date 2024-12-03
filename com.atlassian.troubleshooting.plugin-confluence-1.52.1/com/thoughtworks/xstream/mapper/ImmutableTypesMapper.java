/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import java.util.HashSet;
import java.util.Set;

public class ImmutableTypesMapper
extends MapperWrapper {
    private final Set unreferenceableTypes = new HashSet();
    private final Set immutableTypes = new HashSet();

    public ImmutableTypesMapper(Mapper wrapped) {
        super(wrapped);
    }

    public void addImmutableType(Class type) {
        this.addImmutableType(type, true);
    }

    public void addImmutableType(Class type, boolean isReferenceable) {
        this.immutableTypes.add(type);
        if (!isReferenceable) {
            this.unreferenceableTypes.add(type);
        } else {
            this.unreferenceableTypes.remove(type);
        }
    }

    public boolean isImmutableValueType(Class type) {
        if (this.immutableTypes.contains(type)) {
            return true;
        }
        return super.isImmutableValueType(type);
    }

    public boolean isReferenceable(Class type) {
        if (this.immutableTypes.contains(type)) {
            return !this.unreferenceableTypes.contains(type);
        }
        return super.isReferenceable(type);
    }
}

