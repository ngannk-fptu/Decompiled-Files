/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal.crypto.keywrap;

import java.security.Key;

public interface KeyWrapper {
    public byte[] unwrapCek(byte[] var1, Key var2);

    public byte[] wrapCek(byte[] var1, Key var2);
}

