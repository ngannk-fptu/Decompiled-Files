/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans;

import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;

public abstract class BeansException
extends NestedRuntimeException {
    public BeansException(String msg) {
        super(msg);
    }

    public BeansException(@Nullable String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}

