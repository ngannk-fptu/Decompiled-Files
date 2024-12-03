/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization;

import com.hazelcast.core.ManagedContext;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.FieldDefinition;
import com.hazelcast.nio.serialization.Portable;
import java.io.IOException;
import java.nio.ByteOrder;

public interface PortableContext {
    public int getVersion();

    public int getClassVersion(int var1, int var2);

    public void setClassVersion(int var1, int var2, int var3);

    public ClassDefinition lookupClassDefinition(int var1, int var2, int var3);

    public ClassDefinition lookupClassDefinition(Data var1) throws IOException;

    public ClassDefinition registerClassDefinition(ClassDefinition var1);

    public ClassDefinition lookupOrRegisterClassDefinition(Portable var1) throws IOException;

    public FieldDefinition getFieldDefinition(ClassDefinition var1, String var2);

    public ManagedContext getManagedContext();

    public ByteOrder getByteOrder();
}

