/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.core;

import org.springframework.lang.Nullable;

public interface SqlProvider {
    @Nullable
    public String getSql();
}

