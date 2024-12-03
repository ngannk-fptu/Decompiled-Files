/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v3.decode;

import com.mchange.v3.decode.CannotDecodeException;

public interface Decoder {
    public Object decode(Object var1) throws CannotDecodeException;
}

