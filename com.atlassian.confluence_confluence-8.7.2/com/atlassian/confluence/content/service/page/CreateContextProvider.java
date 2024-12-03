/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.content.service.page;

import com.google.common.collect.Maps;
import java.io.Serializable;
import java.util.Map;

public interface CreateContextProvider {
    public static final CreateContextProvider EMPTY_CONTEXT_PROVIDER = new CreateContextProvider(){
        private final Map<String, Serializable> context = Maps.newHashMap();

        @Override
        public Map<String, Serializable> getContext() {
            return this.context;
        }
    };

    public Map<String, Serializable> getContext();
}

