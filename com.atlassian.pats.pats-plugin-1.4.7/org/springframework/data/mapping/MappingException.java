/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.mapping;

import org.springframework.lang.Nullable;

public class MappingException
extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public MappingException(@Nullable String s) {
        super(s);
    }

    public MappingException(@Nullable String s, @Nullable Throwable throwable) {
        super(s, throwable);
    }
}

