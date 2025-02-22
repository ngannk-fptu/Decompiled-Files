/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.getters;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.PortableContext;
import com.hazelcast.internal.serialization.impl.DefaultPortableReader;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.FieldDefinition;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.query.impl.getters.Getter;

final class PortableGetter
extends Getter {
    private final InternalSerializationService serializationService;

    public PortableGetter(InternalSerializationService serializationService) {
        super(null);
        this.serializationService = serializationService;
    }

    @Override
    Object getValue(Object target, String fieldPath) throws Exception {
        Data data = (Data)target;
        PortableContext context = this.serializationService.getPortableContext();
        PortableReader reader = this.serializationService.createPortableReader(data);
        ClassDefinition classDefinition = context.lookupClassDefinition(data);
        FieldDefinition fieldDefinition = context.getFieldDefinition(classDefinition, fieldPath);
        if (fieldDefinition != null) {
            return ((DefaultPortableReader)reader).read(fieldPath);
        }
        return null;
    }

    @Override
    Object getValue(Object obj) throws Exception {
        throw new IllegalArgumentException("Path agnostic value extraction unsupported");
    }

    @Override
    Class getReturnType() {
        throw new IllegalArgumentException("Non applicable for PortableGetter");
    }

    @Override
    boolean isCacheable() {
        return false;
    }
}

