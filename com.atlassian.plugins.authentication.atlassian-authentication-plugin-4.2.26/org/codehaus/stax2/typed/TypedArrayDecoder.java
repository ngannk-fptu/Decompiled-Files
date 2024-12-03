/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.typed;

public abstract class TypedArrayDecoder {
    public abstract boolean decodeValue(String var1) throws IllegalArgumentException;

    public abstract boolean decodeValue(char[] var1, int var2, int var3) throws IllegalArgumentException;

    public abstract int getCount();

    public abstract boolean hasRoom();
}

