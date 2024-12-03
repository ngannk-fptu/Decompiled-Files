/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.repository.init;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.repository.init.AbstractRepositoryPopulatorFactoryBean;
import org.springframework.data.repository.init.Jackson2ResourceReader;
import org.springframework.data.repository.init.ResourceReader;
import org.springframework.lang.Nullable;

public class Jackson2RepositoryPopulatorFactoryBean
extends AbstractRepositoryPopulatorFactoryBean {
    @Nullable
    private ObjectMapper mapper;

    public void setMapper(@Nullable ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    protected ResourceReader getResourceReader() {
        return new Jackson2ResourceReader(this.mapper);
    }
}

