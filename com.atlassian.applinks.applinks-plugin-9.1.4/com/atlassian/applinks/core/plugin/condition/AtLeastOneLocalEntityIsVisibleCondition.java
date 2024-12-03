/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 *  com.google.common.collect.Iterables
 */
package com.atlassian.applinks.core.plugin.condition;

import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.google.common.collect.Iterables;
import java.util.Map;

public class AtLeastOneLocalEntityIsVisibleCondition
implements Condition {
    private final InternalHostApplication hostApplication;

    public AtLeastOneLocalEntityIsVisibleCondition(InternalHostApplication hostApplication) {
        this.hostApplication = hostApplication;
    }

    public void init(Map<String, String> stringStringMap) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> stringObjectMap) {
        return !Iterables.isEmpty((Iterable)this.hostApplication.getLocalEntities());
    }
}

