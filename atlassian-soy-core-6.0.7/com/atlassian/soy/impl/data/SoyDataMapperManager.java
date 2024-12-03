/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.soy.renderer.SoyDataMapper
 */
package com.atlassian.soy.impl.data;

import com.atlassian.soy.renderer.SoyDataMapper;
import java.util.Collections;
import java.util.List;

public class SoyDataMapperManager {
    private final List<SoyDataMapper<?, ?>> customMappers;

    public SoyDataMapperManager() {
        this.customMappers = Collections.emptyList();
    }

    public SoyDataMapperManager(List<SoyDataMapper<?, ?>> customMappers) {
        this.customMappers = customMappers;
    }

    public <I, O> SoyDataMapper<I, O> getMapper(String mapperName) {
        for (SoyDataMapper<?, ?> customMapper : this.customMappers) {
            if (!mapperName.equals(customMapper.getName())) continue;
            return customMapper;
        }
        return null;
    }
}

