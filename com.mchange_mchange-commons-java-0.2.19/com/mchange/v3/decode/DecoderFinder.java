/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v3.decode;

import com.mchange.v3.decode.CannotDecodeException;

public interface DecoderFinder {
    public String decoderClassName(Object var1) throws CannotDecodeException;
}

