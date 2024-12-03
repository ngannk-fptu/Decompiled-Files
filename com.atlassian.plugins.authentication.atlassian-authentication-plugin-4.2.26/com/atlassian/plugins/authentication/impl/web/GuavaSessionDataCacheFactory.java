/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent
 */
package com.atlassian.plugins.authentication.impl.web;

import com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent;
import com.atlassian.plugins.authentication.impl.web.AbstractSessionDataCacheFactory;
import com.atlassian.plugins.authentication.impl.web.SessionDataCache;
import com.atlassian.plugins.authentication.impl.web.SessionDataCacheConfiguration;

@JiraComponent
@BambooComponent
public class GuavaSessionDataCacheFactory
extends AbstractSessionDataCacheFactory {
    @Override
    public SessionDataCache createSessionDataCache(SessionDataCacheConfiguration configuration) {
        return this.getGuavaSessionDataCache(configuration);
    }
}

