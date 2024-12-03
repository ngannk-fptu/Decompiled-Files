/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context;

import org.springframework.lang.Nullable;

@FunctionalInterface
public interface MessageSourceResolvable {
    @Nullable
    public String[] getCodes();

    @Nullable
    default public Object[] getArguments() {
        return null;
    }

    @Nullable
    default public String getDefaultMessage() {
        return null;
    }
}

