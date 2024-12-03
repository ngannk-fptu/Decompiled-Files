/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  javax.annotation.Nonnull
 *  net.java.ao.DBParam
 *  net.java.ao.Query
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.business.insights.core.ao.dao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.business.insights.core.ao.dao.AoDataPipelineConfigDao;
import com.atlassian.business.insights.core.ao.dao.entity.AoDataPipelineConfig;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.java.ao.DBParam;
import net.java.ao.Query;
import org.apache.commons.lang3.Validate;

public class DefaultAoDataPipelineConfigDao
implements AoDataPipelineConfigDao {
    private final ActiveObjects ao;

    public DefaultAoDataPipelineConfigDao(@Nonnull ActiveObjects ao) {
        this.ao = Objects.requireNonNull(ao);
    }

    @Override
    @Nonnull
    public AoDataPipelineConfig put(@Nonnull String key, @Nonnull String value) {
        Validate.notBlank((CharSequence)key, (String)"The config key cannot be null or empty", (Object[])new Object[0]);
        Validate.notBlank((CharSequence)value, (String)"The config value cannot be null or empty", (Object[])new Object[0]);
        Optional<AoDataPipelineConfig> existingConfig = this.get(key);
        if (existingConfig.isPresent()) {
            existingConfig.get().setValue(value);
            return this.update(existingConfig.get());
        }
        return (AoDataPipelineConfig)this.ao.executeInTransaction(() -> {
            AoDataPipelineConfig config = (AoDataPipelineConfig)this.ao.create(AoDataPipelineConfig.class, new DBParam[]{new DBParam("KEY", (Object)key), new DBParam("VALUE", (Object)value)});
            config.save();
            return config;
        });
    }

    @Override
    @Nonnull
    public Optional<AoDataPipelineConfig> get(@Nonnull String key) {
        Validate.notBlank((CharSequence)key, (String)"The config key cannot be null or empty", (Object[])new Object[0]);
        return (Optional)this.ao.executeInTransaction(() -> Arrays.stream(this.ao.find(AoDataPipelineConfig.class, Query.select().where("KEY = ?", new Object[]{key}))).findFirst());
    }

    @Override
    public void delete(@Nonnull String key) {
        Validate.notBlank((CharSequence)key, (String)"The config key cannot be null or empty", (Object[])new Object[0]);
        this.get(key).ifPresent($ -> this.ao.deleteWithSQL(AoDataPipelineConfig.class, String.format("%s = ?", "KEY"), new Object[]{key}));
    }

    @Nonnull
    private AoDataPipelineConfig update(@Nonnull AoDataPipelineConfig config) {
        Objects.requireNonNull(config);
        return (AoDataPipelineConfig)this.ao.executeInTransaction(() -> {
            config.save();
            return config;
        });
    }
}

