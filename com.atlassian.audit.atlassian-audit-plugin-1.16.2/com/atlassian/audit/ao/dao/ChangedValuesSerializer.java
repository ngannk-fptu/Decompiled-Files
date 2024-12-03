/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.ChangedValue
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.ao.dao;

import com.atlassian.audit.entity.ChangedValue;
import java.util.List;
import javax.annotation.Nonnull;

public interface ChangedValuesSerializer {
    public List<ChangedValue> deserialize(@Nonnull String var1);

    public String serialize(@Nonnull Iterable<ChangedValue> var1);
}

