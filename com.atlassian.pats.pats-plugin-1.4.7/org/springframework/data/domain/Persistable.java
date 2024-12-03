/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.domain;

import org.springframework.lang.Nullable;

public interface Persistable<ID> {
    @Nullable
    public ID getId();

    public boolean isNew();
}

