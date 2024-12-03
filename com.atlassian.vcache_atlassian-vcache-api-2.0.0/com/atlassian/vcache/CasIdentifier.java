/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.Nullable
 */
package com.atlassian.vcache;

import com.atlassian.annotations.PublicApi;
import java.io.Serializable;
import javax.annotation.Nullable;

@PublicApi
public interface CasIdentifier
extends Serializable {
    public boolean equals(@Nullable Object var1);
}

