/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.ao.dao;

import com.atlassian.business.insights.core.ao.dao.entity.AoDataPipelineConfig;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface AoDataPipelineConfigDao {
    @Nonnull
    public AoDataPipelineConfig put(@Nonnull String var1, @Nonnull String var2);

    @Nonnull
    public Optional<AoDataPipelineConfig> get(@Nonnull String var1);

    public void delete(@Nonnull String var1);
}

