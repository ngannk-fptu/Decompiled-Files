/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.business.insights.api.filter.OptOutEntityIdentifier
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.ao.dao;

import com.atlassian.business.insights.api.filter.OptOutEntityIdentifier;
import com.atlassian.business.insights.core.ao.dao.entity.AoEntityOptOutIdentifier;
import java.util.List;
import javax.annotation.Nonnull;

public interface AoEntityOptOutIdentifierDao {
    @Nonnull
    public List<AoEntityOptOutIdentifier> save(@Nonnull List<OptOutEntityIdentifier> var1);

    @Nonnull
    public List<AoEntityOptOutIdentifier> get(int var1, int var2) throws IllegalArgumentException;

    public void delete(@Nonnull List<OptOutEntityIdentifier> var1);
}

