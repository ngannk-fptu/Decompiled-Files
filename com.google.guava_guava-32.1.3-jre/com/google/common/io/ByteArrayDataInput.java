/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.CheckForNull
 */
package com.google.common.io;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.J2ktIncompatible;
import com.google.common.io.ElementTypesAreNonnullByDefault;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.DataInput;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public interface ByteArrayDataInput
extends DataInput {
    @Override
    public void readFully(byte[] var1);

    @Override
    public void readFully(byte[] var1, int var2, int var3);

    @Override
    public int skipBytes(int var1);

    @Override
    @CanIgnoreReturnValue
    public boolean readBoolean();

    @Override
    @CanIgnoreReturnValue
    public byte readByte();

    @Override
    @CanIgnoreReturnValue
    public int readUnsignedByte();

    @Override
    @CanIgnoreReturnValue
    public short readShort();

    @Override
    @CanIgnoreReturnValue
    public int readUnsignedShort();

    @Override
    @CanIgnoreReturnValue
    public char readChar();

    @Override
    @CanIgnoreReturnValue
    public int readInt();

    @Override
    @CanIgnoreReturnValue
    public long readLong();

    @Override
    @CanIgnoreReturnValue
    public float readFloat();

    @Override
    @CanIgnoreReturnValue
    public double readDouble();

    @Override
    @CheckForNull
    @CanIgnoreReturnValue
    public String readLine();

    @Override
    @CanIgnoreReturnValue
    public String readUTF();
}

