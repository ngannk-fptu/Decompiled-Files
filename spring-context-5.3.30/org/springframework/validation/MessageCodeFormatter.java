/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.validation;

import org.springframework.lang.Nullable;

@FunctionalInterface
public interface MessageCodeFormatter {
    public String format(String var1, @Nullable String var2, @Nullable String var3);
}

