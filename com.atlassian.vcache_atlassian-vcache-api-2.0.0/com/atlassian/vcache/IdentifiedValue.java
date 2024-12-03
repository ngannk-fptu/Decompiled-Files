/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.vcache;

import com.atlassian.annotations.PublicApi;
import com.atlassian.vcache.CasIdentifier;

@PublicApi
public interface IdentifiedValue<T> {
    public T value();

    public CasIdentifier identifier();
}

