/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import java.util.Collections;
import java.util.Set;
import org.hibernate.Incubating;
import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;

@Incubating
public interface PersistentAttributeInterceptor
extends LazyPropertyInitializer.InterceptorImplementor {
    public boolean readBoolean(Object var1, String var2, boolean var3);

    public boolean writeBoolean(Object var1, String var2, boolean var3, boolean var4);

    public byte readByte(Object var1, String var2, byte var3);

    public byte writeByte(Object var1, String var2, byte var3, byte var4);

    public char readChar(Object var1, String var2, char var3);

    public char writeChar(Object var1, String var2, char var3, char var4);

    public short readShort(Object var1, String var2, short var3);

    public short writeShort(Object var1, String var2, short var3, short var4);

    public int readInt(Object var1, String var2, int var3);

    public int writeInt(Object var1, String var2, int var3, int var4);

    public float readFloat(Object var1, String var2, float var3);

    public float writeFloat(Object var1, String var2, float var3, float var4);

    public double readDouble(Object var1, String var2, double var3);

    public double writeDouble(Object var1, String var2, double var3, double var5);

    public long readLong(Object var1, String var2, long var3);

    public long writeLong(Object var1, String var2, long var3, long var5);

    public Object readObject(Object var1, String var2, Object var3);

    public Object writeObject(Object var1, String var2, Object var3, Object var4);

    @Override
    @Deprecated
    default public Set<String> getInitializedLazyAttributeNames() {
        return Collections.emptySet();
    }

    @Override
    @Deprecated
    default public void attributeInitialized(String name) {
    }

    @Deprecated
    default public boolean isAttributeLoaded(String fieldName) {
        return false;
    }
}

