/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
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

