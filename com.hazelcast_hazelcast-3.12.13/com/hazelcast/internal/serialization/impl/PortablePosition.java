/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.nio.serialization.FieldType;
import java.util.List;
import javax.annotation.Nullable;

interface PortablePosition {
    public int getStreamPosition();

    public int getIndex();

    public boolean isNull();

    public boolean isEmpty();

    public int getLen();

    public int getFactoryId();

    public int getClassId();

    public boolean isMultiPosition();

    public boolean isNullOrEmpty();

    public boolean isLeaf();

    public boolean isAny();

    @Nullable
    public FieldType getType();

    public List<PortablePosition> asMultiPosition();
}

