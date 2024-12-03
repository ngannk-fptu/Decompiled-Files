/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.typed;

public abstract class TypedValueDecoder {
    public abstract void decode(String var1) throws IllegalArgumentException;

    public abstract void decode(char[] var1, int var2, int var3) throws IllegalArgumentException;

    public abstract void handleEmptyValue() throws IllegalArgumentException;
}

