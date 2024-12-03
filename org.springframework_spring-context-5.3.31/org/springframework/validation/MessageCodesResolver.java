/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.validation;

import org.springframework.lang.Nullable;

public interface MessageCodesResolver {
    public String[] resolveMessageCodes(String var1, String var2);

    public String[] resolveMessageCodes(String var1, String var2, String var3, @Nullable Class<?> var4);
}

