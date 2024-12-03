/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization;

import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.PortableFactory;
import java.util.Collection;

public interface PortableHook {
    public int getFactoryId();

    public PortableFactory createFactory();

    public Collection<ClassDefinition> getBuiltinDefinitions();
}

