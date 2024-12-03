/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.json.marshal.wrapped.JsonableBoolean
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.synchrony.config;

import com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.json.marshal.wrapped.JsonableBoolean;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import org.springframework.beans.factory.annotation.Autowired;

public class SelfManagedWRMDataProvider
implements WebResourceDataProvider {
    private SynchronyConfigurationManager configurationManager;

    @Autowired
    public SelfManagedWRMDataProvider(SynchronyConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    }

    public Jsonable get() {
        return new JsonableBoolean(Boolean.valueOf(this.configurationManager.isUsingLocalSynchrony()));
    }
}

