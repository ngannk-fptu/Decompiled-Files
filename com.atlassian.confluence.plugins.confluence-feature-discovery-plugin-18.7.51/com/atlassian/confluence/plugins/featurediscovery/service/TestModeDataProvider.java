/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.confluence.plugins.featurediscovery.service;

import com.atlassian.json.marshal.Jsonable;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import org.codehaus.jackson.map.ObjectMapper;

public class TestModeDataProvider
implements WebResourceDataProvider {
    public Jsonable get() {
        return writer -> {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(writer, (Object)Boolean.getBoolean("discovery.test.mode"));
        };
    }
}

