/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.codec;

import org.springframework.core.codec.CodecException;
import org.springframework.lang.Nullable;

public class DecodingException
extends CodecException {
    public DecodingException(String msg) {
        super(msg);
    }

    public DecodingException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}

