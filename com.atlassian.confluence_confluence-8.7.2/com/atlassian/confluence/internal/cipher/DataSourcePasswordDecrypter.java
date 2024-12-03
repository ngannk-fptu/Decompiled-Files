/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.internal.cipher;

import javax.annotation.Nullable;

@FunctionalInterface
public interface DataSourcePasswordDecrypter {
    @Nullable
    public String decrypt(@Nullable String var1);
}

