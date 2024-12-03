/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.validation;

import org.springframework.lang.Nullable;

public interface MessageCodesResolver {
    public String[] resolveMessageCodes(String var1, String var2);

    public String[] resolveMessageCodes(String var1, String var2, String var3, @Nullable Class<?> var4);
}

