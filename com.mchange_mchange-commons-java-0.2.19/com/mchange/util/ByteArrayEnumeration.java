/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util;

import com.mchange.io.IOByteArrayEnumeration;
import com.mchange.util.MEnumeration;

public interface ByteArrayEnumeration
extends MEnumeration,
IOByteArrayEnumeration {
    @Override
    public byte[] nextBytes();

    @Override
    public boolean hasMoreBytes();
}

