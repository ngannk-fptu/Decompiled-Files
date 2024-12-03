/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.NestedRuntimeException
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.multipart;

import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;

public class MultipartException
extends NestedRuntimeException {
    public MultipartException(String msg) {
        super(msg);
    }

    public MultipartException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}

